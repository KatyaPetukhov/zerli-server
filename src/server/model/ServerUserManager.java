package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Role;
import common.Shop;
import common.interfaces.UserManager;
//import common.request_data.IncomeReport;
import common.request_data.User;

public class ServerUserManager extends BaseSQL implements UserManager {
	/*
	 * SQL queries for Users manipulation. See UserManager interface for explanation
	 * on each function.
	 */

	/* SQL SCHEMA: */
	private static String TABLE_NAME = "users";
	private static String USERNAME = "username"; // key
	private static String PASSWORD = "password";
	private static String NICKNAME = "nickname";
	private static String USERROLE = "userrole"; // varchar
	private static String SHOP_NAME = "ShopName";
	private static String APPROVED = "approved"; // boolean

	private static String VARCHAR = " varchar(255)";
	private static String BOOLEAN = " boolean";
	
//	private List<IncomeReport> allShopIncomeReportList;
	/* End SQL SCHEMA */

	private User requestedBy;
	private Connection connection;
	private boolean isManager; // Same check is made multiple times. Easier to store variable.

	public ServerUserManager(User requestedBy, Connection connection) {
		this.requestedBy = requestedBy;
		this.connection = connection;
		this.isManager = this.requestedBy != null && this.requestedBy.userrole == Role.MANAGER;
	}

	/* Not interface function: */
	
	public static boolean test(Connection connection) {
		/*
		 * Tests connection to a table, just to validate that table is available. Can be
		 * any other query.
		 */
		try {
			connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " LIMIT 1;").executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void resetUsers(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + TABLE_NAME + " (" + USERNAME + VARCHAR + ", " + PASSWORD + VARCHAR + ", " + NICKNAME
				+ VARCHAR + ", " + SHOP_NAME + VARCHAR + ", "+ USERROLE + VARCHAR + ", " + APPROVED + BOOLEAN + ", PRIMARY KEY (" + USERNAME
				+ "));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void resetOrders(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + "orders" + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + "orders" + " (" + USERNAME + VARCHAR + ", " + "OrderID" + VARCHAR + ", " + SHOP_NAME + VARCHAR + ", "+ APPROVED + VARCHAR + ", PRIMARY KEY (" + USERNAME
				+ "));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public static void resetIncomeReports(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + "income_reports" + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			}
		query = "CREATE TABLE " + "income_reports" + " (" + SHOP_NAME + VARCHAR + ", " + "Year" + VARCHAR + ", " + "Month" + VARCHAR + ", "+ "Income" + VARCHAR +
				", " + "BestSellingProduct" + VARCHAR + ", " + "TotalNumberOfOrders" + VARCHAR + ");";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

//	/* Interface functions: */
//	@Override
//	public IncomeReport getAllIncomeReports(){
//		IncomeReport r = allShopIncomeReportList.get(allShopIncomeReportList.size()-1);
//		allShopIncomeReportList.remove(allShopIncomeReportList.size()-1);
//		return r;
//	}
	
	@Override
	public User getUser(String username, String password) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			/* username is a key, so there can be 0 or 1 objects only. */
			while (rs.next()) {
				User user = new User();
				user.username = rs.getString(USERNAME);
				user.password = rs.getString(PASSWORD);
				user.nickname = rs.getString(NICKNAME);
				user.shopname = Shop.valueOf(rs.getString(SHOP_NAME));
				user.approved = (rs.getInt(APPROVED) != 0 ? true : false);
				user.userrole = Role.valueOf(rs.getString(USERROLE));
				if (!user.password.equals(password)) {
					return null;
				}
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean addNewUser(String username, String password, String nickname,Shop shopname, Role role, boolean approved)
			throws WeakPassword, PermissionDenied {
		if (!isManager && approved == true) {
			throw new PermissionDenied();
		}
		checkPasswordStrength(password);
		String query = "INSERT INTO " + TABLE_NAME + " VALUES (" + "'" + username + "', " + "'" + password + "', " + "'"
				+ nickname + "', " + "'" + shopname.name() +  "', " + "'" + role.name() + "', " + (approved ? 1 : 0) + ");";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
//	@Override
//	public boolean addNewOrder(String username, String orderID, Shop shopname, String approved)
//			throws WeakPassword, PermissionDenied {
//		// TODO Auto-generated method stub
//		String query = "INSERT INTO " + "orders" + " VALUES (" + "'" + username + "', " + "'" + orderID + "', " + 
//		"'" + shopname.name() +  "', " + "'" + approved + "');";
//		try {
//			runUpdate(connection, query);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

	@Override
	public boolean approveUser(String username) throws PermissionDenied {
		if (!isManager) {
			throw new PermissionDenied();
		}
		String query = "UPDATE " + TABLE_NAME + " SET " + APPROVED + "=1 WHERE " + USERNAME + "='" + username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			/* TODO: probably incorrect. Will not fail if result is empty. */
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean removeUser(String username) throws PermissionDenied {
		if (!isManager) {
			throw new PermissionDenied();
		}
		String query = "DELETE FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			/* TODO: Check that when user does not exists - there is an error. */
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public List<User> getUsers(boolean approved, int start, int amount) throws PermissionDenied {
		if (!isManager) {
			throw new PermissionDenied();
		}
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + APPROVED + "=" + (approved ? 1 : 0) + " LIMIT "
				+ start + "," + amount + ";";
		List<User> users = new ArrayList<User>();

		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) {
				User user = new User();
				user.username = rs.getString(USERNAME);
				user.password = rs.getString(PASSWORD);
				user.nickname = rs.getString(NICKNAME);
				user.shopname = Shop.valueOf(rs.getString(SHOP_NAME));
				user.approved = (rs.getInt(APPROVED) != 0 ? true : false);
				user.userrole = Role.valueOf(rs.getString(USERROLE));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
	
//	@Override
//	public boolean addNewIncomeReport(Shop shop,String year,String month,String Income,String BSI,String TNO) {
//		String query = "INSERT INTO " + "income_reports" + " VALUES (" + "'" + shop.name() + "', " + "'" + year + "', " + "'"
//				+ month + "', " + "'" + Income +  "', " + "'" + BSI + "', " + TNO + ");";
//		try {
//			runUpdate(connection, query);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

	private static void checkPasswordStrength(String password) throws WeakPassword {
		if (password == null || password.isEmpty()) {
			throw new WeakPassword("Password cannot be empty.");
		}
	}

//	@Override
//	public IncomeReport getIncomeReport(Shop shop, String year, String month) throws SQLException {
//		IncomeReport incomeReport = new IncomeReport();
//		allShopIncomeReportList = new ArrayList<>();
//		//Get the report the user asked for
//		String query = "SELECT * FROM income_reports WHERE ShopName = '" + shop.name() + "' AND Year = '" + year + "' AND Month = '" + month + "';";
//		try {
//			ResultSet rs = runQuery(connection, query);
//			
//			while (rs.next()) {
//				
//				incomeReport.shop = shop;
//				incomeReport.year = year;
//				incomeReport.month = month;
//				incomeReport.income = rs.getString("Income");
//				incomeReport.bestSellingProduct = rs.getString("BestSellingProduct");
//				incomeReport.totalNumberOfOrders = rs.getString("TotalNumberOfOrders");
//		
//			
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		
//		// Get the income of other shops for the same year and months
//		Shop[] allShops = Shop.values();
//		for(Shop s: allShops) {
//			if(!s.toString().equals(shop.toString())) {
//				query = "SELECT * FROM income_reports WHERE ShopName = '" + s.name() + "' AND Year = '" + year + "' AND Month = '" + month + "';";
//				try {
//					ResultSet rs = runQuery(connection, query);
//					
//					while (rs.next()) {
//						IncomeReport incomeReportM = new IncomeReport();
//						incomeReportM.shop = shop;
//						incomeReportM.year = year;
//						incomeReportM.month = month;
//						incomeReportM.income = rs.getString("Income");
//						incomeReportM.bestSellingProduct = rs.getString("BestSellingProduct");
//						incomeReportM.totalNumberOfOrders = rs.getString("TotalNumberOfOrders");
//						
//						allShopIncomeReportList.add(incomeReportM);
//						
//						
//					}
//					
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		System.out.println(incomeReport.income + "GOT TO GET INCOMEREPORT FUNCTION");
//		return incomeReport;
//	}

	
}

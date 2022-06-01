package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Role;

import common.interfaces.UserManager;
import common.request_data.Complaint;
import common.request_data.ComplaintList;
import common.request_data.IncomeReport;
import common.request_data.IncomeReportList;

import common.request_data.Shop;
import common.request_data.User;
import common.request_data.UsersList;

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
	private static String CARD_NUMBER = "cardNumber"; 
	private static String EXPIRATION_DATE = "expirationDate"; 
	private static String CVV = "CVV"; 
	private static String LOG_INFO = "logInfo"; // boolean
	private static String INT = " int";
	

	private static String VARCHAR = " varchar(255)";
	private static String BOOLEAN = " boolean";
	
	private static IncomeReportList incomeReportList = new IncomeReportList();
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
	
	public static void resetSurvey(Connection connection) {
		String query = "DROP TABLE IF EXISTS surveys;";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		} // TO-CHECK
		query = "CREATE TABLE surveys ( surveyId int NOT NULL AUTO_INCREMENT ,q1 VARCHAR(255) ,q2 VARCHAR(255) ,q3 VARCHAR(255) ,q4 VARCHAR(255) ,q5 VARCHAR(255) ,q6 VARCHAR(255) ,type VARCHAR(255) ,shopName VARCHAR(255) ,date VARCHAR(255) ,PRIMARY KEY (surveyId));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void resetComplaints(Connection connection) {
		String query = "DROP TABLE IF EXISTS complaints;";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE complaints (" + USERNAME + VARCHAR + ", orderId" + VARCHAR + ", " + "complaint" + VARCHAR
				+ ", " + "date" + VARCHAR + ", " + "price" + VARCHAR + ", complaintStatus" + VARCHAR + ", supportName"
				+ VARCHAR + ", refund" + VARCHAR + ", PRIMARY KEY ( orderId));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void resetUsers(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + TABLE_NAME + " (" + USERNAME + VARCHAR + ", " + PASSWORD + VARCHAR + ", " + NICKNAME
				+ VARCHAR + ", " + SHOP_NAME + VARCHAR + ", "+ USERROLE + VARCHAR + ", " + APPROVED + BOOLEAN 
				+ ", "+ CARD_NUMBER + VARCHAR + ", "+ EXPIRATION_DATE + VARCHAR + ", "
				+ CVV + VARCHAR + ", " + LOG_INFO + BOOLEAN + ", " + "userWallet" + VARCHAR + ", PRIMARY KEY (" + USERNAME
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
	@Override
	public User getLoggedInUser(String username, String password) {
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
				user.cardNumber = rs.getString(CARD_NUMBER);
				user.exDate = rs.getString(EXPIRATION_DATE);
				user.cvv = rs.getString(CVV);
				user.logInfo = (rs.getInt(LOG_INFO) != 0 ? true : false);
				user.setAccountStatus();
			
	
				return user;
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	

	/* Interface functions: */
	@Override
	public IncomeReportList getAllIncomeReports(){
		
		return incomeReportList;
	}
	
	
	
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
				user.cardNumber = rs.getString(CARD_NUMBER);
				user.exDate = rs.getString(EXPIRATION_DATE);
				user.cvv = rs.getString(CVV);
				user.logInfo = (rs.getInt(LOG_INFO) != 0 ? true : false);
				user.setAccountStatus();
				if (!user.password.equals(password) || user.logInfo )  {
				
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
	public boolean addNewUser(String username, String password, String nickname,Shop shopname, Role role,
			boolean approved,String cardNumber,String expirationDate,String cvv,boolean logInfo,String userWallet)
			throws WeakPassword, PermissionDenied {
		System.out.println("Line 236 serveruserMnager" + username+password+nickname+role.name()+(approved ? 1 : 0)+cardNumber+expirationDate+cvv);
	
		String query = "INSERT INTO " + TABLE_NAME + " VALUES (" + "'" + username + "', " + "'" + password + "', " + "'"
				+ nickname + "', " + "'" + shopname.name() +  "', " + "'" + role.name() + "', " + (approved ? 1 : 0) 
				+ ", " + "'" + cardNumber + "', " + "'" + expirationDate + "', " + "'" + cvv + "', " + "'" + (logInfo ? 1 : 0) + "', " + "'" + userWallet + "');";
		
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean addNewOrder(String username, String orderID, Shop shopname, String approved)
			throws WeakPassword, PermissionDenied {
		// TODO Auto-generated method stub
		String query = "INSERT INTO " + "orders" + " VALUES (" + "'" + username + "', " + "'" + orderID + "', " + 
		"'" + shopname.name() +  "', " + "'" + approved + "');";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

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
	public UsersList getUsers() {
	
		String query = "SELECT * FROM " + TABLE_NAME + ";";
		UsersList usersList= new UsersList();
		usersList.Users = new ArrayList<User>();

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
				user.setAccountStatus();
				usersList.Users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usersList;
	}
	
	@Override
	public boolean addNewIncomeReport(Shop shop,String year,String month,String Income,String BSI,String TNO) {
		String query = "INSERT INTO " + "income_reports" + " VALUES (" + "'" + shop.name() + "', " + "'" + year + "', " + "'"
				+ month + "', " + "'" + Income +  "', " + "'" + BSI + "', " + TNO + ");";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}



	@Override
	public IncomeReport getIncomeReport(Shop shop, String year, String month) throws SQLException {
		IncomeReport incomeReport = new IncomeReport();
		incomeReportList.Reports = new ArrayList<IncomeReport>();
		
		//Get the report the user asked for
		String query = "SELECT * FROM income_reports WHERE ShopName = '" + shop.name() + "' AND Year = '" + year + "' AND Month = '" + month + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			
			while (rs.next()) {
				
				incomeReport.shop = shop;
				incomeReport.year = year;
				incomeReport.month = month;
				incomeReport.income = rs.getString("Income");
				incomeReport.bestSellingProduct = rs.getString("BestSellingProduct");
				incomeReport.totalNumberOfOrders = rs.getString("TotalNumberOfOrders");
				
				
		
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Get the income of other shops for the same year and months
		Shop[] allShops = Shop.values();
		for(Shop s: allShops) {
			if(!(s.toString().equals(shop.toString())) && !(s.toString().equals(shop.ALL.toString())) && !(s.toString().equals(shop.NONE.toString()))) {
				query = "SELECT * FROM income_reports WHERE ShopName = '" + s.name() + "' AND Year = '" + year + "' AND Month = '" + month + "';";
			
				try {
					ResultSet rs = runQuery(connection, query);
					
					while (rs.next()) {
						IncomeReport incomeReportM = new IncomeReport();
						incomeReportM.shop = s;
						incomeReportM.year = year;
						incomeReportM.month = month;
						incomeReportM.income = rs.getString("Income");
						incomeReportM.bestSellingProduct = rs.getString("BestSellingProduct");
						incomeReportM.totalNumberOfOrders = rs.getString("TotalNumberOfOrders");
						
						incomeReportList.Reports.add(incomeReportM);
					
						
						
						
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return incomeReport;
	}

	@Override
	public boolean changeStatus(User user) {
		String query=null;
		String switchCase = user.accountStatus;
		switch(switchCase) {
		case "Frozen"://FREEZE OPTION
			query = "UPDATE users SET approved = 1, userrole = 'GUEST' WHERE username = '"+user.username+"';";
			break;
		case "Blocked":
			query = "UPDATE users SET approved = 0 WHERE username = '"+user.username+"';";
			break;
		case "Approved":
			query = "UPDATE users SET approved = 1, userrole = 'CUSTOMER' WHERE username = '"+user.username+"';";
			break;
		}
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}

	@Override
	public boolean logInUser(User user) {
		String query = "UPDATE " + TABLE_NAME + " SET " + LOG_INFO + "=1 WHERE " + USERNAME + "='" + user.username + "';";
		try {
			runUpdate(connection, query);
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean logOffUser(User user) {
		String query = "UPDATE " + TABLE_NAME + " SET " + LOG_INFO + "=0 WHERE " + USERNAME + "='" + user.username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean addNewCompliant(String userName, String orderId, String complaint, String date, String price,
			String complaintStatus, String supportName, String refund) {
		String query = "INSERT INTO complaints VALUES (" + "'" + userName + "', " + "'" + orderId + "', " + "'"
				+ complaint + "', " + "'" + date + "', " + "'" + price + "', '" + complaintStatus + "', '" + supportName
				+ "', " + "'" + refund + "');";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public ComplaintList getAllComplaints(String supportName) {
		ComplaintList complaintList = new ComplaintList();
		complaintList.complaints = new ArrayList<Complaint>();
		String query = "SELECT * FROM complaints WHERE supportName = '" + supportName + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) { // for lines
				Complaint complaint = new Complaint();
				complaint.userName = rs.getString("username");
				complaint.orderId = rs.getString("orderId");
				complaint.complaint = rs.getString("complaint");
				complaint.date = rs.getString("date");
				complaint.price = rs.getString("price");
				complaint.complaintStatus = rs.getString("complaintStatus");
				complaint.refund = rs.getString("refund");
				complaintList.complaints.add(complaint);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return complaintList;
	}
	
	@Override
	public boolean setRefundAmount(String orderId, String refund) {
		String query = "UPDATE complaints SET refund ='" + refund + "' , complaintStatus = 'Approved' WHERE orderId ='"
				+ orderId + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean setSurveyAnswers(int q1, int q2, int q3, int q4, int q5, int q6, String type, String shopName,
			String date) {
		int zero = 0;
		String query = "INSERT INTO surveys VALUES (" + "'" + zero + "', " + "'" + q1 + "', " + "'" + q2 + "', " + "'"
				+ q3 + "', " + "'" + q4 + "', " + "'" + q5 + "', '" + q6 + "', '" + type + "', " + "'" + shopName
				+ "', " + "'" + date + "');";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean getMonthAvarge() {
		// TODO Auto-generated method stub
		return false;
	}



	

	
}

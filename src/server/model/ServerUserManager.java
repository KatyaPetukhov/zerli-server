package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Role;
import common.interfaces.UserManager;
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
	private static String DOUBLE = " double";
	

	private static String VARCHAR = " varchar(255)";
	private static String BOOLEAN = " boolean";
	
	//private static IncomeReportList incomeReportList = new IncomeReportList();
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

	/* Interface functions: */
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
				user.userWallet=Double.parseDouble(rs.getString("userWallet"));
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
			boolean approved,String cardNumber,String expirationDate,String cvv,boolean logInfo,double userWallet)
			throws WeakPassword, PermissionDenied {
		String query;
		
		
//		if(cardNumber != null) {
//		query = "UPDATE " + TABLE_NAME + " SET " + NICKNAME + "='" + nickname + "', " + SHOP_NAME + "='" + shopname.name() + "', " + USERROLE + "='" + role.name() + "', " +
//				APPROVED + "='" + (approved ? 1: 0) + "', " + CARD_NUMBER + "='" + cardNumber + "', " + EXPIRATION_DATE + "='" + expirationDate + "', " + 
//				CVV + "='" + cvv + "', " + LOG_INFO + "='" + (logInfo ? 1 : 0) + "', " + "userWallet" + "='" + userWallet + "' WHERE " + USERNAME + "='" + username + "';";
//		}
//		else
			query = "INSERT INTO " + TABLE_NAME + " VALUES (" + "'" + username + "', " + "'" + password + "', " + "'"
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
				user.cardNumber = rs.getString(CARD_NUMBER);
				user.exDate = rs.getString(EXPIRATION_DATE);
				user.cvv = rs.getString(CVV);
				user.logInfo = (rs.getInt(LOG_INFO) != 0 ? true : false);
				user.setAccountStatus();
				usersList.Users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usersList;
	}

	private static void checkPasswordStrength(String password) throws WeakPassword {
		if (password == null || password.isEmpty()) {
			throw new WeakPassword("Password cannot be empty.");
		}
	}

	@Override
	public boolean updateWallet(double wallet) {
		String s = ""+wallet;
		String query = "UPDATE " + TABLE_NAME + " SET " + "userWallet" + "='" + s + "' WHERE " + USERNAME + "='" + requestedBy.username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			/* TODO: probably incorrect. Will not fail if result is empty. */
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import common.Role;
import common.request_data.User;
import server.sql_queries.UsersSQL;

public class DBManager {
	/*
	 * Handles all SQL requests via sql_queries.
	 * Provides high level API for EchoServer for data manipulation.
	 */

	private static String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
	/* We can probably reuse the format for a more flexible connection: */
	private static String PREFIX = "jdbc:mysql://";
	private static String DEFAULT_SERVER = "localhost";
	private static String DELIMITER = "/";
	private static String DEFAULT_DATABASE = "yohannostupid";
	private static String POSTFIX = "?serverTimezone=IST";

	private static String DEFAULT_USERNAME = "root";
	private static String DEFAULT_PASSWORD = "12345";

	private String serverURL;
	private String username;
	private String password;
	private User guestUser;

	private boolean isConnected;

	public DBManager() {
		this(getDefaultURL(), DEFAULT_USERNAME, DEFAULT_PASSWORD);
	}

	public DBManager(String serverURL, String username, String password) {
		this.serverURL = serverURL;
		this.username = username;
		this.password = password;
		isConnected = false;
		guestUser = new User(Role.GUEST.toString(), null);
		guestUser.nickname = Role.GUEST.toString();
		guestUser.userrole = Role.GUEST;
		guestUser.approved = true;
	}

	public static String getDefaultURL() {
		return PREFIX + DEFAULT_SERVER + DELIMITER + DEFAULT_DATABASE + POSTFIX;
	}

	public static String getDefaultUsername() {
		return DEFAULT_USERNAME;
	}

	public static String getDefaultPassword() {
		return DEFAULT_PASSWORD;
	}

	public Connection getConnection()
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		/*
		 * Can be private, but kept public for easier debug and testing. Creates
		 * connection object to execute statements via
		 * getConnection().prepareStatement(String statement).execute()
		 */
		Class.forName(DRIVER_CLASS).newInstance();
		return DriverManager.getConnection(serverURL, username, password);
	}

	public User validateUser(User user) {
		/*
		 * Return null if user is incorrect or does not match the password or role.
		 * Returning null will result in "forbidden" response.
		 */
		if (user == null || user.userrole == Role.GUEST) {
			/* No user information at all is fine and defined as Guest access. */
			return guestUser;
		}
		User new_user;
		try {
			new_user = UsersSQL.getUser(getConnection(), user.username);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
		if (new_user == null) {
			/*
			 * User pretended to be not Guest, but does not exist in system - access
			 * forbidden.
			 */
			return null;
		}
		if (user.password != new_user.password) {
			/* Password mismatch */
			return null;
		}
		return new_user;
	}

	public boolean isConnected() {
		if (isConnected) {
			/* Checked once already. No need to repeat unless something fails. */
			return true;
		}
		try {
			UsersSQL.test(getConnection());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}
		isConnected = true;
		return true;
	}
}

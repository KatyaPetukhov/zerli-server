package server.sql_queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Role;
import common.request_data.User;

public class UsersSQL {
	/*
	 * SQL queries for Users manipulation.
	 */

	/* SQL SCHEMA: */
	private static String TABLE_NAME = "users";
	private static String USERNAME = "username"; // key
	private static String PASSWORD = "password";
	private static String NICKNAME = "nickname";
	private static String USERROLE = "userrole";
	private static String APPROVED = "approved";

	private static String VARCHAR = " varchar(255)";
	private static String BOOLEAN = " boolean";
	/* End SQL SCHEMA */

	public static boolean test(Connection connection) {
		try {
			connection.prepareStatement("select * from " + TABLE_NAME + ";").executeQuery();
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
				+ VARCHAR + ", " + USERROLE + VARCHAR + ", " + APPROVED + BOOLEAN + ", PRIMARY KEY (" + USERNAME
				+ "));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean addNewUser(Connection connection, String username, String password, String nickname,
			Role userrole, boolean approved) {
		String query = "INSERT INTO " + TABLE_NAME + " VALUES (" + "'" + username + "', " + "'" + password + "', " + "'"
				+ nickname + "', " + "'" + userrole.name() + "', " + (approved ? 1 : 0) + ");";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void approveUser(Connection connection, String username) {
		String query = "UPDATE " + TABLE_NAME + " SET " + APPROVED + "=1 WHERE " + USERNAME + "='" + username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeUser(Connection connection, String username) {
		String query = "DELETE FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static User getUser(Connection connection, String username) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) {
				User user = new User();
				user.username = rs.getString(USERNAME);
				user.password = rs.getString(PASSWORD);
				user.nickname = rs.getString(NICKNAME);
				user.approved = (rs.getInt(APPROVED) != 0 ? true : false);
				user.userrole = Role.valueOf(rs.getString(USERROLE));
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<User> getUsers(Connection connection, boolean approved) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + APPROVED + "=" + (approved ? 1 : 0) + ";";
		List<User> users = new ArrayList<User>();

		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) {
				User user = new User();
				user.username = rs.getString(USERNAME);
				user.password = rs.getString(PASSWORD);
				user.nickname = rs.getString(NICKNAME);
				user.approved = (rs.getInt(APPROVED) != 0 ? true : false);
				user.userrole = Role.valueOf(rs.getString(USERROLE));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	private static ResultSet runQuery(Connection connection, String query) throws SQLException {
		System.out.println(query);
		return connection.prepareStatement(query).executeQuery();
	}

	private static int runUpdate(Connection connection, String query) throws SQLException {
		System.out.println(query);
		return connection.prepareStatement(query).executeUpdate();
	}
}

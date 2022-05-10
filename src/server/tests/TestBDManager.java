package server.tests;

import java.sql.SQLException;
import java.util.List;

import common.Role;
import common.request_data.User;
import server.DBManager;
import server.sql_queries.UsersSQL;

public class TestBDManager {
	private static boolean testUsers() {
		boolean status = true;
		DBManager model = new DBManager();
		List<User> approved = null;
		List<User> notApproved = null;
		List<User> notApprovedEmpty = null;
		User user = null;
		User userEmpty = null;
		try {
			UsersSQL.resetUsers(model.getConnection());
			UsersSQL.addNewUser(model.getConnection(), "u", "u", "Katya", Role.CUSTOMER, true);
			UsersSQL.addNewUser(model.getConnection(), "o", "o", "Jessika", Role.OWNER, true);
			UsersSQL.addNewUser(model.getConnection(), "m", "m", "Niv", Role.MANAGER, true);
			UsersSQL.addNewUser(model.getConnection(), "w", "w", "Who", Role.WORKER, true);
			UsersSQL.addNewUser(model.getConnection(), "s", "s", "Aaron", Role.SUPPORT, false);
			approved = UsersSQL.getUsers(model.getConnection(), true);
			notApproved = UsersSQL.getUsers(model.getConnection(), false);
			UsersSQL.approveUser(model.getConnection(), "s");
			notApprovedEmpty = UsersSQL.getUsers(model.getConnection(), false);
			UsersSQL.removeUser(model.getConnection(), "s");
			userEmpty = UsersSQL.getUser(model.getConnection(), "s");
			UsersSQL.addNewUser(model.getConnection(), "s", "s", "Aaron", Role.SUPPORT, true);
			user = UsersSQL.getUser(model.getConnection(), "s");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			status = false;
		}

		if (approved != null && !approved.isEmpty()) {
			System.out.println("PASSED: approved:");
			for (User u : approved) {
				System.out.println(u.nickname);
			}
		} else {
			System.out.println("FAILED: Approved");
			status = false;
		}
		if (notApproved != null && !notApproved.isEmpty()) {
			System.out.println("PASSED: notApproved:");
			for (User u : notApproved) {
				System.out.println(u.nickname);
			}
		} else {
			System.out.println("FAILED: notApproved");
			status = false;
		}
		if (notApprovedEmpty != null && notApprovedEmpty.isEmpty()) {
			System.out.println("PASSED: notApproved: empty");
		} else {
			System.out.println("FAILED: notApproved is not empty or null");
			status = false;
		}
		if (userEmpty == null) {
			System.out.println("PASSED: getUser: empty");
		} else {
			System.out.println("FAILED: getUser: " + userEmpty.nickname);
			status = false;
		}
		if (user != null && user.nickname.equals("Aaron")) {
			System.out.println("PASSED: getUser: " + user.nickname);
		} else {
			System.out.println("FAILED: getUser");
			status = false;
		}
		return status;
	}

	public static void main(String[] args) {
		boolean status = true;
		status &= testUsers();
		
		if (status) {
			System.out.println("\nPASSED");
		} else {
			System.out.println("\nFAILED");
		}
	}
}

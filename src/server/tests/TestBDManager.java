//package server.tests;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//
//import common.Role;
//import common.interfaces.UserManager.PermissionDenied;
//import common.interfaces.UserManager.WeakPassword;
//import common.request_data.User;
//import server.model.DBManager;
//import server.model.ServerUserManager;
//
//public class TestBDManager {
//	private static boolean testUsers() {
//		boolean status = true;
//		DBManager model = new DBManager();
//		List<User> approved = null;
//		List<User> notApproved = null;
//		List<User> notApprovedEmpty = null;
//		User user = null;
//		User userEmpty = null;
//		User manager = new User();
//		manager.userrole = Role.MANAGER;
//		try {
//			Connection connection = model.getConnection();
//			ServerUserManager.resetUsers(connection);
//			ServerUserManager userManager = new ServerUserManager(manager, connection);
//			userManager.addNewUser("u", "u", "Katya", Role.CUSTOMER, true);
//			userManager.addNewUser("o", "o", "Jessika", Role.OWNER, true);
//			userManager.addNewUser("m", "m", "Niv", Role.MANAGER, true);
//			userManager.addNewUser("w", "w", "Who", Role.WORKER, true);
//			userManager.addNewUser("s", "s", "Aaron", Role.SUPPORT, false);
//			approved = userManager.getUsers(true, 0, 10);
//			notApproved = userManager.getUsers(false, 0, 10);
//			userManager.approveUser("s");
//			notApprovedEmpty = userManager.getUsers(false, 0, 10);
//			userManager.removeUser("s");
//			userEmpty = userManager.getUser("s", "s");
//			userManager.addNewUser("s", "s", "Aaron", Role.SUPPORT, true);
//			user = userManager.getUser("s", "s");
//		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | WeakPassword
//				| PermissionDenied e) {
//			e.printStackTrace();
//			status = false;
//		}
//
//		if (approved != null && !approved.isEmpty()) {
//			System.out.println("PASSED: approved:");
//			for (User u : approved) {
//				System.out.println(u.nickname);
//			}
//		} else {
//			System.out.println("FAILED: Approved");
//			status = false;
//		}
//		if (notApproved != null && !notApproved.isEmpty()) {
//			System.out.println("PASSED: notApproved:");
//			for (User u : notApproved) {
//				System.out.println(u.nickname);
//			}
//		} else {
//			System.out.println("FAILED: notApproved");
//			status = false;
//		}
//		if (notApprovedEmpty != null && notApprovedEmpty.isEmpty()) {
//			System.out.println("PASSED: notApproved: empty");
//		} else {
//			System.out.println("FAILED: notApproved is not empty or null");
//			status = false;
//		}
//		if (userEmpty == null) {
//			System.out.println("PASSED: getUser: empty");
//		} else {
//			System.out.println("FAILED: getUser: " + userEmpty.nickname);
//			status = false;
//		}
//		if (user != null && user.nickname.equals("Aaron")) {
//			System.out.println("PASSED: getUser: " + user.nickname);
//		} else {
//			System.out.println("FAILED: getUser");
//			status = false;
//		}
//		return status;
//	}
//
//	public static void main(String[] args) {
//		boolean status = true;
//		status &= testUsers();
//
//		if (status) {
//			System.out.println("\nPASSED");
//		} else {
//			System.out.println("\nFAILED");
//		}
//	}
//}

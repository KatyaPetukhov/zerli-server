package server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import common.Role;
import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.ImageFile;
import common.request_data.Order;
import common.request_data.Product;
import common.request_data.Shop;
import common.request_data.User;
import server.model.DBManager;
import server.model.ServerOrderManager;
import server.model.ServerProductManager;
import server.model.ServerUserManager;

public class InitializeDB {
	/* Add a default set of data that is enough to play with the application. */
	public void f(DBManager model) throws PermissionDenied, SQLIntegrityConstraintViolationException {
		createDatabase(model);

		Connection connection = null;
		try {
			connection = model.getConnection();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return;
		}
		addProducts(connection);
		addUsers(connection);
		addComplaints(connection);
		addOrderTable(connection);
		addSurveys(connection);
		// addOrders(connection);
		// addReports(connection);
	}

	private void createDatabase(DBManager model) {
		model.createDatabase();
	}

	private void addReports(Connection connection) {
		// TODO Auto-generated method stub
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetIncomeReports(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);

			// addRports(userManager);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	private void addOrders(Connection connection) {
//		// TODO Auto-generated method stub
//		User manager = new User();
//		manager.userrole = Role.CUSTOMER;
//		try {
//			ServerUserManager.resetOrders(connection);
//			ServerUserManager userManager = new ServerUserManager(manager, connection);
//
//			userManager.addNewOrder("u", "123", Shop.HAIFA, "Pendig approvel");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	private void addComplaints(Connection connection) {
		User worker = new User();
		worker.userrole = Role.WORKER;
		try {
			ServerUserManager.resetComplaints(connection);
			ServerUserManager userManager = new ServerUserManager(worker, connection);
			userManager.addNewCompliant("Jessica", "123", "ugly flowers", "08.06.22", "100", "Awaiting response",
					"Aaron", "0");
			userManager.addNewCompliant("Yarden", "234", "dry boquet", "30.05.22", "50", "Awaiting response", "Aaron",
					"0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addSurveys(Connection connection) {
		User worker = new User();
		worker.userrole = Role.WORKER;
		try {
			ServerUserManager.resetSurvey(connection);
			ServerUserManager userManager = new ServerUserManager(worker, connection);
			userManager.setSurveyAnswers(1,2, 1, 4, 3, 7, "Shop survey", "HAIFA", "2022/05");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addOrderTable(Connection connection) {
		ServerOrderManager.resetOrders(connection);
	}

	private void addUsers(Connection connection) throws SQLIntegrityConstraintViolationException {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetUsers(connection);
			System.out.println("intzlie DB line 97");
			ServerUserManager userManager = new ServerUserManager(manager, connection);

			userManager.addNewUser("u", "u", "Katya", Shop.NONE, Role.GUEST, false, "1111222233334444", "18/7/2023",
					"132", false);
			userManager.addNewUser("o", "o", "Jessika", Shop.ALL, Role.OWNER, true, null, null, null, false);
			userManager.addNewUser("m", "m", "Niv", Shop.HAIFA, Role.MANAGER, true, null, null, null, false);
			userManager.addNewUser("w", "w", "Who", Shop.HAIFA, Role.WORKER, true, null, null, null, false);
			userManager.addNewUser("s", "s", "Aaron", Shop.ALL, Role.SUPPORT, true, null, null, null, false);
		} catch (WeakPassword | PermissionDenied e) {
			e.printStackTrace();
		}
	}

	private void addProducts(Connection connection) throws PermissionDenied {
		User support = new User();
		support.userrole = Role.SUPPORT;

		ServerProductManager.resetProducts(connection);
		ServerProductManager productManager = new ServerProductManager(support, connection);

		productManager.addProduct(new Product("Field Beauty", 40.0, 0, "Bouquet",
				ImageFile.asEncodedString("./src/server/gallery/b1.jpg")));

		productManager.addProduct(new Product("Warm White", 60.0, 0, "Bouquet",
				ImageFile.asEncodedString("./src/server/gallery/b2.jpg")));

		productManager.addProduct(new Product("Pink Spring", 55.0, 0, "Bouquet",
				ImageFile.asEncodedString("./src/server/gallery/b3.jpg")));

		productManager.addProduct(
				new Product("Cute Ball", 70.0, 0, "Bouquet", ImageFile.asEncodedString("./src/server/gallery/b4.jpg")));

		productManager.addProduct(new Product("High Ground", 85.0, 0, "Bouquet",
				ImageFile.asEncodedString("./src/server/gallery/b5.jpg")));

		productManager.addProduct(
				new Product("With Love", 65.0, 0, "Bouquet", ImageFile.asEncodedString("./src/server/gallery/b6.jpg")));

		productManager.addProduct(new Product("Happy moments", 200.0, 0, "Wedding",
				ImageFile.asEncodedString("./src/server/gallery/w1.jpg")));

		productManager.addProduct(
				new Product("Memories", 150.0, 0, "Funeral", ImageFile.asEncodedString("./src/server/gallery/f1.jpg")));

		productManager.addProduct(new Product("Pink Orchid", 120.0, 0, "Flowerpot",
				ImageFile.asEncodedString("./src/server/gallery/p1.jpg")));

		productManager.addProduct(new Product("1m White Rose", 25.0, 0, "Retail",
				ImageFile.asEncodedString("./src/server/gallery/r1.jpg")));

		productManager.addProduct(new Product("0.6m Red Rose", 10.0, 0, "Retail",
				ImageFile.asEncodedString("./src/server/gallery/r2.jpg")));

	}

	private void addUserTable(Connection connection) {
		ServerUserManager.resetUsers(connection);
		addOrders(connection);
	}

	private void addOrders(Connection connection) {
		User manager = new User();
		manager.userrole = Role.CUSTOMER;
		ServerOrderManager serverOrderManager = new ServerOrderManager(manager, connection);
		Order order = new Order();
		order.orderNumber = "111";
		order.username = "Jess";
		serverOrderManager.submitOrder(order);
	}

}

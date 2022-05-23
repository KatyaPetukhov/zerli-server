package server;

import java.sql.Connection;
import java.sql.SQLException;

import common.Role;
import common.Shop;
import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.ImageFile;
import common.request_data.Product;
import common.request_data.User;
import server.model.DBManager;
import server.model.ServerProductManager;
import server.model.ServerUserManager;

public class InitializeDB {
	/* Add a default set of data that is enough to play with the application. */
	public void f(DBManager model) {
		createDatabase(model);

		Connection connection = null;
		try {
			connection = model.getConnection();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return;
		}
		addUsers(connection);
		addProducts(connection);
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

	private void addUsers(Connection connection) {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetUsers(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);

			userManager.addNewUser("u", "u", "Katya", Shop.NONE, Role.CUSTOMER, true);
			userManager.addNewUser("o", "o", "Jessika", Shop.ALL, Role.OWNER, true);
			userManager.addNewUser("m", "m", "Niv", Shop.HAIFA, Role.MANAGER, true);
			userManager.addNewUser("w", "w", "Who", Shop.HAIFA, Role.WORKER, true);
			userManager.addNewUser("s", "s", "Aaron", Shop.ALL, Role.SUPPORT, true);
		} catch (WeakPassword | PermissionDenied e) {
			e.printStackTrace();
		}
	}

	private void addProducts(Connection connection) {
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

//	private void addRports(ServerUserManager userManager) {
//		// add reports to HAIFA SHOP
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "January", "5000", "Flowers", "40");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "February", "4760", "Flowers", "19");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "March", "6020", "Flowers", "35");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "April", "3000", "Flowers", "20");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "May", "1500", "Flowers", "11");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "June", "730", "Flowers", "6");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "July", "680", "Flowers", "4");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "August", "1356", "Flowers", "12");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "September", "2377", "Flowers", "15");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "October", "2985", "Flowers", "21");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "November", "5505", "Flowers", "45");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "December", "7300", "Flowers", "63");
//
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "January", "3450", "Flowers", "22");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "February", "4530", "Flowers", "32");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "March", "8000", "Flowers", "60");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "April", "9210", "Flowers", "85");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "May", "5760", "Flowers", "46");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "June", "350", "Flowers", "4");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "July", "210", "Flowers", "2");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "August", "860", "Flowers", "7");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "September", "9780", "Flowers", "100");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "October", "1750", "Flowers", "9");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "November", "2800", "Flowers", "27");
//		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "December", "1600", "Flowers", "13");
//		
//	}
}

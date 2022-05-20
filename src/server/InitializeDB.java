package server;

import java.sql.Connection;
import java.sql.SQLException;

import common.Role;
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
	public void f(DBManager model) throws PermissionDenied {
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
	}

	private void createDatabase(DBManager model) {
		model.createDatabase();
	}

	private void addUsers(Connection connection) {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetUsers(connection);
			ServerProductManager.resetProducts(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);

			userManager.addNewUser("u", "u", "Katya", Role.CUSTOMER, true);
			userManager.addNewUser("o", "o", "Jessika", Role.OWNER, true);
			userManager.addNewUser("m", "m", "Niv", Role.MANAGER, true);
			userManager.addNewUser("w", "w", "Who", Role.WORKER, true);
			userManager.addNewUser("s", "s", "Aaron", Role.SUPPORT, true);
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
	
//		productManager.addProduct(new Product("Warm White", 60.0, 0, "Bouquet",
//				ImageFile.asEncodedString("./src/server/gallery/b2.jpg")));
//		
//		productManager.addProduct(new Product("Pink Spring", 55.0, 0, "Bouquet",
//				ImageFile.asEncodedString("./src/server/gallery/b3.jpg")));
//		
//		productManager.addProduct(
//				new Product("Cute Ball", 70.0, 0, "Bouquet", ImageFile.asEncodedString("./src/server/gallery/b4.jpg")));
//		
//		productManager.addProduct(new Product("High Ground", 85.0, 0, "Bouquet",
//				ImageFile.asEncodedString("./src/server/gallery/b5.jpg")));
//		
//		productManager.addProduct(
//				new Product("With Love", 65.0, 0, "Bouquet", ImageFile.asEncodedString("./src/server/gallery/b6.jpg")));
//		
//		productManager.addProduct(new Product("Happy moments", 200.0, 0, "Wedding",
//				ImageFile.asEncodedString("./src/server/gallery/w1.jpg")));
//		
//		productManager.addProduct(
//				new Product("Memories", 150.0, 0, "Funeral", ImageFile.asEncodedString("./src/server/gallery/f1.jpg")));
//		
//		productManager.addProduct(new Product("Pink Orchid", 120.0, 0, "Flowerpot",
//				ImageFile.asEncodedString("./src/server/gallery/p1.jpg")));
//		
//		productManager.addProduct(new Product("1m White Rose", 25.0, 0, "Retail",
//				ImageFile.asEncodedString("./src/server/gallery/r1.jpg")));
//		
//		productManager.addProduct(new Product("0.6m Red Rose", 10.0, 0, "Retail",
//				ImageFile.asEncodedString("./src/server/gallery/r2.jpg")));

		

	}
}

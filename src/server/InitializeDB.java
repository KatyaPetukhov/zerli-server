package server;

import java.sql.SQLException;

import common.Role;
import server.sql_queries.UsersSQL;

public class InitializeDB {
	/* Adds data that does not have any interface to add it yet. */
	public void f(DBManager model) {
		addUsers(model);
//		addProducts(model);
	}

	private void addUsers(DBManager model) {
		try {
			UsersSQL.resetUsers(model.getConnection());
			UsersSQL.addNewUser(model.getConnection(), "u", "u", "Katya", Role.CUSTOMER, true);
			UsersSQL.addNewUser(model.getConnection(), "o", "o", "Jessika", Role.OWNER, true);
			UsersSQL.addNewUser(model.getConnection(), "m", "m", "Niv", Role.MANAGER, true);
			UsersSQL.addNewUser(model.getConnection(), "w", "w", "Who", Role.WORKER, true);
			UsersSQL.addNewUser(model.getConnection(), "s", "s", "Aaron", Role.SUPPORT, true);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

//	private void addProducts(DBManager model) {
//		ProductManager productManager = model.getProductManager();
//		productManager.addProduct(new Product("Field Beauty", 40.0, 0, "Bouquet", new Image("client/gallery/b1.jpg")),
//				"Bouquet");
//		productManager.addProduct(new Product("Warm White", 60.0, 0, "Bouquet", new Image("client/gallery/b2.jpg")),
//				"Bouquet");
//		productManager.addProduct(new Product("Pink Spring", 55.0, 0, "Bouquet", new Image("client/gallery/b3.jpg")),
//				"Bouquet");
//		productManager.addProduct(new Product("Cute Ball", 70.0, 0, "Bouquet", new Image("client/gallery/b4.jpg")), "Bouquet");
//		productManager.addProduct(new Product("High Ground", 85.0, 0, "Bouquet", new Image("client/gallery/b5.jpg")),
//				"Bouquet");
//		productManager.addProduct(new Product("With Love", 65.0, 0, "Bouquet", new Image("client/gallery/b6.jpg")), "Bouquet");
//		productManager.addProduct(new Product("Happy moments", 200.0, 0, "Wedding", new Image("client/gallery/w1.jpg")),
//				"Wedding");
//		productManager.addProduct(new Product("Memories", 150.0, 0, "Funeral", new Image("client/gallery/f1.jpg")), "Funeral");
//		productManager.addProduct(new Product("Pink Orchid", 120.0, 0, "Flowerpot", new Image("client/gallery/p1.jpg")),
//				"Flowerpot");
//		productManager.addProduct(new Product("1m White Rose", 25.0, 0, "Retail", new Image("client/gallery/r1.jpg")),
//				"Retail");
//		productManager.addProduct(new Product("0.6m Red Rose", 10.0, 0, "Retail", new Image("client/gallery/r2.jpg")),
//				"Retail");
//	}
}

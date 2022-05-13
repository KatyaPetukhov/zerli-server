package server.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Role;
import common.interfaces.ProductManager;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.User;

public class ServerProductManager extends BaseSQL implements ProductManager {
	/* SQL SCHEMA: */
	private static String TABLE_NAME = "products";
	/* TODO: Define fields */

	private static String VARCHAR = " varchar(255)";
	/* End SQL SCHEMA */

	private User requestedBy;
	private Connection connection;

	private static Map<String, List<Product>> productsMock = null;

	private static String[] categories = { ALL_CATEGORY, "Bouquet", "Wedding", "Funeral", "Flowerpot", "Retail" };

	public ServerProductManager(User requestedBy, Connection connection) {
		this.requestedBy = requestedBy;
		this.connection = connection;

		/* Mock implementation, replace with SQL. */
		if (productsMock == null) {
			productsMock = new HashMap<String, List<Product>>();
			for (String category : getCategories()) {
				productsMock.put(category, new ArrayList<Product>());
			}
		}
	}

	public static void resetProducts(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO: Create table with schema - same as UserManager
	}

	@Override
	public List<String> getCategories() {
		return Arrays.asList(categories);
	}

	@Override
	public ProductList getProducts(String category, int start, int amount) {
		List<Product> products = new ArrayList<Product>();

		ProductList productList = new ProductList();
		productList.start = start;
		productList.amount = 0;
		productList.category = category;
		productList.items = new ArrayList<Product>();

		if (productsMock.containsKey(category)) {
			products = productsMock.get(category);
			for (int i = start; i < products.size(); i++) {
				productList.amount++;
				productList.items.add(products.get(i));
				if (productList.amount >= amount) {
					break;
				}
			}
		}
		return productList;
	}

	@Override
	public void addProduct(Product product) {
		if (requestedBy.userrole != Role.SUPPORT) {
			/* TODO - raise an error. */
			return;
		}
		productsMock.get(product.category).add(product);
		if (!product.category.equals(ALL_CATEGORY)) {
			productsMock.get(ALL_CATEGORY).add(product);
		}
	}

	@Override
	public void removeProduct(Product product) {
		/* Not required for mock - can be easily added for SQL. */
	}
}

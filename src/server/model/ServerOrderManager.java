package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import common.interfaces.OrderManager;
import common.request_data.Order;
import common.request_data.OrderList;
import common.request_data.OrderStatus;
import common.request_data.OrderType;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.User;

public class ServerOrderManager extends BaseSQL implements OrderManager {
	/* SQL SCHEMA: */
	private static String TABLE_NAME = "orders";
	private static String ORDER_NUMBER = "number";
	private static String USERNAME = "user";
	private static String DATE = "date";
	private static String HOUR = "hour";
	private static String PRODUCTS = "products";
	private static String STATUS = "status";
	private static String PRICE = "price";
	private static String RECIPIENT = "recipient";
	private static String GREETING = "greeting";
	private static String SIGNATURE = "signature";
	private static String SHOP = "shop";
	private static String ADDRESS = "address";
	private static String CITY = "city";
	private static String DELIVERYPHONE = "deliveryPhone";
	private static String PAYMENTPHONE = "paymentPhone";
	private static String ORDERTYPE = "orderType";

	/* TODO: Define fields */

	private static String VARCHAR = " varchar(255)";
	private static String MEDIUMTEXT = " MEDIUMTEXT";
	private static String SMALLINT = " smallint";
	private static String DOUBLE = " double";
	private static String LONGTEXT = " LONGTEXT";

	/* End SQL SCHEMA */

	private User requestedBy;
	private Connection connection;

	public ServerOrderManager(User requestedBy, Connection connection) {
		this.requestedBy = requestedBy;
		this.connection = connection;

	}

	public static void resetOrders(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + TABLE_NAME + " (" + ORDER_NUMBER + VARCHAR + ", " + USERNAME + VARCHAR + ", " + DATE
				+ VARCHAR + ", " + HOUR + VARCHAR + ", " + PRODUCTS + LONGTEXT + ", " + STATUS + VARCHAR + ", " + PRICE
				+ DOUBLE + ", " + RECIPIENT + VARCHAR + ", " + GREETING + MEDIUMTEXT + ", " + SIGNATURE + VARCHAR + ", "
				+ SHOP + VARCHAR + ", " + ADDRESS + VARCHAR + ", " + CITY + VARCHAR + ", " + DELIVERYPHONE + VARCHAR
				+ ", " + PAYMENTPHONE + VARCHAR + ", " + ORDERTYPE + VARCHAR + ", PRIMARY KEY (" + ORDER_NUMBER + "));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO: Create table with schema - same as UserManager
	}

	@Override
	public Order getOrder(String orderNum) {

		return null;
	}

	@Override
	public OrderList getOrders(String username) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			/* name is a key, so there can be 0 or 1 objects only. */
			while (rs.next()) {
				OrderList orderList = new OrderList();
				Order order = new Order();
				order.totalPrice = rs.getDouble(PRICE);
				order.status = OrderStatus.valueOf(rs.getString(STATUS));
				order.signature = rs.getString(SIGNATURE);
				// TODO order.shop=rs.getString(SHOP);
				order.recipient = rs.getString(RECIPIENT);

				String productsJson = rs.getString(PRODUCTS);
				order.products = ProductList.fromJson(productsJson);

				order.phone = rs.getString(DELIVERYPHONE);
				order.paymentPhone = rs.getString(PAYMENTPHONE);
				order.orderType = OrderType.valueOf(rs.getString(ORDERTYPE));
				order.orderNumber = rs.getString(ORDER_NUMBER);
				order.hour = rs.getString(HOUR);
				order.date = rs.getString(DATE);
				order.city = rs.getString(CITY);
				order.address = rs.getString(ADDRESS);

				orderList.orders.add(order);
				return orderList;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OrderList getAllOrders(String username) {
		OrderList orderList = new OrderList();
		orderList.orders = new ArrayList<Order>();
		String query = "SELECT number FROM orders WHERE user = '" + username + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) { // for lines
				Order order = new Order();
				order.orderNumber = rs.getString("orderNumber");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}
	
	private double checkTotalPrice( ProductList products) throws SQLException {
		Double priceToCheck = 0.0;
		for ( Product p: products.items) {
			String query = "SELECT " + PRICE + " FROM " + "products" + " WHERE " + "name" + "='" + p.name + "';";
			
			ResultSet rs = runQuery(connection, query);
			while(rs.next()) {
			Double productPrice = rs.getDouble(PRICE);
			priceToCheck+=productPrice;
			}
			
		}
		return priceToCheck;
	}
	
	//DELETE THIS FUNCTION
public Order submitOrder(Order order) {
		
		
		try {
			order.totalPrice=checkTotalPrice(order.products);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			String query = "INSERT INTO " + TABLE_NAME + " VALUES (" + "'" + order.orderNumber + "', " + "'" + order.username + "', " + "'"
					+ order.date + "', " + "'" + order.hour + "', "+ "'" + order.products.toJson() +"', " + "'"
					+ order.status + "', " + "'" + order.totalPrice + "', " + "'" + order.recipient +"', " + "'" + order.greetingMessage + "', " + "'"
					+ order.signature + "', " + "'" + order.shop.toString() + "', " + "'" + order.address + "', " +"'" + order.city + "', " + "'" 
					+ order.phone + "', " + "'" + order.paymentPhone + "', " +"'" + order.orderType + "'"
					+ ");";
			runUpdate(connection, query);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		
		return order;
		
	}
}
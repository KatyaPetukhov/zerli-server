package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.interfaces.CartManager;
import common.request_data.Order;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.User;

public class ServerCartManager extends BaseSQL implements CartManager{
	
	private static String TABLE_NAME = "orders";
	private static String ORDER_NUMBER ="number";
	private static String USERNAME = "user";
	private static String DATE = "date";
	private static String HOUR = "hour";
	private static String PRODUCTS ="products";
	private static String STATUS ="status";
	private static String PRICE = "price";
	private static String RECIPIENT ="recipient";
	private static String GREETING = "greeting";
	private static String SIGNATURE ="signature";
	private static String SHOP ="shop";
	private static String ADDRESS ="address";
	private static String CITY ="city";
	private static String DELIVERYPHONE ="deliveryPhone";
	private static String PAYMENTPHONE ="paymentPhone";
	private static String ORDERTYPE ="orderType";
	
	/* TODO: Define fields */

	private static String VARCHAR = " varchar(255)";
	private static String MEDIUMTEXT = " MEDIUMTEXT";
	private static String SMALLINT = " smallint";
	private static String  DOUBLE = " double";
	private static String LONGTEXT=" LONGTEXT";
	
	
	/* End SQL SCHEMA */

	private User requestedBy;
	private Connection connection;

	public ServerCartManager(User requestedBy, Connection connection) {
		this.requestedBy = requestedBy;
		this.connection = connection;

	}
	@Override
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
	//Checking if the price is right
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

}
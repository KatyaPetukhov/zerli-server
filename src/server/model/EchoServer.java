package server.model;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

import common.Request;
import common.RequestType;
import common.interfaces.CartManager;
import common.interfaces.OrderManager;
import common.interfaces.ProductManager;
import common.interfaces.UserManager;
import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.CategoriesList;
import common.request_data.Order;
import common.request_data.OrderList;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.ServerError;
import common.request_data.User;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class EchoServer extends AbstractServer {
	/*
	 * Server listener for Client requests. Translates "echo" strings into objects
	 * and handles all the main logic. Uses DBManager to access SQL database. Does
	 * not execute any SQL queries directly, but asks DBManager to do so.
	 */
	private static int DEFAULT_PORT = 5555;
	private DBManager manager;

	public EchoServer(int port) {
		super(port);
	}

	public void setDBManager(DBManager manager) {
		this.manager = manager;
	}

	public static int getDefaultPort() {
		return DEFAULT_PORT;
	}

	@Override
	protected void handleMessageFromClient(Object message, ConnectionToClient client) {
		Request request = Request.fromJson((String) message);
		if (manager == null) {
			/* Should not happen. */
			System.out.println("Warning! DBManager is null, but server runs.");
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Database is offline").toJson();
			respond(client, request);
			return;
		}
		request.user = manager.validateUser(request.user);
		if (request.user == null) {
			/*
			 * If user is null after the validation, meaning some user data is invalid.
			 * Usually should not happen, but can be a result of incorrect client.
			 */
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("User or password is invalid").toJson();
			respond(client, request);
			return;
		}
		if (!request.user.approved) {
			/*
			 * If user is null after the validation, meaning some user data is invalid.
			 * Usually should not happen, but can be a result of incorrect client.
			 */
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("User is not yet approved.").toJson();
			respond(client, request);
			return;
		}
		System.out.println("Request: " + request.requestType.name() + ", Role: " + request.user.userrole.name()
				+ ", User: " + request.user.nickname);
		switch (request.requestType) {
		case PING:
			/*
			 * Ping does not change the request at all. Keeps all the data as is. Required
			 * to prove that server behaves correctly. Requires being a GUEST or correct
			 * user. Incorrect user will fail before this switch.
			 */
			break;
		/* UserManager */
		case GET_USER:
			request = handleGetUser(request);
			break;
		case ADD_USER:
			try {
				request = handleAddUser(request);
			} catch (SQLIntegrityConstraintViolationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case APPROVE_USER:
			request = handleApproveUser(request);
			break;
		case UPDATE_WALLET:
			try {
				request = handleUpdateWallet(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		/* TODO: Missing REMOVE_USER, GET_USERS */
		/* ProductManager */
		case GET_CATEGORIES:
			request = handleGetCategories(request);
			break;
		case GET_PRODUCTS:
			request = handleGetProducts(request);
			break;
		case GET_PRODUCT:
			request = handleGetProduct(request);
			break;
		case TOFROM_CATALOGUE:
			try {
				request = handleToFromCatalogue(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case SET_DISCOUNT:
			try {
				request = handleSetDiscount(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | PermissionDenied
					| SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case ADD_ORDER:
			try {
				request = handleAddOrder(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case GET_ORDERS:
			try {
				request = handleGetOrders(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		/* TODO: Missing ADD_PRODUCT, REMOVE_PRODUCT */
		default:
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Unsupporter reuqest.").toJson();
			break;
		}

		respond(client, request);
	}



	



	private Request handleUpdateWallet(Request request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		UserManager userManager = new ServerUserManager(request.user, manager.getConnection());
		User walletToUpdate = User.fromJson(request.data);
		if (!userManager.updateWallet(walletToUpdate.userWallet)) {
			/* Product does not exists. */
			request.requestType = RequestType.REQUEST_FAILED;
		}
		request.data = null;
		return request;
	}
	

	private void respond(ConnectionToClient client, Request request) {
		try {
			client.sendToClient(request.toJson());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Handler for requestType: */
	private Request handleGetUser(Request request) {
		/*
		 * requestType.GET_USER
		 */
		System.out.println("Correct user requested.");
		User toCheck = User.fromJson(request.data);
		toCheck = manager.validateUser(toCheck);
		if (toCheck == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = toCheck.toJson();
		}
		return request;
	}

	private Request handleAddUser(Request request) throws SQLIntegrityConstraintViolationException {
		/*
		 * requestType.ADD_USER
		 */
		User toAdd = User.fromJson(request.data);
		User checkUser =manager.validateUser(toAdd);
		
		if(checkUser == null  || checkUser.cardNumber == null ) {
			request.requestType = RequestType.REQUEST_FAILED;;
			request.data = new ServerError("User name allready in dataBase").toJson();
			return request;
		}

		try {
			if (manager.getUserManager(request.user).addNewUser(toAdd.username, toAdd.password, toAdd.nickname,toAdd.shopname,
					toAdd.userrole, toAdd.approved,toAdd.cardNumber,toAdd.exDate,toAdd.cvv,toAdd.logInfo,toAdd.userWallet)) {
				/* User already exists. */
				
			}
			else request.requestType = RequestType.REQUEST_FAILED;
			request.data = null;
		} catch (WeakPassword e) {
			/* Bad password. */
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError(e.getMessage()).toJson();
		} catch (PermissionDenied e) {
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("Only manager can approve new users.").toJson();
		}
		 catch (SQLIntegrityConstraintViolationException e1) {
		e1.printStackTrace();	
		request.requestType = RequestType.REQUEST_FAILED;;
		request.data = new ServerError("User name not in Database").toJson();
		}
		return request;
	}

	private Request handleApproveUser(Request request) {
		/*
		 * requestType.APPROVE_USER
		 */
		User toApprove = User.fromJson(request.data);
		try {
			if (!manager.getUserManager(request.user).approveUser(toApprove.username)) {
				/* User does not exists. */
				request.requestType = RequestType.REQUEST_FAILED;
			}
			request.data = null;
		} catch (PermissionDenied e) {
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("Only manager can approve new users.").toJson();
		}
		return request;
	}

	private Request handleGetCategories(Request request) {
		/*
		 * requestType.GET_CATEGORIES
		 */
		CategoriesList categoriesList = new CategoriesList();
		categoriesList.items = new ArrayList<String>();
		categoriesList.items.addAll(manager.getProductManager(request.user).getCategories());
		request.data = categoriesList.toJson();
		return request;
	}

	private Request handleGetProducts(Request request) {
		/*
		 * requestType.GET_PRODUCTS
		 */
		ProductList productList = ProductList.fromJson(request.data);
		productList = manager.getProductManager(request.user).getProducts(productList.category);
		request.data = productList.toJson();
		return request;
	}
	private Request handleGetProduct(Request request) {
		/*
		 * requestType.GET_PRODUCT
		 */
		
	
		Product product = Product.fromJson(request.data);
		product = manager.getProductManager(request.user).getProduct(product.name);
		if (product == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = product.toJson();
		}
		
		return request;
	}
	
	private Request handleAddOrder(Request request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		CartManager cartManager;
		cartManager = new ServerCartManager(request.user, manager.getConnection());
		Order order = Order.fromJson(request.data);
		//System.out.println("GOT + " + order.address);
		order = cartManager.submitOrder(order);
		
		if (order == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = order.toJson();
		}
		
		return request;
	}
	
	
	private Request handleGetOrders(Request request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		OrderManager orderManager = new ServerOrderManager(request.user, manager.getConnection());
		OrderList orders = OrderList.fromJson(request.data);
		orders = orderManager.getOrders(orders.username);
		if (orders == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = orders.toJson();
		}
	
		return request;
	}
	
	private Request handleToFromCatalogue(Request request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		ProductManager  productManager= new ServerProductManager(request.user, manager.getConnection());
		Product toFromCatalogue = Product.fromJson(request.data);
		try {
			if (!productManager.productToFromCatalogue(toFromCatalogue.name,toFromCatalogue.inCatalogue)) {
				/* Product does not exists. */
				request.requestType = RequestType.REQUEST_FAILED;
			}
			request.data = null;
		} catch (PermissionDenied e) {
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("Can not change to/from catalogue field.").toJson();
		}
		return request;
	}
	
	private Request handleSetDiscount(Request request) throws PermissionDenied, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		ProductManager  productManager= new ServerProductManager(request.user, manager.getConnection());
		Product product = Product.fromJson(request.data);		
		if (!productManager.setDiscount(product.name,product.discount)) {
			/* Product does not exists. */
			request.requestType = RequestType.REQUEST_FAILED;
		}
		request.data = null;
		return request;
}
}

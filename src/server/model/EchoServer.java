package server.model;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

import common.Request;
import common.RequestType;
import common.Role;
import common.interfaces.CartManager;
import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.CategoriesList;
import common.request_data.Order;
import common.request_data.IncomeReport;
import common.request_data.IncomeReportList;
import common.request_data.ProductList;
import common.request_data.ServerError;
import common.request_data.User;
import common.request_data.UsersList;
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
		
	
		if(!manager.userLoggedIn(request.user))//If user is loggedIn he doesnt need to be validated
		  request.user = manager.validateUser(request.user);
		
		if(request.requestType.equals(RequestType.LOG_OFF_USER)) {
			
			request = handleLogOff(request);
		
			respond(client, request);
			return;
		}
		if(request.requestType.equals(RequestType.LOGIN)) {
			request = handleLogIn(request);
			respond(client, request);
			return;
		}
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
		case GET_USERS:
			request = handleGetUsers(request);
			break;
		case ADD_USER:
			try {
				request = handleAddUser(request);
			} catch (SQLIntegrityConstraintViolationException e1) {
				e1.printStackTrace();
				
			}
			break;
		case APPROVE_USER:
			request = handleApproveUser(request);
			break;
		/* TODO: Missing REMOVE_USER, GET_USERS */
		/* ProductManager */
		case GET_CATEGORIES:
			request = handleGetCategories(request);
			break;
		case GET_PRODUCTS:
			request = handleGetProducts(request);
			break;
		case GET_INCOME_REPORT:
			try {
				request = handleGetIncomeReports(request);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case GET_INCOME_REPORT_BC:
			try {
				request = handleGetIncomeReportsBC(request);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			
		case CHANGE_STATUS:
			request = handleChangeStatus(request);
			break;
	
		default:
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Unsupporter reuqest.").toJson();
			break;
		}

		respond(client, request);
	}

	private Request handleLogIn(Request request) {
		System.out.println("Correct user requested.");
		User toCheck = User.fromJson(request.data);
		if (!manager.logInUser(toCheck)) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = toCheck.toJson();
		}
		return request;
	}

	private Request handleLogOff(Request request) {
		User user = User.fromJson(request.data);
		
		if(manager.logOffUser(user)) {
			return request;}
		
		request.requestType = RequestType.REQUEST_FAILED;
		return request;
	}

	private Request handleChangeStatus(Request request) {
		User user = User.fromJson(request.data);
		if(manager.changeStatus(user))
			return request;
		request.requestType = RequestType.REQUEST_FAILED;
		return request;
	}

	private Request handleGetUsers(Request request) {
		UsersList tmp = manager.getUsers();
		request.data = tmp.toJson();
		return request;
	}

	private Request handleGetIncomeReportsBC(Request request) throws SQLException {
		IncomeReportList incomeReportList = new IncomeReportList();
		
		incomeReportList = manager.getIncomeReportBC();
		
		if (incomeReportList == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = incomeReportList.toJson();
		}
		return request;
	
	}

	private Request handleGetIncomeReports(Request request) throws SQLException {
		/*
		 * requestType.GET_INCOME_REPORT
		 */
		IncomeReport incomeReport = IncomeReport.fromJson(request.data);
		incomeReport = manager.getIncomeReport(incomeReport);
		if (incomeReport == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = incomeReport.toJson();
		}
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
	private Request handleGetUser(Request request) {////////////////////////////////////////////////////////////////////////////////
		/*
		 * requestType.GET_USER
		 */
		System.out.println("Correct user requested.");
		User toCheck = User.fromJson(request.data);
		
		toCheck.userrole=Role.WORKER;
		if(!manager.userLoggedIn(toCheck)) {
			System.out.println("User to be checkd is: " + toCheck.username);
			toCheck = manager.validateUser(toCheck);}
		
		else toCheck = null;
		
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
		

		try {
			if (!manager.getUserManager(request.user).addNewUser(toAdd.username, toAdd.password, toAdd.nickname,toAdd.shopname,
					toAdd.userrole, toAdd.approved,toAdd.cardNumber,toAdd.exDate,toAdd.cvv,toAdd.logInfo)) {
				/* User already exists. */
				request.requestType = RequestType.REQUEST_FAILED;
			}
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
		request.data = new ServerError("User name allready in dataBase").toJson();
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
	
	private Request handleAddOrder(Request request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		CartManager cartManager;
		cartManager = new ServerCartManager(request.user, manager.getConnection());
		Order order = Order.fromJson(request.data);
		order = cartManager.submitOrder(order);

		if (order == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = order.toJson();
		}

		return request;
	}
}

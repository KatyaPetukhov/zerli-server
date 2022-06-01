package server.model;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

import common.Request;
import common.RequestType;
import common.interfaces.CartManager;
import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.AnalyseSurvey;
import common.request_data.CategoriesList;
import common.request_data.Complaint;
import common.request_data.ComplaintList;
import common.request_data.Order;
import common.request_data.OrderList;
import common.request_data.Product;
//import common.request_data.IncomeReport;
import common.request_data.ProductList;
import common.request_data.Refund;
import common.request_data.ServerError;
import common.request_data.Survey;
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
		if (request.requestType.equals(RequestType.LOG_OFF_USER)) {
			request = handleLogOff(request);
			respond(client, request);
			return;
		}
		if (request.requestType.equals(RequestType.LOGIN)) {
			request = handleLogIn(request);
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
		case GET_PRODUCT:
			request = handleGetProduct(request);
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
			request = handleGetOrders(request);
			break;
		case GET_COMPLAINT:
			try {
				request = handleNewComplaint(request);
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			break;
		case GET_ALL_COMPLAINTS:
			try {
				request = handleGetComplaints(request);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case GET_REFUND_AMOUNT:
			try {
				request = handleGetRefund(request);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case GET_USER_OREDERS:
			try {
				request = handleGetUserOrder(request);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case GET_ANSWERS_SURVEY:
			try {
				request = handleSurveyAnswers(request);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case GET_ANALYSE_SURVEY:
			request = handleSurveyAnalyse(request);
			break;
		default:
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Unsupporter reuqest.").toJson();
			break;
		}
		respond(client, request);
	}

	private Request handleGetOrders(Request request) {
		OrderList orders = OrderList.fromJson(request.data);
		orders = manager.getOrderManager(request.user).getOrders(orders.username);
		if (orders == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = orders.toJson();
		}
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

	private Request handleSurveyAnalyse(Request request) {
		AnalyseSurvey analyseSurvey = AnalyseSurvey.fromJson(request.data);
		Survey survey = manager.analyseTypeSurvey(analyseSurvey.syrveyType, analyseSurvey.shopName, analyseSurvey.date);
		if (survey == null) {
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Could not take from survey reuqest.").toJson();
			return request;
		}
		request.data = survey.toJson();
		return request;
	}

	// NEED-TO-CHECK
	private Request handleSurveyAnswers(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Survey surveyRequest = Survey.fromJson(request.data);
		ServerUserManager serverUserManager = new ServerUserManager(request.user, manager.getConnection());
		if (serverUserManager.setSurveyAnswers(surveyRequest.getQuestion1(), surveyRequest.getQuestion2(),
				surveyRequest.getQuestion3(), surveyRequest.getQuestion4(), surveyRequest.getQuestion5(),
				surveyRequest.getQuestion6(), surveyRequest.getType(), surveyRequest.getShopName(),
				surveyRequest.getDate()))
			request.data = null;
		else {
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Request failed in DB.").toJson();
		}
		return request;
	}
//prbolem - how to take the currect name of Aaron. ask katya/yohan
	private Request handleNewComplaint(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Complaint complaintRequest = Complaint.fromJson(request.data);
		System.out.println("Echo handle complaint " + complaintRequest.userName);
		if (manager.addNewCompliant(complaintRequest.userName, complaintRequest.orderId, complaintRequest.complaint,
				complaintRequest.date, complaintRequest.price, complaintRequest.complaintStatus, "Aaron",
				complaintRequest.refund))
			request.data = null;
		else {
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Request failed in DB.").toJson();
		}
		return request;
	}

	private Request handleGetComplaints(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		ComplaintList complaintList = ComplaintList.fromJson(request.data);
		ServerUserManager serverUserManager = new ServerUserManager(request.user, manager.getConnection());
		complaintList = serverUserManager.getAllComplaints(request.user.nickname);
		request.data = complaintList.toJson();
		return request;
	}

	private Request handleGetUserOrder(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		OrderList orderList = OrderList.fromJson(request.data);
		ServerOrderManager serverOrderManager = new ServerOrderManager(request.user, manager.getConnection());
		orderList = serverOrderManager.getOrders(orderList.username);
		request.data = orderList.toJson();
		return request;
	}

	private Request handleGetRefund(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Refund refundRequest = Refund.fromJson(request.data);
		ServerUserManager serverUserManager = new ServerUserManager(request.user, manager.getConnection());
		if (serverUserManager.setRefundAmount(refundRequest.orderId, refundRequest.refund))
			request.data = null;
		else {
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Request failed in DB.").toJson();
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

		try {
			if (!manager.getUserManager(request.user).addNewUser(toAdd.username, toAdd.password, toAdd.nickname,
					toAdd.shopname, toAdd.userrole, toAdd.approved, toAdd.cardNumber, toAdd.exDate, toAdd.cvv,
					toAdd.logInfo)) {
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
		} catch (SQLIntegrityConstraintViolationException e1) {
			e1.printStackTrace();
			request.requestType = RequestType.REQUEST_FAILED;
			;
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

	private Request handleAddOrder(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		CartManager cartManager;
		cartManager = new ServerCartManager(request.user, manager.getConnection());
		System.out.println("TESTINGGGG");
		Order order = Order.fromJson(request.data);
		System.out.println("GOT + " + order.address);
		order = cartManager.submitOrder(order);

		if (order == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = order.toJson();
		}

		return request;
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

		if (manager.logOffUser(user))
			return request;
		request.requestType = RequestType.REQUEST_FAILED;
		return request;
	}

}

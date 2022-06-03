package server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import common.Role;

import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.ImageFile;

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
		
		
		addOrdersReports(connection);
		addUsers(connection);
		addOrderTable(connection);
		addIncomeReports(connection);
		addComplaints(connection);
		addProducts(connection);
	}
	


	private void createDatabase(DBManager model) {
		model.createDatabase();
	}
	
	private void addOrdersReports(Connection connection) {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetOrdersReports(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);

			addOrdersReports(userManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	


	private void addIncomeReports(Connection connection) {
		
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetIncomeReports(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);

			addIncomeReports(userManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	

	private void addUsers(Connection connection) throws SQLIntegrityConstraintViolationException {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetUsers(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);
			// to USERS SQL: UserName,Password,Nickname,shopname,userRole,cardNumber,expirationDate,cvv,logInfo,userWallet
			userManager.addNewUser("u", "u", "Katya",Shop.NONE, Role.GUEST, true,"1111222233334444","18-7-2023","132",false,"0");
			userManager.addNewUser("o", "o", "Yarden",Shop.ALL, Role.OWNER, true,null,null,null,false,null);
			userManager.addNewUser("m", "m", "Niv",Shop.HAIFA, Role.MANAGER, true,null,null,null,false,null);
			//userManager.addNewUser("w1", "w1", "Good one",Shop.HAIFA, Role.WORKER, true,"10",null,null,false,null);
			//userManager.addNewUser("w2", "w2", "Bad one",Shop.HAIFA, Role.WORKER, true,"0",null,null,false,null);
			userManager.addNewUser("s", "s", "Aaron",Shop.ALL, Role.SUPPORT, true,null,null,null,false,null);
			userManager.addNewUser("d", "d", "Yagan",Shop.ALL, Role.DELIVERY, true,null,null,null,false,null);
		} catch (WeakPassword | PermissionDenied e) {
			e.printStackTrace();
		}
	}
	
	private void addComplaints(Connection connection) {
		User worker = new User();
		worker.userrole = Role.WORKER;
		try {
			ServerUserManager.resetComplaints(connection);
			ServerUserManager userManager = new ServerUserManager(worker, connection);
			userManager.addNewCompliant("Jessica", "123", "ugly flowers", "07/01/2021 12:34:45\r\n"
					+ "", "100", "Awaiting response",
					"Aaron", "0",Shop.HAIFA);
			userManager.addNewCompliant("Yarden", "234", "dry boquet", "07/03/2022 12:34:45\r\n"
					+ "", "50", "Awaiting response", "Aaron",
					"0",Shop.NAHARIYA);
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
			userManager.setSurveyAnswers(1, 2, 1, 4, 3, 7, "Shop survey", "HAIFA", "2022/05");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
		} catch (Exception e) {
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

	private void addOrderTable(Connection connection) {

		ServerOrderManager.resetOrders(connection);

	}
	
	
	private void addIncomeReports(ServerUserManager userManager) throws SQLException {
		// add reports to HAIFA SHOP
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "January", "5000", "Flowers", "40");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "February", "4760", "Flowers", "19");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "March", "6020", "Flowers", "35");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "April", "3000", "Flowers", "20");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "May", "1500", "Flowers", "11");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "June", "730", "Flowers", "6");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "July", "680", "Flowers", "4");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "August", "1356", "Flowers", "12");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "September", "2377", "Flowers", "15");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "October", "2985", "Flowers", "21");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "November", "5505", "Flowers", "45");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "December", "7300", "Flowers", "63");

		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "January", "3450", "Flowers", "22");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "February", "4530", "Flowers", "32");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "March", "8000", "Flowers", "60");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "April", "9210", "Flowers", "85");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "May", "5760", "Flowers", "46");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "June", "350", "Flowers", "4");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "July", "210", "Flowers", "2");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "August", "860", "Flowers", "7");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "September", "9780", "Flowers", "100");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "October", "1750", "Flowers", "9");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "November", "2800", "Flowers", "27");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "December", "1600", "Flowers", "13");
		
		// add reports NAHARIYA SHOP
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "January", "2700", "Flowers", "40");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "February", "4200", "Flowers", "19");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "March", "8960", "Flowers", "35");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "April", "1600", "Flowers", "20");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "May", "720", "Flowers", "11");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "June", "1600", "Flowers", "6");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "July", "500", "Flowers", "4");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "August", "2800", "Flowers", "12");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "September", "1450", "Flowers", "15");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "October", "3270", "Flowers", "21");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "November", "6000", "Flowers", "45");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "December", "7000", "Flowers", "63");

		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "January", "4530", "Flowers", "22");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "February", "3200", "Flowers", "32");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "March", "6000", "Flowers", "60");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "April", "8200", "Flowers", "85");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "May", "4000", "Flowers", "46");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "June", "720", "Flowers", "4");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "July", "900", "Flowers", "2");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "August", "570", "Flowers", "7");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "September", "10000", "Flowers", "100");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "October", "2500", "Flowers", "9");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "November", "1900", "Flowers", "27");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "December", "560", "Flowers", "13");
		
	}
	
	private void addOrdersReports(ServerUserManager userManager) {
		
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "January", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","40");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "February", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","19");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "March", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","35");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "April", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15","20");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "May", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2","11");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "June", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0","6");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "July", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","4");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "August", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","12");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "September", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12","15");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "October", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","21");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "November", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","45");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "December", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2","63");
		
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "January", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "22");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "February", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "32");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "March", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "60");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "April", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "85");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "May", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "46");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "June", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "4");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "July", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "2");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "August", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "7");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "September", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "9");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "October", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20", "9");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "November", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "27");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "December", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "13");
		
		
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "January", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "32");
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "February", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "85");
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "March", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15","19");
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "April", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","21");
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "May", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "31");
		
		
		
		
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "January", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","40");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "February", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","19");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "March", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","35");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "April", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15","20");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "May", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2","11");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "June", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0","6");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "July", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","4");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "August", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","12");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "September", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12","15");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "October", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","21");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "November", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","45");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "December", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2","63");
		
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "January", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "22");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "February", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "32");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "March", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "60");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "April", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "85");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "May", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "46");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "June", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "4");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "July", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "2");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "August", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "7");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "September", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "9");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "October", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20", "9");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "November", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "27");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "December", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "13");
		
		
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "January", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "31");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "February",  "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","21");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "March", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15","19");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "April", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "85");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "May", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "32");
		
		
		
	}


}

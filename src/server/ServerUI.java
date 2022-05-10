package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.gui.ServerController;

public class ServerUI extends Application {
	/* Deletes all existing data from default database and adds default values. */
	private static boolean DO_INITIALIZE = true;
	/* Stops server together with closing GUI. If false - server will continue running. */
	private static boolean STOP_WITH_GUI = true;

	private static ServerManager model;

	public static void main(String args[]) throws Exception {
		launch(args);
		/* Close server on exit of the GUI: */
		if (model != null && STOP_WITH_GUI) {
			model.close();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		if (DO_INITIALIZE) {
			new InitializeDB().f(new DBManager());
		}
		model = new ServerManager();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("./gui/server.fxml"));
		Parent root = (Parent) fxmlLoader.load();
		ServerController controller = fxmlLoader.<ServerController>getController();
		/* Pass a model to server controller: */
		controller.setServerModel(model);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("./gui/style.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}

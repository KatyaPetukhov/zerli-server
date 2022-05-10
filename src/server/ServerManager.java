package server;

import java.io.IOException;

public class ServerManager {
	/*
	 * Stores 2 objects "EchoServer" and "DBManager" and provides APIs to change
	 * them dynamically without restarting the program.
	 */

	private EchoServer server;
	private DBManager manager;

	public void close() {
		/* Run to close all connections. */
		System.out.println("Closing all server connections.");
		stopServer();
	}

	public void setServer(int port) {
		stopServer();
		server = new EchoServer(port);
		server.setDBManager(manager);
		startServer();
	}

	public void setDBManager(String serverURL, String username, String password) {
		stopServer();
		manager = new DBManager(serverURL, username, password);
		if (server != null) {
			server.setDBManager(manager);
		}
		startServer();
	}

	public boolean isServerRunning() {
		return server != null && server.isListening();
	}

	public boolean isSQLCnnected() {
		return manager != null && manager.isConnected();
	}

	private void startServer() {
		if (server == null || manager == null) {
			return;
		}
		try {
			server.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopServer() {
		if (server == null) {
			return;
		}
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

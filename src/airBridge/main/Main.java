package airBridge.main;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import airBridge.receiver.Receiver;

public class Main {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final static Integer SERVER_PORT = 5000;
	private final static String LOCAL_DIR = System.getProperty("user.dir");
	private final static String DESTINATION_FOLDER = "receivedFiles";
	
	public static void main(String[] args) {
		File directory = new File(LOCAL_DIR + "/" + DESTINATION_FOLDER);
		
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		Receiver receiver = new Receiver();
		
		ServerSocket serverSocket = receiver.createServerSocket(SERVER_PORT);
		try {
			receiver.receiveFile(serverSocket, LOCAL_DIR, DESTINATION_FOLDER);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception occured while receiving file.");
			e.printStackTrace();
		}
	}
}

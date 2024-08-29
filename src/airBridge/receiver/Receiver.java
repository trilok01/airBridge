package airBridge.receiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;

public class Receiver {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public ServerSocket createServerSocket(int server_port) {
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(server_port);
			LOGGER.log(Level.INFO, "Server started. Waiting for connection...");
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Exception occured while starting the server.");
			e.printStackTrace();
		}
		
		return serverSocket;
	}
	
	public void receiveFile(ServerSocket serverSocket, String localDirectory, String destinationFolder) throws IOException {
		while(true) {
			Socket clientSocket = serverSocket.accept();
			LOGGER.log(Level.INFO, "Client connected. Connection from " + clientSocket.getInetAddress());
			
			InputStream inputStream = clientSocket.getInputStream();
			byte[] headerBuffer = new byte[108];
			
			inputStream.read(headerBuffer);
			String header = new String(headerBuffer, "UTF-8");
			String fileName = header.substring(0, 100).trim();
			long fileSize = inputStream.available();
			
			LOGGER.log(Level.INFO, "Receiving file. File name: " + fileName + " File size: " + fileSize);
			
			if (fileName != null && !fileName.isEmpty()) {
				LOGGER.log(Level.INFO, "File name received: " + fileName);
				
				// Create a file output stream to save the file
				FileOutputStream fileOutputStream = new FileOutputStream(localDirectory + "/" + destinationFolder + "/" + fileName);
				byte[] buffer = new byte[4096];
				int bytesRead;
				
				// Read the file content
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, bytesRead);
				}
				
				LOGGER.log(Level.INFO, "File received successfully.");
				
				inputStream.close();
				fileOutputStream.close();
				
			} else {
			    LOGGER.log(Level.INFO, "No file name received.");
			}
			
			clientSocket.close();
//			serverSocket.close();
		}
	}
}

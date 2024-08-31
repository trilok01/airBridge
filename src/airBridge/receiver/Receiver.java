package airBridge.receiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.logging.*;

public class Receiver {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final DecimalFormat df = new DecimalFormat("0.00");
	
	public ServerSocket createServerSocket(int server_port) {
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(server_port);
			LOGGER.log(Level.INFO, "\n********************************************\n\nServer started. Waiting for connection...\n\n********************************************");
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Exception occured while starting the server.");
			e.printStackTrace();
		}
		
		return serverSocket;
	}
	
	public void receiveFile(ServerSocket serverSocket, String localDirectory, String destinationFolder) throws IOException {
		while(true) {
			Socket clientSocket = serverSocket.accept();
			LOGGER.log(Level.INFO, "\n********************************************\n\nClient connected. Connection from " + clientSocket.getInetAddress() + "\n\n********************************************");
			
			InputStream inputStream = clientSocket.getInputStream();
			byte[] headerBuffer = new byte[113];
			
			inputStream.read(headerBuffer);
			String header = new String(headerBuffer, "UTF-8");
			String fileName = header.substring(0, 100).trim();
			long fileSize = Long.parseLong(header.substring(100, 113).trim());
			String convertedFileSize = getUserFriendlySize(fileSize);
			
			LOGGER.log(Level.INFO, "\n********************************************\n\nReceiving file. File name: " + fileName + " File size: " + convertedFileSize + "\n\n********************************************");
			
			if (fileName != null && !fileName.isEmpty()) {
				
				// Create a file output stream to save the file
				FileOutputStream fileOutputStream = new FileOutputStream(localDirectory + "/" + destinationFolder + "/" + fileName);
				
				boolean flag = true;
				long bufferSize = 8192;
				byte[] buffer = new byte[8192];
//				int bytesRead;
//				
//				// Read the file content
//				while ((bytesRead = inputStream.read(buffer)) != -1) {
//					fileOutputStream.write(buffer, 0, bytesRead);
//				}
				
				long bytesToRead = fileSize;
				
				while(flag) {
					if(bytesToRead < bufferSize) {
						buffer = new byte[(int) bytesToRead];
						
						flag = false;
					}
					
					bytesToRead -= bufferSize;
					fileOutputStream.write(buffer, 0, inputStream.read(buffer));
				}
				
				LOGGER.log(Level.INFO, "\n********************************************\n\nFile " + fileName + " received successfully.\n\n********************************************");
				
				inputStream.close();
				fileOutputStream.close();
				
			} else {
			    LOGGER.log(Level.INFO, "\n********************************************\n\nNo file name received.\n\n********************************************");
			}
			
			clientSocket.close();
//			serverSocket.close();
		}
	}
	
	private String getUserFriendlySize(double sizeInBytes) {
		double convertedSize = sizeInBytes;
		final double divisor = 1024.00;
		final String[] sizeAbbreviation = new String[] {"Bytes", "KB", "MB", "GB", "TB"};
		int counter = 0;
		
		while(Double.compare(convertedSize / divisor, 1.0) >= 0) {
			convertedSize /= divisor;
			counter++;
		}
		
		return df.format(convertedSize) + " " + sizeAbbreviation[counter];
	}
}

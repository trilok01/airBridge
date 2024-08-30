package airBridge.main;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import airBridge.receiver.Receiver;

public class Main {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final static String LOCAL_DIR = System.getProperty("user.dir");
	private final static String DESTINATION_FOLDER = "receivedFiles";
	
	public static void main(String[] args) {
		File directory = new File(LOCAL_DIR + "/" + DESTINATION_FOLDER);
		
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		try {
			String ipAddress = getIpAddress();
			
			LOGGER.log(Level.INFO, "\n**********************\n\nIP address: " + ipAddress + "\n\n**********************");
		} catch (SocketException e) {
			LOGGER.log(Level.SEVERE, "Exception occured while fetching IP address.");
			e.printStackTrace();
		}
		
		Receiver receiver = new Receiver();
		
		ServerSocket serverSocket = receiver.createServerSocket(0);
		int portNumber = serverSocket.getLocalPort();
		
		LOGGER.log(Level.INFO, "\n**********************\n\nPort number: " + portNumber + "\n\n**********************");
		
		try {
			receiver.receiveFile(serverSocket, LOCAL_DIR, DESTINATION_FOLDER);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception occured while receiving file.");
			e.printStackTrace();
		}
	}
	
	private static String getIpAddress() throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		String ipAddress = null;
		
		while(networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			
			if(networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.isVirtual() || networkInterface.isPointToPoint()) {
				continue;
			}
			
			Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
			
			while(inetAddresses.hasMoreElements()) {
				InetAddress inetAddress = inetAddresses.nextElement();
				
				if(Inet4Address.class.equals(inetAddress.getClass())) {
					ipAddress = inetAddress.getHostAddress();
				}
			}
		}
		
		return ipAddress;
	}
}

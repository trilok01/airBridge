package airBridge.sender;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Sender {
	
	public void sendFile(ServerSocket serverSocket, String fileLocation) {
		
		try {
			Socket clientSocket = serverSocket.accept();
			
			OutputStream outputStream = clientSocket.getOutputStream();
			FileInputStream file = new FileInputStream(fileLocation);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(file);
			
			byte[] buffer = new byte[8192];
			int bytesRead;
			
			while((bytesRead = bufferedInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			
			outputStream.flush();
			outputStream.close();
			bufferedInputStream.close();
			outputStream.close();
			clientSocket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/**
 * 
 */
package one2oneChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Tommaso
 *
 */
public class ServerListener implements Runnable {
	private OutputStream outputStream;
	private Socket clientSocket;

	public ServerListener(OutputStream _outputStream) {
		System.out.println("Server creato");
		outputStream = _outputStream;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			InputStream clientInputStream = clientSocket.getInputStream();
			InputStreamReader inputReader = new InputStreamReader(clientInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputReader);     
			String inputString;
			System.out.println("TomServer started to listen to client");

			while ((inputString = bufferedReader.readLine()) != null) {    
				System.out.println("< " + inputString);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}

}

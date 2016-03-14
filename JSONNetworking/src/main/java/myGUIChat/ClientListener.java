/**
 * 
 */
package myGUIChat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Tommaso
 *
 */
public class ClientListener implements Runnable {

	private Socket socket;
	
	/**
	 * 
	 */
	public ClientListener(Socket _socket) {
		socket = _socket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			InputStream inputStream = socket.getInputStream();
			InputStreamReader inputReader = new InputStreamReader(inputStream);
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

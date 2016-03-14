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
public class TomServer implements Runnable {
	private OutputStream outputStream;
	
	public TomServer(OutputStream _outputStream) {
		System.out.println("Server creato");
		outputStream = _outputStream;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("Server run");
		PrintWriter pw = new PrintWriter(outputStream);
		Scanner keyboard = new Scanner(System.in);
		while (true) {
			System.out.print("- ");
			String inputString = keyboard.nextLine();
			pw.println(inputString);
			pw.flush();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(3003);
			System.out.print("TomServer waiting for a client...");
			Socket clientSocket = serverSocket.accept( );	// catch the client socket connecting
			System.out.println("client connected!");
			
			OutputStream clientOutputStream = clientSocket.getOutputStream();
			
			TomServer tomServer = new TomServer(clientOutputStream);
			Thread myThread = new Thread(tomServer);
			myThread.start();
			
			InputStream clientInputStream = clientSocket.getInputStream();
			InputStreamReader inputReader = new InputStreamReader(clientInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputReader);     
			String inputString;
			System.out.println("TomServer started to listen to client");
			try {
				while ((inputString = bufferedReader.readLine()) != null) {    
					System.out.println("< " + inputString);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}

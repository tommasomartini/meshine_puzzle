/**
 * 
 */
package one2oneChat;

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
public class TomClient implements Runnable {

	private Socket socket;
	/**
	 * 
	 */
//	public TomClient(Socket _socket) {
//		socket = _socket;
//	}
	
	public TomClient(int _port) {
		try {
			socket = new Socket("localhost", _port);
			
			System.out.println("Connected to server");
			
			ServerListener serverListener = new ServerListener(socket);
			Thread listenerThread = new Thread(serverListener);
			listenerThread.start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("TomClient started to listen to server");
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream);
			Scanner keyboard = new Scanner(System.in);
			System.out.println("TomClient started to listen to human");
			while (true) {
				System.out.print("- ");
				String inputString = keyboard.nextLine();
				printWriter.println(inputString);
				printWriter.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		try {
//			Socket socket = new Socket("localhost", 3003);
//
//			TomClient tomClient = new TomClient(socket);
//			Thread myThread = new Thread(tomClient);
//			myThread.start();
//
//			InputStream inputStream = socket.getInputStream();
//			InputStreamReader inputReader = new InputStreamReader(inputStream);
//			BufferedReader bufferedReader = new BufferedReader(inputReader);     
//			String inputString;
//			System.out.println("TomClient started to listen to server");
//			try {
//				while ((inputString = bufferedReader.readLine()) != null) {    
//					System.out.println("< " + inputString);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}  
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

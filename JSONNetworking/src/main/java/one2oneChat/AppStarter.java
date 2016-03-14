/**
 * 
 */
package one2oneChat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author tom
 *
 */
public class AppStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 3003;
		TomServer tomServer = new TomServer(port);
		Thread serverThread = new Thread(tomServer);	
		serverThread.start();
		
//		TomClient tomClient = new TomClient();
//		Thread clientThread = new Thread(tomClient);
//		clientThread.start();
	}

}

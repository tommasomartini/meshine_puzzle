/**
 * 
 */
package myGUIChat;

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
		ServerGUI serverGui = new ServerGUI(port);		
		ClientGUI clientGui = new ClientGUI("localhost", port);
		
//		TomClient tomClient = new TomClient(port);
//		Thread clientThread = new Thread(tomClient);
//		clientThread.start();
	}

}

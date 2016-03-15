/**
 * 
 */
package myGUIChat;

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
		new ServerGUI(port);		
		new ClientGUI("localhost", port);
	}

}

package myGUIChat;


import java.net.*;
import java.io.*;
import java.util.*;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client {

	private ObjectInputStream inputStream;		// to read from the socket
	private ObjectOutputStream outputStream;		// to write on the socket
	private Socket socket;

	// if I use a GUI or not
	private ClientGUI clientGUI;
	
	// the server, the port and the username
	private String serverAddress, username;
	private int port;

	/*
	 *  Constructor called by console mode
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */
	Client(String _serverAddress, int _port, String _username) {
		this(_serverAddress, _port, _username, null);
	}

	Client(String _serverAddress, int _port, String _username, ClientGUI _clientGUI) {
		this.serverAddress = _serverAddress;
		this.port = _port;
		this.username = _username;
		this.clientGUI = _clientGUI;
	}
	
	public boolean startClient() {
		try {
			socket = new Socket(serverAddress, port);
		} 
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream  = new ObjectInputStream(socket.getInputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
		new ListenFromServer().start();
		System.out.println("creato!!!");
		try {
			ChatMessage myMsg = new ChatMessage(ChatMessage.MESSAGE, username);
			outputStream.writeObject(myMsg);
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		return true;
	}

	private void display(String msg) {
		if(clientGUI == null)
			System.out.println(msg);      // println in console mode
		else
			clientGUI.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			outputStream.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try { 
			if(inputStream != null) 
				inputStream.close();
		} catch(Exception e) {} // not much else I can do
		try {
			if(outputStream != null) 
				outputStream.close();
		} catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) 
				socket.close();
		} catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		if(clientGUI != null)
			clientGUI.connectionFailed();
	}

	class ListenFromServer extends Thread {
		public void run() {
			System.out.println("client prnto ad ascoltare il server");
			boolean keepListeningFromServer = true;
			while(keepListeningFromServer) {
				try {
					String msg = (String)inputStream.readObject();
					clientGUI.append(msg);
				} catch(IOException e) {
					display("Server has close the connection: " + e);
					if(clientGUI != null) 
						clientGUI.connectionFailed();
					break;
				} catch(ClassNotFoundException e2) {}
			}
		}
	}
}



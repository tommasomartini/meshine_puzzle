package solution;

import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread {
	private ChatServer server = null;
	private Socket clientSocket = null;
	private int ID = -1;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;

	public ChatServerThread(ChatServer _server, Socket _socket, int _id) {
		super();
		server = _server;
		clientSocket = _socket;
		ID = _id;
//		ID = clientSocket.getPort();
	}

	/**
	 * Send a message to the client this server is connected to.
	 * @param msg Message to send.
	 */
	public void send(String msg) {   
		try {
			streamOut.writeUTF(msg);
			streamOut.flush();
		} catch(IOException ioe) {
			System.out.println(ID + " ERROR sending: " + ioe.getMessage());
			server.remove(ID);	// remove this client from the client list
			interrupt();	// stop this thread
		}
	}

	/**
	 * Returns the ID of this thread.
	 * @return The ID of this thread. The ID is equal to the port used by this thread.
	 */
	public int getID() {
		return ID;
	}

	public void run() {
		System.out.println("Server Thread " + ID + " running.");
		while (true) {
			try {  
				server.handle(ID, streamIn.readUTF());
			} catch(IOException ioe) {
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.remove(ID);
//				stop();
				interrupt();
			}
		}
	}

	public void open() throws IOException {
		streamIn = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
	}

	public void close() throws IOException {
		if (clientSocket != null)    
			clientSocket.close();
		if (streamIn != null)  
			streamIn.close();
		if (streamOut != null) 
			streamOut.close();
	}
}
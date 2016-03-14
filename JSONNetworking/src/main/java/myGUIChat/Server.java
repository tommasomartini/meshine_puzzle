package myGUIChat;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server implements Runnable {
	private static int uniqueId;
	private ServerGUI serverGUI;
	private SimpleDateFormat dateFormat;
	private int port;
	
	private ServerSocket serverSocket;
	private ObjectInputStream objInputStream;		
	private ObjectOutputStream objOutputStream;		
	private Socket clientSocket;
	
	public Server(int _port) {
		this(_port, null);
	}
	
	public Server(int _port, ServerGUI _serverGUI) {
		this.serverGUI = _serverGUI;
		this.port = _port;
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Un nuovo server è stato creato");
	}
	
    /*
     * For the GUI to stop the server
     */
	protected void stop() {
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	
	private void display(String msg) {
		String time = dateFormat.format(new Date()) + " " + msg;
		if(serverGUI == null)
			System.out.println(time);
		else
			serverGUI.appendEvent(time + "\n");
	}
	
	public void sendMessage(ChatMessage msg) {
		try {
			objOutputStream.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			display("Server waiting for the client on port " + port + "...");
			
			clientSocket = serverSocket.accept( );	// a client connects!
			
			display("client connected!");
			serverGUI.notifyConnection(true);
			
			InputStream clientIntputStream = clientSocket.getInputStream();
			OutputStream clientOutputStream = clientSocket.getOutputStream();
			
			objInputStream  = new ObjectInputStream(clientIntputStream);
			objOutputStream = new ObjectOutputStream(clientOutputStream);
			
			ClientThread clientThread = new ClientThread(clientSocket); 	// this thread listens to the client 
			clientThread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream inputStream;
		ObjectOutputStream outputStream;
		int id;
		String username;
		ChatMessage chatMessage;
		String date;

		ClientThread(Socket _socket) {
			id = ++uniqueId;	// a unique id
			this.socket = _socket;
			System.out.println("Thread trying to create Object Input/Output Streams");
			try {
				// create output first
				outputStream = new ObjectOutputStream(_socket.getOutputStream());
				inputStream  = new ObjectInputStream(_socket.getInputStream());
				// read the username
				username = (String)inputStream.readObject();
				display(username + " just connected.");
			} catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			} catch (ClassNotFoundException e) {}
            date = new Date().toString() + "\n";
		}

		public void run() {
			boolean keepListeningFromClient = true;
			while(keepListeningFromClient) {
				try {
					chatMessage = (ChatMessage)inputStream.readObject();
				} catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				} catch(ClassNotFoundException e2) {
					break;
				}
				String message = chatMessage.getMessage();
				switch(chatMessage.getType()) {
				case ChatMessage.MESSAGE:
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepListeningFromClient = false;
					break;
				}
			}
			close();
		}
		
		private void close() {
			try {
				if(outputStream != null) 
					outputStream.close();
			} catch(Exception e) {}
			try {
				if(inputStream != null) 
					inputStream.close();
			} catch(Exception e) {};
			try {
				if(socket != null) 
					socket.close();
			} catch (Exception e) {}
		}

//		private boolean writeMsg(String msg) {
//			// if Client is still connected send the message to it
//			if(!socket.isConnected()) {
//				close();
//				return false;
//			}
//			// write the message to the stream
//			try {
//				outputStream.writeObject(msg);
//			}
//			// if an error occurs, do not abort just inform the user
//			catch(IOException e) {
//				display("Error sending message to " + username);
//				display(e.toString());
//			}
//			return true;
//		}
	}
}



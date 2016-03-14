package solution;

import java.net.*;
import java.io.*;

public class ChatServer {
	private ChatServerThread clients[] = new ChatServerThread[50];
	private ServerSocket serverSocket = null;
//	private Thread thread = null;
//	private int clientCount = 0;

	/**
	 * Creates a <code>ChatServer</code> waiting for some clients to connect.
	 * @param port Number of the port this server is binded to.
	 */
	public ChatServer(int port) {
		try	{  
			System.out.println("Binding to port " + port + ", please wait  ...");
			serverSocket = new ServerSocket(port);  // create a socket waiting for clients to connect
			System.out.println("Server started: " + serverSocket);
			
			while (true) {
				try {
			        Socket clientSocket = serverSocket.accept();
			        int clSockIndex = 0;
			        for (clSockIndex = 0; clSockIndex < clients.length; clSockIndex++) {
			          if (clients[clSockIndex] == null) {
			            clients[clSockIndex] = new ChatServerThread(this, clientSocket, clSockIndex);
			            clients[clSockIndex].start();
			            break;
			          }
			        }
			        if (clSockIndex == clients.length) {
			          PrintStream os = new PrintStream(clientSocket.getOutputStream());
			          os.println("Server too busy. Try later.");
			          os.close();
			          clientSocket.close();
			        }
			      } catch (IOException e) {
			        System.out.println(e);
			      }
			}			
		} catch(IOException ioe) {
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); 
		}
	}

//	public void run() {
//		while (thread != null) {
//			try	{
//				System.out.println("Waiting for a client ..."); 
//				addThread(serverSocket.accept()); 
//			} catch(IOException ioe) {
//				System.out.println("Server accept error: " + ioe); stop(); 
//			}
//		}
//	}
//
//	public void stop() { 
//		if (thread != null) {
//			thread.interrupt(); 
//			thread = null;
//		}
//	}

	private int findClient(int ID) {
		for (int i = 0; i < clients.length; i++)
			if (clients[i].getID() == ID)
				return i;
		return -1;
	}

	/**
	 * Handles the input message coming from a client.
	 * @param ID ID assigned to a client.
	 * @param input	Message from the client
	 */
	public void handle(int ID, String input) {
		if (input.equals(".bye")) {	// clients wants to close the communication
			ChatServerThread _serverThread = clients[findClient(ID)]; // take the realtive thread
			_serverThread.send(".bye");	// confirm to client the connection close
			remove(ID); // remove the thread from my list
		}
//		} else
//			for (int i = 0; i < clientCount; i++)
//				clients[i].send(ID + ": " + input);   
	}

	public synchronized void remove(int ID)	{ 
		int pos = findClient(ID);
		if (pos >= 0) {
			ChatServerThread toTerminate = clients[pos];
			System.out.println("Removing client thread " + ID + " at " + pos);
			if (pos < clientCount-1)
				for (int i = pos+1; i < clientCount; i++)
					clients[i-1] = clients[i];
			clientCount--;
			try {  
				toTerminate.close(); 
			} catch(IOException ioe) {
				System.out.println("Error closing thread: " + ioe); 
			}
			toTerminate.interrupt(); 
		}
	}

//	private void addThread(Socket socket) {
//		if (clientCount < clients.length) {
//			System.out.println("Client accepted: " + socket);
//			clients[clientCount] = new ChatServerThread(this, socket);
//			try	{
//				clients[clientCount].open(); 
//				clients[clientCount].start();  
//				clientCount++; 
//			} catch(IOException ioe) {
//				System.out.println("Error opening thread: " + ioe); 
//			}
//		} else
//			System.out.println("Client refused: maximum " + clients.length + " reached.");
//	}

	public static void main(String args[]) { 
		
		/*
		 * Create a new chat server listening on port 3333
		 */
		new ChatServer(3333);
	}
}



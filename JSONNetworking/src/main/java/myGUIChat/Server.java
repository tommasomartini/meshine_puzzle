package myGUIChat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

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
		serverGUI.appendEvent(msg + "\n");
	}
	
	private void displayChat(String msg) {
		serverGUI.appendChat(msg + "\n");
	}
	
//	public void sendMessage(ChatMessage msg) {
//		try {
//			objOutputStream.writeObject(msg);
//		}
//		catch(IOException e) {
//			display("Exception writing to server: " + e);
//		}
//	}
	
	public void sendMessage(JSONPacket jsonPacket) {
		try {
			objOutputStream.writeObject(jsonPacket);
		}
		catch(IOException e) {
			display("Exception writing to client: " + e);
		}
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			display("Server waiting for the client on port " + port + "...");
			
			clientSocket = serverSocket.accept( );	// a client connects!
			
			display("client connected!");
			serverGUI.notifyConnection(true);
			
			objOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			objInputStream = new ObjectInputStream(clientSocket.getInputStream());
			
			ClientThread clientThread = new ClientThread(clientSocket); 	// this thread listens to the client 
			clientThread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket toListenSocket;
		int id;
		String username;
		ChatMessage chatMessage;
		JSONPacket jsonPacket;
		String date;

		ClientThread(Socket _socket) {
			id = ++uniqueId;	// a unique id
			this.toListenSocket = _socket;
			try {
//				ChatMessage firstChatMessage = (ChatMessage)objInputStream.readObject();
				
				JSONPacket firstMessage = (JSONPacket)objInputStream.readObject();
				
				JSONObject receivedObjPayload = new JSONObject(firstMessage.getPayload());
				String msgType = receivedObjPayload.getString("message_type");
				username = receivedObjPayload.getString("username");
				boolean isClient = receivedObjPayload.getBoolean("is_client");
				JSONObject sendingTime = receivedObjPayload.getJSONObject("message_time");
				int hour = sendingTime.getInt("hour");
				int minute = sendingTime.getInt("minute");
				int second = sendingTime.getInt("second");
				String timeString = hour + ":" + minute + ":" + second;
				
				if (isClient && msgType.equals("login")) {
					byte[] receivedObjData = firstMessage.getExtraData();
					String dataString = new String(receivedObjData);
					//				username = firstMessage.getMessage();
					display(username + " connected at " + timeString + ".");
					displayChat(username + "[" + timeString + "] > " + dataString);
				}
			} catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			} catch (ClassNotFoundException e) {}
            date = new Date().toString() + "\n";
		}

		public void run() {
			boolean keepListeningFromClient = true;
			while(keepListeningFromClient) {
				boolean isClient;
				String msgUsername;
				String msgType;
				String timeString;
				try {
//					chatMessage = (ChatMessage)objInputStream.readObject();
					
					jsonPacket = (JSONPacket)objInputStream.readObject();
					
					JSONObject rxPayload = new JSONObject(jsonPacket.getPayload());
					msgType = rxPayload.getString("message_type");
					msgUsername = rxPayload.getString("username");
					isClient = rxPayload.getBoolean("is_client");
					JSONObject sendingTime = rxPayload.getJSONObject("message_time");
					int hour = sendingTime.getInt("hour");
					int minute = sendingTime.getInt("minute");
					int second = sendingTime.getInt("second");
					timeString = hour + ":" + minute + ":" + second;
				} catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				} catch(ClassNotFoundException e2) {
					break;
				}
				
				if (isClient && msgUsername.equals(username)) {
					if (msgType.equals(JSONPacket.LOGIN_STRING)) {
						// do nothing
					} else if (msgType.equals(JSONPacket.LOGOUT_STRING)) {
						keepListeningFromClient = false;
					} else if (msgType.equals(JSONPacket.MESSAGE_STRING)) {
						byte[] receivedObjData = jsonPacket.getExtraData();
						String dataString = new String(receivedObjData);
						displayChat(username + "[" + timeString + "] > " + dataString);
						
						JSONObject ackObj = new JSONObject();
						ackObj.put("username", "YourServer");
						ackObj.put("is_client", new Boolean(false));
						ackObj.put("message_type", JSONPacket.ACK_STRING);
						String now = dateFormat.format(new Date());
						String[] nowParts = now.split(":");
						JSONObject dateObj = new JSONObject();
						dateObj.put("hour", Integer.parseInt(nowParts[0]));
						dateObj.put("minute", Integer.parseInt(nowParts[1]));
						dateObj.put("second", Integer.parseInt(nowParts[2]));
						ackObj.put("message_time", dateObj);

						JSONPacket jsonPacket = new JSONPacket(ackObj.toString(), timeString.getBytes());	// send the hour the message has been sent
						sendMessage(jsonPacket);
						
					} else {
						// do nothing
					}
				}
				
//				String message = chatMessage.getMessage();
//				switch(chatMessage.getType()) {
//				case ChatMessage.MESSAGE:
//					displayChat(message);
//					break;
//				case ChatMessage.LOGOUT:
//					display(username + " disconnected with a LOGOUT message.");
//					keepListeningFromClient = false;
//					break;
//				}
			}
			close();
		}
		
		private void close() {
			try {
				if(objOutputStream != null) 
					objOutputStream.close();
			} catch(Exception e) {}
			try {
				if(objInputStream != null) 
					objInputStream.close();
			} catch(Exception e) {};
			try {
				if(toListenSocket != null) 
					toListenSocket.close();
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



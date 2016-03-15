package myGUIChat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	private void displayEvent(String msg) {
		serverGUI.appendEvent(msg);
	}
	
	private void displayChat(String msg) {
		serverGUI.appendChat(msg);
	}
	
	public void sendMessage(JSONPacket jsonPacket) {
		try {
			objOutputStream.writeObject(jsonPacket);
		}
		catch(IOException e) {
			displayEvent("Exception writing to client: " + e);
		}
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(port);

			while (true) {

				displayEvent("Server waiting for the client on port " + port + "...");
				clientSocket = serverSocket.accept( );	// a client connects!

				displayEvent("client connected!");
				serverGUI.notifyConnection(true);

				objOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				objInputStream = new ObjectInputStream(clientSocket.getInputStream());

				ClientThread clientThread = new ClientThread(clientSocket); 	// this thread listens to the client 
				clientThread.start();
			}

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
		JSONPacket jsonPacket;
		String date;
		String msgIDstr;

		ClientThread(Socket _socket) {
			id = ++uniqueId;	// a unique id
			this.toListenSocket = _socket;
			try {				
				JSONPacket firstMessage = (JSONPacket)objInputStream.readObject();
				
				JSONObject receivedObjPayload = new JSONObject(firstMessage.getPayload());
				String msgType = receivedObjPayload.getString("message_type");
				username = receivedObjPayload.getString("username");
				boolean isClient = receivedObjPayload.getBoolean("is_client");
				msgIDstr = receivedObjPayload.getString("message_id");
				JSONObject sendingTime = receivedObjPayload.getJSONObject("message_time");
				int hour = sendingTime.getInt("hour");
				int minute = sendingTime.getInt("minute");
				int second = sendingTime.getInt("second");
				String timeString = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
				
				if (isClient && msgType.equals("login")) {
					byte[] receivedObjData = firstMessage.getExtraData();
					String dataString = new String(receivedObjData);
					//				username = firstMessage.getMessage();
					serverGUI.appendEvent(username + " connected at " + timeString + ".");
					serverGUI.appendChat(username + "[" + timeString + "] > " + dataString);
				}
			} catch (IOException e) {
				displayEvent("Exception creating new Input/output Streams: " + e);
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
				String msgIDstr;
				try {
					jsonPacket = (JSONPacket)objInputStream.readObject();
					
					JSONObject rxPayload = new JSONObject(jsonPacket.getPayload());
					msgType = rxPayload.getString("message_type");
					msgUsername = rxPayload.getString("username");
					isClient = rxPayload.getBoolean("is_client");
					msgIDstr = rxPayload.getString("message_id");
					JSONObject sendingTime = rxPayload.getJSONObject("message_time");
					int hour = sendingTime.getInt("hour");
					int minute = sendingTime.getInt("minute");
					int second = sendingTime.getInt("second");
					timeString = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
				} catch (IOException e) {
					displayEvent(username + " Exception reading Streams: " + e);
					break;				
				} catch(ClassNotFoundException e2) {
					break;
				}
				
				if (isClient && msgUsername.equals(username)) {
					if (msgType.equals(JSONPacket.LOGIN_STRING)) {
						// do nothing
					} else if (msgType.equals(JSONPacket.LOGOUT_STRING)) {
						displayEvent(username + " requestes logout [" + timeString + "]");
						keepListeningFromClient = false;
						serverGUI.notifyConnection(false);
						try {
							objOutputStream.close();
							objInputStream.close();
							clientSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (msgType.equals(JSONPacket.MESSAGE_STRING)) {
						byte[] receivedObjData = jsonPacket.getExtraData();
						String dataString = new String(receivedObjData);
						displayChat(username + "[" + timeString + "] > " + dataString);
						
						JSONObject ackObj = new JSONObject();
						ackObj.put("username", "YourServer");
						ackObj.put("is_client", new Boolean(false));
						ackObj.put("message_type", JSONPacket.ACK_STRING);
						String myMsgIDstr = "s" + ServerGUI.getNextServerMsgID();
						ackObj.put("message_id", myMsgIDstr);
						String now = dateFormat.format(new Date());
						String[] nowParts = now.split(":");
						JSONObject dateObj = new JSONObject();
						dateObj.put("hour", Integer.parseInt(nowParts[0]));
						dateObj.put("minute", Integer.parseInt(nowParts[1]));
						dateObj.put("second", Integer.parseInt(nowParts[2]));
						ackObj.put("message_time", dateObj);

						JSONPacket jsonPacket = new JSONPacket(ackObj.toString(), msgIDstr.getBytes());	// send back the msg ID
						sendMessage(jsonPacket);
						
					} else if (msgType.equals(JSONPacket.ACK_STRING)) {
						byte[] receivedObjData = jsonPacket.getExtraData();
						String msgId = new String(receivedObjData);
						displayChat("ACK [" + timeString + "] " + msgId);
					} else {
						// do nothing
					}
				}
			}	// while cicle
			
//			serverGUI.startNewServer();
//			close();
		}
		
//		private void close() {
//			try {
//				if(objOutputStream != null) 
//					objOutputStream.close();
//			} catch(Exception e) {}
//			try {
//				if(objInputStream != null) 
//					objInputStream.close();
//			} catch(Exception e) {};
//			try {
//				if(toListenSocket != null) 
//					toListenSocket.close();
//			} catch (Exception e) {}
//		}
	}
}



package myGUIChat;


import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import org.json.JSONObject;


public class Client {

	private ObjectInputStream inputStream;		// to read from the socket
	private ObjectOutputStream outputStream;		// to write on the socket
	private Socket socket;

	private ClientGUI clientGUI;
	private SimpleDateFormat dateFormat;
	
	private String serverAddress, username;
	private int port;

	public Client(String _serverAddress, int _port, String _username) {
		this(_serverAddress, _port, _username, null);
	}

	public Client(String _serverAddress, int _port, String _username, ClientGUI _clientGUI) {
		this.serverAddress = _serverAddress;
		this.port = _port;
		this.username = _username;
		this.clientGUI = _clientGUI;
		dateFormat = new SimpleDateFormat("HH:mm:ss");
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
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("username", username);
			jsonObj.put("is_client", new Boolean(true));
			jsonObj.put("message_type", JSONPacket.LOGIN_STRING);
			String msgIDstr = "c" + ClientGUI.getNextClientMsgID();
			jsonObj.put("message_id", msgIDstr);
			String now = dateFormat.format(new Date());
			String[] nowParts = now.split(":");
			JSONObject dateObj = new JSONObject();
			dateObj.put("hour", Integer.parseInt(nowParts[0]));
			dateObj.put("minute", Integer.parseInt(nowParts[1]));
			dateObj.put("second", Integer.parseInt(nowParts[2]));
			jsonObj.put("message_time", dateObj);

			String helloString = "Hello Server!";
			byte[] dataString = helloString.getBytes();
			JSONPacket jsonPacket = new JSONPacket(jsonObj.toString(), dataString);

//			ChatMessage myMsg = new ChatMessage(ChatMessage.MESSAGE, username);
			
			outputStream.writeObject(jsonPacket);
			outputStream.flush();
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
//	public void sendMessage(ChatMessage msg) {
//		try {
//			outputStream.writeObject(msg);
//		}
//		catch(IOException e) {
//			display("Exception writing to server: " + e);
//		}
//	}
	
	public void sendMessage(JSONPacket jsonPacket) {
		try {
			outputStream.writeObject(jsonPacket);
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
		JSONPacket jsonPacket;
		String msgType;
		String msgUsername;
		boolean isClient;
		String timeString;
		String msgIDstr;
		
		public void run() {
			boolean keepListeningFromServer = true;
			while(keepListeningFromServer) {
				try {
					
					jsonPacket = (JSONPacket)inputStream.readObject();

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
					
//					ChatMessage currentChatMsg = (ChatMessage)inputStream.readObject();
//					String msg = currentChatMsg.getMessage();
//					clientGUI.append(msg);
					
					if (!isClient && msgUsername.equals("YourServer")) {
						if (msgType.equals(JSONPacket.LOGIN_STRING)) {
							// do nothing
						} else if (msgType.equals(JSONPacket.LOGOUT_STRING)) {
							// do nothing
						} else if (msgType.equals(JSONPacket.MESSAGE_STRING)) {
							byte[] receivedObjData = jsonPacket.getExtraData();
							String dataString = new String(receivedObjData);
							clientGUI.append(msgUsername + "[" + timeString + "] > " + dataString);
							
							JSONObject ackObj = new JSONObject();
							ackObj.put("username", username);
							ackObj.put("is_client", new Boolean(true));
							ackObj.put("message_type", JSONPacket.ACK_STRING);
							String myMsgIDstr = "c" + ClientGUI.getNextClientMsgID();
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
							clientGUI.append(msgUsername + " ACK at [" + timeString + "] " + msgId);
						} else {
							// do nothing
						}
					}
					
					
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



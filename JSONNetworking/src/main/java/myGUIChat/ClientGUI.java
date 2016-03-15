package myGUIChat;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.JSONObject;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static int clientMsgID = 0;
	
	private JLabel lbUsernameOrMessage;
	private JTextField tfUsernameOrMessage;
	private JTextField tfServerAddress, tfServerPort;
	private JButton btLogin;
	private JButton btLogout;
	private JTextArea taChat;
	
	private boolean connected;
	private Client client;
	private int defaultPort;
	private String defaultHost;
	private SimpleDateFormat dateFormat;
	private String clientUsername;
	
	public ClientGUI(String host, int port) {
		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		
		JPanel northPanel = new JPanel(new GridLayout(3,1));	// panel 3 x 1
		// 1 North)
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		tfServerAddress = new JTextField(host);
		tfServerPort = new JTextField("" + port);
		tfServerPort.setHorizontalAlignment(SwingConstants.RIGHT);
		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServerAddress);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfServerPort);
		serverAndPort.add(new JLabel(""));
		northPanel.add(serverAndPort);
		// 2 North)
		lbUsernameOrMessage = new JLabel("Enter your username below", SwingConstants.CENTER);
		northPanel.add(lbUsernameOrMessage);
		// 3 North)
		tfUsernameOrMessage = new JTextField("Anonymous");
		tfUsernameOrMessage.setBackground(Color.WHITE);
		northPanel.add(tfUsernameOrMessage);
		add(northPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		taChat = new JTextArea("Welcome to the Chat room\n", 80, 80);
		taChat.setEditable(false);
		centerPanel.add(new JScrollPane(taChat));
		add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		btLogin = new JButton("Login");
		btLogin.addActionListener(this);
		btLogout = new JButton("Logout");
		btLogout.addActionListener(this);
		btLogout.setEnabled(false);		
		southPanel.add(btLogin);
		southPanel.add(btLogout);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tfUsernameOrMessage.requestFocus();		// this is automatically focused
	}
	
	public static int getNextClientMsgID() {
		return clientMsgID++;
	}

	// called by the Client to append text in the TextArea 
	public void append(String str) {	
		taChat.append(str + "\n");
		taChat.setCaretPosition(taChat.getText().length() - 1);
	}
	
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	public void connectionFailed() {
		btLogin.setEnabled(true);
		btLogout.setEnabled(false);
		lbUsernameOrMessage.setText("Enter your username below");
		tfUsernameOrMessage.setText("Anonymous");
		tfServerPort.setText("" + defaultPort);
		tfServerAddress.setText(defaultHost);
		tfServerAddress.setEditable(false);
		tfServerPort.setEditable(false);
		tfUsernameOrMessage.removeActionListener(this);
		connected = false;
	}
		
	public void actionPerformed(ActionEvent e) {
		Object eventSource = e.getSource();		
		if(eventSource == btLogout) {	// if I want to logout I let the server close the communication
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("username", clientUsername);
			jsonObj.put("is_client", new Boolean(true));
			jsonObj.put("message_type", JSONPacket.LOGOUT_STRING);
			String msgIDstr = "c" + ClientGUI.getNextClientMsgID();
			jsonObj.put("message_id", msgIDstr);
			String now = dateFormat.format(new Date());
			String[] nowParts = now.split(":");
			JSONObject dateObj = new JSONObject();
			dateObj.put("hour", Integer.parseInt(nowParts[0]));
			dateObj.put("minute", Integer.parseInt(nowParts[1]));
			dateObj.put("second", Integer.parseInt(nowParts[2]));
			jsonObj.put("message_time", dateObj);
							
			String myOutputMsg = "Me [" + now + "] < LOGOUT";
			append(myOutputMsg);
			
			JSONPacket jsonPacket = new JSONPacket(jsonObj.toString());
			
			tfUsernameOrMessage.setText("Anonymous");
			lbUsernameOrMessage.setText("Enter your username below");
			btLogin.setEnabled(true);
			btLogout.setEnabled(false);
			tfServerPort.setText("" + defaultPort);
			tfServerAddress.setText(defaultHost);
			tfServerAddress.setEditable(false);
			tfServerPort.setEditable(false);
			tfUsernameOrMessage.removeActionListener(this);
			
			client.stopClientWithMessage(jsonPacket);
			connected = false;
			client = null;
			
			return;
		}

		// ok it is coming from the JTextField
		if (connected) {
			String messageString = tfUsernameOrMessage.getText();
			if (messageString.trim().length() > 0) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("username", clientUsername);
				jsonObj.put("is_client", new Boolean(true));
				jsonObj.put("message_type", JSONPacket.MESSAGE_STRING);
				String msgIDstr = "c" + ClientGUI.getNextClientMsgID();
				jsonObj.put("message_id", msgIDstr);
				String now = dateFormat.format(new Date());
				String[] nowParts = now.split(":");
				JSONObject dateObj = new JSONObject();
				dateObj.put("hour", Integer.parseInt(nowParts[0]));
				dateObj.put("minute", Integer.parseInt(nowParts[1]));
				dateObj.put("second", Integer.parseInt(nowParts[2]));
				jsonObj.put("message_time", dateObj);
								
				String myOutputMsg = "Me [" + now + "] < " + messageString;
				append(myOutputMsg);
				
				byte[] dataString = messageString.getBytes();
				JSONPacket jsonPacket = new JSONPacket(jsonObj.toString(), dataString);
				client.sendMessage(jsonPacket);
								
				tfUsernameOrMessage.setText("");
			}
			return;
		}

		if(eventSource == btLogin) {	// I want the client to connect
			String username = tfUsernameOrMessage.getText().trim();		// take the username
			if(username.length() == 0)	// empty userame -> do nothing
				return;
			clientUsername = username;
			String serverAddress = tfServerAddress.getText().trim();	// empty address -> do nothing
			if(serverAddress.length() == 0)
				return;
			String portNumber = tfServerPort.getText().trim();
			if(portNumber.length() == 0)	// empty or invalid port -> do nothing
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}
			
			client = new Client(serverAddress, port, username, this);
			if(!client.startClient()) // let's test if the client can connect
				return;
						
			tfUsernameOrMessage.setText("");
			lbUsernameOrMessage.setText("Enter your message below");
			connected = true;
			
			btLogin.setEnabled(false);
			btLogout.setEnabled(true);
			tfServerAddress.setEditable(false);
			tfServerPort.setEditable(false);
			tfUsernameOrMessage.addActionListener(this);
		}
	}
}


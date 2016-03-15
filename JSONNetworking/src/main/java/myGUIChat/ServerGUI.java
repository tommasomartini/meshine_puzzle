package myGUIChat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
 
public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	
	private static int serverMsgID = 0;
	
	private JLabel lbMessage;
	private JTextField tfMessage;
	private JButton btStart;
	private JTextArea taChat, taEvent;
	private JTextField tfPortNumber;
	
	private int serverPort;
	
	private Server server;
	
	private Thread serverThread;
	private SimpleDateFormat dateFormat;
	
	public ServerGUI(int _port) {
		super("Chat Server");
		server = null;
		serverPort = _port;
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		
		JPanel north = new JPanel(new GridLayout(3, 1));
		// 1 North)
		JPanel portAndStartPanel = new JPanel();
		portAndStartPanel.add(new JLabel("Port number: "));
		tfPortNumber = new JTextField("  " + _port);
		btStart = new JButton("Start");
		btStart.addActionListener(this);
		portAndStartPanel.add(tfPortNumber);
		portAndStartPanel.add(btStart);
		north.add(portAndStartPanel);
		// 2 North)
		lbMessage = new JLabel("Enter your message below", SwingConstants.CENTER);
		north.add(lbMessage);
		// 3 North)
		tfMessage = new JTextField("Message");
		tfMessage.setBackground(Color.WHITE);
		tfMessage.setEditable(false); // cannnot write here until I am connected
		north.add(tfMessage);
		add(north, BorderLayout.NORTH);
		
		JPanel center = new JPanel(new GridLayout(2,1));
		taChat = new JTextArea(80,80);
		taChat.setEditable(false);
		appendChat("Chat room.");
		center.add(new JScrollPane(taChat));
		taEvent = new JTextArea(80,80);
		taEvent.setEditable(false);
		appendEvent("Events log.");
		center.add(new JScrollPane(taEvent));	
		add(center);
		
		addWindowListener(this);
		setSize(400, 600);
		setVisible(true);
	}		

	public void appendChat(String str) {
		taChat.append(str + '\n');
		taChat.setCaretPosition(taChat.getText().length() - 1);
	}
	
	public void appendEvent(String str) {
		taEvent.append(str + '\n');
		taEvent.setCaretPosition(taChat.getText().length() - 1);
	}
	
	public static int getNextServerMsgID() {
		return serverMsgID++;
	}
	
	public void notifyConnection(boolean isConnected) {
		tfMessage.setEditable(isConnected);
		tfMessage.addActionListener(this);
		tfMessage.setText("");
		tfMessage.requestFocus();
		tfPortNumber.setEditable(!isConnected);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object eventSource = e.getSource();
		if (eventSource == tfMessage) {
			String messageString = tfMessage.getText();
			if (messageString.trim().length() > 0) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("username", "YourServer");
				jsonObj.put("is_client", new Boolean(false));
				jsonObj.put("message_type", JSONPacket.MESSAGE_STRING);
				String msgIDstr = "s" + ServerGUI.getNextServerMsgID();
				jsonObj.put("message_id", msgIDstr);
				String now = dateFormat.format(new Date());
				String[] nowParts = now.split(":");
				JSONObject dateObj = new JSONObject();
				dateObj.put("hour", Integer.parseInt(nowParts[0]));
				dateObj.put("minute", Integer.parseInt(nowParts[1]));
				dateObj.put("second", Integer.parseInt(nowParts[2]));
				jsonObj.put("message_time", dateObj);

				String myOutputMsg = "Me [" + now + "] < " + messageString;
				appendChat(myOutputMsg);

				byte[] dataString = messageString.getBytes();
				JSONPacket jsonPacket = new JSONPacket(jsonObj.toString(), dataString);
				server.sendMessage(jsonPacket);

				tfMessage.setText("");
			}
		} else if (eventSource == btStart && server == null) {	// there is no server up. Let's tart one!
			try {
				serverPort = Integer.parseInt(tfPortNumber.getText().trim());
			}
			catch(Exception er) {
				appendEvent("Invalid port number");
				return;
			}

			btStart.setEnabled(false);		// the button is used to stop this server
			tfPortNumber.setEditable(false);
			server = new Server(serverPort, this);
			serverThread = new Thread(server);
			serverThread.start(); 	// I have to put the server into a thread, otherwise my app sticks waiting for a client	
		}
	}
	
	public void startNewServer() {
		serverThread.interrupt();	// actually it should be already be dead!
		server = new Server(serverPort, this);
		serverThread = new Thread(server);
		serverThread.start(); 
	}

	/*
	 * If the user click the X button to close the application
	 * I need to close the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		if (serverThread != null) {
			serverThread.interrupt();	// actually it should be already be dead!
			serverThread = null;
		}
//		if(server != null) {	// if my Server exist
//			try {
//				server.stop();			// ask the server to close the conection
//			} catch(Exception eClose) {}
//			server = null;
//		}
		dispose();	// dispose the frame
		System.exit(0);
	}
	// I can ignore the other WindowListener method
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}



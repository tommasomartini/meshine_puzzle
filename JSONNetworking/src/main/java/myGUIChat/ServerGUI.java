package myGUIChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.json.JSONObject;
 
public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	
	private JLabel lbMessage;
	private JTextField tfMessage;
	private JButton btStopStart;
	private JTextArea taChat, taEvent;
	private JTextField tfPortNumber;
	
	private Server server;
	
	private Thread serverThread;
	
	private boolean connected;
	
	public ServerGUI(int _port) {
		super("Chat Server");
		server = null;
		
		JPanel north = new JPanel(new GridLayout(3, 1));
		// 1 North)
		JPanel portAndStartPanel = new JPanel();
		portAndStartPanel.add(new JLabel("Port number: "));
		tfPortNumber = new JTextField("  " + _port);
		btStopStart = new JButton("Start");
		btStopStart.addActionListener(this);
		portAndStartPanel.add(tfPortNumber);
		portAndStartPanel.add(btStopStart);
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
		appendChat("Chat room.\n");
		center.add(new JScrollPane(taChat));
		taEvent = new JTextArea(80,80);
		taEvent.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(taEvent));	
		add(center);
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(400, 600);
		setVisible(true);
	}		

	// append message to the two JTextArea
	// position at the end
	public void appendChat(String str) {
		taChat.append(str);
		taChat.setCaretPosition(taChat.getText().length() - 1);
	}
	
	public void appendEvent(String str) {
		taEvent.append(str);
		taEvent.setCaretPosition(taChat.getText().length() - 1);
	}
	
	public void notifyConnection(boolean isConnected) {
		connected = isConnected;
		tfMessage.setEditable(isConnected);
		tfMessage.addActionListener(this);
		tfMessage.setText("");
		tfMessage.requestFocus();
		tfPortNumber.setEditable(!isConnected);
	}
	
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		Object eventSource = e.getSource();
		if (eventSource == tfMessage) {
			server.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tfMessage.getText()));
			tfMessage.setText("");
			return;
		} else if (eventSource == btStopStart && server != null) {	// there is a server running. MAYBE it is connected to a client
			server.stop();	// stop the server
			server = null;	// delete it
			tfPortNumber.setEditable(true);		// now I can set the port again
			btStopStart.setText("Start");	// the button is ready to make the server start
			return;
		} else if (eventSource == btStopStart && server == null) {	// there is no server up
			int port;
			try {
				port = Integer.parseInt(tfPortNumber.getText().trim());
			}
			catch(Exception er) {
				appendEvent("Invalid port number");
				return;
			}

			server = new Server(port, this);
			serverThread = new Thread(server);
			serverThread.start(); 	// I have to put the server into a thread, otherwise my app sticks waiting for a client
			btStopStart.setText("Stop");		// the button is used to stop this server
			tfPortNumber.setEditable(false);	
		}
	}

	/*
	 * If the user click the X button to close the application
	 * I need to close the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		if(server != null) {	// if my Server exist
			try {
				server.stop();			// ask the server to close the conection
			} catch(Exception eClose) {}
			server = null;
		}
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



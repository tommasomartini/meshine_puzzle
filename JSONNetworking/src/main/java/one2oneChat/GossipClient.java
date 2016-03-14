package one2oneChat;

import java.io.*;
import java.net.*;

public class GossipClient implements Runnable {
	private Socket socket;
	
	public GossipClient() {
		try {
			socket = new Socket("127.0.0.1", 3000);

			// receiving from server ( receiveRead  object)
			InputStream istream = socket.getInputStream();
			BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

			System.out.println("Start the chitchat, type and press Enter key");
			
			String receiveMessage;               
			while(true)
			{
				if((receiveMessage = receiveRead.readLine()) != null) //receive from server
				{
					System.out.println(receiveMessage); // displaying at DOS prompt
				}         
			} 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
		// sending to client (pwrite object)
		OutputStream ostream;
		try {
			ostream = socket.getOutputStream();

			PrintWriter pwrite = new PrintWriter(ostream, true);
			while (true) {
				//			String a1 = keyRead.readLine(); 
				//			String a2 = keyRead.readLine(); 
				//			String a3 = keyRead.readLine(); 
				//
				//			String mms = "a3: " + a3 + "\na1: " + a1 + "\na2: " + a2;
				//			System.out.println(mms);

				String sendMessage;        

				sendMessage = keyRead.readLine(); 
				System.out.print("- ");
				pwrite.println(sendMessage); 
				//    if((receiveMessage = receiveRead.readLine()) != null)  
				//    {
				//       System.out.println(receiveMessage);         
				//    }         
				sendMessage = keyRead.readLine(); 
				pwrite.println(sendMessage);             
				pwrite.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}    

	public static void main(String[] args) {
		new GossipClient();
	}
}       
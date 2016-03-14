package myGUIChat;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class GossipServer implements Runnable {

	private static Socket clientSocket;

	public GossipServer() throws Exception {

		//	  BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
		//	  System.out.print("Name: ");
		//	  String in_name = keyRead.readLine(); 
		//	  System.out.print("Surname: ");
		//	  String in_surname = keyRead.readLine(); 
		//	  System.out.print("Birthday (dd/mm/yyyy): ");
		//	  String in_birthday = keyRead.readLine(); 
		//	  System.out.print("Male (y or n): ");
		//	  String in_gender = keyRead.readLine(); 
		//	  
		//	  String[] dateParts = in_birthday.split("/");
		//	  int day = Integer.parseInt(dateParts[0]);
		//	  int month = Integer.parseInt(dateParts[1]);
		//	  int year = Integer.parseInt(dateParts[2]);
		//	
		//	 JSONObject jsonObj = new JSONObject();
		//
		//     jsonObj.put("name", in_name);
		//     jsonObj.put("surname", in_surname);
		//     if (in_gender.equals("y")) {
		//    	 jsonObj.put("genderMale", new Boolean(true));
		//	  } else if (in_gender.equals("n")) {
		//		  jsonObj.put("genderMale", new Boolean(false));
		//	  } else {
		//		  jsonObj.put("genderMale", JSONObject.NULL);
		//	  }
		//     
		//     JSONObject dateObj = new JSONObject();
		//     dateObj.put("day", day);
		//     dateObj.put("month", month);
		//     dateObj.put("year", year);
		//     
		//     jsonObj.put("birthday", dateObj);
		//
		//     System.out.println(jsonObj);


		ServerSocket serverSocket = new ServerSocket(3000);
		System.out.println("Server ready for chatting");
		clientSocket = serverSocket.accept( );      

		InputStream istream;
		try {
			istream = clientSocket.getInputStream();
			BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
			while (true) {
				System.out.println("Seeee");
				String receiveMessage;
				if((receiveMessage = receiveRead.readLine()) != null)  
				{
					System.out.println("< " + receiveMessage);         
				}  
				if (receiveMessage.equals("bye")) {
					break;
				}
			} 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		serverSocket.close();

	}

	public void run() {
		System.out.println("Server running");
		BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
		// sending to client (pwrite object)
		OutputStream ostream;
		try {
			ostream = clientSocket.getOutputStream();

			PrintWriter pwrite = new PrintWriter(ostream, true);
			while (true) {
//				String a1 = keyRead.readLine(); 
//				String a2 = keyRead.readLine(); 
//				String a3 = keyRead.readLine(); 
//
//				String mms = "a3: " + a3 + "\na1: " + a1 + "\na2: " + a2;
//				System.out.println(mms);

				String sendMessage;        

				System.out.print("- ");
				sendMessage = keyRead.readLine(); 
				
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
		try {
			GossipServer gossipServer = new GossipServer();
			Thread t = new Thread(gossipServer);
			t.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}                        
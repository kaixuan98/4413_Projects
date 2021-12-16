package microservices;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Geo extends Thread {
	
	public static PrintStream Log = System.out; 
	
	
	private Socket client; 
	
	public Geo(Socket client) {
		this.client = client;
	}
	
	
	public void run() {
		Log.printf("Connected to client %s:%d\n", client.getInetAddress(), client.getPort());

//		 anything that is in the try () they will force to close
	    try (Socket  client = this.client;
	      Scanner req = new Scanner(client.getInputStream());
	      PrintStream res = new PrintStream(client.getOutputStream(), true);)
	    {
		      String response;
		      String request = req.nextLine();
		      
	
		      if (request.matches("^[+-]?\\d+\\.*\\d+\\s[+-]?\\d+\\.*\\d+\\s[+-]?\\d+\\.*\\d+\\s[+-]?\\d+\\.*\\d+$") || request.matches("^[+-]?\\d+\\s[+-]?\\d+\\s[+-]?\\d+\\s[+-]?\\d+$")) {
		    	  //geo calc logic
			      String[] arrOfCoordinates = request.split(" ");
			      
			    	// get 2 coordinates and turn to radians
				      double t1 = (Float.parseFloat(arrOfCoordinates[0])) * (Math.PI/180);
				      double n1 = (Float.parseFloat(arrOfCoordinates[1])) * (Math.PI/180);
				      double t2 = (Float.parseFloat(arrOfCoordinates[2])) * (Math.PI/180);
				      double n2 = (Float.parseFloat(arrOfCoordinates[3])) * (Math.PI/180);
				     
				      double Y = Math.cos(t1) * Math.cos(t2);
				      double X = Math.pow(Math.sin((t2-t1)/2), 2) + Y * Math.pow(Math.sin((n2-n1)/2), 2); 
				      
				      double distance = 12742 * Math.atan2(Math.sqrt(X), Math.sqrt((1-X)));
				        
				      response = "" + distance; 
			    
			      
			      
		      } else {
		    	  response = "Don't understand: " + request;
		      }
		      
		      res.println(response);
		      
		      
	    }catch (Exception e) {
	    		Log.println(e);
	    }finally {
	    		Log.printf("Disconnected from %s:%d\n", client.getInetAddress(), client.getPort());
	    }
		
	}
	
	  public static void main(String[] args) throws Exception
	  {
	    int port = 0;

	    InetAddress host = InetAddress.getLocalHost(); // .getLoopbackAddress();

	    try (ServerSocket server = new ServerSocket(port, 0, host)) {
	      Log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());

	      while (true) {
	        Socket client = server.accept(); // server accept a thread( and return it as a client(socket))

	        (new Geo(client)).start(); // Geo is the server that takes client previosly accpeted as a client and start will invoke run
	      }
	    }
	  }

}

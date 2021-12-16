package microservices;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import g.Util;

public class Auth extends Thread{
	
	public static PrintStream Log = System.out; 
	
	
	private Socket client; 
	
	public Auth(Socket client) {
		this.client = client;
	}
	
	
	public void run() {
		
		Log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());

//		 anything that is in the try () they will force to close
	    try ( Socket  client = this.client;
	      Scanner req = new Scanner(client.getInputStream());
	      PrintStream res = new PrintStream(client.getOutputStream(), true);)
	    {
		      String response ="";
		      String request = req.nextLine();
		      String home = System.getProperty("user.home");
		      String url = "jdbc:sqlite:" + home + "/4413/pkg/sqlite/Models_R_US.db";
		      
		      String[] arrOfStr = request.split(" ", 2);
		      String username = arrOfStr[0];
		      String password = arrOfStr[1];
		      String inputHash;
		      
		      Log.printf(username + " " + password);
		      
		      
		      try(Connection connection = DriverManager.getConnection(url)){
//		    	  Log.printf("Connected to database: %s\n", connection.getMetaData().getURL());
		    	  String query = "SELECT name, salt, count, hash FROM client " + "WHERE name=?" ;
		    	  
		    	  try(PreparedStatement statement = connection.prepareStatement(query)){
		    		  statement.setString(1, username);
		    		  
		    		  try (ResultSet rs = statement.executeQuery()){
		    			  
		    			  if(rs.next() == false) {
		    				  response = "FAILURE";
		    			  }else {
		    				  do {
		    					  inputHash = Util.hash(password, rs.getString("salt"), rs.getInt("count"));
		    					  if (inputHash.equals(rs.getString("hash"))) {
		    						  response = "OK";
		    					  }else {
		    						  response = "FAILURE";
		    					  } 
		    					  
		    				  }while(rs.next());
		    			  }
		    			  
		    		  }
		    		  
		    		  
		    	  } 
		    	  
		      }catch(SQLException e) {
		    	  Log.println(e);
		      }finally {
		    	  Log.println("Disconnected from database.");
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
	        Socket client = server.accept();

	        (new Auth(client)).start();
	      }
	    }
	  }

}

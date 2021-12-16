package microservices;

import java.io.ByteArrayOutputStream;
//import java.io.ByteArrayOutputStream;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Marshaller;

import com.google.gson.Gson;

import microservices.modal.ProductBean;

public class Quote extends Thread {
	
	private static PrintStream log = System.out;  
	
	
	private Socket client; 
	
	public Quote(Socket client) {
		this.client = client;
	}
	
	
	public void run() {
		
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());

	    try ( Socket  client = this.client;
	      Scanner req = new Scanner(client.getInputStream());
	      PrintStream res = new PrintStream(client.getOutputStream(), true);)
	    {
		      String request = req.nextLine();
		      String url = "jdbc:derby://localhost:64413/EECS";
		      String response = "";
		      
		      String[] arr = request.split("\\s");
		      
		      
	
		    	  try(Connection connection = DriverManager.getConnection(url)){
		  			log.printf("Connected to database: %s\n", connection.getMetaData().getURL());
		  			String query = "SELECT id, name, msrp FROM hr.product " 
		  							+ "WHERE id= ? "; 
		  			
		  			try (PreparedStatement statement = connection.prepareStatement(query)){
		  				statement.setString(1, arr[0]);
		  				
		  				try(ResultSet rs = statement.executeQuery()){
		  					ProductBean bean = new ProductBean();
		  				
		  					
		  					if(rs.next() == false) {
		  						bean.setID(request + " not found");
		  						bean.setName("");
		  						bean.setPrice(0.0);
		  						
		  					}else {
		  						do{
		  							bean.setID(rs.getString("id"));
		  							bean.setName(rs.getString("name"));
		  							bean.setPrice(rs.getDouble("msrp"));
		  						}while(rs.next());
		  					};
		  					
		  					if(arr[1].equals("json")) {
		  						Gson gson = new Gson();
			  					log.println(gson.toJson(bean));
			  					response = gson.toJson(bean).toString();
		  					}else if (arr[1].equals("xml")) {
		  						try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			  						JAXBContext context = JAXBContext.newInstance(ProductBean.class);
			  						Marshaller m = context.createMarshaller();
			  						m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			  						m.marshal(bean, baos);
			  						response = baos.toString();
			  					}catch(Exception e) {
			  						log.println(e);
			  					}
		  						
		  					}else {
		  						response = "Does not support this format.\n";
		  					}	
		  				}
		  				
		  			}
		  			res.println(response);
		  			
		  			
		  		}catch(SQLException e) {
		  			log.println(e);
		  		}finally {
		  			log.println("Disconnected from Databases");
		  		}
	    }catch (Exception e) {
	    		log.println(e);
	    }finally {
	    		log.printf("Disconnected from %s:%d\n", client.getInetAddress(), client.getPort());
	    }
		
	}
		
	
	
	
	 public static void main(String[] args) throws Exception
	  {
	    int port = 0;

	    InetAddress host = InetAddress.getLocalHost(); // .getLoopbackAddress();

	    // create a server socket
	    try (ServerSocket server = new ServerSocket(port, 0, host)) {
	      log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());

	      while (true) {
	    	  // create a client socket and make the server accept the socket
	        Socket client = server.accept();
	        
	        // make the server take the client socket and invoke the run function in the class 
	        (new Quote(client)).start();
	      }
	    }
	  }


}

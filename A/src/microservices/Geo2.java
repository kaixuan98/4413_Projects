package microservices;

import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Geo2 extends Thread {
	
	private static PrintStream log = System.out; 
	
	private static boolean isHostAddress(String address) {
	    if (address.isEmpty()) {
	      return false;
	    }
	    try {
	      Object res = InetAddress.getByName(address);
	      return res instanceof Inet4Address || res instanceof Inet6Address;
	    } catch (final UnknownHostException exception) {
	      return false;
	    }
	  }

	  private static boolean isInteger(String integer) { return isInteger(integer, 10); }
	  private static boolean isInteger(String integer, int radix) {
	    try (Scanner sc = new Scanner(integer.trim())) {
	      if(!sc.hasNextInt(radix)) return false;
	      sc.nextInt(radix);
	      return !sc.hasNext();
	    }
	  }

	  private static boolean inPortRange(int port) {
	    return 0 <= port && port < 65536;
	  }

	  private static void abort(int exitCode, String message) {
	    log.println(message);
	    System.exit(exitCode);
	  }

	  private static void validateArgs(String[] args) {
	    String[] messages = {
	      "ERROR: Not enough arguments. Usage: <host> <port>. Expected 2 args, got %d\n",
	      "ERROR: Fail to parse host as an IP address or hostname, given '%s'.\n",
	      "ERROR: Fail to parse port as an integer, given '%s'.\n",
	      "ERROR: Port number out of range, expected integer between 0 and 65536, given %d.\n",
	    };

	    if (args.length < 2)         abort(1, String.format(messages[0], args.length));
	    if (!isHostAddress(args[0])) abort(2, String.format(messages[1], args[0]));
	    if (!isInteger(args[1]))     abort(3, String.format(messages[2], args[1]));

	    int port = Integer.parseInt(args[1]);
	    if (!inPortRange(port)) {
	      abort(4, String.format(messages[3], port));
	    }
	    return;
	  }
	  
	  // to prevent anyone come in and change the cookie
	  public synchronized int cookieCounter( String[] arrOfCoordinates) {
		  	mapOfCoor.put(counter, arrOfCoordinates);
	  		int geo2Response = counter ;
	  		counter ++; 
		  return geo2Response; 
	  }
	  

		private Socket client; 
		private PrintStream request; 
		private Scanner response; 
		private static ConcurrentHashMap<Integer, String[]> mapOfCoor = new ConcurrentHashMap<>();
		private static int counter = 0; 
		
		public Geo2(Socket client, PrintStream request , Scanner response) {
			this.client = client;
			this.request = request; 
			this.response = response; 
		}
	
		
	public void run() {

//		 anything that is in the try () they will force to close
		try (Socket s = this.client; // connect in telnet
		     Scanner req = new Scanner(client.getInputStream()); // input from CLI telnet 
		     PrintStream res = new PrintStream(client.getOutputStream(), true);) // show in CLI telnet
		{
			String geo2Response = "";
		    String geo2Request = req.nextLine();
		    
		    String[] arrOfCoordinates = geo2Request.split(" ");
		    
		    if(arrOfCoordinates.length == 2) {
		    	if (geo2Request.matches("^[+-]?\\d+\\.*\\d+\\s[+-]?\\d+\\.*\\d+$") || geo2Request.matches("^[+-]?\\d+\\s[+-]?\\d+$")) {
		    		int temp = cookieCounter(arrOfCoordinates);
		    		geo2Response =  "" + temp ;
		    		log.print("The response is: ");
			        log.println(geo2Response);
		    	}else {
		    		geo2Response = "Don't understand: " + geo2Request;
		    	}
		    	  
		      }else if(arrOfCoordinates.length == 3) {
		    	  if((arrOfCoordinates[0].matches("^[+-]?\\d+\\.*\\d+$") && arrOfCoordinates[1].matches("^[+-]?\\d+\\.*\\d+$") && arrOfCoordinates[2].matches("^\\d+$"))
		    			  || (arrOfCoordinates[0].matches("^[+-]?\\d+$") && arrOfCoordinates[1].matches("^[+-]?\\d+$") && arrOfCoordinates[2].matches("^\\d+$"))) { 
		    		  Integer cookie = Integer.parseInt(arrOfCoordinates[2]);
		    		  if(mapOfCoor.containsKey(cookie)) {
		    			  String t1 = mapOfCoor.get(cookie)[0];
		    			  String n1 = mapOfCoor.get(cookie)[1];
		    			  String t2 = arrOfCoordinates[0];
		    			  String n2 = arrOfCoordinates[1];
		    			  geo2Request = "" + t1 +" "+ n1 +" "+ t2 +" "+ n2 ; 
		    			  log.printf("Connected to Geo server %s:%d\n", client.getInetAddress(), client.getPort()); 
		    			  request.println(geo2Request);
				    	  String serviceRes = response.nextLine();
				    	  log.print("The response is: ");
				          log.println(serviceRes);
				          geo2Response = "" + serviceRes;
		    		  }else {
		    			  geo2Response = "Wrong Cookie";
		    		  }
		    	  }else {
		    		  geo2Response = "Don't understand: " + geo2Request;
		    	  }
		    		  
		      }else if(arrOfCoordinates.length == 4){
		    	  request.println(geo2Request);
		    	  String serviceRes = response.nextLine(); 
		    	  log.print("The response is: ");
		          log.println(serviceRes);
		          geo2Response = "" + serviceRes;
		    	  
		      }else {
		    	  geo2Response = "Don't understand: " + geo2Request;
		      }
	    	
		    
		    res.println(geo2Response); // print this in CLI 
	    }catch (Exception e) {
	    		log.println(e);
	    }finally {
	    		log.printf("Disconnected from %s:%d\n", client.getInetAddress(), client.getPort());
	    }
		
	}
		
	
	
	
	 public static void main(String[] args) throws Exception
	  { 
		 
		 validateArgs(args);
		 int port = 0;
		 InetAddress host = InetAddress.getLocalHost();
		 

		    try (
		      Socket client   = new Socket(args[0], Integer.parseInt(args[1])); // this is the geo client socket
		      PrintStream req = new PrintStream(client.getOutputStream(), true); // this will be pass in by the Geo server as a request
		      Scanner res     = new Scanner(client.getInputStream()); // this will be the output from the geo Server(the response)
		      Scanner in      = new Scanner(System.in);
		      ServerSocket server = new ServerSocket(port, 0, host);
		    ) {
		      log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
		      
		      // this is where the telnet will run - will only run 2 times
		      while (true) {
		        Socket geo2Client = server.accept();
		        (new Geo2(geo2Client, req , res)).start(); 
		      }
		     
		    } catch (Exception e) {
		      log.println(e);
		    } finally {
		      log.println("Client connection closed.");
		    }
	  }


}


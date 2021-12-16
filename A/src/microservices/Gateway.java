package microservices;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Gateway extends Thread {
	
	private static final PrintStream log = System.out;
	private static final Map<Integer, String> httpResponseCodes = new HashMap<>();
	static {
	    httpResponseCodes.put(100, "HTTP CONTINUE");
	    httpResponseCodes.put(101, "SWITCHING PROTOCOLS");
	    httpResponseCodes.put(200, "OK");
	    httpResponseCodes.put(201, "CREATED");
	    httpResponseCodes.put(202, "ACCEPTED");
	    httpResponseCodes.put(203, "NON AUTHORITATIVE INFORMATION");
	    httpResponseCodes.put(204, "NO CONTENT");
	    httpResponseCodes.put(205, "RESET CONTENT");
	    httpResponseCodes.put(206, "PARTIAL CONTENT");
	    httpResponseCodes.put(300, "MULTIPLE CHOICES");
	    httpResponseCodes.put(301, "MOVED PERMANENTLY");
	    httpResponseCodes.put(302, "MOVED TEMPORARILY");
	    httpResponseCodes.put(303, "SEE OTHER");
	    httpResponseCodes.put(304, "NOT MODIFIED");
	    httpResponseCodes.put(305, "USE PROXY");
	    httpResponseCodes.put(400, "BAD REQUEST");
	    httpResponseCodes.put(401, "UNAUTHORIZED");
	    httpResponseCodes.put(402, "PAYMENT REQUIRED");
	    httpResponseCodes.put(403, "FORBIDDEN");
	    httpResponseCodes.put(404, "NOT FOUND");
	    httpResponseCodes.put(405, "METHOD NOT ALLOWED");
	    httpResponseCodes.put(406, "NOT ACCEPTABLE");
	    httpResponseCodes.put(407, "PROXY AUTHENTICATION REQUIRED");
	    httpResponseCodes.put(408, "REQUEST TIME OUT");
	    httpResponseCodes.put(409, "CONFLICT");
	    httpResponseCodes.put(410, "GONE");
	    httpResponseCodes.put(411, "LENGTH REQUIRED");
	    httpResponseCodes.put(412, "PRECONDITION FAILED");
	    httpResponseCodes.put(413, "REQUEST ENTITY TOO LARGE");
	    httpResponseCodes.put(414, "REQUEST URI TOO LARGE");
	    httpResponseCodes.put(415, "UNSUPPORTED MEDIA TYPE");
	    httpResponseCodes.put(500, "INTERNAL SERVER ERROR");
	    httpResponseCodes.put(501, "NOT IMPLEMENTED");
	    httpResponseCodes.put(502, "BAD GATEWAY");
	    httpResponseCodes.put(503, "SERVICE UNAVAILABLE");
	    httpResponseCodes.put(504, "GATEWAY TIME OUT");
	    httpResponseCodes.put(505, "HTTP VERSION NOT SUPPORTED");
	}
	private Socket client;
	private PrintStream geoRequest; 
	private Scanner geoResponse; 
	private PrintStream quoteRequest; 
	private Scanner quoteResponse; 
	private PrintStream authRequest; 
	private Scanner authResponse; 
	private Socket geoClient;
	private Socket quoteClient;
	private Socket authClient;
	
	private Gateway(Socket client, PrintStream geoRequest, Scanner geoResponse,PrintStream quoteRequest, Scanner quoteResponse,PrintStream authRequest, Scanner authResponse, 
			Socket geoClient, Socket quoteClient, Socket authClient ) {
		this.client = client;
		this.geoRequest = geoRequest; 
		this.geoResponse = geoResponse;
		this.quoteRequest = quoteRequest;
		this.quoteResponse = quoteResponse;
		this.authRequest = authRequest;
		this.authResponse = authResponse;
		this.geoClient = geoClient;
		this.quoteClient = quoteClient;
		this.authClient = authClient;
		
	}
	
	
	private void sendHeaders(PrintStream res, int code, String contentType, String response) {
	    sendHeaders(res, code, contentType, response, new String[]{});
	  }
	  private void sendHeaders(PrintStream res, int code, String contentType, String response, String[] headers) {
	    // send HTTP Headers
	    res.printf("HTTP/1.1 %d %s\n", code, httpResponseCodes.get(code));
	    res.println("Content-type: " + contentType);
	    Arrays.stream(headers).forEach(h -> res.println(h));
	    res.println(); // blank line between headers and content, very important !
	  }
	  
	  // this function takes in a resource path and return what is the resources(example: Geo, Quote, Auth)
//	  private String[] getComponents(String resourcePath) {
//		    if (!resourcePath.contains("?")) {
//		      return new String[]{ resourcePath, "" };
//		    } else {
//		      return resourcePath.split("\\?", 2);
//		    }
//		  }
		  
	  // input : p1=v1&p2=v2...
	  // output: array of the fields
	  private Map<String, String> getQueryStrings(String qs) throws Exception {
	    Map<String, String> queries = new HashMap<>();
	    String[] fields = qs.split("&");
	    
	    for (String field : fields) {
	      String[] pairs = field.split("=", 2);
	      if (pairs.length == 2) {
	        queries.put(pairs[0], URLDecoder.decode(pairs[1], "UTF-8"));
	      }
	    }

	    return queries;
	  }
	
	
	public void run() {
		final String clientAddress = String.format("%s:%d", client.getInetAddress(), client.getPort());
		log.printf("Connected to Gateway at %s\n", clientAddress);
		
		try(Socket client = this.client ; Scanner req = new Scanner(client.getInputStream()); PrintStream res = new PrintStream(client.getOutputStream(),true)){
			String request = req.nextLine();
			String method, resource, version;
			String response = "";
			
			try(Scanner parse = new Scanner (request)){
				method = parse.next();
				resource = parse.next();
				version = parse.next();
			}
			
			int status = 200 ; 
			
			try {
				if(!method.equals("GET")) {
					// this is not a get request
					status = 501;
				}else if(!version.equals("HTTP/1.1")) {
					// the version is not correct
					status = 505; 
				}else if(resource.startsWith("/Geo?") || resource.startsWith("/Quote?") || resource.startsWith("/Auth?") ) {
					Map<String, String> qs = getQueryStrings(resource.substring(resource.indexOf("?") + 1 ));
					
					// connect to geo (take 4 paramenters)
					if (resource.startsWith("/Geo?")) {
						log.printf("Connected to Geo server at %s:%d\n", geoClient.getInetAddress(), geoClient.getPort()); 
						if (qs.containsKey("t1") && qs.containsKey("n1") && qs.containsKey("t2") && qs.containsKey("n2")){
							String info = qs.get("t1") +" "+ qs.get("n1")+ " " + qs.get("t2") + " "+qs.get("n2");
							geoRequest.println(info);
							String serviceRes = geoResponse.nextLine();
							response = "" + serviceRes;
							
						}else {
							status = 400; 
						}
						log.printf("Disconnected from %s:%d\n", geoClient.getInetAddress(), geoClient.getPort());
						
					}
					
					
					// connect to quote 
					if(resource.startsWith("/Quote?")) {
						log.printf("Connected to Quote server at %s:%d\n", quoteClient.getInetAddress(), quoteClient.getPort());
						if(qs.containsKey("id") && qs.containsKey("format")) {
							String productid = qs.get("id");
							String format = qs.get("format");
							quoteRequest.println(productid + " " + format);
							if(format.equals("json")) {
								String serviceRes = quoteResponse.nextLine();
								response = "" + serviceRes;
							}else if (format.equals("xml")) {
								while(quoteResponse.hasNext()) {
									response = response + quoteResponse.nextLine() + "\n";
								}
							}
							
						}else {
							status = 400; 
						}
						log.printf("Disconnected from %s:%d\n", quoteClient.getInetAddress(), quoteClient.getPort());
					}
					
					
					// connect to auth (take 2 parameters)
					if(resource.startsWith("/Auth?")) {
						 log.printf("Connected to Auth server at %s:%d\n", authClient.getInetAddress(), authClient.getPort());
						if(qs.containsKey("name") && qs.containsKey("password")) {
							String info = qs.get("name") +" "+ qs.get("password");
							authRequest.println(info);
							String serviceRes = authResponse.nextLine();
							response = "" + serviceRes;
						}else {
							status = 400; 
						}
						log.printf("Disconnected from %s:%d\n", authClient.getInetAddress(), authClient.getPort());
					}
					
					
					
				}else {
					status = 404; 
				}
				
			}catch(Exception e) {
				log.println(e);
				e.printStackTrace(log);
				status = 500; 
			}
			
			if (status != 200) {
				response = httpResponseCodes.get(status);
			}
			
		    sendHeaders(res, status, "text/plain", response);

		    res.println(response);
		    res.flush(); // flush character output stream buffer
			
			
		}catch(Exception e) {
			log.println(e);	
		}finally {
			log.printf("Disconnected from %s\n", clientAddress);
		}
		
	}
	
	
	// this is what a user would create
	 public static void main(String[] args) throws Exception {
	   
		 int port = 0;
		 InetAddress host = InetAddress.getLocalHost();
		 

		    try (
		      Socket geoClient   = new Socket(args[0], Integer.parseInt(args[1]));
		      Socket quoteClient   = new Socket(args[2], Integer.parseInt(args[3])); 
		      Socket authClient   = new Socket(args[4], Integer.parseInt(args[5])); 
		      PrintStream geoReq = new PrintStream(geoClient.getOutputStream(), true); 
		      Scanner geoRes     = new Scanner(geoClient.getInputStream()); 
    		  PrintStream quoteReq = new PrintStream(quoteClient.getOutputStream(), true); 
		      Scanner quoteRes     = new Scanner(quoteClient.getInputStream());
		      PrintStream authReq = new PrintStream(authClient.getOutputStream(), true); 
		      Scanner authRes     = new Scanner(authClient.getInputStream());
		      ServerSocket server = new ServerSocket(port, 0, host);
		    ) {
		      log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
		      
		      // this is where the telnet will run - will only run 2 times
		      while (true) {
		        Socket gatewayClient = server.accept();
		        (new Gateway(gatewayClient, geoReq, geoRes, quoteReq, quoteRes, authReq, authRes , geoClient, quoteClient, authClient)).start(); 
		      }
		      
		    } catch (Exception e) {
		      log.println(e);
		    } finally {
		      log.println("Client connection closed.");
		    }
		 
	}

}

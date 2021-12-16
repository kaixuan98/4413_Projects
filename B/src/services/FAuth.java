package services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "FAuth", urlPatterns = {"/FAuth"})
public class FAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static PrintStream log = System.out; 
       
    public FAuth() {
        super();
        
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		  
		PrintStream out = new PrintStream(response.getOutputStream(), true);
		Map<String, String[]> params = request.getParameterMap();
		
		 if (!params.containsKey("username") && !params.containsKey("password")) {
			 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			 return;
		}
		 
		 String username = request.getParameter("username");
		 String password = request.getParameter("password");
		 
		 // getting the address and port from web.xml
		 String authAddress = getInitParameter("authAddress");
		 String authPort = getInitParameter("authPort");
		 
		 // connected with Auth server 
		 // create a new fauth socket to connect with auth
		 try(Socket authService = new Socket(authAddress, Integer.parseInt(authPort));
			 PrintStream req = new PrintStream(authService.getOutputStream(),true);
			 Scanner res = new Scanner(authService.getInputStream());){
			 log.printf("Connected to Auth %s:%d\n", authService.getInetAddress(), authService.getPort());
			 
			// send the username and password to TCP server 
			 String TCPRequest = username + " " + password;
			 req.printf("%s\n", TCPRequest);
			 String status = res.nextLine();
			 
			// get the output from tcp server and print it on browser 
			 out.print(status);
			 
		 }catch(Exception e) {
			 log.println(e);
		 }finally {
			 log.printf("Disconnected from Auth %s:%d\n", authAddress, Integer.parseInt(authPort) );
		 }
		 
		 
		 
		 
		 
		
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	


}

package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Engine;


@WebServlet(name = "Drone", urlPatterns = {"/drone"})
public class Drone extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
    public Drone() {
        super();
        
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.setContentType("text/plain");
		response.setContentType("application/json");
		PrintWriter res = response.getWriter(); // send character text to the client
		
		// get two parameters : source and destination 
		Map<String, String[]> params = request.getParameterMap();
		
		 if (!params.containsKey("source") && !params.containsKey("destination")) {
			 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			 return;
		}
		 
		 String source = request.getParameter("source");
		 source = URLEncoder.encode(source, "UTF-8");
		 String destination = request.getParameter("destination");
		 destination = URLEncoder.encode(destination, "UTF-8");
		 
		 try {
			 // get the model 
			 Engine engine = Engine.getInstance();
			 String APIkey = getServletContext().getInitParameter("MapQuestAPIKey");
			 
			 // find the coordinates for the source and destination
			 String sourceCoordinate = engine.search(source, APIkey); 
			 String destinationCoordinate = engine.search(destination, APIkey); 
			 
			 // converting string to json 
			 JsonParser parser = new JsonParser();  
			 JsonObject sourceJson = (JsonObject) parser.parse(sourceCoordinate);  
			 JsonObject destinationJson = (JsonObject) parser.parse(destinationCoordinate); 
			 
			 // set the points 
			 engine.setX1(sourceJson.get("lat").toString());
			 engine.setY1(sourceJson.get("lng").toString());
			 engine.setX2(destinationJson.get("lat").toString());
			 engine.setY2(destinationJson.get("lng").toString());
			 
			 // find the distance between two coordinates
			if(response.isCommitted()) return; // check for the sessions
			String geoWebAddress = getInitParameter("geoWebAddress");
			String geoWebPort = getInitParameter("geoWebPort");
			@SuppressWarnings("unused")
			Double distance = engine.getDistance(geoWebAddress, geoWebPort); 
			Double time = engine.estimateTime();
//			res.printf("The estimated delivery time is: %.2f minutes", time );
			res.print(time);
			 
		 }catch(Exception e ) {
			 System.out.println(e);
		 }
		
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

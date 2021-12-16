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

import model.Engine;


@WebServlet(name = "Loc", urlPatterns = {"/loc"})
public class Loc extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public Loc() {
        super();
       
    }

    // getting information from the server
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get the location from request 
		response.setContentType("application/json");
		PrintWriter res = response.getWriter(); // send character text to the client
		Map<String, String[]> params = request.getParameterMap();
		
		 if (!params.containsKey("location")) {
			 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			 return;
		}
		 
		 String location = request.getParameter("location");
		 location = URLEncoder.encode(location, "UTF-8");
		
		
		// search with MapQuestAPI (pass in the location, and return the JSON lat and long)
		 Engine engine = Engine.getInstance();
		 String APIkey = getServletContext().getInitParameter("MapQuestAPIKey");
		 String coordinates = engine.search(location, APIkey); // getting the string of json
		 res.print(coordinates);
		
	}

	// sending information to the server
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}

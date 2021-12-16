package services;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Engine;

@WebServlet(name = "GeoWeb", urlPatterns = {"/GeoWeb"})
public class GeoWeb extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GeoWeb() {
        super();
    }

	// getting information from the server
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// response : will be printed to the servlet
		response.setContentType("text/plain");
		PrintWriter res = response.getWriter(); // send character text to the client
				
		if(response.isCommitted()) return; // check for the sessions
		String geoWebAddress = getInitParameter("geoWebAddress");
		String geoWebPort = getInitParameter("geoWebPort");
		
		HttpSession session = request.getSession(true);
		String lat1; 
		String lng1; 
		String lat2 = "";
		String lng2 = "";
		
		Engine engine = Engine.getInstance();
		/*
		 * We are reusing lat and lng as for the parameter.
		 * We need to first check is there any parameter lat and lng. 
		 * if there is lat and lng, we can save both number on lat2 and lng2 and keep the session empty for now
		 * then we check is lat1 and lat2 is empty, send recieved and set the session lat and lng with lat2 and lng2 
		 * also need to set the engine for the calculation 
		 * 
		 * if there is something in the session, we can move forward to calculate the distance
		 * */
		
		if(request.getParameter("lat") != null && request.getParameter("lng") != null) {
			lat1 = (String)session.getAttribute("lat");
			lng1 = (String)session.getAttribute("lng");
			lat2 = request.getParameter("lat");
			lng2 = request.getParameter("lng");
			engine.setX2(lat2);
			engine.setY2(lng2);
			
			if(lat1 == null || lng2 == null) {
				res.print("RECEIVED");
				session.setAttribute("lat", lat2);
				session.setAttribute("lng", lng2);
				engine.setX1(lat2);
				engine.setY1(lng2);
			}else {
				Double distance = engine.getDistance(geoWebAddress, geoWebPort);
				res.printf("The distance from (%s, %s) to (%s, %s) is: %.2f km\n", lat1, lng1, lat2, lng2, distance);
			}
		}else {
			res.print("Bad Request");
		}
		res.println();
	}

	
	// sending information to the server
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	

}

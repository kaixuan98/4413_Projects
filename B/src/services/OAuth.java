package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "OAuth", urlPatterns = {"/OAuth"})
public class OAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public OAuth() {
        super();
        
    }


    // get information from the server
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter res = response.getWriter(); // send character text to the client
		Map<String, String[]> params = request.getParameterMap();
		
		 if (params.containsKey("user") && params.containsKey("name")) {
			 String name = request.getParameter("name");
			 String user = request.getParameter("user");
			 res.printf("Hello, %s. You are logged in as %s.\n", name, user);
			 return;
		}else {
			response.setStatus(301);
			String backUrl = "http://localhost:4413/B/OAuth";
			String location = "https://www.eecs.yorku.ca/~roumani/servers/auth/oauth.cgi?back=" + backUrl;
			response.sendRedirect(location);
		}
	}


	// post information to the server
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

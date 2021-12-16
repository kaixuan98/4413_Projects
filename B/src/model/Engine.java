package model;

import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Engine {
	
	private static Engine singleton = null; 
	private static final double DRONESPEED = 150 ; 
	private static PrintStream log = System.out;
	private static double distance; 
	private static double time;
	private String x1 = "";
	private String y1 = "";
	private String x2 = "";
	private String y2 = ""; 

	
	private Engine() {
		
	}
	
	public void setX1(String x1) {
		this.x1 = x1; 
	}
	public void setX2(String x2) {
		this.x2 = x2; 
	}
	public void setY1(String y1) {
		this.y1 = y1;
	}
	public void setY2(String y2) {
		this.y2 = y2; 
	}
	
	// get the distance from Geo service
	public double getDistance(String address, String port) {
		
		String geoWebAddress = address;
		String geoWebPort = port;
		try(Socket geoService = new Socket(geoWebAddress, Integer.parseInt(geoWebPort));
			 PrintStream req = new PrintStream(geoService.getOutputStream(),true);
			 Scanner response = new Scanner(geoService.getInputStream());){
			 log.printf("Connected to Geo %s:%d\n", geoService.getInetAddress(),geoService.getPort());
			 
			// send the 4 coordinates to Geo server 
			 String GeoRequest = x1 + " " + y1 + " " + x2 + " " + y2;
			 req.printf("%s\n", GeoRequest);
			 String status = response.nextLine();
			 Double result = Double.parseDouble(status);
			 distance = result; 
				 
		 }catch(Exception e) {
			 log.println(e);
		 }finally {
			 log.printf("Disconnected from Geo %s:%d\n", geoWebAddress, Integer.parseInt(geoWebPort) );
		 }
		return distance; 
				
	}
	
	// search for coordinates 
	public String search(String location, String APIkey) {
		
		JsonObject coordinates = new JsonObject(); 
		
		try {
			URL url = new URL("http://www.mapquestapi.com/geocoding/v1/address?key=" + APIkey + "&location=" + location);
			@SuppressWarnings("resource")
			Scanner http = new Scanner(url.openStream());
			String payload = "";
			while(http.hasNextLine()) {payload += http.nextLine();}
			
			JsonParser parser = new JsonParser();
			JsonArray resultsArr = parser.parse(payload).getAsJsonObject().getAsJsonArray("results");
			JsonObject results = (JsonObject) resultsArr.get(0);
			JsonArray locationArr  = results.getAsJsonObject().getAsJsonArray("locations");
			
			if(locationArr.size()  > 0 ) {
				JsonObject locations = (JsonObject) locationArr.get(0);
				coordinates = locations.getAsJsonObject("latLng");
			}else {
				coordinates.add("lat", null);
				coordinates.add("lng", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (coordinates.toString()); 
	}
	
	// calculate the estimate time in minutes
	public double estimateTime() {
		time = distance / DRONESPEED ; 
		Double timeinMin = time * 60; 
		return timeinMin; 
	}

	
	public static Engine getInstance() {
		if(singleton == null) {
			singleton = new Engine();
		}
		return singleton; 
	}

}

package model;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DroneEngine {
	
	private static DroneEngine singleton = null; 
	private static final double DRONESPEED = 150 ; 
	private static final String ROOTURL = "http://localhost:4413/B/";
	
	private DroneEngine() {
		
	}
	
	// loc request: get the location 
	// input : address 
	// output: the coordinates 
	public String getLoc(String address) {
		
		String coordinates = ""; 
		
		try {
			String location = URLEncoder.encode(address, "UTF-8");
			String locStringUrl = ROOTURL + "loc?location=" + location;
			URL locUrl = new URL(locStringUrl);
			@SuppressWarnings("resource")
			Scanner http = new Scanner(locUrl.openStream());
			String payload = "";
			while(http.hasNextLine()) {payload += http.nextLine();}
			
			JsonParser parser = new JsonParser();
			JsonObject lat = (JsonObject) parser.parse(payload).getAsJsonObject().get("lat");
			JsonObject lng = (JsonObject) parser.parse(payload).getAsJsonObject().get("lng");
			
			coordinates = lat.toString() + "," + lng.toString(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return coordinates; 
	}
	
	// geoweb request: get the distance
	// input: a coordinate
	// output: the distance in km 
	public Double getDistance(String x1, String y1, String x2, String y2) {
		String firstRequest = ROOTURL + "GeoWeb?x1=" + x1 + "&y1="+ y1;
		String secondRequest =  ROOTURL + "GeoWeb?x2=" + x2 + "&y2="+ y2;
		
		try {
			
			URL firstUrl = new URL(firstRequest);
			@SuppressWarnings("resource")
			Scanner http = new Scanner(firstUrl.openStream());
			String payload1 = "";
			while(http.hasNextLine()) {payload1 += http.nextLine();}
			System.out.print(payload1); 
			
			if(payload1.equals("RECIEVED")) {
				URL secondUrl = new URL(secondRequest);
				@SuppressWarnings("resource")
				Scanner http2 = new Scanner(secondUrl.openStream());
				String payload2 = "";
				while(http.hasNextLine()) {payload2 += http2.nextLine();}
				
				System.out.print(payload2);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Double distance = 0.0 ; 
		return distance;
	}
	
	// computation: calculate the time
	// input: distance 
	// return: estimated time(double) 
	public Double estimateTime (String distance) {
		return 0.0; 
	}
	
	// create only one model object, and all the computation will be done in this class
	public static DroneEngine getInstance() {
		if(singleton == null) {
			singleton = new DroneEngine();
		}
		return singleton; 
	}
	
}

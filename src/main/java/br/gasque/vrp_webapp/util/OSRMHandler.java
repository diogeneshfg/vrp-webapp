package br.gasque.vrp_webapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import br.gasque.vrp_webapp.entity.Client;
import br.gasque.vrp_webapp.entity.ClientRoute;
import br.gasque.vrp_webapp.entity.Route;

public class OSRMHandler implements Serializable{
	
	
	private static final long serialVersionUID = 5792834985547882537L;
	
	public static final String URL = "http://localhost:5000/table/v1/driving/";
	public static final String ROUTE_URL = "http://localhost:5000/route/v1/driving/";
	
	private double[][] distancesMatrix;
	private double[][] timeTravelMatrix;
	
	
	/**
	 * Build the URL string that would be send  to OSRM C++ server to retrieve distances.
	 * @param mainUrl
	 * @param clients
	 * @return a URL string
	 */
	private String buildRequestStringParameters(String mainUrl, List<Client> clients, boolean isRoute) {
		
		StringBuilder sb = new StringBuilder(mainUrl);
		int size = clients.size();
		for(int i=0; i<size; ++i) {
			Client c = clients.get(i);
			sb.append(c.getLocation().getLon());
			sb.append(",");
			sb.append(c.getLocation().getLat());
			if(i < size-1) {
				sb.append(";");
			}
		}
		if(!isRoute) {
			sb.append("?annotations=duration,distance");
		}else {
			sb.append("?overview=full&geometries=polyline");
		}
		System.out.println(sb.toString());
		return sb.toString();		
	}
	
	/**
	 * Method responsible for establish HTTP connection with OSRM C++ Server and get JSON response containing the distance
	 * between all sources and destinations.
	 * @param clients source and destination list to retrieve distances. 
	 * @param URLpath formed URL to get access to OSRM server.
	 * @return distances 2-D double matrix filled with distances or empty in case of no distances had been available.
	 */
	public void fillDistanceAndTimeTravelMatrix(List<Client> clients, String URLpath) {
				
		int n = clients.size();
		this.distancesMatrix = new double[n][n];
		this.timeTravelMatrix = new double[n][n];
		
		String pathURL = buildRequestStringParameters(URLpath, clients, false);
		
		URI uri;
		CloseableHttpClient osrmHttpClient;
		HttpGet httpget;
		CloseableHttpResponse response = null;
		HttpEntity entity;
		try {
			uri = new URIBuilder(pathURL).build();			
			osrmHttpClient = HttpClients.createDefault();
			httpget = new HttpGet(uri);
			try {
				response = osrmHttpClient.execute(httpget);
				entity = response.getEntity();
				
				InputStream ios = entity.getContent();
				String input = convertInputStreamToString(ios);
				//System.out.println(input);										

				JSONObject json = new JSONObject(input);
				
				//Get distances from JSON response.
				JSONArray distancesArray = json.getJSONArray("distances");
				
				for(int i=0; i<distancesArray.length(); ++i) {
					JSONArray srcDestArray = distancesArray.getJSONArray(i);
					
					for(int j=0; j<srcDestArray.length(); ++j) {	
						distancesMatrix[i][j] = srcDestArray.getDouble(j);
					}
				}
				
				//Get time travel from JSON response
				JSONArray timeTravelArray = json.getJSONArray("durations");
				
				for(int i=0; i<timeTravelArray.length(); ++i) {
					JSONArray srcDestArray = timeTravelArray.getJSONArray(i);
					
					for(int j=0; j<srcDestArray.length(); ++j) {	
						BigDecimal time = BigDecimal.valueOf(srcDestArray.getDouble(j)/60);
						time = time.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						timeTravelMatrix[i][j] = time.doubleValue();
					}
				}
						
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public String getRoutePath(Route route) {
		
		List<Client> clients = new ArrayList<>();
		for(ClientRoute cr : route.getClients()) {
			clients.add(cr.getClient());
		}
		
		String path = null;
		String pathURL = buildRequestStringParameters(OSRMHandler.ROUTE_URL, clients, true);
		
		URI uri;
		CloseableHttpClient osrmHttpClient;
		HttpGet httpget;
		CloseableHttpResponse response = null;
		HttpEntity entity;
		try {
			uri = new URIBuilder(pathURL).build();			
			osrmHttpClient = HttpClients.createDefault();
			httpget = new HttpGet(uri);
			try {
				response = osrmHttpClient.execute(httpget);
				entity = response.getEntity();
				
				InputStream ios = entity.getContent();
				String input = convertInputStreamToString(ios);
				
				JSONObject json = new JSONObject(input);
				
				//Get POLYLINE				
				JSONArray routesArray = json.getJSONArray("routes");				
				
				JSONObject jsonObj = routesArray.getJSONObject(0);
				path = jsonObj.getString("geometry");
				
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if(response !=null) {
						response.close();
					}										
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	/**
	 * Convert a InputStream into a String to be supplied to JSON
	 * @param ios representing a InputStream
	 * @return a String converted to UTF-8 format
	 */
	private String convertInputStreamToString(InputStream ios) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ios));
		
		StringBuilder sbr = new StringBuilder();
		String line = null;		
		try {
			while((line=reader.readLine())!=null) {
				sbr.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				ios.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String res = sbr.toString();
		try {
			res = new String(res.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public double[][] getDistances(){
		return this.distancesMatrix;
	}
	
	public double[][] getTimeTravel(){
		return this.timeTravelMatrix;
	}
}

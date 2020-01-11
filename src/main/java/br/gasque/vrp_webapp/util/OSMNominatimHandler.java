package br.gasque.vrp_webapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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

import br.gasque.vrp_webapp.entity.Location;

public class OSMNominatimHandler implements Serializable{
	
	private static final long serialVersionUID = -1519361699733808905L;
	
	private final String SCHEME = "https";
	private final String HOST = "nominatim.openstreetmap.org";
	private final String PATH = "/search";
	private final String PATH_REVERSE = "/reverse";
	
	private List<Location> locationsFound;
	
	public OSMNominatimHandler() {
		
	}
	
	
	public void searchReverseAddress(String latitude, String longitude) throws Exception {
		locationsFound = new ArrayList<>();
		
		URI uri;
		CloseableHttpClient osmHttpClient;
		HttpGet httpget;
		CloseableHttpResponse response = null;
		HttpEntity entity;
		
		try {
			uri = new URIBuilder()
					.setScheme(this.SCHEME)
					.setHost(this.HOST)
					.setPath(this.PATH_REVERSE)
					.setParameter("format", "json")
					.setParameter("lat", latitude)
					.setParameter("lon", longitude).build();
			
			osmHttpClient = HttpClients.createDefault();
			httpget = new HttpGet(uri);
			try {
				response = osmHttpClient.execute(httpget);
				entity = response.getEntity();
				if(entity != null) {
					InputStream ios = entity.getContent();
					String input = convertInputStreamToString(ios);
					System.out.println(input);	
					
					if(input.startsWith("[")) {
						JSONArray json = new JSONArray(input);
						for(int i=0; i<json.length(); ++i) {
							JSONObject ads = json.getJSONObject(i);
							locationsFound.add(new Location(
									ads.getDouble("lat"), 
									ads.getDouble("lon"),
									ads.getString("display_name")));
						}
					}else {						
						JSONObject json = new JSONObject(input);
						locationsFound.add(new Location(
								json.getDouble("lat"), 
								json.getDouble("lon"), 
								json.getString("display_name")));
					}
					ios.close();
										
				}else {
					throw new Exception("Não foram encontrados resultados para o endereço pesquisado.");
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
	
	/**
	 * Finds  addresses by Open Street Map Nominatim via Http
	 * @param address - String address representation 
	 * @param numberOfResults - number of address that matched the search
	 * @throws Exception
	 */
	public void searchAddress(String address, int numberOfResults) throws Exception {
		locationsFound = new ArrayList<>();
		
		URI uri;
		CloseableHttpClient osmHttpClient;
		HttpGet httpget;
		CloseableHttpResponse response = null;
		HttpEntity entity;
		
		try {
			uri = new URIBuilder()
					.setScheme(this.SCHEME)
					.setHost(this.HOST)
					.setPath(this.PATH)
					.setParameter("format", "json")
					.setParameter("limit", String.valueOf(numberOfResults))
					.setParameter("q", address).build();
			
			osmHttpClient = HttpClients.createDefault();
			httpget = new HttpGet(uri);
			try {
				response = osmHttpClient.execute(httpget);
				entity = response.getEntity();
				if(entity != null) {
					InputStream ios = entity.getContent();
					String input = convertInputStreamToString(ios);
					System.out.println(input);	
					
					if(input.startsWith("[")) {
						JSONArray json = new JSONArray(input);
						for(int i=0; i<json.length(); ++i) {
							JSONObject ads = json.getJSONObject(i);
							locationsFound.add(new Location(
									ads.getDouble("lat"), 
									ads.getDouble("lon"),
									ads.getString("display_name")));
						}
					}else {						
						JSONObject json = new JSONObject(input);
						locationsFound.add(new Location(
								json.getDouble("lat"), 
								json.getDouble("lon"), 
								json.getString("display_name")));
					}
					ios.close();
										
				}else {
					throw new Exception("Não foram encontrados resultados para o endereço pesquisado.");
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
	
	public List<Location> getLocationsFound(){
		return this.locationsFound;
	}

}

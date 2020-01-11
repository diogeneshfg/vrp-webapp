package br.gasque.vrp_webapp.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class Location implements Serializable{

	private static final long serialVersionUID = 7141273697363825419L;
	
	private double lat;
	
	private double lon;
	
	private String address;
	
	public Location() {
		
	}
	
	public Location(double lat, double lon, String address) {
		this.lat = lat;
		this.lon = lon;
		this.address = address;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	

}

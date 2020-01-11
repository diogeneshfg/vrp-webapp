package br.gasque.vrp_webapp.entity;

public enum ClientType {
	
	NODE("N","Node"),
	DEPOT("D","Depot");
	
	private final String type;
	private final String name;
	
	private ClientType(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}

}

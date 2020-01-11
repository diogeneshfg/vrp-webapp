package br.gasque.vrp_webapp.bean;

import java.io.Serializable;

public class VRPException extends Exception  implements Serializable{
	
	private static final long serialVersionUID = -5614892122981557753L;
	
	public VRPException(String message) {
		super(message);
	}

}

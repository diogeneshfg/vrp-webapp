package br.gasque.vrp_webapp.dao;

import java.io.Serializable;

public class DAOException extends Exception implements Serializable{
	
	private static final long serialVersionUID = -3434117112716194541L;

	public DAOException(String msg) {
		super(msg);
	}

}

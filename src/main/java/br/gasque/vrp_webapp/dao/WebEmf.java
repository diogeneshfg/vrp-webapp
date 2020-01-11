package br.gasque.vrp_webapp.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class WebEmf implements ServletContextListener {
	
	private static EntityManagerFactory emf;
	
	@Override
	public void contextInitialized(ServletContextEvent envet) {
		emf = Persistence.createEntityManagerFactory("vrp-webapp-pu");		
	}

	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		emf.close();
	}
	
	public static EntityManager createEntityManager() {
		if(emf == null) {
			throw new IllegalStateException("Persistence Conxtext not initialized!");
		}
		return emf.createEntityManager();
		
	}
}

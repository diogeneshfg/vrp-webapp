package br.gasque.vrp_webapp.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gasque.vrp_webapp.entity.Client;
import br.gasque.vrp_webapp.entity.constrains.ClientTime;

public class ClientDAO extends ObjectDAO<Client> implements Serializable{
	
	private static final long serialVersionUID = 8805976689079750807L;
	
	
	public ClientDAO() {	
		
	}
	
		
	public void insertClient(Client client) throws DAOException{
		insert(client);
	}
	
	public void removeClient(Client client) throws DAOException {
		remove(client);
	}
	
	public Client updateClient(Client client) throws DAOException{
		client = update(client);
		return client;
	}
	
	public Client findClientById(Client client) throws DAOException {
		return find(client, client.getId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Client> getAllClients() throws DAOException{
		EntityManager em = getEntityManager();
		List<Client> list = null;
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM Client c");
			list = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			em.close();
			throw new DAOException("Erro ao tentar realizar a busca por todos os clientes");
		}finally {
			em.close();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Client> getClientsByName(Client client) throws DAOException{
		EntityManager em = getEntityManager();
		List<Client> list = null;
		try {
			Query query = em.createQuery("SELECT c FROM Client c WHERE c.name like :name");
			query.setParameter("name", "%"+client.getName()+"%");
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			em.close();
			throw new DAOException("Erro ao tentar realizar a busca por clientes");
		}finally {
			em.close();
		}		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Client> getClientsByAddress(Client client) throws DAOException{
		EntityManager em = getEntityManager();
		List<Client> list = null;
		try {
			Query query = em.createQuery("SELECT c FROM Client c WHERE c.location.address like %:ad%");
			query.setParameter("ad", client.getLocation().getAddress());
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			em.close();
			throw new DAOException("Erro ao tentar realizar a busca por clientes");
		}finally {
			em.close();
		}		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Client getClientTimeWindows(Client client)throws DAOException{
		EntityManager em = getEntityManager();		
		try {
			Query query = em.createQuery("SELECT c FROM Client c JOIN FETCH c.timeWindows tw WHERE c.id = :id");
			query.setParameter("id", client.getId());
			client = (Client)query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			em.close();
			throw new DAOException("Erro ao tentar realizar a busca por janelas de tempo para o cliente "+client.getName());
		}finally {
			em.close();
		}		
		return client;
	}

}

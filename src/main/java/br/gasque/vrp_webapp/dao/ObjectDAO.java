package br.gasque.vrp_webapp.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;

public abstract class ObjectDAO<E> implements Serializable{

	private static final long serialVersionUID = 4822303813197462945L;
	
	protected Class<E> entityClass; 
	
	@SuppressWarnings("unchecked")
	public ObjectDAO() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
	}

	public EntityManager getEntityManager() {
		return WebEmf.createEntityManager();
	}
	
	public void insert(E entity) throws DAOException{
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(entity);
			em.getTransaction().commit();
		}catch(Exception e) {
			em.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOException("Erro ao tentar inserir "+entity.toString());
		}finally {
			em.close();
		}		
	}
	
	public void remove(E entity) throws DAOException {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.remove(em.merge(entity));
			em.getTransaction().commit();
		}catch(Exception e) {
			em.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOException("Erro ao tentar remover "+entity.toString());			
		}finally {
			em.close();
		}		
	}
	
	public E update(E entity) throws DAOException{
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.flush();
			entity = em.merge(entity);			
			em.getTransaction().commit();
			return entity;
		}catch(Exception e) {
			em.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOException("Erro ao tentar atualizar "+entity.toString());
		}finally {
			em.close();
		}		
	}	
	
	public E find(E entity, int id) throws DAOException {
		EntityManager em = getEntityManager();		
		try {			
			entity = em.find(entityClass, id);					
			return entity;
		}catch(Exception e) {
			e.printStackTrace();
			em.close();
			throw new DAOException("Erro ao tentar encontrar "+entity.toString());			
		}finally {
			em.close();
		}
	}
}

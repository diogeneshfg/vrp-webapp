package br.gasque.vrp_webapp.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gasque.vrp_webapp.entity.Vehicle;

public class VehicleDAO extends ObjectDAO<Vehicle> implements Serializable{

	private static final long serialVersionUID = -418596252976218083L;
	
	public void insertVehicle(Vehicle veh) throws DAOException {
		insert(veh);
	}
	
	public Vehicle updateVehicle(Vehicle veh) throws DAOException {
		veh = update(veh);
		return veh;
	}
	
	public void removeVehicle(Vehicle veh) throws DAOException {
		remove(veh);
	}
	
	public Vehicle findVehicleById(Vehicle veh) throws DAOException {
		return find(veh, veh.getId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Vehicle> getAllVehicles() throws DAOException{
		EntityManager em = getEntityManager();
		List<Vehicle> list = null;
		try {
			Query query = em.createQuery("SELECT v FROM Vehicle v");
			list = query.getResultList();
		} catch (Exception e) {
			em.close();
			e.printStackTrace();
			throw new DAOException("Erro ao tentar buscar todos os veículos.");
		}finally {
			em.close();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Vehicle> getVehiclesByDescription(Vehicle veh) throws DAOException{
		EntityManager em = getEntityManager();
		List<Vehicle> list = null;
		try {
			Query query = em.createQuery("SELECT v FROM Vehicle v WHERE v.description LIKE :cap");
			query.setParameter("cap", "%"+veh.getDescription()+"%");
			list = query.getResultList();
		} catch (Exception e) {
			em.close();
			e.printStackTrace();
			throw new DAOException("Erro ao tentar buscar todos os veículos.");
		}finally {
			em.close();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Vehicle> getVehiclesByCapacity(Vehicle veh) throws DAOException{
		EntityManager em = getEntityManager();
		List<Vehicle> list = null;
		try {
			Query query = em.createQuery("SELECT v FROM Vehicle v WHERE v.capacity = :cap");
			query.setParameter("cap", veh.getCapacity());
			list = query.getResultList();
		} catch (Exception e) {
			em.close();
			e.printStackTrace();
			throw new DAOException("Erro ao tentar buscar todos os veículos.");
		}finally {
			em.close();
		}
		return list;
	}

}

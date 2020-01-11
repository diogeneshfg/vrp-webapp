package br.gasque.vrp_webapp.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gasque.vrp_webapp.entity.Product;

public class ProductDAO extends ObjectDAO<Product> implements Serializable {

	private static final long serialVersionUID = 6533620527412162323L;
	
	public void insertProduct(Product prd) throws DAOException {
		insert(prd);
	}
	
	public Product updateProduct(Product prd) throws DAOException {
		prd = (Product)update(prd);
		return prd;
	}
	
	public void removeProduct(Product prd) throws DAOException{
		remove(prd);
	} 
	
	public Product findProductbyId(Product prd) throws DAOException {
		return find(prd, prd.getId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Product> getAllProducts() throws DAOException{
		EntityManager em = getEntityManager();
		List<Product> list = null;
		try {
			Query query = getEntityManager().createQuery("SELECT p FROM Product p");
			list = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			em.close();
			throw new DAOException("Erro ao tentar realizar a busca por todos os produtos");
		}finally {
			em.close();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Product> getProductsByName(Product prd) throws DAOException{
		EntityManager em = getEntityManager();
		List<Product> list = null;
		try {
			Query query = getEntityManager().createQuery("SELECT p FROM Product p WHERE p.name like :name");
			query.setParameter("name", "%"+prd.getName()+"%");
			list = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			em.close();
			throw new DAOException("Erro ao tentar realizar a busca por todos os produtos");
		}finally {
			em.close();
		}
		return list;
	}
	
}

package br.gasque.vrp_webapp.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import br.gasque.vrp_webapp.dao.DAOException;
import br.gasque.vrp_webapp.dao.ProductDAO;
import br.gasque.vrp_webapp.entity.Product;
import br.gasque.vrp_webapp.entity.UnitMeasure;

@ManagedBean(name="prdView")
@ViewScoped
public class ProductView implements Serializable {

	
	private static final long serialVersionUID = 2182072567517176405L;
	
	private Product prd;
	private ProductDAO prdDao;
	
	private UnitMeasure units;
	private List<Product> products;
	
	public ProductView() {
		prd = new Product();
		prdDao = new ProductDAO();
		products = new ArrayList<>();
	}
	
	public void removeProduct() {
		if(products.contains(prd)) {
			try {
				prdDao.removeProduct(prd);
				if(products.remove(prd)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Produto Removido:", prd.getName()));
				}else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Produto Removido mas não retirado da lista contate o administrador do sistema:", prd.getName()));
				}
				
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível remover o produto:", prd.getName()));
				e.printStackTrace();
			}
			cleanAll();
		}
	}
	
	public void insertOrUpdateProduct() {
		if(prd.getId() <= 0) {
			try {
				prdDao.insertProduct(prd);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Produto inserido com sucesso:", prd.getName()));
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível inserir o produto:", prd.getName()));
				e.printStackTrace();
			}		
		}else {			
			try {
				Product updatedPrd = prdDao.updateProduct(prd);
				products.clear();
				products.add(updatedPrd);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Produto atualizado com sucesso:", prd.getName()));
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível atualizar o produto:", prd.getName()));
				e.printStackTrace();
			}
		}
		cleanAll();
	}
	
	public void searchProducts() {
		try {
			if(prd.getId() > 0) {
				products.clear();
				products.add(prdDao.findProductbyId(prd));
			}else if(!prd.getName().isEmpty()) {
				products = prdDao.getProductsByName(prd);
			}else {
				products = prdDao.getAllProducts();
			}
			cleanAll();
		}catch(DAOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), prd.getName()));
		}
	}
	
	public void cancel() {
		prd = new Product();
	}
	
	public void cleanAll() {
		prd = new Product();
	}
	
	@SuppressWarnings("static-access")
	public UnitMeasure[] getUnitsMesuare() {
		return units.values();
	}
	
	public void setProduct(Product prd){
		this.prd = prd;
		System.out.println("PRoduto>>>");
	}
	
	public Product getProduct() {
		return this.prd;
	}
	
	public List<Product> getProducts(){
		return this.products;
	}

}

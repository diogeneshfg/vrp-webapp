package br.gasque.vrp_webapp.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.DragDropEvent;
import br.gasque.vrp_webapp.bean.PDPTWBean;
import br.gasque.vrp_webapp.bean.VRPException;
import br.gasque.vrp_webapp.dao.DAOException;
import br.gasque.vrp_webapp.entity.Client;
import br.gasque.vrp_webapp.entity.ClientRoute;
import br.gasque.vrp_webapp.entity.Demand;
import br.gasque.vrp_webapp.entity.Product;
import br.gasque.vrp_webapp.entity.Route;
import br.gasque.vrp_webapp.entity.RoutingPlan;
import br.gasque.vrp_webapp.entity.UnitMeasure;
import br.gasque.vrp_webapp.entity.Vehicle;
import br.gasque.vrp_webapp.entity.constrains.Pairing;
import br.gasque.vrp_webapp.metaheuristic.PDPTWAlns;

@ManagedBean(name="pdptwView")
@ViewScoped
public class PDPTWView implements Serializable{

	private static final long serialVersionUID = 588771667601090632L;
	
	private Client client;
	private Pairing pair;
	private RoutingPlan routingPlan;
	
	private Product prd;
	private Demand demand;
	private List<Demand> demands;
	
	private Vehicle vehicle;
	
	private UnitMeasure unit;
	
	private PDPTWBean pdptwBean;
	
	private List<Client> searchedClients;
	private List<Product> searchedProducts;
	private List<Vehicle> searchedVehicles;
	
	private String path;
	
	private String routeVisits;
	
	private String locations;
	
	public PDPTWView() {
		client = new Client();
		pair = new Pairing();
		
		prd = new Product();
		demand = new Demand();
		demands = new ArrayList<>();
		
		vehicle = new Vehicle();
		
		pdptwBean = new PDPTWBean();
	}
	
	/**
	 * 
	 * @param event
	 */
	public void onDropPickup(DragDropEvent event) {
		Client pickup = (Client)event.getData();
		try {
			pair = pdptwBean.addPickupClientToRequest(pickup, pair);
		} catch (VRPException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	/**
	 * 
	 * @param event
	 */
	public void onDropDelivery(DragDropEvent event) {
		Client delivery = (Client)event.getData();
		try {
			pair = pdptwBean.addDeliveryClientToRequest(delivery, pair);
		} catch (VRPException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	/**
	 * 
	 * @param event
	 */
	public void onDropProduct(DragDropEvent event) {
		Product prdDropped = (Product)event.getData();
		try {
			demands = pdptwBean.addProductToDemandList(prdDropped, demands);
			pdptwBean.addProductToProblem(prdDropped);
		} catch (VRPException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	/**
	 * 
	 * @param dem
	 */
	public void removeDemand(Demand dem) {
		try {
			pdptwBean.removeDemandFromDemandList(dem, demands);
			pdptwBean.removeProductFromProblem(dem.getProduct());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Demanda removida com sucesso.",""));
		} catch (VRPException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	public void addRequest() {
		try {
			pair = pdptwBean.addDemandToPairingClients(pair, demands);
			pdptwBean.addPairToRequestList(pair);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Requisição adicionada com sucesso.",""));
		} catch (VRPException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
		client = new Client();
		pair = new Pairing();
		demand = new Demand();
		demands = new ArrayList<>();
		prd = new Product();
	}
	
	public void removeRequest(Pairing pair) {
		try {			
			pdptwBean.removePairFromRequestList(pair);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Requisição removida com sucesso.",""));
		} catch (VRPException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	public void cleanRequest() {
		client = new Client();
		pair = new Pairing();
		demand = new Demand();
		prd = new Product();
		
		demands = new ArrayList<>();
		searchedClients = new ArrayList<>();
		searchedProducts = new ArrayList<>();
	}
	
	
	public void onDropVehicle(DragDropEvent event) {
		Vehicle veh = (Vehicle)event.getData();
		try {
			pdptwBean.addVehicle(veh);
		} catch (VRPException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	public void removeUsedVehicle(Vehicle veh) {
		try {
			pdptwBean.removeVehicle(veh);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Veículo removido com sucesso.",""));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	public void generatePath(Route route) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<route.getClients().size(); ++i) {
			ClientRoute c = route.getClients().get(i);
			sb.append("[");
			sb.append(c.getClient().getName());
			sb.append("]");
			sb.append(c.getClient().getLocation().getLat());
			sb.append(",");
			sb.append(c.getClient().getLocation().getLon());
			if(i<route.getClients().size()-1) {
				sb.append(";");
			}
		}
		this.locations = sb.toString();
		
		path = pdptwBean.getRoutePath(route);
	}
	
	/**
	 * Get route visits for display
	 * @param route
	 */
	public void displayRouteVisits(Route route) {
		try {
			routeVisits = pdptwBean.getRouteSequenceOfVisits(route);
		} catch (VRPException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		} 
	}
	
	public void teste() {
		System.out.println("TEST");
		routingPlan = pdptwBean.runALNS();		
		System.out.println(routingPlan.getRoutes().size());
	}
	
	/**
	 * Search clients by id, name and address. After the search, if it does return some value, 
	 * the method fills the clients list.
	 */
	public void searchClients() {
		try {
			if(client.getId() > 0) {
				client = pdptwBean.findClientById(client);			
				searchedClients.clear();
				searchedClients.add(client);
							
			}else if((client.getName() != null) && (!client.getName().isEmpty())){
				searchedClients = pdptwBean.getClientsByName(client);
			}else if((client.getLocation().getAddress() != null) && (!client.getLocation().getAddress().isEmpty())){
				searchedClients = pdptwBean.getClientsByAdress(client);
			}else {
				searchedClients = pdptwBean.getAllClients();
			}
			client = new Client();
		}catch(DAOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	/**
	 * Search products by id and name. If the query returns some value, it fills the product list with them.
	 */
	public void searchProducts() {
		try {
			if(prd.getId() > 0) {
				prd = pdptwBean.findProductById(prd);
				searchedProducts.clear();
				searchedProducts.add(prd);
			}else if((prd.getName() != null) && (!prd.getName().isEmpty())) {
				searchedProducts = pdptwBean.getProductsByName(prd);
			}else {
				searchedProducts = pdptwBean.getAllProducts();
			}
			prd = new Product();
		} catch (DAOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}
	}
	
	public void searchVehicles() {
		try {
			if(vehicle.getId() > 0) {
				vehicle = pdptwBean.findVehicleById(vehicle);
				searchedVehicles.clear();
				searchedVehicles.add(vehicle);
			}else if((vehicle.getDescription() != null) && (!vehicle.getDescription().isEmpty())) {
				searchedVehicles = pdptwBean.getVehiclesByDescrption(vehicle);
			}else if(vehicle.getCapacity() > 0) {
				searchedVehicles = pdptwBean.getVehiclesByCapacity(vehicle);
			}else {
				searchedVehicles = pdptwBean.getAllVehicles();
				System.out.println("oi");
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),""));
		}		
	}

	public List<Vehicle> getSearchedVehicles() {
		return searchedVehicles;
	}

	public List<Client> getSearchedClients() {
		return searchedClients;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Product getPrd() {
		return prd;
	}

	public void setPrd(Product prd) {
		this.prd = prd;
	}

	public List<Product> getSearchedProducts() {
		return searchedProducts;
	}

	public Demand getDemand() {
		return demand;
	}

	public void setDemand(Demand demand) {
		this.demand = demand;
	}

	public Pairing getPair() {
		return pair;
	}

	public void setPair(Pairing pair) {
		this.pair = pair;
	}

	public List<Route> getRoutes() {
		return (routingPlan != null ? routingPlan.getRoutes() : null);
	}	

	public List<Demand> getDemands() {
		return demands;
	}
	
	@SuppressWarnings("static-access")
	public UnitMeasure[] getUnitsMeasure() {
		return unit.values();
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public List<Vehicle> getUsedVehicles(){
		return pdptwBean.getVehicles();
	}
	
	public List<Pairing> getRequests(){
		return pdptwBean.getRequests();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getRouteVisits() {
		return this.routeVisits;
	}

	public String getLocations() {
		return locations;
	}

	public void setLocations(String locations) {
		this.locations = locations;
	}

}

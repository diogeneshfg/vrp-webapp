package br.gasque.vrp_webapp.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import br.gasque.vrp_webapp.dao.ClientDAO;
import br.gasque.vrp_webapp.dao.DAOException;
import br.gasque.vrp_webapp.entity.Client;
import br.gasque.vrp_webapp.entity.ClientType;
import br.gasque.vrp_webapp.entity.Location;
import br.gasque.vrp_webapp.entity.constrains.ClientTime;
import br.gasque.vrp_webapp.util.OSMNominatimHandler;

@ManagedBean(name="clientView")
@ViewScoped
public class ClientView implements Serializable{

	private static final long serialVersionUID = -3861613257019563807L;
	
	private Client client;	
	private Location location;
	private ClientTime timeWindow;
	private ClientDAO clientDao;
	
	private ClientType type;
	
	private OSMNominatimHandler osmNomi;
		
	private List<Client> clients;
	private List<Location> locations;	
		
	public ClientView() {		
		client = new Client();
		clientDao = new ClientDAO();
		location = new Location();
		clients = new ArrayList<>();
		osmNomi = new OSMNominatimHandler();
	}	
	
	/**
	 * Receives client from interface
	 * @param pickupClient
	 */
	public void setClientForAction(Client client) {
		this.client = client;
		if(this.client.isTimeWindow()) {
			this.timeWindow = client.getTimeWindows().get(0);
		}
	}
	
	/**
	 * Receives location from interface
	 * @param location
	 */
	public void setLocationForAction(Location location) {
		this.location = location;
		client.setLocation(location);		
	}
	
	
	public void cancel() {
		cleanAll();
	}
	
	public void removeClient() {
		if(clients.contains(client)) {
			try {
				clientDao.removeClient(client);
				if(clients.remove(client)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente Removido:", client.getName()));
				}else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Cliente Removido mas não retirado da lista contate o administrador do sistema:", client.getName()));
				}
				
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível remover o cliente:", client.getName()));
				e.printStackTrace();
			}
			cleanAll();
		}
	}
	
	public void insertOrUpdateClient() {	
		if(client.isTimeWindow()) {
			client.getTimeWindows().add(timeWindow);
		}
		if(client.getId() <= 0) {
			try {				
				clientDao.insertClient(client);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente inserido com sucesso:", client.getName()));
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível inserir o cliente:", client.getName()));
				e.printStackTrace();
			}		
		}else {			
			try {
				Client updatedCli = clientDao.updateClient(client);
				clients.clear();
				clients.add(updatedCli);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente atualizado com sucesso:", client.getName()));
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível atualizar o cliente:", client.getName()));
				e.printStackTrace();
			}
		}
		cleanAll();
	}
	
	public void searchClients() {
		try {
			if(client.getId() > 0) {
				clients.clear();
				clients.add(clientDao.findClientById(client));
			}else if(!client.getName().isEmpty()) {
				clients = clientDao.getClientsByName(client);
			}else {
				clients = clientDao.getAllClients();
			}
			cleanAll();
		}catch(DAOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), client.getName()));
		}
	}
	
	public void searchAddress() {
		try {
			osmNomi.searchAddress(client.getLocation().getAddress(), 3);
			locations = osmNomi.getLocationsFound();
		} catch (Exception e) {			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Endereço Erro:", e.getMessage()));
		}	
	}
	
	public void searchReverseAddress() {
		try {
			osmNomi.searchReverseAddress(
					String.valueOf(client.getLocation().getLat()), 
					String.valueOf(client.getLocation().getLon()));			
			locations = osmNomi.getLocationsFound();
		} catch (Exception e) {			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Endereço Erro:", e.getMessage()));
		}	
	}
	
	private void cleanAll() {
		client = new Client();	
		location = new Location();
		locations = new ArrayList<>();
	}
	
	public void addTimeWindow() {
		if(client.isTimeWindow()) {
			timeWindow = new ClientTime();
			timeWindow.setClient(client);
			if(client.getTimeWindows()==null) {
				client.setTimeWindows(new ArrayList<>());
			}else {
				client.getTimeWindows().clear();
			}
		}
	}
	
	public Client getClient() {
		return client;
	}	
	
	public Location getLocation() {
		return this.location;
	}
	
	public List<Client> getClients(){
		return clients;
	}	
	
	public List<Location> getLocations(){
		return this.locations;
	}

	public ClientTime getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(ClientTime timeWindow) {
		this.timeWindow = timeWindow;
	}
	
	@SuppressWarnings("static-access")
	public ClientType[] getTypes() {
		return type.values();
	}
}

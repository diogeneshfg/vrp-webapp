package br.gasque.vrp_webapp.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import br.gasque.vrp_webapp.dao.DAOException;
import br.gasque.vrp_webapp.dao.VehicleDAO;
import br.gasque.vrp_webapp.entity.UnitMeasure;
import br.gasque.vrp_webapp.entity.Vehicle;

@ManagedBean(name="veView")
@ViewScoped
public class VehicleView implements Serializable{
	
	private static final long serialVersionUID = 4444759282573648113L;
	private Vehicle veh;
	private VehicleDAO vehDao;
	
	private UnitMeasure unit;
	private List<Vehicle> vehicles;
	
	public VehicleView() {
		veh = new Vehicle();
		vehDao = new VehicleDAO();
		vehicles = new ArrayList<>();		
	}
	
	public void removeVehicle() {
		if(vehicles.contains(veh)) {
			try {
				vehDao.removeVehicle(veh);
				if(vehicles.remove(veh)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Veículo Removido:", veh.getDescription()));
				}else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Veículo Removido mas não retirado da lista contate o administrador do sistema:", veh.getDescription()));
				}
				
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível remover o veículo:", veh.getDescription()));
				e.printStackTrace();
			}
			cleanAll();
		}
	}
	
	public void insertOrUpdateVehicle() {
		if(veh.getId() <= 0) {
			try {
				vehDao.insertVehicle(veh);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Veículo inserido com sucesso:", veh.getDescription()));
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível inserir o veículo:", veh.getDescription()));
				e.printStackTrace();
			}		
		}else {			
			try {
				Vehicle updatedVeh = vehDao.updateVehicle(veh);
				vehicles.clear();
				vehicles.add(updatedVeh);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Veículo atualizado com sucesso:", veh.getDescription()));
			} catch (DAOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível atualizar o veículo:", veh.getDescription()));
				e.printStackTrace();
			}
		}
		cleanAll();
	}
	
	public void searchVehicles() {
		try {
			if(veh.getId() > 0) {
				vehicles.clear();
				vehicles.add(vehDao.findVehicleById(veh));
			}else if(!veh.getDescription().isEmpty()) {
				vehicles = vehDao.getVehiclesByDescription(veh);
			}else if(veh.getCapacity() > 0) {
				vehicles = vehDao.getVehiclesByCapacity(veh);
			}else {
				vehicles = vehDao.getAllVehicles();
			}
			cleanAll();
		}catch(DAOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), veh.getDescription()));
		}
	}
	
	
	public void cancel() {
		veh = new Vehicle();
	}
	
	public void cleanAll() {
		veh = new Vehicle();
	}
	
	@SuppressWarnings("static-access")
	public UnitMeasure[] getUnitsMesuare() {
		return this.unit.values();
	}
	
	public void setVehicle(Vehicle veh) {
		this.veh = veh;
	}
	
	public Vehicle getVehicle() {
		return this.veh;
	}
	
	public List<Vehicle> getVehicles(){
		return this.vehicles;
	}

}

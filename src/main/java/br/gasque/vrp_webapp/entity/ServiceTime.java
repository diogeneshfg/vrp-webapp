package br.gasque.vrp_webapp.entity;

import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;


/**
 * The persistent class for the services_times database table.
 * 
 */
@Entity
@Table(name="services_times")
public class ServiceTime implements Serializable {

	private static final long serialVersionUID = -529201709414102002L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "service_time_id")
	private Integer Id;
		
	@OneToOne
	@JoinColumn(name = "client_route_id", referencedColumnName = "client_route_id")
	private ClientRoute clientRoute;
	
	@Column(name = "service_time_start")
	private Double serviceTimeStart;
	
	public ServiceTime() {
	}
	
	public ServiceTime(Double serviceTimeStart) {
		this.serviceTimeStart = serviceTimeStart;
	}
	
	public ServiceTime(ClientRoute clientRoute, Double serviceTimeStart) {
		this.clientRoute = clientRoute;
		this.serviceTimeStart = serviceTimeStart;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public ClientRoute getClientRoute() {
		return clientRoute;
	}

	public void setClientRoute(ClientRoute clientRoute) {
		this.clientRoute = clientRoute;
	}

	public Double getServiceTimeStart() {
		return serviceTimeStart;
	}

	public void setServiceTimeStart(Double serviceTimeStart) {
		this.serviceTimeStart = serviceTimeStart;
	}

}
package br.gasque.vrp_webapp.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;


/**
 * The persistent class for the routes database table.
 * 
 */
@Entity
@Table(name="routes")
public class Route implements Serializable {

	private static final long serialVersionUID = 2731121341307855371L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "route_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "vehicle_id", referencedColumnName = "vehicle_id")
	private Vehicle vehicle;
	
	@ManyToOne
	@JoinColumn(name = "routing_plan_id", referencedColumnName = "routing_plan_id")
	private RoutingPlan routingPlan;
	
	@OneToMany(mappedBy = "client")
	private List<ClientRoute> clients;

	public Route() {
		this.clients = new ArrayList<>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public RoutingPlan getRoutingPlan() {
		return routingPlan;
	}

	public void setRoutingPlan(RoutingPlan routingPlan) {
		this.routingPlan = routingPlan;
	}

	public List<ClientRoute> getClients() {
		return clients;
	}

	public void setClients(List<ClientRoute> clients) {
		this.clients = clients;
	}

}
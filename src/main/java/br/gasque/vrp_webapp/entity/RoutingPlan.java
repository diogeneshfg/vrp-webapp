package br.gasque.vrp_webapp.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;


/**
 * The persistent class for the routing_plans database table.
 * 
 */
@Entity
@Table(name="routing_plans")
public class RoutingPlan implements Serializable {
	
	private static final long serialVersionUID = 4839896146374425333L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "routing_plan_id")
	private Integer Id;
	
	@Column(name = "routing_plan_description")
	private String description;
	
	@Column(name = "routing_plan_date")
	private Date date;
	
	@Column(name = "routing_plan_total_distance")
	private Double totalDistance;
	
	@Column(name = "routing_plan_total_cost")
	private BigDecimal totalCost;
	
	@Column(name = "routing_plan_qtd_vehicles")
	private Integer numberOfVehicle;
	
	@OneToMany(mappedBy = "routingPlan")
	private List<Route> routes;

	public RoutingPlan() {
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(Double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public Integer getNumberOfVehicle() {
		return numberOfVehicle;
	}

	public void setNumberOfVehicle(Integer numberOfVehicle) {
		this.numberOfVehicle = numberOfVehicle;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

}
package br.gasque.vrp_webapp.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;


/**
 * The persistent class for the demands_met database table.
 * 
 */
@Entity
@Table(name="demands_met")
public class DemandMet implements Serializable {

	private static final long serialVersionUID = -7808341106577945728L;

	@Id
	@Column(name = "demand_met_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "client_route_id", referencedColumnName = "client_route_id")
	private ClientRoute clientRoute;
	
	@ManyToOne
	@JoinColumn(name = "demand_id", referencedColumnName = "demand_id")
	private Demand demand;
	
	@Column(name = "demand_met_product_qtd")
	private Double quantityMet;
	
	public DemandMet() {
	}
	
	public DemandMet(ClientRoute clientRoute, Demand demand, Double quantityMet) {
		this.clientRoute = clientRoute;
		this.demand = demand;
		this.quantityMet = quantityMet;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ClientRoute getClientRoute() {
		return clientRoute;
	}

	public void setClientRoute(ClientRoute clientRoute) {
		this.clientRoute = clientRoute;
	}

	public Demand getDemand() {
		return demand;
	}

	public void setDemand(Demand demand) {
		this.demand = demand;
	}

	public Double getQuantityMet() {
		return quantityMet;
	}

	public void setQuantityMet(Double quantityMet) {
		this.quantityMet = quantityMet;
	}

}
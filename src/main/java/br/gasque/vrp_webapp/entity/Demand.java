package br.gasque.vrp_webapp.entity;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "demands")
public class Demand implements Serializable{
	
	private static final long serialVersionUID = -7524969007673908538L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "demand_id")
	private int id;
		
	@ManyToOne
	@JoinColumn(name = "routing_plan_id", referencedColumnName = "routing_plan_id")
	private RoutingPlan routingPlan;
	
	@ManyToOne
	@JoinColumn(name = "client_id", referencedColumnName = "client_id")
	private Client client;
	
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "product_id")
	private Product product;
	
	@Column(name = "demand_product_qtd")
	private Double quantity;	
	
	@Enumerated(STRING)
	@Column(name = "demand_product_unit_measure")
	private UnitMeasure unitMeasure;
	
	public Demand() {
		
	}
	
	public Demand(Client client, Product product, Double quantity, UnitMeasure unit) {
		this.client = client;
		this.product = product;
		this.quantity = quantity;
		this.unitMeasure = unit;
	}
	
	public Demand(Product product) {
		this.product = product;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public UnitMeasure getUnitMeasure() {
		return unitMeasure;
	}

	public void setUnitMeasure(UnitMeasure unitMeasure) {
		this.unitMeasure = unitMeasure;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public RoutingPlan getRoutingPlan() {
		return routingPlan;
	}

	public void setRoutingPlan(RoutingPlan routingPlan) {
		this.routingPlan = routingPlan;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(!(obj instanceof Demand)) {
			return false;
		}
		
		Demand other = (Demand) obj;
		if (product == null) {			
			if (other.product != null) {
				return false;
			}
		} else if (!(this.product.equals(other.product))) {
			return false;
		}
		return true;
	}
	
	
	
}

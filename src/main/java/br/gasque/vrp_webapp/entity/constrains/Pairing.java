package br.gasque.vrp_webapp.entity.constrains;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gasque.vrp_webapp.entity.Client;
import br.gasque.vrp_webapp.entity.RoutingPlan;

@Entity
@Table(name = "clients_paring")
public class Pairing implements Serializable{	

	private static final long serialVersionUID = 9149434422761605881L;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "client_paring_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "routing_plan_id", referencedColumnName = "routing_plan_id")
	private RoutingPlan routingPlan;
	
	@ManyToOne
	@JoinColumn(name = "pickup_client_id", referencedColumnName = "client_id")
	private Client pickup;
	
	@ManyToOne
	@JoinColumn(name = "delivery_client_id", referencedColumnName = "client_id")
	private Client delivery;
	
	public Pairing() {
		
	}

	public Pairing(Client pickup, Client delivery) {
		this.pickup = pickup;
		this.delivery = delivery;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RoutingPlan getRoutingPlan() {
		return routingPlan;
	}

	public void setRoutingPlan(RoutingPlan routingPlan) {
		this.routingPlan = routingPlan;
	}

	public Client getPickup() {
		return pickup;
	}

	public void setPickup(Client pickup) {
		this.pickup = pickup;
	}

	public Client getDelivery() {
		return delivery;
	}

	public void setDelivery(Client delivery) {
		this.delivery = delivery;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if((obj != null) && (obj instanceof Pairing)) {
			Pairing pair = (Pairing)obj;
			if((pair.getPickup()!=null) && (pair.getDelivery()!=null)) {				
				if(pair.getPickup().equals(this.getPickup()) && pair.getDelivery().equals(this.getDelivery())) {
					return true;
				}				
			}
		}		
		return false;
	}

	
}

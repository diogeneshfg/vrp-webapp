package br.gasque.vrp_webapp.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;


/**
 * The persistent class for the routes_clients database table.
 * 
 */
@Entity
@Table(name="clients_routes")
public class ClientRoute implements Serializable {
	
	private static final long serialVersionUID = 7080237958680860820L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "client_route_id")
	private Integer Id;
	
	@ManyToOne
	@JoinColumn(name = "route_id", referencedColumnName = "route_id")
	private Route route;
	
	@ManyToOne
	@JoinColumn(name = "client_id", referencedColumnName = "client_id")
	private Client client;
	
	@Column(name = "client_position")
	private Integer positionInRoute;
	
	@OneToOne(mappedBy = "clientRoute")
	private ServiceTime serviceTime;
	
	@OneToMany(mappedBy = "clientRoute")
	private List<DemandMet> demands;

	public ClientRoute() {
	}
	
	public ClientRoute(Route route, Client client, Integer positionInRoute) {
		this.route = route;
		this.client = client;
		this.positionInRoute = positionInRoute;
	}
	
	public ClientRoute(Route route, Client client, Integer positionInRoute, ServiceTime serviceTime) {
		this.route = route;
		this.client = client;
		this.positionInRoute = positionInRoute;
		this.serviceTime = serviceTime;
		this.serviceTime.setClientRoute(this);
	}
	
	public ClientRoute(Route route, Client client, Integer positionInRoute, List<DemandMet> demands) {
		this.route = route;
		this.client = client;
		this.positionInRoute = positionInRoute;
		this.demands = demands;
	}	

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Integer getPositionInRoute() {
		return positionInRoute;
	}

	public void setPositionInRoute(Integer positionInRoute) {
		this.positionInRoute = positionInRoute;
	}

	public List<DemandMet> getDemands() {
		return demands;
	}

	public void setDemands(List<DemandMet> demands) {
		this.demands = demands;
	}

}
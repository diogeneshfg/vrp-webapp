package br.gasque.vrp_webapp.entity;

import static javax.persistence.CascadeType.ALL;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gasque.vrp_webapp.entity.constrains.ClientTime;

import javax.persistence.Embedded;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Enumerated;
import static javax.persistence.EnumType.STRING;
import javax.persistence.Basic;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.CascadeType.DETACH;

@Entity
@Table(name="clients")
@Inheritance(strategy=InheritanceType.JOINED)
public class Client implements Serializable{
	
	private static final long serialVersionUID = -6569250724137214856L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "client_id")
	private int id;
	
	@Transient
	private int index;
	
	@Column(name="client_name", nullable=false)
	private String name;
	
	@Enumerated(STRING)
	@Column(name = "client_type")
	private ClientType type;
	
	@Embedded
	@AttributeOverrides({ 
		@AttributeOverride(name = "lat", column = @Column(name = "client_lat", table = "clients", nullable = false)),			
		@AttributeOverride(name = "lon", column = @Column(name = "client_lon", table = "clients", nullable = false)),
		@AttributeOverride(name = "address", column = @Column(name = "client_address", table = "clients", nullable = false)) 
	})
	private Location location;
	
	@Column(name = "client_time_window", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean timeWindow;
		
	@OneToMany(mappedBy = "client", cascade = ALL)
	private List<ClientTime> timeWindows;
	
	@Transient
	private List<Demand> demands;
	
	public Client() {
		this.location = new Location();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	public List<Demand> getDemands() {
		return demands;
	}

	public void setDemands(List<Demand> demands) {
		this.demands = demands;
	}
	
	@Override
	public boolean equals(Object obj) {		
		if((obj!=null) && (obj instanceof Client)) {
			Client c = (Client)obj;
			if((this.getLocation().getLat() == c.getLocation().getLat()) && (this.getLocation().getLon() == c.getLocation().getLon())) {
				return true;
			}
		}
		return false;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<ClientTime> getTimeWindows() {
		return timeWindows;
	}

	public boolean isTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(boolean timeWindow) {
		this.timeWindow = timeWindow;
	}

	public void setTimeWindows(List<ClientTime> timeWindows) {
		this.timeWindows = timeWindows;
	}

	public ClientType getType() {
		return type;
	}

	public void setType(ClientType type) {
		this.type = type;
	}	
}

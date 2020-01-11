package br.gasque.vrp_webapp.entity.constrains;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gasque.vrp_webapp.entity.Client;

@Entity
@Table(name="clients_times")
public class ClientTime implements Serializable {
	
	
	private static final long serialVersionUID = -3301279607859613117L;

	@Id
	@Column(name = "client_time_id")
	@GeneratedValue(strategy = IDENTITY)
	private int id;
	
	@Column(name = "client_time_window_open")
	private String timeWindowOpen;
	
	@Column(name = "client_time_window_close")
	private String timeWindowClose;
	
	@Column(name = "client_time_service_duration")
	private String serviceDuration;
	
	@ManyToOne
	@JoinColumn(name = "client_id", referencedColumnName = "client_id")
	private Client client;
	
	public ClientTime() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTimeWindowOpen() {
		return timeWindowOpen;
	}

	public void setTimeWindowOpen(String timeWindowOpen) {
		this.timeWindowOpen = timeWindowOpen;
	}

	public String getTimeWindowClose() {
		return timeWindowClose;
	}

	public void setTimeWindowClose(String timeWindowClose) {
		this.timeWindowClose = timeWindowClose;
	}

	public String getServiceDuration() {
		return serviceDuration;
	}

	public void setServiceDuration(String serviceDuration) {
		this.serviceDuration = serviceDuration;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}

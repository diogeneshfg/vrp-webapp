package br.gasque.vrp_webapp.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.EnumType.STRING;


/**
 * The persistent class for the vehicles database table.
 * 
 */
@Entity
@Table(name="vehicles")
public class Vehicle implements Serializable {
	
	private static final long serialVersionUID = -3085916686215786881L;

	@Id
	@Column(name = "vehicle_id")
	@GeneratedValue(strategy = IDENTITY)
	private int id;
	
	@Column(name="vehicle_description")
	private String description;
	
	@Column(name="vehicle_fixed_cost")
	private BigDecimal fixedCost;
	
	@Column(name="vehicle_variable_cost")
	private BigDecimal variableCost;
	
	@Column(name="vehicle_capacity")
	private double capacity;
	
	@Column(name="vehicle_capacity_unit_measure")
	@Enumerated(STRING)
	private UnitMeasure capacityUnit;
	
	public Vehicle() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getVariableCost() {
		return variableCost;
	}

	public void setVariableCost(BigDecimal variableCost) {
		this.variableCost = variableCost;
	}

	public BigDecimal getFixedCost() {
		return fixedCost;
	}

	public void setFixedCost(BigDecimal fixedCost) {
		this.fixedCost = fixedCost;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public UnitMeasure getCapacityUnit() {
		return capacityUnit;
	}

	public void setCapacityUnit(UnitMeasure capacityUnit) {
		this.capacityUnit = capacityUnit;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehicle other = (Vehicle) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
}
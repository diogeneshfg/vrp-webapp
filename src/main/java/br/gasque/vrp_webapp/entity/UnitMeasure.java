package br.gasque.vrp_webapp.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public enum UnitMeasure implements Serializable{
	
	gr("gr"),
	kg("kg"),
	ton("ton"),
	ml("ml"),
	l("l"),	
	pc("pc");
	
	private final String unit;
	
	private UnitMeasure(String unit) {
		this.unit = unit;
	}
	
	public String unit() {
		return this.unit;
	}
	
	public BigDecimal toTon(final BigDecimal value) {
		BigDecimal converted = null;
		
		switch(this) {			
			case ton:
				converted = value.divide(BigDecimal.valueOf(1));
				break;
			case kg:
				converted = value.divide(BigDecimal.valueOf(1000d));
				break;
			case gr:
				converted = value.divide(BigDecimal.valueOf(1000000d));
				break;
			default:
				throw new IllegalArgumentException("Value "+value+"can not be converted");
		}		
		return converted;				
	}
	
	public BigDecimal toKg(final BigDecimal value) {
		BigDecimal converted = null;
		
		switch(this) {			
			case ton:
				converted = value.multiply(BigDecimal.valueOf(1000d));
				break;
			case kg:
				converted = value.divide(BigDecimal.valueOf(1));
				break;
			case gr:
				converted = value.divide(BigDecimal.valueOf(1000d));
				break;
			default:
				throw new IllegalArgumentException("Value "+value+"can not be converted");
		}		
		return converted;				
	}
	
	public BigDecimal toGr(final BigDecimal value) {
		BigDecimal converted = null;
		
		switch(this) {			
			case ton:
				converted = value.multiply(BigDecimal.valueOf(1000000d));
				break;
			case kg:
				converted = value.multiply(BigDecimal.valueOf(1000d));
				break;
			case gr:
				converted = value.divide(BigDecimal.valueOf(1));
				break;
			default:
				throw new IllegalArgumentException("Value "+value+"can not be converted");
		}		
		return converted;
	}
	
	public BigDecimal convertTo(UnitMeasure unit, BigDecimal value) {
		BigDecimal converted = null;
		switch(unit) {			
		case ton:
			converted = this.toTon(value);
			break;
		case kg:
			converted = this.toKg(value);
			break;
		case gr:
			converted = this.toGr(value);
			break;
		default:
			throw new IllegalArgumentException("Value "+value+"can not be converted");
	}		
	return converted;
	}
}

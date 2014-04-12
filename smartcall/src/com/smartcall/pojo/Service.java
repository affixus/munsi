package com.smartcall.pojo;

import java.io.Serializable;
import java.util.Date;

public class Service implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String type;
	private Date serviceDate;
	private String callingDate;
	private Float price;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Date getServiceDate() {
		return serviceDate;
	}
	
	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}
	
	public String getCallingDate() {
		return callingDate;
	}
	
	public void setCallingDate(String callingDate) {
		this.callingDate = callingDate;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}
	
}

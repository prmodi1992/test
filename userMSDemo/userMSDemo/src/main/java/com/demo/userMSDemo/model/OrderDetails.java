package com.demo.userMSDemo.model;

public class OrderDetails {

	private int orderId;
	private String orderName;
	private String envDetails;
	public String getEnvDetails() {
		return envDetails;
	}
	public void setEnvDetails(String envDetails) {
		this.envDetails = envDetails;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
}

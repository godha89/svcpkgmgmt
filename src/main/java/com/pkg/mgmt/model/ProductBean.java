package com.pkg.mgmt.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Product Id Cannot Be Null")
	@JsonProperty(value = "id", required = true)
	private String id;

	// @NotNull(message = "Product Name Cannot Be Null")
	@JsonProperty(value = "name")
	private String name;

	// @NotNull(message = "Product Price Cannot Be Null")
	@JsonProperty(value = "usdPrice")
	private BigDecimal usdPrice;

	public ProductBean() {

	}

	public ProductBean(String id, String name, BigDecimal usdPrice) {
		this.id = id;
		this.name = name;
		this.usdPrice = usdPrice;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getUsdPrice() {
		return usdPrice;
	}

	public void setUsdPrice(BigDecimal usdPrice) {
		this.usdPrice = usdPrice;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

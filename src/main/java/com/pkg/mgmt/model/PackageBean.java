package com.pkg.mgmt.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	@NotNull(message = "Package Name Cannot Be Null")
	@JsonProperty(value = "name", required = true)
	private String name;

	@NotNull(message = "Package Description Cannot Be Null")
	@JsonProperty(value = "description", required = true)
	private String description;

	@NotNull(message = "Products in a package Cannot Be Null")
	@JsonProperty(value = "products", required = true)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<ProductBean> products;

	private BigDecimal price;

	@JsonProperty(value = "currency", defaultValue = "USD")
	private String currency;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<ProductBean> getProducts() {
		if (products == null) {
			products = new ArrayList<ProductBean>();
		}
		return products;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setProducts(List<ProductBean> products) {
		this.products = products;
	}

	public PackageBean() {
	}

}

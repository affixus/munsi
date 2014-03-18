package com.munsi.pojo.master;

import java.util.Set;

import com.munsi.pojo.BasePojo;

public class Product extends BasePojo {

	private static final long serialVersionUID = 1L;

	private String _id;

	private String code;
	private String name;
	private String alias;
	private Float weight;
	private Float margin;
	private Float mrp;
	private Float purchaseRate;
	private Float purchaseUnit;
	private Float salesRate;
	private Float salesUnit;

	/** Number of item in a BOX (if Purchase unit is box) */
	private Integer pack;
	private Boolean lockItem;
	/** way of tax calculation */
	private String vatType;
	private Set<Tax> taxList;
	private Manufacturer manufacturar;
	private ProductGroup productGroup;
	private Set<OpeningProductStock> openingStockList;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Float getMargin() {
		return margin;
	}

	public void setMargin(Float margin) {
		this.margin = margin;
	}

	public Float getMrp() {
		return mrp;
	}

	public void setMrp(Float mrp) {
		this.mrp = mrp;
	}

	public Float getPurchaseRate() {
		return purchaseRate;
	}

	public void setPurchaseRate(Float purchaseRate) {
		this.purchaseRate = purchaseRate;
	}

	public Float getPurchaseUnit() {
		return purchaseUnit;
	}

	public void setPurchaseUnit(Float purchaseUnit) {
		this.purchaseUnit = purchaseUnit;
	}

	public Float getSalesRate() {
		return salesRate;
	}

	public void setSalesRate(Float salesRate) {
		this.salesRate = salesRate;
	}

	public Float getSalesUnit() {
		return salesUnit;
	}

	public void setSalesUnit(Float salesUnit) {
		this.salesUnit = salesUnit;
	}

	public Integer getPack() {
		return pack;
	}

	public void setPack(Integer pack) {
		this.pack = pack;
	}

	public Boolean getLockItem() {
		return lockItem;
	}

	public void setLockItem(Boolean lockItem) {
		this.lockItem = lockItem;
	}

	public String getVatType() {
		return vatType;
	}

	public void setVatType(String vatType) {
		this.vatType = vatType;
	}

	public Set<Tax> getTaxList() {
		return taxList;
	}

	public void setTaxList(Set<Tax> taxList) {
		this.taxList = taxList;
	}

	public Manufacturer getManufacturar() {
		return manufacturar;
	}

	public void setManufacturar(Manufacturer manufacturar) {
		this.manufacturar = manufacturar;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public Set<OpeningProductStock> getOpeningStockList() {
		return openingStockList;
	}

	public void setOpeningStockList(Set<OpeningProductStock> openingStockList) {
		this.openingStockList = openingStockList;
	}

}
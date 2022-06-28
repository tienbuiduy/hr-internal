package com.hr.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer extends AbstractAuditEntity implements Comparable<Customer> {
	private static final long serialVersionUID = 1L;

	private String customerCode;
	private String customerName;
	private String countryCode;
	private String rank;
	private String experience;
	private int isDeleted = 0;

	@Basic
	@Column(name = "customer_code")
	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	@Basic
	@Column(name = "customer_name")
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Basic
	@Column(name = "country_code")
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Basic
	@Column(name = "rank")
	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	@Basic
	@Column(name = "experience")
	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	@Basic
	@Column(name = "is_deleted")
	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public int compareTo(Customer customer) {
		if(customer == null){
			return 1;
		}
		return this.getId() - customer.getId();
	}
}

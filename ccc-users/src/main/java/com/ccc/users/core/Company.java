package com.ccc.users.core;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="company")
public class Company implements Serializable {

	private static final long serialVersionUID = 1L;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Set<BasicUser> getWorkers() {
		return workers;
	}

	public void setWorkers(Set<BasicUser> workers) {
		this.workers = workers;
	}

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="domain_name")
	private String domainName;

	@Column(name="phone")
	private String phone;

	@Column(name="address")
	private String address;

	@Column(name="company_name")
	private String companyName;

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinTable(name = "works_for", joinColumns = { @JoinColumn(name = "company_id") }, inverseJoinColumns = { @JoinColumn(name = "user_name")})
	private Set<BasicUser> workers;

}


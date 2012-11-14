package com.ccc.oauth.apimanagement.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.ccc.util.strings.CSVUtils;

@Entity
@Table(name="contact")
public class Contact implements Serializable {

	
	
	public void appendEmail(String email) {
		setEmails(CSVUtils.appendTo(emails, email));
	}
	
	public void appendNumber(String number) {
		setNumbers(CSVUtils.appendTo(numbers, number));
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2042986717821868559L;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserBelongsTo() {
		return userBelongsTo;
	}
	public void setUserBelongsTo(String userBelongsTo) {
		this.userBelongsTo = userBelongsTo;
	}
	public String getNumbers() {
		return numbers;
	}
	public void setNumbers(String numbers) {
		this.numbers = numbers;
	}
	public String getEmails() {
		return emails;
	}
	public void setEmails(String emails) {
		this.emails = emails;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@Id
	@Column(name="id")
	@JsonSerialize(include = Inclusion.NON_NULL)
	private int id;
	@Column(name="user")
	@JsonSerialize(include = Inclusion.NON_NULL)
	private String userBelongsTo;
	@Column(name="number")
	@JsonSerialize(include = Inclusion.NON_NULL)
	private String numbers;
	@Column(name="emails")
	@JsonSerialize(include = Inclusion.NON_NULL)
	private String emails;
	@Column(name="first_name")
	@JsonSerialize(include = Inclusion.NON_NULL)
	private String firstName;
	@Column(name="last_name")
	@JsonSerialize(include = Inclusion.NON_NULL)
	private String lastName;
	
}

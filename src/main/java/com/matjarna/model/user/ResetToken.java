package com.matjarna.model.user;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ResetToken {

	@Column(name = "token")
	private String token;

	@Column(name = "expiry_date")
	private Date expiryDate;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public String toString() {
		return "ResetToken [token=" + token + ", expiryDate=" + expiryDate + "]";
	}

}

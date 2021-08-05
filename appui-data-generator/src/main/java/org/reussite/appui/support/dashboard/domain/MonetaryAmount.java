package org.reussite.appui.support.dashboard.domain;
import java.math.BigDecimal;


public class MonetaryAmount  {
	private BigDecimal amount;
	private String currencyCode;
	

	public MonetaryAmount() {
	}
	public MonetaryAmount(BigDecimal amount, String currencyCode) {
		super();
		this.amount = amount;
		this.currencyCode=currencyCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
}

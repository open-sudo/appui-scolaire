package org.reussite.appui.support.dashboard.domain;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


public class MonetaryAmount  {
	private BigDecimal amount;
    @NotBlank(message="Currency may not be null")
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
	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}

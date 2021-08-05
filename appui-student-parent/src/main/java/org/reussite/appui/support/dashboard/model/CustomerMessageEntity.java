package org.reussite.appui.support.dashboard.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.twilio.rest.api.v2010.account.Message;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class CustomerMessageEntity extends PanacheEntityBase{
	@Id
	public String id=UUID.randomUUID().toString();
	
   	public String accountSid;
    public String apiVersion;
    @Lob
	@Type(type = "org.hibernate.type.TextType")
    public String body;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime createDate=TimeUtils.getCurrentTime();
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime lastUpdatedDate;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime sendDate;
    public Integer errorCode;
    public String errorMessage;
    public String sender;
    public String messagingServiceSid;
    public String numMedia;
    public String numSegments;
    public String sid;
    public String status;
    public String recipient;
    public String uri;	
    public String firstName;
    public String lastName;
    public String sourceType;
    public String sourceId;
    public boolean sms;
    public boolean email;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
    
	public String tenantKey;

    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime deleteDate;
	 @Transient
    @JsonIgnore
    public transient Message message;

    public CustomerMessageEntity() {
    }
    
    public CustomerMessageEntity(Message msg, String firstName, String lastName, String recipientPhone) {
    	this.accountSid=msg.getAccountSid();
    	this.apiVersion=msg.getApiVersion();
    	this.body=msg.getBody().substring(0,Math.min(500, msg.getBody().length()));
    	this.createDate=msg.getDateCreated()==null?null:msg.getDateCreated();
    	this.lastUpdatedDate=msg.getDateUpdated()==null?null:msg.getDateUpdated();
    	this.sendDate=msg.getDateSent()==null?null:msg.getDateSent();
    	this.errorCode=msg.getErrorCode();
    	this.sender=msg.getFrom().toString();
    	this.messagingServiceSid=msg.getMessagingServiceSid();
    	this.numMedia=msg.getNumMedia();
    	this.sid=msg.getSid();
    	this.uri=msg.getUri();
    	this.firstName=firstName;
    	this.lastName=lastName;
    	this.recipient=recipientPhone;
    }
   
    public CustomerMessageEntity(Message msg, StudentParentEntity parent) {
    	this(msg,parent.firstName,parent.lastName,parent.phoneNumber);
    	this.sourceType=StudentParentEntity.class.getSimpleName().toLowerCase();
    	this.sourceId=parent.id;
    }
    @Transient
    public Message getMessage() {
    	return message;
    }
    
    public void setMessage(Message message) {
    	this.message=message;
    }
    

	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this); 
	}
}

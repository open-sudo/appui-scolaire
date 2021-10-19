package org.reussite.appui.support.dashboard.model;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;



@Entity(name = "StudentProfile")
@Table(name = "student_profile")
public class StudentProfileEntity extends PanacheEntityBase{
	@Id
	public String id;
	
	public String email;
	public String lastName;
	public String firstName;
	public String studentParentId;
	public Integer grade=1;
	public String conferenceUrl;

	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}

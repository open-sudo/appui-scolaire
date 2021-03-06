package org.reussite.appui.support.dashboard.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;



@Entity(name = "Course")
@Table(name = "course")
public class CourseEntity extends PanacheEntityBase{

	@Id
	public String id;
    @NotNull(message="Subject may not be null")
    @ManyToOne
	public SubjectEntity subject;
    public String name;
    @ElementCollection(fetch = FetchType.EAGER)
  	public Set<Integer>  grades= new HashSet<Integer>();
   
    @Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}


}

package org.reussite.appui.support.dashboard.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;



@Entity(name = "Course")
@Table(name = "course")
public class CourseEntity extends PanacheEntityBase{

	@Id
	public String id;
    @NotBlank(message="Subject may not be null")
	public String subject;
    public String name;
    @ElementCollection(fetch = FetchType.EAGER)
  	public Set<Integer>  grades= new HashSet<Integer>();
   
	


}

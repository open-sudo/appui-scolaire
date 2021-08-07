package org.reussite.appui.support.dashboard.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


@Entity(name = "Subject")
@Table(name = "subject")
public class SubjectEntity  extends PanacheEntityBase {
	

	@Id
	public String id;
	public String name;
	public String language;
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,
	    		ToStringStyle.MULTI_LINE_STYLE); 
	}

}

package org.reussite.appui.support.dashboard.model;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;



@Entity(name = "Course")
@Table(name = "course")
public class CourseEntity extends PanacheEntityBase{

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	public String id;
	

    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime createDate=TimeUtils.getCurrentTime();
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime deleteDate;
	
    @NotNull(message="Subject may not be null")
    @ManyToOne
	public SubjectEntity subject;
    @ElementCollection(fetch = FetchType.EAGER)
	public Set<Integer>  grades= new HashSet<Integer>();
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
    
    public Integer durationInMinutes=120;
    public String imageUrl;
    public String name;
    public String language;
    
    @OneToMany
	public Collection<MonetaryAmountEntity>prices= new HashSet<MonetaryAmountEntity>();

	public  static List<CourseEntity> findByTag(String tag, ZonedDateTime createDate) {
		return find("select t from Course t, IN(t.tags.name) f where ?1 = lower(f) AND t.deleteDate IS NULL AND f.createDate > ?2",
				Sort.by("createDate").descending(),tag.toLowerCase(),createDate).list();
	}
	public List<CourseEntity> findByTenantAfter(String tenantKey, ZonedDateTime after){
		return find("tenantKey=?1 and sessionDate > ?2",tenantKey.toLowerCase(),after).list();
	}
	
	public List<CourseEntity> findByTenantAfterAndGrade(String tenantKey, Integer grade,ZonedDateTime after){
		return find("lower(tenantKey) = ?1 and sessionDate > ?2 and ?3 in grades",tenantKey,after,grade).list();
	}

	public  List<CourseEntity> findBySubjectAndTenantKey(String subject, String tenantKey) {
		return find("lower(tenantKey)= ?1 and lower(subject) = ?2",tenantKey.toLowerCase(),subject.toLowerCase()).list();
	}
	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}

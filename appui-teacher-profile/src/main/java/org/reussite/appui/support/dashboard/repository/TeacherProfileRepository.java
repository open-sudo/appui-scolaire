package org.reussite.appui.support.dashboard.repository;



import java.time.ZonedDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class TeacherProfileRepository implements PanacheRepositoryBase<TeacherProfileEntity, String> {
	
	

	public  TeacherProfileEntity findByConferenceUrl(String path) {
		return find("conferenceUrl like lower(concat('%', concat(?1,'%')))",path.toLowerCase()).firstResult();
	}

	public  TeacherProfileEntity findByExactConferenceUrl(String path) {
		return find("conferenceUrl = lower(?1)",path.toLowerCase()).firstResult();
	}

	public  TeacherProfileEntity findByEmail(String email) {
		return find(" (lower(externalEmail) = ?1 or lower(internalEmail) = ?1)",email.toLowerCase()).firstResult();
	}
	public  List<TeacherProfileEntity> findByTag(String tag, ZonedDateTime createDate) {
		return find("select t from TeacherProfile t, IN(t.tags.name) f where ?1 = lower(f) AND t.deleteDate IS NULL AND f.createDate > ?2",
				Sort.by("createDate").descending(),tag.toLowerCase(),createDate).list();
	}

	public TeacherProfileEntity findByPhoneNumber(String phoneNumber) {
		 return find("phoneNumber = ?1",phoneNumber).firstResult();
	}

}

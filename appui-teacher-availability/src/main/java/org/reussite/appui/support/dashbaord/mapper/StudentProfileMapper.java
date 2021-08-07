package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;

@Mapper(componentModel = "cdi")
public interface StudentProfileMapper {
	StudentProfileEntity toEntity(StudentProfile domain);
	StudentProfile toDomain(StudentProfileEntity entity);
	void updateModel(StudentProfile domain, @MappingTarget StudentProfileEntity entity);

}

package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;

@Mapper(componentModel = "cdi")
public interface TeacherProfileMapper {

	TeacherProfileEntity toEntity(TeacherProfile domain);
	TeacherProfile toDomain(TeacherProfileEntity entity);
	void updateModel(TeacherProfile domain, @MappingTarget TeacherProfileEntity entity);

}

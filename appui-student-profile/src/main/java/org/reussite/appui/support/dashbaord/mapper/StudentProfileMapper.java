package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;

@Mapper(componentModel = "CDI")
public interface StudentProfileMapper {

	StudentProfileEntity toEntity(StudentProfile domain);

    @Mapping(source = "entity.parent.id", target = "parentId")
	StudentProfile toDomain(StudentProfileEntity entity);
	void updateModel(StudentProfile domain, @MappingTarget StudentProfileEntity entity);

}

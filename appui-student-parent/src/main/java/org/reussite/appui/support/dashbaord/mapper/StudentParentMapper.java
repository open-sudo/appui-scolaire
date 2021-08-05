package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;

@Mapper(componentModel = "CDI",unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)

public interface StudentParentMapper {

	StudentParentEntity toEntity(StudentParent domain);

	StudentParent toDomain(StudentParentEntity entity);
	void updateModel(StudentParent domain, @MappingTarget StudentParentEntity entity);

}

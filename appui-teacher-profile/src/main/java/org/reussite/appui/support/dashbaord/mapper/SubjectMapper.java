package org.reussite.appui.support.dashbaord.mapper;



import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Subject;
import org.reussite.appui.support.dashboard.model.SubjectEntity;

@Mapper(componentModel = "cdi")
public interface SubjectMapper {

	SubjectEntity toEntity(Subject domain);
	Subject toDomain(SubjectEntity entity);
	void updateModel(Subject domain, @MappingTarget SubjectEntity entity);
	
	
}

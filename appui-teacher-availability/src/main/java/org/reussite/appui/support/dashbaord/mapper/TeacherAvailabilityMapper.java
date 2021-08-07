package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.TeacherAvailability;
import org.reussite.appui.support.dashboard.model.TeacherAvailabilityEntity;

@Mapper(componentModel = "CDI", uses = {TeacherProfileMapper.class,ScheduleMapper.class})
public interface TeacherAvailabilityMapper {

	TeacherAvailabilityEntity toEntity(TeacherAvailability domain);

	TeacherAvailability toDomain(TeacherAvailabilityEntity entity);
    
	void updateModel(TeacherAvailability domain, @MappingTarget TeacherAvailabilityEntity entity);

}

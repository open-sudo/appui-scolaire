package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.TeacherAvailability;
import org.reussite.appui.support.dashboard.model.TeacherAvailabilityEntity;

@Mapper(componentModel = "CDI")
public interface TeacherAvailabilityMapper {

	TeacherAvailabilityEntity toEntity(TeacherAvailability domain);

    @Mapping(source = "entity.teacherProfile.id", target = "teacherProfileId")
    @Mapping(source = "entity.schedule.id", target = "scheduleId")
	TeacherAvailability toDomain(TeacherAvailabilityEntity entity);
    
	void updateModel(TeacherAvailability domain, @MappingTarget TeacherAvailabilityEntity entity);

}

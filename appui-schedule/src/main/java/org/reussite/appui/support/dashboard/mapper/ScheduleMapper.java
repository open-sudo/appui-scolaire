package org.reussite.appui.support.dashboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;

@Mapper(componentModel = "cdi")
public interface ScheduleMapper {
	@Mapping(source = "domain.courseId", target = "course.id")
	ScheduleEntity toEntity(Schedule domain);
    @Mapping(source = "entity.course.id", target = "courseId")
	Schedule toDomain(ScheduleEntity entity);
	void updateModel(Schedule domain, @MappingTarget ScheduleEntity entity);

}

package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;

@Mapper(componentModel = "cdi")
public interface ScheduleMapper {
	ScheduleEntity toEntity(Schedule domain);
	void updateModel(Schedule domain, @MappingTarget ScheduleEntity entity);
	Schedule toDomain(ScheduleEntity entity);

}

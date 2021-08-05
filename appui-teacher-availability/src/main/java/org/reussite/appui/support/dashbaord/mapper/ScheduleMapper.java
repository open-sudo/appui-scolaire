package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;

@Mapper(componentModel = "CDI")
public interface ScheduleMapper {
	ScheduleEntity toEntity(Schedule domain);

	Schedule toDomain(ScheduleEntity entity);
	void updateModel(Schedule domain, @MappingTarget ScheduleEntity entity);

}

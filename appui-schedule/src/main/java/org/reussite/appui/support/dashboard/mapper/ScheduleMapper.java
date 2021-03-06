package org.reussite.appui.support.dashboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "cdi", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS )
public interface ScheduleMapper {
	ScheduleEntity toEntity(Schedule domain);
	Schedule toDomain(ScheduleEntity entity);
    
    @Mapping(ignore = true, target = "createDate")
    @Mapping(ignore = true, target = "lastUpdateDate")
    @Mapping(ignore = true, target = "id")
    void updateModel(Schedule domain, @MappingTarget ScheduleEntity entity);

}

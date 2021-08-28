package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.reussite.appui.support.dashboard.domain.TeacherNotification;
import org.reussite.appui.support.dashboard.model.TeacherNotificationEntity;

@Mapper(componentModel = "CDI", uses= {TeacherProfileMapper.class,
		StudentParentMapper.class})
public interface TeacherNotificationMapper {

	TeacherNotificationEntity toEntity(TeacherNotification domain);

	TeacherNotification toDomain(TeacherNotificationEntity entity);

}

package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.reussite.appui.support.dashboard.domain.ParentNotification;
import org.reussite.appui.support.dashboard.model.ParentNotificationEntity;

@Mapper(componentModel = "CDI", uses= {TeacherProfileMapper.class,
		StudentParentMapper.class})
public interface ParentNotificationMapper {

	ParentNotificationEntity toEntity(ParentNotification domain);

	ParentNotification toDomain(ParentNotificationEntity entity);

}

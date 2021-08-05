package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;

@Mapper(componentModel = "cdi")
public interface StudentBookingMapper {

	StudentBookingEntity toEntity(StudentBooking domain);

	@Mapping(source = "entity.studentProfile.id", target = "studentProfileId")
    @Mapping(source = "entity.schedule.id", target = "scheduleId")
	StudentBooking toDomain(StudentBookingEntity entity);

	void updateModel(StudentBooking domain, @MappingTarget StudentBookingEntity entity);

}

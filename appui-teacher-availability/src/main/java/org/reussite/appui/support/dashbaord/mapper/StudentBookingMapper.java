package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;

@Mapper(componentModel = "CDI", uses = { StudentProfileMapper.class, TeacherAvailabilityMapper.class})
public interface StudentBookingMapper {

	
	StudentBookingEntity toEntity(StudentBooking domain);

	StudentBooking toDomain(StudentBookingEntity entity);
	void updateModel(StudentBooking domain, @MappingTarget StudentBookingEntity entity);

}

package org.reussite.appui.support.dashboard.mapper;



import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.model.CourseEntity;

@Mapper(componentModel = "cdi", uses = {MonetaryAmountMapper.class})
public interface CourseMapper {

	CourseEntity toEntity(Course domain);
	Course toDomain(CourseEntity entity);
	void updateModel(Course domain, @MappingTarget CourseEntity entity);
	
	
}

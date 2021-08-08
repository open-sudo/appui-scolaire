package org.reussite.appui.support.dashboard.mapper;



import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.model.CourseEntity;

@Mapper(componentModel = "cdi", uses = {MonetaryAmountMapper.class, SubjectMapper.class})
public interface CourseMapper {

	CourseEntity toEntity(Course domain);
	Course toDomain(CourseEntity entity);
	
	@Mapping(ignore = true, target = "createDate")
	@Mapping(ignore = true, target = "id")
	@Mapping(ignore = true, target = "lastUpdateDate")
	
	void updateModel(Course domain, @MappingTarget CourseEntity entity);
	
	
}

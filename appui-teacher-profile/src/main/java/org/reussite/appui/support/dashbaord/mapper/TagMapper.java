package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Tag;
import org.reussite.appui.support.dashboard.model.TagEntity;

@Mapper(componentModel = "cdi")
public interface TagMapper {

	TagEntity toEntity(Tag domain);
	
	
	Tag toDomain(TagEntity entity);
	void updateModel(Tag domain, @MappingTarget TagEntity entity);

}

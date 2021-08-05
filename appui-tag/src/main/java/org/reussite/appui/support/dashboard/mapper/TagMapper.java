package org.reussite.appui.support.dashboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.TagRequest;
import org.reussite.appui.support.dashboard.domain.TagResponse;
import org.reussite.appui.support.dashboard.model.TagEntity;

@Mapper(componentModel = "CDI")
public interface TagMapper {

	TagEntity toEntity(TagRequest domain);
	TagResponse toDomain(TagEntity entity);
	void updateModel(TagRequest domain, @MappingTarget TagEntity entity);

}

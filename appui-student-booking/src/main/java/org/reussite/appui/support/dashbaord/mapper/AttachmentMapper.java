package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.reussite.appui.support.dashboard.domain.Attachment;
import org.reussite.appui.support.dashboard.model.AttachmentEntity;

@Mapper(componentModel = "cdi", uses=SubjectMapper.class)
public interface AttachmentMapper {
	AttachmentEntity toEntity(Attachment domain);
	Attachment toDomain(AttachmentEntity entity);
	void updateModel(Attachment domain, @MappingTarget AttachmentEntity entity);

}

package org.reussite.appui.support.dashbaord.mapper;

import org.mapstruct.Mapper;
import org.reussite.appui.support.dashboard.domain.TeacherComment;
import org.reussite.appui.support.dashboard.model.TeacherCommentEntity;

@Mapper(componentModel = "CDI")
public interface TeacherCommentMapper {

	TeacherCommentEntity toEntity(TeacherComment domain);

	TeacherComment toDomain(TeacherCommentEntity entity);

}

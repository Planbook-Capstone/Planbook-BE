package com.BE.mapper;

import com.BE.model.entity.LessonPlanNode;
import com.BE.model.request.CreateLessonPlanNodeRequest;
import com.BE.model.request.UpdateLessonPlanNodeRequest;
import com.BE.model.response.LessonPlanNodeDTO;
import org.mapstruct.*;


import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonPlanNodeMapper {

    // ⚠️ Không ánh xạ children ở đây (tránh đệ quy), sẽ xử lý bên ngoài
    @Mapping(target = "children", ignore = true)
    LessonPlanNodeDTO toDTO(LessonPlanNode entity);

    // Flat list
    List<LessonPlanNodeDTO> toDTOList(List<LessonPlanNode> entities);

    LessonPlanNode toEntity(CreateLessonPlanNodeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(@MappingTarget LessonPlanNode entity, UpdateLessonPlanNodeRequest request);
}

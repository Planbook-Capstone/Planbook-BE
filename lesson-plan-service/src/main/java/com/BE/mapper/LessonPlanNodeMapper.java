package com.BE.mapper;

import com.BE.model.entity.LessonPlanNode;
import com.BE.model.request.CreateLessonPlanNodeRequest;
import com.BE.model.request.UpdateLessonPlanNodeRequest;
import com.BE.model.response.LessonPlanNodeDTO;
import org.mapstruct.*;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LessonPlanNodeMapper {

    // ⚠️ Không ánh xạ children ở đây (tránh đệ quy), sẽ xử lý bên ngoài
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "children", ignore = true)
    LessonPlanNodeDTO toDTO(LessonPlanNode entity);


    default LessonPlanNodeDTO toTreeDTO(LessonPlanNode entity) {
        if (entity == null) return null;

        LessonPlanNodeDTO dto = toDTO(entity);

        List<LessonPlanNodeDTO> childDTOs = entity.getChildren().stream()
                .sorted(Comparator.comparingInt(LessonPlanNode::getOrderIndex))
                .map(this::toTreeDTO)
                .collect(Collectors.toList());

        dto.setChildren(childDTOs);
        return dto;
    }

    /**
     ○  Chuyển danh sách các root node thành DTO dạng cây
     */
    default List<LessonPlanNodeDTO> toDTOList(List<LessonPlanNode> entities) {
        return entities.stream()
                .map(this::toTreeDTO)
                .collect(Collectors.toList());
    }


//    List<LessonPlanNodeDTO> toDTOList(List<LessonPlanNode> entities);

    LessonPlanNode toEntity(CreateLessonPlanNodeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(@MappingTarget LessonPlanNode entity, UpdateLessonPlanNodeRequest request);
}
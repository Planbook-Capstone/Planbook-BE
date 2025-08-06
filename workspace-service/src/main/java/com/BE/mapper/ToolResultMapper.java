package com.BE.mapper;

import com.BE.model.entity.ToolResult;
import com.BE.model.request.CreateToolResultRequest;
import com.BE.model.request.UpdateToolResultRequest;
import com.BE.model.response.ToolResultResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Mapper để chuyển đổi giữa ToolResult Entity và các DTO
 */
@Mapper(componentModel = "spring")
public interface ToolResultMapper {
    
    ToolResultMapper INSTANCE = Mappers.getMapper(ToolResultMapper.class);

    /**
     * Chuyển đổi từ Entity sang Response DTO
     */
    ToolResultResponse toResponse(ToolResult entity);

    /**
     * Chuyển đổi từ CreateRequest sang Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ToolResult toEntity(CreateToolResultRequest request);

    /**
     * Cập nhật Entity từ UpdateRequest, bỏ qua các field null
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "academicYearId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateToolResultRequest request, @MappingTarget ToolResult entity);
}

package com.BE.mapper;

import com.BE.model.entity.ExamResultDetail;
import com.BE.model.entity.ExamSubmission;
import com.BE.model.response.ExamResultDetailData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExamResultDetailMapper {

    /**
     * Map ExamResultDetailData to ExamResultDetail entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submission", source = "submission")
    @Mapping(target = "questionId", source = "detailData.questionId")
    @Mapping(target = "studentAnswer", source = "detailData.studentAnswer")
    @Mapping(target = "correctAnswer", source = "detailData.correctAnswer")
    @Mapping(target = "isCorrect", source = "detailData.isCorrect")
    ExamResultDetail toEntity(ExamResultDetailData detailData, ExamSubmission submission);

    /**
     * Map list of ExamResultDetailData to list of ExamResultDetail entities
     */
    default List<ExamResultDetail> toEntityList(List<ExamResultDetailData> detailDataList, ExamSubmission submission) {
        return detailDataList.stream()
                .map(detail -> toEntity(detail, submission))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Map ExamResultDetail entity to ExamResultDetailData
     */
    ExamResultDetailData toData(ExamResultDetail entity);

    /**
     * Map list of ExamResultDetail entities to list of ExamResultDetailData
     */
    List<ExamResultDetailData> toDataList(List<ExamResultDetail> entities);
}

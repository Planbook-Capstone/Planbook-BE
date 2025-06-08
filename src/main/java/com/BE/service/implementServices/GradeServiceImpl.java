package com.BE.service.implementServices;


import com.BE.enums.StatusEnum;
import com.BE.mapper.GradeMapper;
import com.BE.model.entity.Grade;
import com.BE.model.request.GradeRequest;
import com.BE.model.response.GradeResponse;
import com.BE.repository.GradeRepository;
import com.BE.service.interfaceServices.IGradeService;
import com.BE.utils.DateNowUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeServiceImpl implements IGradeService {


    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    GradeMapper gradeMapper;


    @Override
    public GradeResponse createGrade(GradeRequest request) {
        // Map request -> entity
        Grade grade = gradeMapper.toGrade(request);

        // Set thời gian tạo
        grade.setCreatedAt(dateNowUtils.dateNow());

        // Set status mặc định nếu có logic (tuỳ theo enum của bạn)
        grade.setStatus(StatusEnum.ACTIVE); // hoặc StatusEnum.PENDING,...

        // Lưu DB
        try {
            grade = gradeRepository.save(grade);
        }catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Duplicate GradeName");
        }

        // Map entity -> response
        return gradeMapper.toGradeResponse(grade);
    }

}

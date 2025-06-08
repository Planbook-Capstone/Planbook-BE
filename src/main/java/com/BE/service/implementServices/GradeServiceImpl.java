package com.BE.service.implementServices;


import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.GradeMapper;
import com.BE.model.entity.Grade;
import com.BE.model.request.GradeRequest;
import com.BE.model.request.StatusRequest;
import com.BE.model.response.GradeResponse;
import com.BE.repository.GradeRepository;
import com.BE.service.interfaceServices.IGradeService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    PageUtil pageUtil;


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

    @Override
    public Page<GradeResponse> getAllGrades(int page, int size, String search, String status, String sortBy, String sortDirection) {
        Pageable pageable = pageUtil.getPageable(page, size, sortBy, sortDirection);

        Page<Grade> gradesPage;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        if (hasSearch && hasStatus) {
            StatusEnum statusEnum = null;
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status + ". Must be ACTIVE or INACTIVE.");
            }
            gradesPage = gradeRepository.findByNameContainingIgnoreCaseAndStatus(search.trim(), statusEnum, pageable);
        } else if (hasSearch) {
            gradesPage = gradeRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
        } else if (hasStatus) {
            StatusEnum statusEnum = null;
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status + ". Must be ACTIVE or INACTIVE.");
            }
            gradesPage = gradeRepository.findByStatus(statusEnum, pageable);
        } else {
            gradesPage = gradeRepository.findAll(pageable);
        }

        return gradesPage.map(gradeMapper::toGradeResponse);
    }

    @Override
    public GradeResponse getGradeById(long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade not found with ID: " + id));
        return gradeMapper.toGradeResponse(grade);
    }

    @Override
    public GradeResponse updateGrade(long id, GradeRequest request) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade not found with ID: " + id));

        // CHỈ CẬP NHẬT TÊN NẾU THAY ĐỔI
        if (!existingGrade.getName().equalsIgnoreCase(request.getName())) {
            existingGrade.setName(request.getName());
        }
        existingGrade.setUpdatedAt(dateNowUtils.dateNow());

        try {
            Grade updatedGrade = gradeRepository.save(existingGrade);
            return gradeMapper.toGradeResponse(updatedGrade);
        } catch (DataIntegrityViolationException e) {
            // Kiểm tra xem lỗi có phải do unique constraint trên cột 'name' không
            // (Điều này có thể phức tạp tùy thuộc vào loại DB và thông báo lỗi cụ thể)
            // Một cách đơn giản hơn là giả định nếu có lỗi ở đây thì là trùng tên.
            throw new DataIntegrityViolationException("Grade with name '" + request.getName() + "' already exists.");
        }
    }

    @Override
    public GradeResponse changeGradeStatus(long id, String newStatus) { // THAY ĐỔI Ở ĐÂY: Nhận String newStatus
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade not found with ID: " + id));

        StatusEnum statusEnum = null;
        try {
            statusEnum = StatusEnum.valueOf(newStatus.toUpperCase()); // Chuyển đổi String sang StatusEnum
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus + ". Must be ACTIVE or INACTIVE.");
        }

        existingGrade.setStatus(statusEnum);
        existingGrade.setUpdatedAt(dateNowUtils.dateNow());

        Grade updatedGrade = gradeRepository.save(existingGrade);
        return gradeMapper.toGradeResponse(updatedGrade);
    }



}

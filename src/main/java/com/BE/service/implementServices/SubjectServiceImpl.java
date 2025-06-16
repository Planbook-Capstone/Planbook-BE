package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.SubjectMapper;
import com.BE.model.entity.Grade;
import com.BE.model.entity.Subject;
import com.BE.model.request.SubjectRequest;
import com.BE.model.response.SubjectResponse;
import com.BE.repository.GradeRepository;
import com.BE.repository.SubjectRepository;
import com.BE.service.interfaceServices.ISubjectService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectServiceImpl implements ISubjectService {

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    GradeRepository gradeRepository; // Cần để tìm Grade theo ID

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    SubjectMapper subjectMapper;

    @Autowired
    PageUtil pageUtil;

    @Override
    public SubjectResponse createSubject(SubjectRequest request) {
        // Kiểm tra tên môn học đã tồn tại trong cùng một Grade chưa
        // Nên dùng DuplicateResourceException để consistent với API
        if (subjectRepository.findByNameAndGradeId(request.getName().trim(), request.getGradeId()).isPresent()) {
            throw new DataIntegrityViolationException("Môn học có tên '" + request.getName() + "' đã tồn tại trong lớp có ID: " + request.getGradeId());
        }

        Grade grade = gradeRepository.findById(request.getGradeId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lớp có ID: " + request.getGradeId()));

        Subject subject = subjectMapper.toSubject(request);
        subject.setGrade(grade); // Set Grade entity
        subject.setStatus(StatusEnum.ACTIVE); // Mặc định là ACTIVE khi tạo
        subject.setCreatedAt(dateNowUtils.dateNow());
        subject.setUpdatedAt(dateNowUtils.dateNow()); // Đảm bảo set updatedAt ở đây luôn

        Subject savedSubject = subjectRepository.save(subject);
        return subjectMapper.toSubjectResponse(savedSubject);
    }

    @Override
    public Page<SubjectResponse> getAllSubjects(int page, int size, String search, String status, String sortBy, String sortDirection) {
//        pageUtil.checkOffset(page); // Kiểm tra số trang
        Pageable pageable = pageUtil.getPageable(page, size, sortBy, sortDirection);

        StatusEnum statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Giá trị trạng thái không hợp lệ: " + status + ". Phải là ACTIVE hoặc INACTIVE.");
            }
        }

        // Đảm bảo truyền search parameter đúng cách nếu nó là null hoặc rỗng
        Page<Subject> subjectsPage = subjectRepository.findAllSubjects(
                (search != null && !search.trim().isEmpty()) ? search : null, // Truyền null nếu search rỗng
                statusEnum,
                pageable
        );
        return subjectsPage.map(subjectMapper::toSubjectResponse);
    }

    @Override
    public SubjectResponse getSubjectById(long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy môn học có ID: " + id));
        return subjectMapper.toSubjectResponse(subject);
    }

    @Override
    public SubjectResponse updateSubject(long id, SubjectRequest request) {
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy môn học có ID: " + id));

        // Kiểm tra xem Grade ID có thay đổi không
        // SỬA LỖI Ở ĐÂY: So sánh primitive long với Long wrapper
        if (existingSubject.getGrade().getId() != request.getGradeId()) {
            Grade newGrade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy lớp mới có ID: " + request.getGradeId()));
            existingSubject.setGrade(newGrade);
        }

        // Kiểm tra tên môn học có trùng với môn khác trong CÙNG grade không
        // Logic này cần được xem xét kỹ:
        // 1. Tên có thay đổi so với tên cũ của subject không?
        // 2. Grade có thay đổi không?
        // Nếu tên thay đổi HOẶC grade thay đổi (hoặc cả hai), thì mới cần kiểm tra trùng lặp
        boolean nameChanged = !existingSubject.getName().equalsIgnoreCase(request.getName());
        boolean gradeChanged = existingSubject.getGrade().getId() != request.getGradeId(); // SỬA LỖI Ở ĐÂY tương tự

        if (nameChanged || gradeChanged) { // Chỉ kiểm tra khi có thay đổi liên quan đến tên hoặc Grade
            Optional<Subject> duplicateSubject = subjectRepository.findByNameAndGradeIdAndIdNot(request.getName(), request.getGradeId(), id);
            if (duplicateSubject.isPresent()) {
                throw new DataIntegrityViolationException("Môn học có tên '" + request.getName() + "' đã tồn tại trong lớp có ID: " + request.getGradeId());
            }
        }

        existingSubject.setName(request.getName());
        existingSubject.setUpdatedAt(dateNowUtils.dateNow());

        try {
            Subject updatedSubject = subjectRepository.save(existingSubject);
            return subjectMapper.toSubjectResponse(updatedSubject);
        } catch (DataIntegrityViolationException e) {
            // Đây là lỗi từ DB (ví dụ: unique constraint) - xử lý race condition
            // Nên throw DuplicateResourceException để consistent với createSubject
            throw new DataIntegrityViolationException("Không thể cập nhật môn học: Môn học có tên '" + request.getName() + "' đã tồn tại trong lớp có ID: " + request.getGradeId() + " (Có thể xung đột).");
        }
    }

    @Override
    public SubjectResponse changeSubjectStatus(long id, String newStatus) {
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy môn học có ID: " + id));

        StatusEnum statusEnum = null;
        try {
            statusEnum = StatusEnum.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Giá trị trạng thái không hợp lệ: " + newStatus + ". Phải là ACTIVE hoặc INACTIVE.");
        }

        existingSubject.setStatus(statusEnum);
        existingSubject.setUpdatedAt(dateNowUtils.dateNow());

        Subject updatedSubject = subjectRepository.save(existingSubject);
        return subjectMapper.toSubjectResponse(updatedSubject);
    }

    @Override
    public Page<SubjectResponse> getSubjectsByGradeId(Long gradeId, int page, int size, String search, String status, String sortBy, String sortDirection) {
//        pageUtil.checkOffset(page);
        Pageable pageable = pageUtil.getPageable(page, size, sortBy, sortDirection);

        // Kiểm tra sự tồn tại của Grade
        if (!gradeRepository.existsById(gradeId)) {
            throw new NotFoundException("Không tìm thấy lớp có ID: " + gradeId);
        }

        StatusEnum statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Giá trị trạng thái không hợp lệ: " + status + ". Phải là ACTIVE hoặc INACTIVE.");
            }
        }

        // Đảm bảo truyền search parameter đúng cách nếu nó là null hoặc rỗng
        Page<Subject> subjectsPage = subjectRepository.findByGradeId(
                gradeId,
                (search != null && !search.trim().isEmpty()) ? search : null, // Truyền null nếu search rỗng
                statusEnum,
                pageable
        );
        return subjectsPage.map(subjectMapper::toSubjectResponse);
    }

}

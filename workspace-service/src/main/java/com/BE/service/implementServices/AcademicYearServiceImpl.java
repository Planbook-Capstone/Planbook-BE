package com.BE.service.implementServices;

import com.BE.enums.AcademicYearStatusEnum;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.mapper.AcademicYearMapper;
import com.BE.model.entity.AcademicYear;
import com.BE.model.request.AcademicYearRequest;
import com.BE.model.response.AcademicYearResponse;
import com.BE.repository.AcademicYearRepository;
import com.BE.service.interfaceServices.IAcademicYearService;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AcademicYearServiceImpl implements IAcademicYearService {
    @Autowired
    DateNowUtils dateNowUtils;
    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private AcademicYearMapper academicYearMapper;

    @Override
    public List<AcademicYearResponse> getAll() {
        return academicYearRepository.findAll().stream()
                .map(academicYearMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AcademicYearResponse create(AcademicYearRequest request) {
        dateNowUtils.validateAcademicYear(request);
        AcademicYear academicYear = academicYearMapper.toEntity(request);

        String yearLabel = request.getStartDate().getYear() + "-" + request.getEndDate().getYear();
        academicYear.setYearLabel(yearLabel);
        academicYear.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
        academicYear.setUpdatedAt(dateNowUtils.getCurrentDateTimeHCM());
        // Set default status
        academicYear.setStatus(AcademicYearStatusEnum.UPCOMING);
        AcademicYear saved = academicYearRepository.save(academicYear);
//        if (saved.getStatus() == AcademicYearStatusEnum.UPCOMING) {
//            List<User> users = authenRepository.findAll();
//            for (User user : users) {
//                boolean hasWorkspace = workSpaceRepository.existsByUserAndAcademicYear(user, saved);
//                if (!hasWorkspace) {
//                    WorkSpace ws = new WorkSpace();
//                    ws.setName(saved.getYearLabel() + " - " + user.getUsername());
//                    ws.setAcademicYear(saved);
//                    ws.setUser(user);
//                    workSpaceRepository.save(ws);
//                }
//            }
//        }
        return academicYearMapper.toResponse(saved);
    }

    @Override
    public AcademicYearResponse update(Long id, AcademicYearRequest request) {
        dateNowUtils.validateAcademicYear(request);
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy năm học"));
        academicYearMapper.updateAcademicYear(academicYear, request);
        // Set default status
        academicYear.setStatus(AcademicYearStatusEnum.INACTIVE);
        academicYear.setUpdatedAt(dateNowUtils.getCurrentDateTimeHCM());
        return academicYearMapper.toResponse(academicYearRepository.save(academicYear));
    }

    @Override
    public AcademicYearResponse updateStatus(Long id, AcademicYearStatusEnum status) {
        if (status == AcademicYearStatusEnum.ACTIVE) {
            AcademicYear currentActive = getActiveAcademicYear();
            if (currentActive != null && !currentActive.getId().equals(id)) {
                throw new BadRequestException(
                        "Chỉ được phép có 1 năm học ACTIVE tại một thời điểm. Hãy dừng active năm hiện tại trước!");
            }
        }
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy năm học"));
        academicYear.setStatus(status);
        academicYear.setUpdatedAt(dateNowUtils.getCurrentDateTimeHCM());
        return academicYearMapper.toResponse(academicYearRepository.save(academicYear));
    }

    @Override
    public void delete(Long id) {
        academicYearRepository.deleteById(id);
    }

    @Override
    public AcademicYear getActiveAcademicYear() {
        return academicYearRepository.findAll().stream()
                .filter(y -> y.getStatus() == AcademicYearStatusEnum.ACTIVE)
                .findFirst().orElse(null);
    }

//    @Override
//    @Transactional
//    public WorkSpace createWorkspaceForNewUser(User user) {
//        AcademicYear activeYear = getActiveAcademicYear();
//        if (activeYear != null) {
//            WorkSpace ws = new WorkSpace();
//            ws.setName(activeYear.getYearLabel() + " - " + user.getUsername());
//            ws.setAcademicYear(activeYear);
//            ws.setUser(user);
//            return ws;
//        }
//        return null;
//    }

}
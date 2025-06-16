package com.BE.service.implementServices;

import com.BE.exception.exceptions.BadRequestException;
import com.BE.mapper.WorkSpaceMapper;
import com.BE.model.entity.AcademicYear;
import com.BE.model.entity.User;
import com.BE.model.entity.WorkSpace;
import com.BE.model.request.WorkSpaceRequest;
import com.BE.model.response.WorkSpaceResponse;
import com.BE.repository.AcademicYearRepository;
import com.BE.repository.UserRepository;
import com.BE.repository.WorkSpaceRepository;
import com.BE.service.interfaceServices.IWorkSpaceService;
import com.BE.utils.AccountUtils;
import com.BE.service.interfaceServices.IAcademicYearService;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkSpaceServiceImpl implements IWorkSpaceService {
    @Autowired
    private WorkSpaceRepository workSpaceRepository;
    @Autowired
    private AcademicYearRepository academicYearRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkSpaceMapper workSpaceMapper;
    @Autowired
    private AccountUtils accountUtils;
    @Autowired
    private IAcademicYearService academicYearService;

    @Autowired
    private DateNowUtils dateNowUtils;

    @Override
    public List<WorkSpaceResponse> getAll() {
        return workSpaceRepository.findAll().stream()
                .map(workSpaceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkSpaceResponse getById(UUID id) {
        WorkSpace ws = workSpaceRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy không gian làm việc"));
        return workSpaceMapper.toResponse(ws);
    }

    @Override
    public WorkSpaceResponse create(WorkSpaceRequest request) {
        WorkSpace ws = new WorkSpace();
        ws.setName(request.getName());
        ws.setCreatedAt(dateNowUtils.dateNow());
        ws.setUpdatedAt(dateNowUtils.dateNow());
        AcademicYear ay = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy năm học"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng"));
        ws.setAcademicYear(ay);
        ws.setUser(user);
        return workSpaceMapper.toResponse(workSpaceRepository.save(ws));
    }

    @Override
    public WorkSpaceResponse update(UUID id, WorkSpaceRequest request) {
        WorkSpace ws = workSpaceRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy không gian làm việc"));
        ws.setName(request.getName());
        ws.setUpdatedAt(dateNowUtils.dateNow());
        AcademicYear ay = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy năm học"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng"));
        ws.setAcademicYear(ay);
        ws.setUser(user);
        return workSpaceMapper.toResponse(workSpaceRepository.save(ws));
    }

    @Override
    public void delete(UUID id) {
        workSpaceRepository.deleteById(id);
    }

    @Override
    public Page<WorkSpaceResponse> getAll(Pageable pageable) {
        return workSpaceRepository.findAll(pageable).map(workSpaceMapper::toResponse);
    }

    @Override
    public List<WorkSpaceResponse> getCurrentUserWorkspacesInActiveYear() {
        User user = accountUtils.getCurrentUser();
        AcademicYear activeYear = academicYearService.getActiveAcademicYear();
        if (activeYear == null)
            return List.of();
        return user.getWorkSpaces().stream()
                .filter(ws -> ws.getAcademicYear() != null
                        && ws.getAcademicYear().getId().equals(activeYear.getId()))
                .map(workSpaceMapper::toResponse)
                .toList();
    }

    @Override
    public void save(WorkSpace workSpace) {
        workSpaceRepository.save(workSpace);
    }
}
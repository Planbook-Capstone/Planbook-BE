package com.BE.service.interfaceServices;

import com.BE.model.request.WorkSpaceRequest;
import com.BE.model.response.WorkSpaceResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.BE.model.entity.WorkSpace;

public interface IWorkSpaceService {
    List<WorkSpaceResponse> getAll();

    WorkSpaceResponse getById(UUID id);

    WorkSpaceResponse create(WorkSpaceRequest request);

    WorkSpaceResponse update(UUID id, WorkSpaceRequest request);

    void delete(UUID id);

    Page<WorkSpaceResponse> getAll(Pageable pageable);

//    List<WorkSpaceResponse> getCurrentUserWorkspacesInActiveYear();

    void save(WorkSpace workSpace);
}
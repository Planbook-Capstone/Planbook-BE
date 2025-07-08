package com.BE.service.interfaceServices;

import com.BE.model.request.AcademicResourceCreateRequest;
import com.BE.model.request.AcademicResourceCreateWithFileRequest;
import com.BE.model.request.AcademicResourceSearchRequest;
import com.BE.model.request.AcademicResourceUpdateRequest;
import com.BE.model.response.AcademicResourceInternalResponse;
import com.BE.model.response.AcademicResourceResponse;
import com.BE.model.response.PagedResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface AcademicResourceService {
    AcademicResourceResponse createResource(AcademicResourceCreateRequest request);
    AcademicResourceInternalResponse createResourceInternal(MultipartFile file);
    AcademicResourceResponse createResourceWithFile(AcademicResourceCreateWithFileRequest request);
    AcademicResourceResponse getResourceById(Long id);
    PagedResponse<AcademicResourceInternalResponse> getResourcesByCreatorId(int page, int size);
    AcademicResourceResponse updateResource(Long id, AcademicResourceUpdateRequest request);
    void deleteResource(Long id);
    PagedResponse<AcademicResourceResponse> searchResources(AcademicResourceSearchRequest request);


}

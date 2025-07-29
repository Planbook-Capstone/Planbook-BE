package com.BE.service.interfaceServices;

import com.BE.model.request.CreateToolResultRequest;
import com.BE.model.request.ToolResultFilterRequest;
import com.BE.model.request.UpdateToolResultRequest;
import com.BE.model.response.ToolResultResponse;
import org.springframework.data.domain.Page;

/**
 * Interface service cho ToolResult với Specification-based filtering
 */
public interface IToolResultService {

    /**
     * Tạo mới ToolResult
     * @param request dữ liệu tạo mới
     * @return ToolResultResponse
     */
    ToolResultResponse create(CreateToolResultRequest request);

    /**
     * Cập nhật ToolResult theo id
     * @param id id của ToolResult
     * @param request dữ liệu cập nhật
     * @return ToolResultResponse
     */
    ToolResultResponse update(Long id, UpdateToolResultRequest request);

    /**
     * Lấy ToolResult theo id
     * @param id id của ToolResult
     * @return ToolResultResponse
     */
    ToolResultResponse getById(Long id);

    /**
     * Lấy danh sách ToolResult với filter linh hoạt và phân trang
     * @param filterRequest request chứa tất cả filter criteria và pagination info
     * @return Page<ToolResultResponse>
     */
    Page<ToolResultResponse> getAllWithFilter(ToolResultFilterRequest filterRequest);

    /**
     * Xóa ToolResult theo id
     * @param id id của ToolResult
     */
    void delete(Long id);

    /**
     * Kiểm tra tồn tại ToolResult theo id
     * @param id id của ToolResult
     * @return boolean
     */
    boolean existsById(Long id);

    /**
     * Đếm tổng số ToolResult
     * @return long
     */
    long count();
}

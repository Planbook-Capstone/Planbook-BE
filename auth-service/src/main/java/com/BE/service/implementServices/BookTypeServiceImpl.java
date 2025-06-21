package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.BookTypeMapper;
import com.BE.model.entity.BookType;
import com.BE.model.request.BookTypeRequest;
import com.BE.model.response.BookTypeResponse;
import com.BE.repository.BookTypeRepository;
import com.BE.service.interfaceServices.IBookTypeService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookTypeServiceImpl implements IBookTypeService {

    @Autowired
    PageUtil pageUtil;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    BookTypeMapper bookTypeMapper;

    @Autowired
    BookTypeRepository bookTypeRepository;


    @Override
    public BookTypeResponse createBookType(BookTypeRequest request) {
        // Kiểm tra tên loại sách đã tồn tại chưa
        if (bookTypeRepository.findByNameIgnoreCase(request.getName().trim()).isPresent()) {
            throw new DataIntegrityViolationException("Loại sách với tên '" + request.getName() + "' đã tồn tại.");
        }

        BookType bookType = bookTypeMapper.toBookType(request);
        if (request.getHref() == null || request.getHref().trim().isEmpty()) {
            bookType.setHref("/");
        }
        bookType.setStatus(StatusEnum.ACTIVE); // Mặc định là ACTIVE khi tạo
        bookType.setCreatedAt(dateNowUtils.dateNow());
        bookType.setUpdatedAt(dateNowUtils.dateNow());

        try {
            BookType savedBookType = bookTypeRepository.save(bookType);
            return bookTypeMapper.toBookTypeResponse(savedBookType);
        } catch (DataIntegrityViolationException e) {
            // Trường hợp hiếm gặp nếu có race condition, tên vẫn trùng
            throw new DataIntegrityViolationException("Tạo loại sách thất bại: Loại sách với tên '" + request.getName() + "' đã tồn tại (có thể do đồng thời).");
        }
    }

    @Override
    public Page<BookTypeResponse> getAllBookTypes(int page, int size, String search, String status, String sortBy, String sortDirection) {
//        pageUtil.checkOffset(page); // Kiểm tra page hợp lệ
        Pageable pageable = pageUtil.getPageable(page, size, sortBy, sortDirection);

        StatusEnum statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status + ". Giá trị hợp lệ là ACTIVE hoặc INACTIVE.");
            }
        }

        String actualSearch = (search != null && !search.trim().isEmpty()) ? search.toLowerCase() : null; // Chuyển search sang chữ thường
        Page<BookType> bookTypesPage = bookTypeRepository.findAllBookTypes(actualSearch, statusEnum, pageable);
        return bookTypesPage.map(bookTypeMapper::toBookTypeResponse);
    }

    @Override
    public BookTypeResponse getBookTypeById(UUID id) { // Quan trọng: Tham số là UUID
        BookType bookType = bookTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại sách với ID: " + id));
        return bookTypeMapper.toBookTypeResponse(bookType);
    }

    @Override
    public BookTypeResponse updateBookType(UUID id, BookTypeRequest request) { // Quan trọng: Tham số là UUID
        BookType existingBookType = bookTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại sách với ID: " + id));

        // Kiểm tra tên có trùng với loại sách khác (không phải chính nó) không
        if (!existingBookType.getName().equalsIgnoreCase(request.getName())) { // Chỉ kiểm tra nếu tên thay đổi
            Optional<BookType> duplicateBookType = bookTypeRepository.findByNameIgnoreCaseAndIdNot(request.getName(), id);
            if (duplicateBookType.isPresent()) {
                throw new DataIntegrityViolationException("Loại sách với tên '" + request.getName() + "' đã tồn tại.");
            }
        }

        // Cập nhật thông tin
        existingBookType.setName(request.getName());
        existingBookType.setDescription(request.getDescription());
        existingBookType.setPriority(request.getPriority());
        existingBookType.setHref(request.getHref());
        existingBookType.setIcon(request.getIcon());
        existingBookType.setUpdatedAt(dateNowUtils.dateNow());

        try {
            BookType updatedBookType = bookTypeRepository.save(existingBookType);
            return bookTypeMapper.toBookTypeResponse(updatedBookType);
        } catch (DataIntegrityViolationException e) {
            // Trường hợp hiếm gặp nếu có race condition, tên vẫn trùng
            throw new DataIntegrityViolationException("Cập nhật loại sách thất bại: Loại sách với tên '" + request.getName() + "' đã tồn tại (có thể do đồng thời).");
        }
    }

    @Override
    public BookTypeResponse changeBookTypeStatus(UUID id, String newStatus) { // Quan trọng: Tham số là UUID
        BookType existingBookType = bookTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại sách với ID: " + id));

        StatusEnum statusEnum = null;
        try {
            statusEnum = StatusEnum.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + newStatus + ". Giá trị hợp lệ là ACTIVE hoặc INACTIVE.");
        }

        existingBookType.setStatus(statusEnum);
        existingBookType.setUpdatedAt(dateNowUtils.dateNow());

        BookType updatedBookType = bookTypeRepository.save(existingBookType);
        return bookTypeMapper.toBookTypeResponse(updatedBookType);
    }

}
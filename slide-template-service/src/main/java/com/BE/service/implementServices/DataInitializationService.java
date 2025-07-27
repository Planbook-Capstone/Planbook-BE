package com.BE.service.implementServices;

import com.BE.enums.PlaceholderTypeEnum;
import com.BE.enums.StatusEnum;
import com.BE.model.entity.SlidePlaceholder;
import com.BE.repository.SlidePlaceholderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInitializationService implements CommandLineRunner {

    SlidePlaceholderRepository slidePlaceholderRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeSlidePlaceholders();
    }

    private void initializeSlidePlaceholders() {
        // Kiểm tra xem đã có dữ liệu chưa
        if (slidePlaceholderRepository.count() > 0) {
            return; // Đã có dữ liệu, không cần khởi tạo
        }

        // Tạo các placeholder mẫu
        createPlaceholder(PlaceholderTypeEnum.LessonName, "Tên bài học", "Tên của bài học được hiển thị trên slide");
        createPlaceholder(PlaceholderTypeEnum.LessonDescription, "Tóm tắt bài học", "Mô tả ngắn gọn về nội dung bài học");
        createPlaceholder(PlaceholderTypeEnum.CreatedDate, "Ngày tạo", "Ngày tạo bài học hoặc slide");
        createPlaceholder(PlaceholderTypeEnum.TitleName, "Tên mục lớn", "Tiêu đề của một phần lớn trong bài học");
        createPlaceholder(PlaceholderTypeEnum.TitleContent, "Nội dung mục lớn", "Nội dung chi tiết của mục lớn");
        createPlaceholder(PlaceholderTypeEnum.SubtitleName, "Tên mục nhỏ", "Tiêu đề của mục nhỏ nằm trong mục lớn");
        createPlaceholder(PlaceholderTypeEnum.SubtitleContent, "Nội dung mục nhỏ", "Nội dung chi tiết của mục nhỏ");
        createPlaceholder(PlaceholderTypeEnum.ImageName, "Tên hình ảnh", "Tên hoặc tiêu đề của hình ảnh");
        createPlaceholder(PlaceholderTypeEnum.ImageContent, "Nội dung hình ảnh", "Mô tả hoặc chú thích cho hình ảnh");
    }

    private void createPlaceholder(PlaceholderTypeEnum type, String name, String description) {
        SlidePlaceholder placeholder = new SlidePlaceholder();
        placeholder.setType(type);
        placeholder.setName(name);
        placeholder.setDescription(description);
        placeholder.setStatus(StatusEnum.ACTIVE);
        slidePlaceholderRepository.save(placeholder);
    }
}

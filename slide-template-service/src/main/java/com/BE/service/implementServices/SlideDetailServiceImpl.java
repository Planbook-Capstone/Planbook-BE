package com.BE.service.implementServices;

import com.BE.enums.PlaceholderTypeEnum;
import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.SlideDetailMapper;
import com.BE.model.entity.SlideDetail;
import com.BE.model.entity.SlidePlaceholder;
import com.BE.model.entity.SlideTemplate;
import com.BE.model.response.SlideDetailResponse;
import com.BE.repository.SlideDetailRepository;
import com.BE.repository.SlidePlaceholderRepository;
import com.BE.repository.SlideTemplateRepository;
import com.BE.service.interfaceServices.ISlideDetailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlideDetailServiceImpl implements ISlideDetailService {

    SlideDetailRepository slideDetailRepository;
    SlideTemplateRepository slideTemplateRepository;
    SlidePlaceholderRepository slidePlaceholderRepository;
    SlideDetailMapper slideDetailMapper;
    ObjectMapper objectMapper;

    @Override
    public SlideDetailResponse getSlideDetail(String id) {
        SlideDetail slideDetail = slideDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy slide detail với id: " + id));
        return slideDetailMapper.toResponse(slideDetail);
    }

    @Override
    public Map<String, Object> getSlideDetailsByTemplateId(Long templateId) {
        List<SlideDetail> slideDetails = slideDetailRepository.findBySlideTemplateId(templateId);
        Map<String, Object> result = new LinkedHashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        for (int i = 0; i < slideDetails.size(); i++) {
            SlideDetail slide = slideDetails.get(i);
            Map<String, Object> slideMap = new LinkedHashMap<>();

            slideMap.put("id", slide.getId());
            slideMap.put("title", slide.getTitle());

            try {
                Map<String, Object> parsedSlideData = mapper.readValue(slide.getSlideData(), Map.class);
                slideMap.put("slideData", parsedSlideData);
            } catch (Exception e) {
                slideMap.put("slideData", null); // hoặc slide.getSlideData() nếu muốn giữ raw string
            }

            slideMap.put("description", slide.getDescription());
            slideMap.put("status", slide.getStatus().name());
            slideMap.put("slideTemplateId", slide.getSlideTemplate().getId());
            slideMap.put("createdAt", slide.getCreatedAt());
            slideMap.put("updatedAt", slide.getUpdatedAt());

            result.put(String.valueOf(i), slideMap);
        }

        return result;
    }

    @Override
    @Transactional
    public void processSlideDetailsFromTemplate(Long templateId, String slideDataJson) {
        SlideTemplate slideTemplate = slideTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy slide template với id: " + templateId));

        // Xóa các slide detail cũ
        slideDetailRepository.deleteBySlideTemplateId(templateId);

        try {
            JsonNode rootNode = objectMapper.readTree(slideDataJson);
            JsonNode slidesNode = rootNode.get("slides");

            if (slidesNode != null && slidesNode.isArray()) {
                // Lấy danh sách tất cả placeholder types để kiểm tra
                Set<String> validPlaceholderTypes = Arrays.stream(PlaceholderTypeEnum.values())
                        .map(Enum::name)
                        .collect(Collectors.toSet());

                for (JsonNode slideNode : slidesNode) {
                    String slideId = slideNode.get("id").asText();
                    String title = slideNode.get("title").asText();

                    // Phân tích placeholder từ elements
                    String placeholderDescription = analyzePlaceholders(slideNode, validPlaceholderTypes);

                    // Tạo SlideDetail entity
                    SlideDetail slideDetail = new SlideDetail();
                    slideDetail.setId(slideId);
                    slideDetail.setTitle(title);
                    slideDetail.setSlideData(slideNode.toString());
                    slideDetail.setDescription(placeholderDescription);
                    slideDetail.setStatus(StatusEnum.ACTIVE);
                    slideDetail.setSlideTemplate(slideTemplate);

                    slideDetailRepository.save(slideDetail);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xử lý slide data JSON: " + e.getMessage(), e);
        }
    }

    private String analyzePlaceholders(JsonNode slideNode, Set<String> validPlaceholderTypes) {
        List<String> placeholderList = new ArrayList<>();
        Map<String, Integer> mainPointCounts = new HashMap<>();
        Map<String, Map<Integer, Integer>> subPointCounts = new HashMap<>();

        JsonNode elementsNode = slideNode.get("elements");
        if (elementsNode != null && elementsNode.isArray()) {
            for (JsonNode element : elementsNode) {
                if ("text".equals(element.get("type").asText())) {
                    String text = element.get("text").asText();

                    // Phân tích text để tìm placeholder với format mới
                    PlaceholderInfo placeholderInfo = extractPlaceholderInfo(text);
                    if (placeholderInfo != null && validPlaceholderTypes.contains(placeholderInfo.name)) {
                        String placeholderDescription = createPlaceholderDescription(placeholderInfo);
                        if (placeholderDescription != null) {
                            placeholderList.add(placeholderDescription);
                        }

                        // Đếm để tạo các placeholder liên quan
                        updatePlaceholderCounts(placeholderInfo, mainPointCounts, subPointCounts);
                    }
                }
            }
        }

        // Sắp xếp và trả về danh sách placeholder
        Collections.sort(placeholderList);
        return String.join(", ", placeholderList);
    }

    // Inner class để lưu thông tin placeholder
    private static class PlaceholderInfo {
        String name;
        Integer position;
        Integer subPosition;
        Integer maxLength;

        PlaceholderInfo(String name, Integer position, Integer subPosition, Integer maxLength) {
            this.name = name;
            this.position = position;
            this.subPosition = subPosition;
            this.maxLength = maxLength;
        }
    }

    private PlaceholderInfo extractPlaceholderInfo(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        // Tách text theo khoảng trắng và lấy phần đầu tiên
        String[] parts = text.trim().split("\\s+");
        if (parts.length > 0) {
            String placeholderPart = parts[0];

            // Kiểm tra format: TitleName_80 hoặc MainPointName_1_80 hoặc
            // SubPointContent_1_1_80
            String[] placeholderParts = placeholderPart.split("_");

            if (placeholderParts.length >= 2) {
                String name = placeholderParts[0];

                // Kiểm tra xem có phải là placeholder hợp lệ không
                try {
                    PlaceholderTypeEnum.valueOf(name);

                    if ("TitleName".equals(name) || "LessonName".equals(name) || "LessonDescription".equals(name) || "CreatedDate".equals(name)) {
                        // Format: TitleName_80
                        if (placeholderParts.length == 2) {
                            Integer maxLength = Integer.parseInt(placeholderParts[1]);
                            return new PlaceholderInfo(name, null, null, maxLength);
                        }
                    } else if ("MainPointName".equals(name) || "MainPointContent".equals(name)) {
                        // Format: MainPointName_1_80
                        if (placeholderParts.length == 3) {
                            Integer position = Integer.parseInt(placeholderParts[1]);
                            Integer maxLength = Integer.parseInt(placeholderParts[2]);
                            return new PlaceholderInfo(name, position, null, maxLength);
                        }
                    } else if ("SubPointName".equals(name) || "SubPointContent".equals(name)) {
                        // Format: SubPointContent_1_1_80
                        if (placeholderParts.length == 4) {
                            Integer position = Integer.parseInt(placeholderParts[1]);
                            Integer subPosition = Integer.parseInt(placeholderParts[2]);
                            Integer maxLength = Integer.parseInt(placeholderParts[3]);
                            return new PlaceholderInfo(name, position, subPosition, maxLength);
                        }
                    } else {
                        // Các placeholder khác giữ nguyên logic cũ
                        return new PlaceholderInfo(name, null, null, null);
                    }
                } catch (IllegalArgumentException e) {
                    // Không phải placeholder hợp lệ hoặc format số không đúng
                    return null;
                }
            }
        }
        return null;
    }

    private String createPlaceholderDescription(PlaceholderInfo info) {
        if (info == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(info.name);

        if (info.position != null) {
            sb.append("_").append(info.position);
        }

        if (info.subPosition != null) {
            sb.append("_").append(info.subPosition);
        }

        if (info.maxLength != null) {
            sb.append("_").append(info.maxLength);
        }

        return sb.toString();
    }

    private void updatePlaceholderCounts(PlaceholderInfo info,
            Map<String, Integer> mainPointCounts,
            Map<String, Map<Integer, Integer>> subPointCounts) {
        // Logic này có thể được mở rộng trong tương lai nếu cần
        // Hiện tại chỉ để placeholder cho việc mở rộng
    }
}

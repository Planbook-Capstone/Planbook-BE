//package com.BE.config;
//
//import com.BE.enums.NodeType;
//import com.BE.model.entity.LessonPlanNode;
//import com.BE.repository.LessonPlanNodeRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Data seeder to create sample lesson plan tree structure
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataSeeder implements CommandLineRunner {
//
//    private final LessonPlanNodeRepository lessonPlanNodeRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        try {
//            // Only seed if no data exists
//            if (lessonPlanNodeRepository.countByLessonPlanId(101L) == 0) {
//                seedSampleData();
//            }
//        } catch (Exception e) {
//            log.warn("Could not seed data - table may not exist yet: {}", e.getMessage());
//            // Skip seeding if table doesn't exist
//        }
//    }
//
//    private void seedSampleData() {
//        log.info("Seeding sample lesson plan tree data...");
//
//        // Create root section: Mục tiêu bài học
//        LessonPlanNode objectiveSection = createNode(
//                101L, null, "Mục tiêu bài học",
//                "Xác định rõ những gì học sinh cần đạt được sau bài học",
//                NodeType.SECTION, 1, createMetadata("icon", "target")
//        );
//        objectiveSection = lessonPlanNodeRepository.save(objectiveSection);
//
//        // Create subsections under objectives
//        LessonPlanNode knowledgeSubsection = createNode(
//                101L, objectiveSection, "Kiến thức",
//                "Những kiến thức cần truyền đạt cho học sinh",
//                NodeType.SUBSECTION, 1, null
//        );
//        knowledgeSubsection = lessonPlanNodeRepository.save(knowledgeSubsection);
//
//        LessonPlanNode skillSubsection = createNode(
//                101L, objectiveSection, "Kỹ năng",
//                "Những kỹ năng học sinh cần phát triển",
//                NodeType.SUBSECTION, 2, null
//        );
//        skillSubsection = lessonPlanNodeRepository.save(skillSubsection);
//
//        // Create list items under knowledge
//        createAndSaveNode(101L, knowledgeSubsection, "Hiểu khái niệm cơ bản",
//                "Nắm vững định nghĩa và ý nghĩa", NodeType.LIST_ITEM, 1);
//        createAndSaveNode(101L, knowledgeSubsection, "Phân biệt các loại",
//                "Có khả năng phân loại và so sánh", NodeType.LIST_ITEM, 2);
//
//        // Create list items under skills
//        createAndSaveNode(101L, skillSubsection, "Kỹ năng phân tích",
//                "Phát triển tư duy phân tích và tổng hợp", NodeType.LIST_ITEM, 1);
//        createAndSaveNode(101L, skillSubsection, "Kỹ năng thuyết trình",
//                "Cải thiện khả năng diễn đạt và trình bày", NodeType.LIST_ITEM, 2);
//
//        // Create content section
//        LessonPlanNode contentSection = createNode(
//                101L, null, "Nội dung bài học",
//                "Chi tiết nội dung sẽ được giảng dạy trong bài",
//                NodeType.SECTION, 2, createMetadata("icon", "book")
//        );
//        contentSection = lessonPlanNodeRepository.save(contentSection);
//
//        // Create content subsections
//        LessonPlanNode introSubsection = createNode(
//                101L, contentSection, "Mở bài",
//                "Phần giới thiệu và tạo hứng thú cho học sinh",
//                NodeType.SUBSECTION, 1, null
//        );
//        introSubsection = lessonPlanNodeRepository.save(introSubsection);
//
//        LessonPlanNode mainSubsection = createNode(
//                101L, contentSection, "Bài mới",
//                "Phần chính của bài học với nội dung cốt lõi",
//                NodeType.SUBSECTION, 2, null
//        );
//        mainSubsection = lessonPlanNodeRepository.save(mainSubsection);
//
//        // Add paragraphs to main content
//        createAndSaveNode(101L, mainSubsection, "Khái niệm cơ bản",
//                "Giới thiệu các khái niệm cơ bản và định nghĩa quan trọng. Sử dụng ví dụ cụ thể để học sinh dễ hiểu.",
//                NodeType.PARAGRAPH, 1);
//        createAndSaveNode(101L, mainSubsection, "Ứng dụng thực tế",
//                "Trình bày các ứng dụng của kiến thức trong thực tế. Khuyến khích học sinh đưa ra ví dụ từ cuộc sống.",
//                NodeType.PARAGRAPH, 2);
//
//        // Create assessment section
//        LessonPlanNode assessmentSection = createNode(
//                101L, null, "Đánh giá",
//                "Phương pháp và tiêu chí đánh giá kết quả học tập",
//                NodeType.SECTION, 3, createMetadata("icon", "assessment")
//        );
//        assessmentSection = lessonPlanNodeRepository.save(assessmentSection);
//
//        // Add assessment items
//        createAndSaveNode(101L, assessmentSection, "Đánh giá quá trình",
//                "Quan sát và đánh giá sự tham gia của học sinh trong giờ học", NodeType.LIST_ITEM, 1);
//        createAndSaveNode(101L, assessmentSection, "Đánh giá kết quả",
//                "Kiểm tra kiến thức thông qua bài tập và câu hỏi", NodeType.LIST_ITEM, 2);
//
//        log.info("Sample lesson plan tree data seeded successfully!");
//    }
//
//    private LessonPlanNode createNode(Long lessonPlanId, LessonPlanNode parent, String title,
//                                    String content, NodeType type, Integer orderIndex,
//                                    Map<String, Object> metadata) {
//        return LessonPlanNode.builder()
//                .lessonPlanId(lessonPlanId)
//                .parent(parent)
//                .title(title)
//                .content(content)
//                .type(type)
//                .orderIndex(orderIndex)
//                .metadata(metadata)
//                .build();
//    }
//
//    private void createAndSaveNode(Long lessonPlanId, LessonPlanNode parent, String title,
//                                 String content, NodeType type, Integer orderIndex) {
//        LessonPlanNode node = createNode(lessonPlanId, parent, title, content, type, orderIndex, null);
//        lessonPlanNodeRepository.save(node);
//    }
//
//    private Map<String, Object> createMetadata(String key, String value) {
//        Map<String, Object> metadata = new HashMap<>();
//        metadata.put(key, value);
//        return metadata;
//    }
//}

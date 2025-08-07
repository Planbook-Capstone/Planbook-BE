package com.BE.service.implementService;

import com.BE.exception.BadRequestException;
import com.BE.model.dto.DifficultyCountDTO;
import com.BE.model.dto.IndividualBankExamDTO;
import com.BE.model.dto.SystemBankQuestionDTO;
import com.BE.model.request.ExamGenerationRequest;
import com.BE.service.interfaceService.IExamGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamGenerationServiceImpl implements IExamGenerationService {

    @Override
    public List<Map<String, Object>> generateExams(ExamGenerationRequest request) {

        // 1. Xây dựng một ngân hàng câu hỏi (questionBank) có cấu trúc phẳng, đơn giản.
        Map<String, List<Map<String, Object>>> questionBank = new HashMap<>();

        // 2. Nạp câu hỏi từ kho đề cá nhân (Personal Exams)
        if (request.getPersonalExams() != null) {
            for (IndividualBankExamDTO exam : request.getPersonalExams()) {
                Map<String, Object> contentJson = exam.getContentJson();
                List<Map<String, Object>> parts = (List<Map<String, Object>>) contentJson.get("parts");
                if (parts == null) continue;

                for (Map<String, Object> part : parts) {
                    String partName = (String) part.get("part");
                    List<Map<String, Object>> questions = (List<Map<String, Object>>) part.get("questions");
                    if (partName == null || questions == null) continue;

                    for (Map<String, Object> q : questions) {
                        // Thêm metadata và nạp vào ngân hàng
                        q.put("sourceType", "USER_UPLOAD");
                        q.put("sourceExamId", exam.getSourceExamId());
                        questionBank.computeIfAbsent(partName, k -> new ArrayList<>()).add(q);
                    }
                }
            }
        }

        // 3. Nạp câu hỏi từ kho hệ thống (System Questions)
        if (request.getSystemQuestions() != null) {
            for (SystemBankQuestionDTO dto : request.getSystemQuestions()) {
                if (dto.getQuestionType() != null && !dto.getQuestionType().trim().isEmpty()) {
                    String partName = mapQuestionTypeToPartName(dto.getQuestionType());
                    Map<String, Object> questionMap = convertQuestionToMap(dto);
                    questionBank.computeIfAbsent(partName, k -> new ArrayList<>()).add(questionMap);
                }
            }
        }

        // 4. Kiểm tra xem ngân hàng câu hỏi có đủ để sinh đề theo ma trận không
        validateMatrixRequirements(questionBank, request.getMatrixConfig());

        // 5. Bắt đầu sinh các đề thi
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < request.getNumberOfExams(); i++) {
            // Tạo một bản sao của ngân hàng để mỗi đề được sinh ra không ảnh hưởng tới các đề khác
            Map<String, List<Map<String, Object>>> tempBankForSingleExam = deepCopyBank(questionBank);
            Map<String, Object> exam = generateSingleExam(tempBankForSingleExam, request.getMatrixConfig());
            results.add(exam);
        }

        return results;
    }

    private Map<String, Object> generateSingleExam(Map<String, List<Map<String, Object>>> questionBank, Map<String, DifficultyCountDTO> matrix) {
        Map<String, Object> exam = new LinkedHashMap<>();
        List<Map<String, Object>> generatedParts = new ArrayList<>();

        for (String partName : matrix.keySet()) {
            DifficultyCountDTO config = matrix.get(partName);
            // Lấy trực tiếp danh sách câu hỏi cho phần này từ ngân hàng
            List<Map<String, Object>> partPool = questionBank.get(partName);

            // Chọn câu hỏi ngẫu nhiên từ pool
            List<Map<String, Object>> selectedQuestions = randomSelectByDifficulty(partPool, config);


            for (int i = 0; i < selectedQuestions.size(); i++) {
                Map<String, Object> q = selectedQuestions.get(i);
                q.remove("questionNumber");  
                q.put("questionNumber", i + 1);
            }


            Map<String, Object> partJson = new LinkedHashMap<>();
            partJson.put("part", partName);
            partJson.put("questions", selectedQuestions);
            generatedParts.add(partJson);
        }

        exam.put("parts", generatedParts);
        return exam;
    }

    private void validateMatrixRequirements(Map<String, List<Map<String, Object>>> questionBank, Map<String, DifficultyCountDTO> matrix) {
        for (String partName : matrix.keySet()) {
            DifficultyCountDTO config = matrix.get(partName);
            List<Map<String, Object>> pool = questionBank.getOrDefault(partName, Collections.emptyList());

            long nb = countQuestionsByDifficulty(pool, "KNOWLEDGE");
            long th = countQuestionsByDifficulty(pool, "COMPREHENSION");
            long vd = countQuestionsByDifficulty(pool, "APPLICATION");

            if (nb < config.getNb() || th < config.getTh() || vd < config.getVd()) {
                throw new BadRequestException("Không đủ câu hỏi trong phần " + partName + " (NB: " + nb + "/" + config.getNb() + ", TH: " + th + "/" + config.getTh() + ", VD: " + vd + "/" + config.getVd() + ")");
            }
        }
    }

    private List<Map<String, Object>> randomSelectByDifficulty(List<Map<String, Object>> questions, DifficultyCountDTO config) {
        Map<String, List<Map<String, Object>>> grouped = questions.stream()
                .filter(q -> q.get("difficultyLevel") instanceof String)
                .collect(Collectors.groupingBy(q -> ((String) q.get("difficultyLevel")).trim().toUpperCase()));

        List<Map<String, Object>> result = new ArrayList<>();
        result.addAll(pickRandom(grouped.get("KNOWLEDGE"), config.getNb()));
        result.addAll(pickRandom(grouped.get("COMPREHENSION"), config.getTh()));
        result.addAll(pickRandom(grouped.get("APPLICATION"), config.getVd()));

        Collections.shuffle(result);
        return result;
    }

    // --- CÁC HÀM TRỢ GIÚP (HELPER METHODS) ---

    private long countQuestionsByDifficulty(List<Map<String, Object>> pool, String difficulty) {
        return pool.stream()
                .filter(q -> q.get("difficultyLevel") instanceof String &&
                        difficulty.equals(((String) q.get("difficultyLevel")).trim().toUpperCase()))
                .count();
    }

    private List<Map<String, Object>> pickRandom(List<Map<String, Object>> list, int n) {
        if (list == null || list.isEmpty() || n <= 0) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> tempList = new ArrayList<>(list);
        Collections.shuffle(tempList);
        return new ArrayList<>(tempList.subList(0, Math.min(n, tempList.size())));
    }

    private Map<String, List<Map<String, Object>>> deepCopyBank(Map<String, List<Map<String, Object>>> original) {
        Map<String, List<Map<String, Object>>> copy = new HashMap<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    private String mapQuestionTypeToPartName(String questionType) {
        switch (questionType) {
            case "PART_I": return "PHẦN I";
            case "PART_II": return "PHẦN II";
            case "PART_III": return "PHẦN III";
            default: return questionType;
        }
    }

    private Map<String, Object> convertQuestionToMap(SystemBankQuestionDTO dto) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", dto.getId());
        map.put("difficultyLevel", dto.getDifficultyLevel());
        Map<String, Object> content = dto.getQuestionContent();
        String questionType = dto.getQuestionType();
        if (content != null) {
            map.put("question", content.get("question"));

            switch (questionType) {
                case "PART_I":
                    // Trắc nghiệm nhiều phương án lựa chọn
                    map.put("answer", content.get("answer"));
                    map.put("options", content.get("options"));
                    break;

                case "PART_II":
                    // Câu đúng/sai: chỉ cần statements, không cần answer
                    map.put("statements", content.get("statements"));
                    break;

                case "PART_III":
                    // Tự luận: có answer
                    map.put("answer", content.get("answer"));
                    break;

                default:
                    throw new IllegalArgumentException("Không hỗ trợ questionType: " + questionType);
            }
        }
        map.put("explanation", dto.getExplanation());
        map.put("lessonIds", dto.getLessonIds());
        map.put("sourceType", "SYSTEM");
        map.put("sourceExamId", null);
        return map;
    }
}
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
        Map<String, List<Map<String, Object>>> questionBank = new HashMap<>();

        // Load personal exams
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
                        q.put("sourceType", "USER_UPLOAD");
                        q.put("sourceExamId", exam.getSourceExamId());
                        // Gán id tạm nếu chưa có
                        if (!q.containsKey("id")) {
                            q.put("id", "TEMP_" + UUID.randomUUID());
                        }
                        questionBank.computeIfAbsent(partName, k -> new ArrayList<>()).add(q);
                    }
                }
            }
        }

        // Load system questions
        if (request.getSystemQuestions() != null) {
            for (SystemBankQuestionDTO dto : request.getSystemQuestions()) {
                if (dto.getQuestionType() != null && !dto.getQuestionType().trim().isEmpty()) {
                    String partName = mapQuestionTypeToPartName(dto.getQuestionType());
                    Map<String, Object> questionMap = convertQuestionToMap(dto);
                    questionBank.computeIfAbsent(partName, k -> new ArrayList<>()).add(questionMap);
                }
            }
        }

        validateMatrixRequirements(questionBank, request.getMatrixConfig());

        // Tạo đề
        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> usedQuestionIds = new HashSet<>();

        for (int i = 0; i < request.getNumberOfExams(); i++) {
            Map<String, List<Map<String, Object>>> tempBank = deepCopyBank(questionBank);
            Map<String, Object> both = generateSingleExam(tempBank, request.getMatrixConfig(), usedQuestionIds);
            Map<String, Object> questionOnly = (Map<String, Object>) both.get("questionOnly");
            Map<String, Object> answerOnly = (Map<String, Object>) both.get("answerOnly");


            addMetadataToExam(questionOnly, i, request);
            addMetadataToExam(answerOnly, i, request);

            results.add(Map.of(
                    "questionOnly", questionOnly,
                    "answerOnly", answerOnly
            ));
        }

        return results;
    }

    private void addMetadataToExam(Map<String, Object> exam,
                                   int index,
                                   ExamGenerationRequest request) {
        exam.put("examCode", String.format("%04d", index + 1));
        exam.put("examTitle", request.getExamTitle());
        exam.put("school", request.getSchool());
        exam.put("duration", request.getDuration());
    }


    private Map<String, Object> generateSingleExam(Map<String, List<Map<String, Object>>> questionBank,
                                                   Map<String, DifficultyCountDTO> matrix,
                                                   Set<String> usedQuestionIds) {
        Map<String, Object> questionOnlyExam = new LinkedHashMap<>();
        Map<String, Object> answerOnlyExam = new LinkedHashMap<>();
        List<Map<String, Object>> questionParts = new ArrayList<>();
        List<Map<String, Object>> answerParts = new ArrayList<>();

        for (String partName : matrix.keySet()) {
            DifficultyCountDTO config = matrix.get(partName);
            List<Map<String, Object>> partPool = questionBank.get(partName);

            List<Map<String, Object>> selectedQuestions = randomSelectByDifficultyAvoidingReuse(partPool, config, usedQuestionIds);
            List<Map<String, Object>> questionList = new ArrayList<>();
            List<Map<String, Object>> answerList = new ArrayList<>();

            for (int i = 0; i < selectedQuestions.size(); i++) {
                Map<String, Object> original = selectedQuestions.get(i);
                original.put("questionNumber", i + 1);
                if (original.get("id") != null) usedQuestionIds.add(original.get("id").toString());

                // Clone cho question-only và answer-only
                Map<String, Object> questionItem = new LinkedHashMap<>(original);
                Map<String, Object> answerItem = new LinkedHashMap<>();
                // Giữ explanation chỉ ở đáp án
                Object explanation = questionItem.remove("explanation");
                if (explanation != null) {
                    answerItem.put("explanation", explanation);
                }


                // Remove answer khỏi đề, remove question khỏi đáp án
                if ("PHẦN II".equals(partName)) {
                    // xử lý statement: giữ text trong đề, answer trong đáp án
                    Map<String, Map<String, Object>> statements = (Map<String, Map<String, Object>>) original.get("statements");

                    Map<String, Map<String, Object>> questionStatements = new LinkedHashMap<>();
                    Map<String, Boolean> answerStatements = new LinkedHashMap<>();

                    for (Map.Entry<String, Map<String, Object>> entry : statements.entrySet()) {
                        String key = entry.getKey();
                        Map<String, Object> value = entry.getValue();
                        questionStatements.put(key, Map.of("text", value.get("text")));
                        answerStatements.put(key, (Boolean) value.get("answer"));
                    }

                    questionItem.put("statements", questionStatements);
                    answerItem.put("statements", answerStatements);
                } else {
                    questionItem.remove("answer");
                    answerItem.put("answer", original.get("answer"));
                }

                answerItem.put("questionNumber", i + 1);
                answerItem.put("id", original.get("id"));
                answerItem.put("difficultyLevel", original.get("difficultyLevel"));
                answerItem.put("sourceType", original.get("sourceType"));
                answerItem.put("sourceExamId", original.get("sourceExamId"));
                answerItem.put("lessonIds", original.get("lessonIds"));

                questionList.add(questionItem);
                answerList.add(answerItem);
            }

            questionParts.add(Map.of("part", partName, "questions", questionList));
            answerParts.add(Map.of("part", partName, "questions", answerList));
        }

        questionOnlyExam.put("parts", questionParts);
        answerOnlyExam.put("parts", answerParts);

        return Map.of(
                "questionOnly", questionOnlyExam,
                "answerOnly", answerOnlyExam
        );
    }


    private List<Map<String, Object>> randomSelectByDifficultyAvoidingReuse(List<Map<String, Object>> questions,
                                                                            DifficultyCountDTO config,
                                                                            Set<String> usedIds) {
        Map<String, List<Map<String, Object>>> grouped = questions.stream()
                .filter(q -> q.get("difficultyLevel") instanceof String)
                .collect(Collectors.groupingBy(q -> ((String) q.get("difficultyLevel")).trim().toUpperCase()));

        List<Map<String, Object>> result = new ArrayList<>();
        result.addAll(pickRandomAvoidingReuse(grouped.get("KNOWLEDGE"), config.getNb(), usedIds));
        result.addAll(pickRandomAvoidingReuse(grouped.get("COMPREHENSION"), config.getTh(), usedIds));
        result.addAll(pickRandomAvoidingReuse(grouped.get("APPLICATION"), config.getVd(), usedIds));

        Collections.shuffle(result);
        return result;
    }

    private List<Map<String, Object>> pickRandomAvoidingReuse(List<Map<String, Object>> list, int n, Set<String> usedIds) {
        if (list == null || list.isEmpty() || n <= 0) return Collections.emptyList();

        List<Map<String, Object>> unused = list.stream()
                .filter(q -> q.get("id") != null && !usedIds.contains(q.get("id").toString()))
                .collect(Collectors.toList());

        List<Map<String, Object>> selected = new ArrayList<>();
        List<Map<String, Object>> source = unused.size() >= n ? unused : list;

        List<Map<String, Object>> shuffled = new ArrayList<>(source);
        Collections.shuffle(shuffled);

        for (int i = 0; i < Math.min(n, shuffled.size()); i++) {
            selected.add(shuffled.get(i));
        }

        return selected;
    }

    private void validateMatrixRequirements(Map<String, List<Map<String, Object>>> questionBank, Map<String, DifficultyCountDTO> matrix) {
        for (String partName : matrix.keySet()) {
            DifficultyCountDTO config = matrix.get(partName);
            List<Map<String, Object>> pool = questionBank.getOrDefault(partName, Collections.emptyList());

            long nb = countQuestionsByDifficulty(pool, "KNOWLEDGE");
            long th = countQuestionsByDifficulty(pool, "COMPREHENSION");
            long vd = countQuestionsByDifficulty(pool, "APPLICATION");

            if (nb < config.getNb() || th < config.getTh() || vd < config.getVd()) {
                throw new BadRequestException("Không đủ câu hỏi trong phần " + partName +
                        " (NB: " + nb + "/" + config.getNb() +
                        ", TH: " + th + "/" + config.getTh() +
                        ", VD: " + vd + "/" + config.getVd() + ")");
            }
        }
    }

    private long countQuestionsByDifficulty(List<Map<String, Object>> pool, String difficulty) {
        return pool.stream()
                .filter(q -> q.get("difficultyLevel") instanceof String &&
                        difficulty.equals(((String) q.get("difficultyLevel")).trim().toUpperCase()))
                .count();
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
                    map.put("answer", content.get("answer"));
                    map.put("options", content.get("options"));
                    break;
                case "PART_II":
                    map.put("statements", content.get("statements"));
                    break;
                case "PART_III":
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

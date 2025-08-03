package com.BE.controller;

import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
import com.BE.model.request.CreateQuestionBankRequest;
import com.BE.model.request.UpdateQuestionBankRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.QuestionBankResponse;
import com.BE.service.interfaceService.IQuestionBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question-banks")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@Slf4j
public class QuestionBankController {

    private final IQuestionBankService questionBankService;

    @Operation(
        summary = "Tạo câu hỏi mới trong ngân hàng câu hỏi",
        description = """
            Tạo một câu hỏi mới với các thông tin:

            **Thông tin cơ bản:**
            - lessonId: ID của bài học (bắt buộc)
            - questionType: Loại câu hỏi (PART_I/PART_II/PART_III)
            - difficultyLevel: Mức độ khó (KNOWLEDGE/COMPREHENSION/APPLICATION/ANALYSIS)

            **Thông tin bổ sung:**
            - explanation: Giải thích đáp án (tùy chọn)
            - referenceSource: Nguồn tham khảo (tùy chọn, tối đa 300 ký tự)
            - estimatedTimeMinutes: Thời gian ước tính (tùy chọn, tối thiểu 1 phút)

            **Nội dung câu hỏi (questionContent):**
            - PART_I: Phải có question, options (object), answer (key của options)
            - PART_II: Phải có question, statements (object), answers (object với true/false)
            - PART_III: Phải có question, answer, keywords (array - tùy chọn)

            Hệ thống sẽ tự động validate cấu trúc JSON theo từng loại câu hỏi.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Tạo câu hỏi thành công",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = DataResponseDTO.class))),
        @ApiResponse(responseCode = "400",
                    description = "Dữ liệu không hợp lệ - Kiểm tra format JSON hoặc validation rules",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<DataResponseDTO<QuestionBankResponse>> createQuestionBank(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Question bank creation data",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    examples = {
                        @ExampleObject(
                            name = "Part I - Multiple Choice",
                            value = """
                                {
                                    "lessonId": 1,
                                    "questionType": "PART_I",
                                    "difficultyLevel": "KNOWLEDGE",
                                    "questionContent": {
                                        "question": "Đơn vị đo khối lượng nguyên tử là gì?",
                                        "image": "https://example.com/images/atomic-mass-unit.png",
                                        "options": {
                                            "A": "kg",
                                            "B": "g",
                                            "C": "amu",
                                            "D": "Å"
                                        },
                                        "answer": "C"
                                    },
                                    "explanation": "Đơn vị khối lượng nguyên tử (amu) được sử dụng để đo khối lượng của các nguyên tử và phân tử",
                                    "referenceSource": "Sách giáo khoa Hóa học 10 - Trang 25"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Part II - True/False",
                            value = """
                                {
                                    "lessonId": 1,
                                    "questionType": "PART_II",
                                    "difficultyLevel": "COMPREHENSION",
                                    "questionContent": {
                                        "question": "Xét một nguyên tử X bất kỳ. Cho biết các phát biểu sau đúng hay sai:",
                                        "statements": {
                                            "a": {
                                                "text": "Nguyên tử X được cấu tạo từ ba loại hạt cơ bản: electron, proton và neutron.",
                                                "answer": true
                                            },
                                            "b": {
                                                "text": "Trong nguyên tử X, số proton luôn bằng số neutron.",
                                                "answer": false
                                            },
                                            "c": {
                                                "text": "Khối lượng của electron đóng góp đáng kể vào khối lượng của nguyên tử X.",
                                                "answer": false
                                            },
                                            "d": {
                                                "text": "Nguyên tử X luôn trung hòa về điện.",
                                                "answer": true
                                            }
                                        }
                                    },
                                    "explanation": "Nguyên tử được cấu tạo từ electron, proton và neutron. Số proton không luôn bằng số neutron. Khối lượng tập trung chủ yếu ở hạt nhân. Nguyên tử trung hòa về điện."
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Part III - Short Answer",
                            value = """
                                {
                                    "lessonId": 1,
                                    "questionType": "PART_III",
                                    "difficultyLevel": "APPLICATION",
                                    "questionContent": {
                                        "question": "Một nguyên tử X có 13 proton và 14 neutron. Tính khối lượng gần đúng của nguyên tử X theo đơn vị amu.",
                                        "answer": "27"
                                    },
                                    "explanation": "Khối lượng nguyên tử gần đúng = số proton + số neutron = 13 + 14 = 27 amu"
                                }
                                """
                        )
                    }
                )
            )
            @RequestBody CreateQuestionBankRequest request) {

        log.info("Creating question bank with type: {}", request.getQuestionType());
        QuestionBankResponse response = questionBankService.createQuestionBank(request);
        DataResponseDTO<QuestionBankResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.CREATED.value(),
            "Tạo câu hỏi thành công",
            response
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dataResponse);
    }



    @Operation(
        summary = "Lấy thông tin chi tiết một câu hỏi theo ID",
        description = """
            Lấy thông tin chi tiết của một câu hỏi cụ thể trong ngân hàng câu hỏi.

            **Tham số:**
            - id: UUID của câu hỏi cần lấy thông tin

            **Kết quả trả về:**
            - Thông tin đầy đủ của câu hỏi bao gồm:
              + Metadata: title, subject, grade, questionType, difficultyLevel
              + Nội dung: questionContent (JSON theo format của từng loại)
              + Thông tin bổ sung: topic, chapter, keywords, explanation, referenceSource
              + Thống kê: (removed usage tracking)
              + Audit: createdBy, updatedBy, createdAt, updatedAt

            **Quyền truy cập:**
            - Có thể xem câu hỏi công khai (PUBLIC) của bất kỳ ai
            - Có thể xem câu hỏi riêng tư (PRIVATE) do chính mình tạo ra
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Lấy thông tin câu hỏi thành công",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = DataResponseDTO.class))),
        @ApiResponse(responseCode = "404",
                    description = "Không tìm thấy câu hỏi hoặc không có quyền truy cập",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DataResponseDTO<QuestionBankResponse>> getQuestionBankById(
            @Parameter(description = "Question bank ID", example = "1")
            @PathVariable Long id) {

        log.info("Getting question bank {}", id);
        QuestionBankResponse response = questionBankService.getQuestionBankById(id);
        DataResponseDTO<QuestionBankResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Lấy thông tin câu hỏi thành công",
            response
        );
        return ResponseEntity.ok(dataResponse);
    }

    @Operation(
        summary = "Cập nhật thông tin câu hỏi trong ngân hàng câu hỏi",
        description = """
            Cập nhật thông tin của một câu hỏi đã tồn tại. Chỉ các field được cung cấp sẽ được cập nhật (partial update).

            **Các field có thể cập nhật:**
            - title: Tiêu đề câu hỏi (tối đa 500 ký tự)
            - subject: Môn học (tối đa 100 ký tự)
            - grade: Lớp học (từ 1-12)
            - questionType: Loại câu hỏi (PART_I/PART_II/PART_III)
            - difficultyLevel: Mức độ khó (KNOWLEDGE/COMPREHENSION/APPLICATION/ANALYSIS)
            - topic: Chủ đề (tối đa 200 ký tự)
            - chapter: Chương (tối đa 200 ký tự)
            - keywords: Từ khóa (tối đa 500 ký tự)
            - questionContent: Nội dung câu hỏi (JSON theo format của từng loại)
            - explanation: Giải thích đáp án
            - referenceSource: Nguồn tham khảo (tối đa 300 ký tự)
            - estimatedTimeMinutes: Thời gian ước tính (tối thiểu 1 phút)
            - visibility: Mức độ hiển thị (PUBLIC/PRIVATE) - không thể thay đổi sau khi tạo

            **Lưu ý quan trọng:**
            - Nếu thay đổi questionType, phải đảm bảo questionContent phù hợp với loại mới
            - Hệ thống sẽ validate lại toàn bộ cấu trúc JSON khi cập nhật
            - Chỉ có thể cập nhật câu hỏi do chính mình tạo ra
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Cập nhật câu hỏi thành công",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = DataResponseDTO.class))),
        @ApiResponse(responseCode = "400",
                    description = "Dữ liệu không hợp lệ - Kiểm tra format JSON hoặc validation rules",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404",
                    description = "Không tìm thấy câu hỏi hoặc không có quyền truy cập",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DataResponseDTO<QuestionBankResponse>> updateQuestionBank(
            @Parameter(description = "Question bank ID", example = "1")
            @PathVariable Long id,
            @RequestBody UpdateQuestionBankRequest request) {

        log.info("Updating question bank {}", id);
        QuestionBankResponse response = questionBankService.updateQuestionBank(id, request);
        DataResponseDTO<QuestionBankResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Cập nhật câu hỏi thành công",
            response
        );
        return ResponseEntity.ok(dataResponse);
    }

    @Operation(
        summary = "Xóa câu hỏi khỏi ngân hàng câu hỏi",
        description = """
            Xóa vĩnh viễn một câu hỏi khỏi ngân hàng câu hỏi.

            **Đặc điểm:**
            - Câu hỏi sẽ bị xóa vĩnh viễn khỏi database
            - Không thể khôi phục sau khi xóa
            - Tất cả dữ liệu liên quan sẽ bị mất

            **Bảo mật:** Chỉ có thể xóa câu hỏi do chính mình tạo ra.

            **Lưu ý:** Nếu câu hỏi đang được sử dụng trong các đề thi,
            nên cân nhắc kỹ trước khi xóa để tránh ảnh hưởng đến hệ thống.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Xóa câu hỏi thành công",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = DataResponseDTO.class))),
        @ApiResponse(responseCode = "404",
                    description = "Không tìm thấy câu hỏi hoặc không có quyền truy cập",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DataResponseDTO<Void>> deleteQuestionBank(
            @Parameter(description = "Question bank ID", example = "1")
            @PathVariable Long id) {

        log.info("Deleting question bank {}", id);
        questionBankService.deleteQuestionBank(id);
        DataResponseDTO<Void> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Xóa câu hỏi thành công",
            null
        );
        return ResponseEntity.ok(dataResponse);
    }



    @Operation(
        summary = "Lấy danh sách câu hỏi với khả năng lọc",
        description = """
            Lấy danh sách câu hỏi có thể truy cập với khả năng lọc theo các tiêu chí.

            **Quyền truy cập:**
            - Hiển thị câu hỏi PUBLIC (kho chung) + câu hỏi PRIVATE của bản thân (kho cá nhân)

            **Chức năng:**
            - Không có filter: Trả về TẤT CẢ câu hỏi có thể truy cập
            - Có filter: Lọc theo các tiêu chí được cung cấp

            **Hỗ trợ lọc đa lựa chọn:**
            - lessonId: Lọc theo bài học cụ thể
            - questionTypes: Có thể chọn nhiều loại câu hỏi (PART_I, PART_II, PART_III)
            - difficultyLevels: Có thể chọn nhiều mức độ khó (KNOWLEDGE, COMPREHENSION, APPLICATION, ANALYSIS)
            - Hỗ trợ phân trang với page và size

            **Ví dụ sử dụng:**
            - GET /api/v1/question-banks/filter (lấy tất cả)
            - GET /api/v1/question-banks/filter?questionTypes=PART_I
            - GET /api/v1/question-banks/filter?questionTypes=PART_I,PART_II
            - GET /api/v1/question-banks/filter?difficultyLevels=KNOWLEDGE,COMPREHENSION
            - GET /api/v1/question-banks/filter?lessonId=1&questionTypes=PART_I
            - GET /api/v1/question-banks/filter?page=0&size=20 (phân trang)
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Filter completed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("")
    public ResponseEntity<DataResponseDTO<List<QuestionBankResponse>>> filterQuestionBanks(
            @Parameter(description = "Lesson ID", example = "1")
            @RequestParam(required = false) Long lessonId,
            @Parameter(description = "Question types (comma-separated)", example = "PART_I,PART_II")
            @RequestParam(required = false) List<QuestionType> questionTypes,
            @Parameter(description = "Difficulty levels (comma-separated)", example = "KNOWLEDGE,COMPREHENSION")
            @RequestParam(required = false) List<DifficultyLevel> difficultyLevels,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (0 for no pagination)", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        log.info("Filtering question banks with lessonId: {}, types: {}, difficulties: {}",
                lessonId, questionTypes, difficultyLevels);

        if (size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionBankResponse> questionBanks = questionBankService.getQuestionBanksByFilters(
                lessonId, questionTypes, difficultyLevels, pageable);
            DataResponseDTO<List<QuestionBankResponse>> dataResponse = new DataResponseDTO<>(
                HttpStatus.OK.value(),
                "Lọc câu hỏi thành công",
                questionBanks.getContent()
            );
            return ResponseEntity.ok(dataResponse);
        } else {
            List<QuestionBankResponse> questionBanks = questionBankService.getQuestionBanksByFilters(
                lessonId, questionTypes, difficultyLevels);
            DataResponseDTO<List<QuestionBankResponse>> dataResponse = new DataResponseDTO<>(
                HttpStatus.OK.value(),
                "Lọc câu hỏi thành công",
                questionBanks
            );
            return ResponseEntity.ok(dataResponse);
        }
    }






}

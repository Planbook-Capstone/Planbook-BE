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
@Tag(name = "Question Bank Management",
     description = """
         APIs for managing question banks with support for 3 question types:
         - PART_I: Multiple choice questions (Câu trắc nghiệm nhiều phương án lựa chọn)
         - PART_II: True/False questions (Câu trắc nghiệm đúng sai)
         - PART_III: Short answer questions (Câu trả lời ngắn)

         Each question has 4 difficulty levels:
         - KNOWLEDGE: Knowledge level
         - COMPREHENSION: Comprehension level
         - APPLICATION: Application level
         - ANALYSIS: Higher-order application
         """)
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
                                        "question": "Xét tính đúng sai của các phát biểu sau:",
                                        "image": "https://example.com/images/atomic-structure.png",
                                        "statements": {
                                            "a": "Nguyên tử có cấu tạo đặc khít",
                                            "b": "Hạt nhân nguyên tử mang điện tích dương",
                                            "c": "Electron chuyển động quanh hạt nhân",
                                            "d": "Khối lượng nguyên tử tập trung ở electron"
                                        },
                                        "answers": {
                                            "a": false,
                                            "b": true,
                                            "c": true,
                                            "d": false
                                        }
                                    },
                                    "explanation": "Nguyên tử có cấu tạo rỗng, hạt nhân mang điện dương, electron chuyển động quanh hạt nhân, khối lượng tập trung ở hạt nhân"
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
                                        "question": "Tính khối lượng nguyên tử của carbon biết carbon có 6 proton và 6 neutron",
                                        "image": "https://example.com/images/carbon-atom.png",
                                        "answer": "Khối lượng nguyên tử carbon = 6 + 6 = 12 amu",
                                        "keywords": ["proton", "neutron", "khối lượng nguyên tử", "12 amu"]
                                    },
                                    "explanation": "Khối lượng nguyên tử bằng tổng số proton và neutron"
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
        summary = "Lấy danh sách tất cả câu hỏi của người dùng hiện tại",
        description = """
            Lấy danh sách tất cả câu hỏi trong ngân hàng câu hỏi do người dùng hiện tại tạo ra.

            **Tham số:**
            - page: Số trang (bắt đầu từ 0, mặc định 0)
            - size: Số lượng câu hỏi mỗi trang (mặc định 20, nếu = 0 thì lấy tất cả)

            **Kết quả trả về:**
            - Danh sách câu hỏi được sắp xếp theo thời gian tạo (mới nhất trước)
            - Chỉ hiển thị câu hỏi đang active (isActive = true)
            - Bao gồm đầy đủ thông tin: nội dung, metadata, thống kê sử dụng

            **Lưu ý:** Người dùng chỉ có thể xem câu hỏi do chính họ tạo ra.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Lấy danh sách câu hỏi thành công",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = DataResponseDTO.class))),
        @ApiResponse(responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<DataResponseDTO<List<QuestionBankResponse>>> getQuestionBanks(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        if (size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionBankResponse> questionBanks = questionBankService.getQuestionBanksByCurrentUser(pageable);
            DataResponseDTO<List<QuestionBankResponse>> dataResponse = new DataResponseDTO<>(
                HttpStatus.OK.value(),
                "Lấy danh sách câu hỏi thành công",
                questionBanks.getContent()
            );
            return ResponseEntity.ok(dataResponse);
        } else {
            List<QuestionBankResponse> questionBanks = questionBankService.getQuestionBanksByCurrentUser();
            DataResponseDTO<List<QuestionBankResponse>> dataResponse = new DataResponseDTO<>(
                HttpStatus.OK.value(),
                "Lấy danh sách câu hỏi thành công",
                questionBanks
            );
            return ResponseEntity.ok(dataResponse);
        }
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

            **Bảo mật:** Chỉ có thể xem câu hỏi do chính mình tạo ra.
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
            - isActive: Trạng thái hoạt động (true/false)

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
        summary = "Xóa câu hỏi khỏi ngân hàng câu hỏi (soft delete)",
        description = """
            Xóa mềm một câu hỏi bằng cách đặt trạng thái isActive = false.

            **Đặc điểm của soft delete:**
            - Câu hỏi không bị xóa vĩnh viễn khỏi database
            - Chỉ đặt isActive = false để ẩn khỏi danh sách
            - Có thể khôi phục lại bằng cách update isActive = true
            - Dữ liệu thống kê và lịch sử sử dụng được giữ nguyên

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
        summary = "Tìm kiếm câu hỏi theo từ khóa",
        description = """
            Tìm kiếm câu hỏi trong ngân hàng câu hỏi dựa trên từ khóa.

            **Phạm vi tìm kiếm:**
            - title: Tiêu đề câu hỏi
            - topic: Chủ đề
            - chapter: Chương
            - keywords: Từ khóa đã lưu

            **Tham số:**
            - keyword: Từ khóa tìm kiếm (bắt buộc)
            - page: Số trang (bắt đầu từ 0, mặc định 0)
            - size: Số lượng kết quả mỗi trang (mặc định 20, nếu = 0 thì lấy tất cả)

            **Đặc điểm tìm kiếm:**
            - Không phân biệt hoa thường (case-insensitive)
            - Tìm kiếm theo pattern LIKE '%keyword%'
            - Chỉ tìm trong câu hỏi đang active (isActive = true)
            - Kết quả sắp xếp theo thời gian tạo (mới nhất trước)
            - Chỉ tìm trong câu hỏi của người dùng hiện tại
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Tìm kiếm hoàn tất thành công",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = DataResponseDTO.class))),
        @ApiResponse(responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/search")
    public ResponseEntity<DataResponseDTO<List<QuestionBankResponse>>> searchQuestionBanks(
            @Parameter(description = "Search keyword", example = "nguyên tử")
            @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (0 for no pagination)", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        log.info("Searching question banks with keyword: {}", keyword);

        if (size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionBankResponse> questionBanks = questionBankService.searchQuestionBanks(keyword, pageable);
            DataResponseDTO<List<QuestionBankResponse>> dataResponse = new DataResponseDTO<>(
                HttpStatus.OK.value(),
                "Tìm kiếm câu hỏi thành công",
                questionBanks.getContent()
            );
            return ResponseEntity.ok(dataResponse);
        } else {
            List<QuestionBankResponse> questionBanks = questionBankService.searchQuestionBanks(keyword);
            DataResponseDTO<List<QuestionBankResponse>> dataResponse = new DataResponseDTO<>(
                HttpStatus.OK.value(),
                "Tìm kiếm câu hỏi thành công",
                questionBanks
            );
            return ResponseEntity.ok(dataResponse);
        }
    }

    @Operation(
        summary = "Filter question banks",
        description = """
            Filter question banks by lesson ID, question types, and difficulty levels.

            **Multiple Selection Support:**
            - questionTypes: Can select multiple question types (PART_I, PART_II, PART_III)
            - difficultyLevels: Can select multiple difficulty levels (BIET, HIEU, VAN_DUNG, VAN_DUNG_CAO)

            **Examples:**
            - Single type: ?questionTypes=PART_I
            - Multiple types: ?questionTypes=PART_I,PART_II
            - Multiple difficulties: ?difficultyLevels=KNOWLEDGE,COMPREHENSION
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Filter completed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/filter")
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





    @Operation(
        summary = "Get question bank statistics",
        description = "Get statistics about question banks for the current user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/statistics")
    public ResponseEntity<DataResponseDTO<IQuestionBankService.QuestionBankStatistics>> getQuestionBankStatistics() {

        log.info("Getting question bank statistics");
        IQuestionBankService.QuestionBankStatistics statistics = questionBankService.getQuestionBankStatistics();
        DataResponseDTO<IQuestionBankService.QuestionBankStatistics> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Lấy thống kê câu hỏi thành công",
            statistics
        );
        return ResponseEntity.ok(dataResponse);
    }
}

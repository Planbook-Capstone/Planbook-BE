# Student Submission Result API - Final Version

## Endpoint
**`GET /api/exam-instances/submission/{submissionId}`**

### Mô tả
- **Public endpoint** - Không cần authentication
- Cho phép học sinh xem kết quả bài thi và đáp án sau khi exam instance đã COMPLETED
- Nhận vào submissionId và trả về bài làm của học sinh đó

### Response Structure
```json
{
    "code": 200,
    "message": "Lấy kết quả bài nộp thành công",
    "data": {
        "submissionId": "uuid",
        "studentName": "Nguyen Van A",
        "score": 8.5,
        "correctCount": 17,
        "totalQuestions": 20,
        "maxScore": 10.0,
        "percentage": 85.0,
        "submittedAt": "2024-01-15T10:30:00",
        "examInstanceId": "uuid",
        "examInstanceCode": "ABC123",
        "examTitle": "Kiểm tra Hóa học",
        "examDescription": "Bài kiểm tra 45 phút",
        "examStartAt": "2024-01-15T09:00:00",
        "examEndAt": "2024-01-15T10:45:00",
        "examContentWithAnswers": {
            // Nội dung đề thi với đáp án đúng
        },
        "resultDetails": [
            {
                "questionId": "q1",
                "questionNumber": 1,
                "partName": "PHẦN I",
                "question": "Câu hỏi...",
                "studentAnswer": "A",
                "correctAnswer": "A",
                "isCorrect": true
            }
            // ... các câu hỏi khác
        ]
    }
}
```

## Implementation Details

### Files Created/Modified
1. **`StudentSubmissionResultResponse.java`** - Response model
2. **`StudentSubmissionResultMapper.java`** - MapStruct mapper
3. **`ExamInstanceController.java`** - Added new endpoint
4. **`IExamInstanceService.java`** - Added interface method
5. **`ExamInstanceServiceImpl.java`** - Implementation

### Key Features
- ✅ **MapStruct Integration**: Sử dụng mapper thay vì manual mapping
- ✅ **Type Safety**: Compile-time checking
- ✅ **Clean Code**: Ngắn gọn và maintainable
- ✅ **Status Validation**: Chỉ cho phép xem khi exam COMPLETED
- ✅ **Complete Data**: Bao gồm đề thi với đáp án và chi tiết từng câu
- ✅ **Sorted Results**: Kết quả được sắp xếp theo part và question number

### Access Control
- **Public Access**: Không cần authentication
- **Status Check**: Exam instance phải ở trạng thái COMPLETED
- **Data Validation**: Kiểm tra submission tồn tại

### Error Handling
- **404**: Submission không tồn tại
- **400**: Exam chưa completed
- **400**: Invalid UUID format

### Usage Flow
1. Student submit exam → nhận submissionId
2. Teacher complete exam → đánh dấu COMPLETED
3. Student dùng submissionId để xem kết quả
4. Xem điểm số, đáp án đúng, chi tiết từng câu

## Test Example
```bash
curl -X GET "http://localhost:8080/api/exam-instances/submission/{submissionId}" \
     -H "Content-Type: application/json"
```

## Notes
- Removed `performanceMessage` và `gradeLevel` fields theo yêu cầu
- Sử dụng MapStruct mapper để đảm bảo code quality
- Backward compatible với existing API structure
- Ready for production deployment

# Hướng dẫn sử dụng ToolResult API (Phiên bản cuối - Đã tối ưu)

## Tổng quan
API ToolResult được thiết kế để quản lý kết quả từ các công cụ AI trong hệ thống giáo dục, bao gồm giáo án, slide, đề kiểm tra, v.v.

**Cải tiến cuối cùng:**
- ✅ **Single GET endpoint** với filter đơn giản và thực tế
- ✅ **Single values** cho tất cả filters (phù hợp với thực tế sử dụng)
- ✅ **JpaSpecificationExecutor** cho query động
- ✅ **PageUtil integration** với phân trang bắt đầu từ trang 1
- ✅ **Enum select** cho sortBy và sortDirection
- ✅ **Soft delete** thay vì hard delete
- ✅ **Loại bỏ templateId** khỏi update request
- ✅ **Loại bỏ date range filters** (không cần thiết)

## Endpoints

### 1. Tạo mới ToolResult
**POST** `/api/tool-results`

**Request Body:**
```json
{
  "userId": "0d29b45a-5d6a-44e2-b58d-d7aa5180cb0f",
  "workspaceId": 101,
  "type": "LESSON_PLAN",
  "templateId": 5,
  "name": "Giáo án bài 3",
  "description": "Giáo án được tạo từ AI",
  "data": {
    "title": "Bài học về nguyên tố hóa học",
    "objectives": ["Hiểu khái niệm nguyên tố", "Tính nguyên tử khối"]
  },
  "status": "DRAFT"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Tạo kết quả công cụ AI thành công!",
  "data": {
    "id": 123,
    "userId": "0d29b45a-5d6a-44e2-b58d-d7aa5180cb0f",
    "workspaceId": 101,
    "type": "LESSON_PLAN",
    "templateId": 5,
    "name": "Giáo án bài 3",
    "description": "Giáo án được tạo từ AI",
    "data": {
      "title": "Bài học về nguyên tố hóa học",
      "objectives": ["Hiểu khái niệm nguyên tố", "Tính nguyên tử khối"]
    },
    "status": "DRAFT",
    "createdAt": "2025-07-27T12:34:56",
    "updatedAt": "2025-07-27T12:34:56"
  }
}
```

### 2. Cập nhật ToolResult
**PUT** `/api/tool-results/{id}`

**Request Body:**
```json
{
  "name": "Giáo án bài 3 - Cập nhật",
  "description": "Giáo án được cập nhật",
  "status": "PUBLISHED"
}
```

### 3. Lấy chi tiết ToolResult
**GET** `/api/tool-results/{id}`

### 4. Lấy danh sách với filter đơn giản và phân trang (ENDPOINT CHÍNH)
**GET** `/api/tool-results`

**Query Parameters (tất cả đều optional):**

#### Filter Parameters:
- `userId`: UUID người dùng (single value)
- `workspaceId`: ID workspace (single value)
- `type`: Loại công cụ (single value, enum select)
- `status`: Trạng thái (single value, enum select)
- `templateId`: ID template (single value)
- `nameContains`: Tìm kiếm theo tên (contains, case insensitive)
- `descriptionContains`: Tìm kiếm theo mô tả (contains, case insensitive)

#### Pagination Parameters:
- `page`: Số trang (bắt đầu từ 1, mặc định: 1)
- `size`: Kích thước trang (mặc định: 10)
- `sortBy`: Trường sắp xếp (enum select: ID, NAME, CREATED_AT, UPDATED_AT, TYPE, STATUS)
- `sortDirection`: Hướng sắp xếp (enum select: ASC, DESC)

**Ví dụ đơn giản:**
```
GET /api/tool-results?userId=uuid1&workspaceId=101&type=LESSON_PLAN&status=DRAFT&page=1&size=10
```

**Ví dụ với text search:**
```
GET /api/tool-results?nameContains=giáo án&descriptionContains=AI&page=1&size=10
```

**Ví dụ với sorting:**
```
GET /api/tool-results?userId=uuid1&sortBy=UPDATED_AT&sortDirection=ASC&page=1&size=20
```

**Ví dụ lấy tất cả (không filter):**
```
GET /api/tool-results?page=1&size=10
```

### 5. Xóa ToolResult (Soft Delete)
**DELETE** `/api/tool-results/{id}`

**Lưu ý:** API này thực hiện soft delete, chỉ cập nhật status thành `DELETED` thay vì xóa record khỏi database.

## Enum Values

### ToolResultType
- `LESSON_PLAN`: Giáo án
- `SLIDE`: Slide bài giảng
- `EXAM`: Đề kiểm tra
- `QUIZ`: Câu hỏi trắc nghiệm
- `WORKSHEET`: Bài tập
- `ASSIGNMENT`: Bài tập về nhà
- `RUBRIC`: Thang đánh giá
- `CURRICULUM`: Chương trình học
- `ACTIVITY`: Hoạt động học tập
- `ASSESSMENT`: Đánh giá
- `OTHER`: Khác

### ToolResultStatus
- `DRAFT`: Bản nháp
- `PUBLISHED`: Đã xuất bản
- `ARCHIVED`: Đã lưu trữ
- `DELETED`: Đã xóa
- `IN_REVIEW`: Đang xem xét
- `APPROVED`: Đã phê duyệt
- `REJECTED`: Đã từ chối

## Validation Rules

### CreateToolResultRequest
- `userId`: Bắt buộc, không được null
- `workspaceId`: Bắt buộc, không được null
- `type`: Bắt buộc, phải là một trong các giá trị enum
- `name`: Bắt buộc, không được trống, tối đa 255 ký tự
- `description`: Tùy chọn, tối đa 1000 ký tự
- `data`: Bắt buộc, không được null
- `status`: Bắt buộc, phải là một trong các giá trị enum

### UpdateToolResultRequest
- Tất cả các field đều tùy chọn
- `name`: Tối đa 255 ký tự
- `description`: Tối đa 1000 ký tự
- `type` và `status`: Phải là giá trị enum hợp lệ nếu được cung cấp
- **Không thể cập nhật `templateId`** (đã loại bỏ khỏi update request)

## Error Handling

API sẽ trả về các mã lỗi sau:
- `200`: Thành công
- `400`: Dữ liệu không hợp lệ hoặc thiếu trường bắt buộc
- `404`: Không tìm thấy resource
- `500`: Lỗi server

## Ví dụ Response với Phân trang

```json
{
  "statusCode": 200,
  "message": "Lấy danh sách kết quả công cụ AI thành công!",
  "data": {
    "content": [
      {
        "id": 123,
        "userId": "0d29b45a-5d6a-44e2-b58d-d7aa5180cb0f",
        "workspaceId": 101,
        "type": "LESSON_PLAN",
        "name": "Giáo án bài 3",
        "status": "DRAFT",
        "createdAt": "2025-07-27T12:34:56"
      }
    ],
    "totalPages": 5,
    "totalElements": 50,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false,
    "numberOfElements": 1
  }
}
```

## Tính năng chính

### 1. Single Value Filters
Tất cả filters đều là single value, phù hợp với thực tế sử dụng:
```
userId=uuid1
workspaceId=101
type=LESSON_PLAN
status=DRAFT
```

### 2. Text Search
Tìm kiếm linh hoạt trong tên và mô tả:
```
nameContains=giáo án
descriptionContains=AI
```

### 3. Enum Select cho Sorting
Sử dụng enum để đảm bảo tính chính xác:
```
sortBy=CREATED_AT  // Thay vì string "createdAt"
sortDirection=DESC // Thay vì string "desc"
```

### 4. Phân trang bắt đầu từ 1
Khác với Spring Data mặc định (bắt đầu từ 0), API này bắt đầu từ trang 1:
```
page=1  // Trang đầu tiên
page=2  // Trang thứ hai
```

### 5. Soft Delete
Xóa mềm bằng cách cập nhật status thành DELETED thay vì xóa record.

### 6. Dynamic Query với JpaSpecificationExecutor
API sử dụng Specification pattern để build query động, hiệu suất cao và linh hoạt.

## Testing

Để chạy test:
```bash
mvn test -Dtest=ToolResultControllerTest
```

## Swagger Documentation

Sau khi khởi động ứng dụng, bạn có thể truy cập Swagger UI tại:
```
http://localhost:8080/swagger-ui.html
```

## Migration từ API cũ

Nếu bạn đang sử dụng API cũ, đây là cách migrate:

**Cũ:**
```
GET /api/tool-results/user/{userId}
GET /api/tool-results/workspace/{workspaceId}
GET /api/tool-results?userId=uuid&workspaceId=101&type=LESSON_PLAN&status=DRAFT&page=0&size=10&sort=createdAt,desc
```

**Mới:**
```
GET /api/tool-results?userId={userId}&page=1&size=10
GET /api/tool-results?workspaceId={workspaceId}&page=1&size=10
GET /api/tool-results?userId=uuid&workspaceId=101&type=LESSON_PLAN&status=DRAFT&page=1&size=10&sortBy=CREATED_AT&sortDirection=DESC
```

## Enum Values cho Sorting

### ToolResultSortBy
- `ID`: Sắp xếp theo ID
- `NAME`: Sắp xếp theo tên
- `CREATED_AT`: Sắp xếp theo ngày tạo
- `UPDATED_AT`: Sắp xếp theo ngày cập nhật
- `TYPE`: Sắp xếp theo loại
- `STATUS`: Sắp xếp theo trạng thái

### SortDirection
- `ASC`: Tăng dần
- `DESC`: Giảm dần

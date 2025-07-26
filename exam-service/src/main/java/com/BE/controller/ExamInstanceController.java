package com.BE.controller;

import com.BE.enums.ExamInstanceStatus;
import com.BE.model.request.ChangeExamStatusRequest;
import com.BE.model.request.CreateExamInstanceRequest;
import com.BE.model.request.SubmitExamRequest;
import com.BE.model.request.UpdateExamInstanceRequest;
import com.BE.model.response.*;
import com.BE.service.interfaceService.IExamInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/exam-instances")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@Slf4j

public class ExamInstanceController {
    
    private final IExamInstanceService examInstanceService;
    
    // Teacher Instance Management APIs
    
    @PostMapping
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Create exam instance",
        description = """
            ## Create Exam Instance

            Creates a new exam instance from an existing template, generating a unique access code
            that students can use to take the exam. This is the bridge between template design and actual exam delivery.

            ### Features:
            - **Template-Based**: Creates instance from existing template
            - **Unique Access Code**: Generates random code for student access
            - **Time Control**: Set specific start and end times
            - **Status Management**: Instance starts in DRAFT status
            - **Content Isolation**: Instance content is independent of template changes

            ### Instance Creation Process:
            1. **Template Validation**: Verifies template exists and user has access
            2. **Content Copy**: Copies template content to instance (snapshot)
            3. **Code Generation**: Creates unique 6-character access code
            4. **Time Validation**: Ensures end time is after start time
            5. **Status Initialization**: Sets initial status to DRAFT

            ### Generated Data:
            - **Unique ID**: New UUID for the instance
            - **Access Code**: Random 6-character code (e.g., "ABC123")
            - **Content Snapshot**: Copy of template content at creation time
            - **Time Window**: Specific start and end times for the exam
            - **Status**: Initial DRAFT status (students cannot access yet)

            ### Time Management:
            - **Start Time**: When students can begin accessing the exam
            - **End Time**: When exam submissions are no longer accepted
            - **Duration**: Inherited from template but can be overridden
            - **Time Zone**: All times are in server timezone

            ### Access Control:
            - **Teacher Only**: Only template owner can create instances
            - **Template Ownership**: Must own the source template
            - **Instance Ownership**: Creator becomes instance owner

            ### Use Cases:
            - **Scheduled Exams**: Create exam for specific date/time
            - **Multiple Sessions**: Create multiple instances from same template
            - **Class Sections**: Different instances for different class sections
            - **Makeup Exams**: Additional instances for students who missed original
            - **Practice Tests**: Create practice versions with different timing

            ### Business Rules:
            - **Template Access**: Must own the source template
            - **Time Logic**: End time must be after start time
            - **Code Uniqueness**: Access codes are guaranteed unique
            - **Content Snapshot**: Instance content won't change if template is updated

            ### Post-Creation Steps:
            1. **Review Instance**: Verify all settings are correct
            2. **Set Status**: Change from DRAFT to SCHEDULED or ACTIVE
            3. **Share Code**: Provide access code to students
            4. **Monitor Progress**: Track student submissions during exam

            ### Security Features:
            - **Ownership Validation**: Only template owner can create instances
            - **Access Code Security**: Codes are random and unpredictable
            - **Time Enforcement**: Students can only access during specified window
            - **Content Protection**: Answers are hidden from students
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo phiên thi thành công"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<DataResponseDTO<ExamInstanceResponse>> createExamInstance(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Exam instance data",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Create Instance Example",
                        value = """
                            {
                                "templateId": "550e8400-e29b-41d4-a716-446655440001",
                                "description": "Math test for class 10A",
                                "startAt": "2024-01-15T08:00:00",
                                "endAt": "2024-01-15T09:00:00"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody CreateExamInstanceRequest request,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {
        
        log.info("Creating exam instance for teacher: {}", teacherId);
        ExamInstanceResponse response = examInstanceService.createExamInstance(request, teacherId);
        DataResponseDTO<ExamInstanceResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.CREATED.value(),
            "Tạo phiên thi thành công",
            response
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dataResponse);
    }
    
    @GetMapping
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Get exam instances by teacher",
        description = """
            ## Get All Exam Instances

            Retrieves all exam instances created by the authenticated teacher, providing a comprehensive
            overview of all exams that have been scheduled, are running, or have completed.

            ### Features:
            - **Teacher Isolation**: Only returns instances created by authenticated teacher
            - **Complete List**: Includes instances in all statuses (DRAFT, SCHEDULED, ACTIVE, etc.)
            - **Sorted Results**: Ordered by creation date (newest first)
            - **Status Information**: Shows current status of each instance
            - **Quick Overview**: Essential information for exam management

            ### Response Data:
            Each instance includes:
            - **Basic Info**: ID, template name, access code, description
            - **Timing**: Start time, end time, duration
            - **Status**: Current status and last change information
            - **Template Reference**: Source template information
            - **Creation Info**: When instance was created

            ### Instance Statuses:
            - **DRAFT**: Being prepared, not accessible to students
            - **SCHEDULED**: Waiting for scheduled start time
            - **ACTIVE**: Currently running, students can access
            - **PAUSED**: Temporarily suspended
            - **COMPLETED**: Finished, results available
            - **CANCELLED**: Cancelled permanently

            ### Use Cases:
            - **Exam Dashboard**: Overview of all teacher's exams
            - **Status Monitoring**: Check current status of all instances
            - **Quick Access**: Navigate to specific exam management
            - **Planning**: See upcoming and past exams
            - **Statistics**: Overview of exam activity

            ### Filtering & Sorting:
            - **Default Sort**: Creation date descending (newest first)
            - **Status Grouping**: Can group by status for better organization
            - **Time-based**: Can filter by date ranges (future enhancement)

            ### Performance Notes:
            - **Optimized Query**: Efficient database query with proper indexing
            - **Minimal Data**: Returns summary data, not full content
            - **Pagination Ready**: Prepared for pagination if needed

            ### Management Actions:
            From this list, teachers can:
            - **View Details**: Click to see full instance information
            - **Change Status**: Start, pause, complete, or cancel exams
            - **View Results**: Access submissions and statistics
            - **Download Reports**: Generate Excel reports
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách phiên thi thành công"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<DataResponseDTO<List<ExamInstanceResponse>>> getExamInstancesByTeacher(
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {

        log.info("Getting exam instances for teacher: {}", teacherId);
        List<ExamInstanceResponse> instances = examInstanceService.getExamInstancesByTeacher(teacherId);
        DataResponseDTO<List<ExamInstanceResponse>> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Lấy danh sách phiên thi thành công",
            instances
        );
        return ResponseEntity.ok(dataResponse);
    }
    
    @GetMapping("/{instanceId}")
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Get exam instance by ID",
        description = """
            ## Get Specific Exam Instance

            Retrieves detailed information about a specific exam instance by its unique identifier.
            Only the teacher who created the instance can access it, ensuring data privacy and security.

            ### Features:
            - **Complete Details**: Full instance information including status and timing
            - **Security**: Only instance owner can access (ownership validation)
            - **Real-time Status**: Current status and recent changes
            - **Template Reference**: Information about source template
            - **Usage Statistics**: Number of submissions and completion rates

            ### Response Includes:
            - **Instance Information**: ID, code, description, timing
            - **Template Details**: Source template name and basic info
            - **Status Information**: Current status, last change time and reason
            - **Access Details**: Unique access code for students
            - **Timing**: Start time, end time, duration
            - **Statistics**: Submission count, completion rate (if available)

            ### Use Cases:
            - **Instance Management**: View and manage specific exam instance
            - **Status Monitoring**: Check current status and recent changes
            - **Pre-exam Review**: Verify settings before starting exam
            - **Troubleshooting**: Debug issues with specific instance
            - **Student Support**: Get access code and timing information

            ### Status-Specific Information:
            - **DRAFT**: Shows preparation status, ready for scheduling
            - **SCHEDULED**: Shows countdown to start time
            - **ACTIVE**: Shows real-time submission progress
            - **PAUSED**: Shows pause reason and duration
            - **COMPLETED**: Shows final statistics and results summary
            - **CANCELLED**: Shows cancellation reason and timestamp

            ### Security Notes:
            - **Ownership Check**: Only instance creator can access
            - **Data Privacy**: Instances are isolated between teachers
            - **Access Control**: Returns 403 if user doesn't own instance

            ### Management Actions:
            From instance details, teachers can:
            - **Change Status**: Modify instance status as needed
            - **View Submissions**: Access student submissions and results
            - **Download Reports**: Generate Excel reports
            - **Update Settings**: Modify description or timing (if allowed)
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thông tin phiên thi thành công"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your instance"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<DataResponseDTO<ExamInstanceResponse>> getExamInstanceById(
            @PathVariable UUID instanceId,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {

        log.info("Getting exam instance {} for teacher: {}", instanceId, teacherId);
        ExamInstanceResponse instance = examInstanceService.getExamInstanceById(instanceId, teacherId);
        DataResponseDTO<ExamInstanceResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Lấy thông tin phiên thi thành công",
            instance
        );
        return ResponseEntity.ok(dataResponse);
    }
    
    @PutMapping("/{instanceId}")
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Update exam instance",
        description = """
            ## Update Exam Instance

            Updates an existing exam instance with new information. This endpoint supports partial updates,
            meaning only the fields provided in the request will be modified, leaving other fields unchanged.

            ### Features:
            - **Partial Updates**: Only provided fields are updated
            - **Status Validation**: Updates are validated against current status
            - **Time Validation**: Ensures time changes are logical and valid
            - **Ownership Security**: Only instance owner can make updates
            - **Atomic Operation**: All changes are applied together or none at all

            ### Updatable Fields:
            - **description**: Instance description or notes
            - **startAt**: Exam start time (with restrictions)
            - **endAt**: Exam end time (with restrictions)

            ### Update Restrictions by Status:

            **DRAFT Status:**
            - ✅ Can update: description, startAt, endAt
            - ✅ Full flexibility in timing changes
            - ✅ No restrictions on modifications

            **SCHEDULED Status:**
            - ✅ Can update: description, startAt, endAt
            - ⚠️ Time changes should be reasonable
            - ⚠️ Consider impact on students who have the schedule

            **ACTIVE Status:**
            - ✅ Can update: description
            - ❌ Cannot update: startAt, endAt (exam is running)
            - ⚠️ Limited modifications during active exam

            **PAUSED Status:**
            - ✅ Can update: description
            - ⚠️ Limited timing modifications
            - ⚠️ Consider impact on paused exam

            **COMPLETED/CANCELLED Status:**
            - ❌ No updates allowed (final states)
            - 🔒 Instance is locked for data integrity

            ### Time Validation Rules:
            - **End After Start**: End time must be after start time
            - **Future Times**: Start time should be in future (for DRAFT/SCHEDULED)
            - **Reasonable Duration**: Duration should be reasonable for exam type
            - **Student Impact**: Consider impact on students who may have started

            ### Use Cases:
            - **Schedule Adjustment**: Modify exam timing before it starts
            - **Description Update**: Add notes or clarifications
            - **Emergency Changes**: Adjust timing due to unforeseen circumstances
            - **Preparation**: Fine-tune settings before activation

            ### Business Rules:
            - **Ownership Required**: Only instance creator can update
            - **Status Dependent**: Available updates depend on current status
            - **Time Logic**: All time changes must be logically valid
            - **Student Impact**: Consider impact on students who may be preparing

            ### Best Practices:
            - **Communicate Changes**: Inform students of any timing changes
            - **Reasonable Notice**: Give adequate notice for schedule changes
            - **Status Check**: Verify current status before making changes
            - **Validation**: Double-check all changes before submitting

            ### Error Scenarios:
            - **Invalid Status**: Attempting updates on completed/cancelled instances
            - **Time Conflicts**: End time before start time
            - **Past Times**: Setting start time in the past
            - **Access Denied**: User doesn't own the instance
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật phiên thi thành công"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your instance"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<DataResponseDTO<ExamInstanceResponse>> updateExamInstance(
            @PathVariable UUID instanceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated instance data (only provided fields will be updated)",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Update Instance Example",
                        value = """
                            {
                                "description": "Updated: Math test for class 10A - Final exam",
                                "startAt": "2024-01-15T09:00:00",
                                "endAt": "2024-01-15T10:30:00"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody UpdateExamInstanceRequest request,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {
        
        log.info("Updating exam instance {} for teacher: {}", instanceId, teacherId);
        ExamInstanceResponse response = examInstanceService.updateExamInstance(instanceId, request, teacherId);
        DataResponseDTO<ExamInstanceResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Cập nhật phiên thi thành công",
            response
        );
        return ResponseEntity.ok(dataResponse);
    }
    
    @DeleteMapping("/{instanceId}")
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Delete exam instance",
        description = """
            ## Delete Exam Instance

            Permanently deletes an exam instance and all associated data including student submissions,
            results, and generated reports. This is a destructive operation that cannot be undone.

            ### ⚠️ Critical Warning:
            - **Irreversible Action**: Deleted instances cannot be recovered
            - **Data Loss**: All submissions, results, and reports will be permanently lost
            - **Student Impact**: Students lose access to their results and certificates
            - **Audit Impact**: Historical exam data is permanently removed

            ### What Gets Deleted:
            - **Instance Record**: Complete instance data and metadata
            - **Student Submissions**: All student answers and submission data
            - **Results**: Calculated scores, grades, and detailed results
            - **Excel Reports**: Generated report files and statistics
            - **Access Logs**: Student access and activity logs
            - **Status History**: All status changes and audit trail

            ### Cascade Deletion Process:
            1. **Submission Cleanup**: Removes all student submissions
            2. **Result Details**: Deletes detailed result breakdowns
            3. **File Cleanup**: Removes generated Excel files
            4. **Instance Removal**: Finally removes the instance record
            5. **Audit Logging**: Logs deletion event for compliance

            ### Pre-deletion Validation:
            - **Ownership Check**: Confirms user owns the instance
            - **Status Validation**: Ensures deletion is appropriate for current status
            - **Data Integrity**: Verifies deletion won't break system integrity

            ### Status-Specific Considerations:

            **DRAFT/SCHEDULED:**
            - ✅ Safe to delete (no student data yet)
            - ✅ No impact on students
            - ✅ Clean deletion with minimal consequences

            **ACTIVE:**
            - ⚠️ Students may be currently taking exam
            - ⚠️ Consider pausing instead of deleting
            - ⚠️ High impact on active students

            **PAUSED:**
            - ⚠️ Students may have partial submissions
            - ⚠️ Consider completing instead of deleting
            - ⚠️ Loss of student progress

            **COMPLETED:**
            - ❌ High impact - students lose their results
            - ❌ Loss of valuable assessment data
            - ❌ Consider archiving instead of deletion

            **CANCELLED:**
            - ✅ Reasonable to delete if no longer needed
            - ⚠️ Still contains submission data if any

            ### Use Cases:
            - **Test Cleanup**: Remove test instances created during setup
            - **Duplicate Removal**: Clean up accidentally created duplicates
            - **Privacy Compliance**: Remove instances containing sensitive data
            - **Storage Management**: Free up database space
            - **Error Correction**: Remove instances created with wrong settings

            ### Alternatives to Deletion:
            - **Status Change**: Change to CANCELLED instead of deleting
            - **Archiving**: Mark as archived but keep data (future feature)
            - **Export First**: Download results before deletion
            - **Template Recreation**: Recreate template from instance if needed

            ### Best Practices:
            - **Export Data**: Download Excel reports before deletion
            - **Student Notification**: Inform students if they had access
            - **Team Communication**: Notify team members of deletion
            - **Double Check**: Verify you're deleting the correct instance
            - **Consider Alternatives**: Evaluate if deletion is really necessary

            ### Recovery Options:
            - **No Recovery**: Deleted instances cannot be restored
            - **Backup Restore**: Only possible if system backup exists
            - **Manual Recreation**: Must recreate instance and re-enter data

            ### Compliance Notes:
            - **Audit Trail**: Deletion is logged with user and timestamp
            - **Data Retention**: Consider legal requirements before deletion
            - **Student Rights**: Students may have right to their exam results
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa phiên thi thành công"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your instance"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<DataResponseDTO<Void>> deleteExamInstance(
            @PathVariable UUID instanceId,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {
        
        log.info("Deleting exam instance {} for teacher: {}", instanceId, teacherId);
        examInstanceService.deleteExamInstance(instanceId, teacherId);
        DataResponseDTO<Void> dataResponse = new DataResponseDTO<>(
            HttpStatus.NO_CONTENT.value(),
            "Xóa phiên thi thành công",
            null
        );
        return ResponseEntity.ok(dataResponse);
    }
    
    // Student Exam Access APIs (Public - No Authentication Required)
    
    @GetMapping("/code/{code}")
    @Operation(
        summary = "Get exam by code (Student API)",
        description = """
            ## Student Exam Access

            **🎓 PUBLIC ENDPOINT - No Authentication Required**

            Students use this endpoint to access exam content using the unique access code provided by their teacher.
            This is the primary entry point for students to begin taking an exam.

            ### Features:
            - **Public Access**: No authentication or login required
            - **Code-Based Access**: Uses unique 6-character access code
            - **Secure Content**: Returns questions WITHOUT correct answers
            - **Status Validation**: Only accessible when exam is ACTIVE
            - **Time Validation**: Respects exam start/end time windows

            ### Access Requirements:
            - **Valid Code**: Must provide correct 6-character access code
            - **Active Status**: Exam must be in ACTIVE status
            - **Time Window**: Current time must be within exam start/end window
            - **No Authentication**: Students don't need to log in

            ### Response Content:
            - **Exam Information**: Name, subject, grade level, duration, total score
            - **School Information**: School name and exam code (if provided)
            - **Chemistry Support**: Atomic masses information for chemistry exams
            - **Questions**: Complete question structure without answers
            - **Instructions**: Any special instructions for the exam
            - **Timing**: Start time, end time, remaining duration
            - **Format**: Questions in student-friendly format

            ### Content Security:
            - **Answer Removal**: All correct answers are stripped from response
            - **Clean Structure**: Questions formatted for student display
            - **No Hints**: No information that could reveal correct answers
            - **Fair Access**: All students get identical question structure

            ### Question Formats Supported:
            - **Multiple Choice**: Options without indicating correct answer
            - **True/False**: Statements without correct values
            - **Fill in Blank**: Questions without answer keys
            - **Essay**: Open-ended questions for written responses

            ### Use Cases:
            - **Exam Start**: Students begin taking the exam
            - **Content Review**: Students review questions before starting
            - **Technical Check**: Verify exam loads correctly
            - **Accessibility**: Students with special needs access content

            ### Error Scenarios:
            - **Invalid Code**: Code doesn't exist or is incorrect
            - **Wrong Status**: Exam is not in ACTIVE status
            - **Time Restriction**: Outside of allowed time window
            - **System Error**: Technical issues preventing access

            ### Student Experience:
            1. **Receive Code**: Teacher provides 6-character access code
            2. **Enter Code**: Student enters code in exam interface
            3. **Access Granted**: System validates and returns exam content
            4. **Begin Exam**: Student can start answering questions
            5. **Submit Answers**: Use submit endpoint when finished

            ### Status-Based Access:
            - **DRAFT**: ❌ Not accessible (exam being prepared)
            - **SCHEDULED**: ❌ Not accessible (waiting for start time)
            - **ACTIVE**: ✅ Accessible (exam is live)
            - **PAUSED**: ❌ Not accessible (temporarily suspended)
            - **COMPLETED**: ❌ Not accessible (exam has ended)
            - **CANCELLED**: ❌ Not accessible (exam cancelled)

            ### Time Validation:
            - **Before Start**: Returns error if accessed too early
            - **After End**: Returns error if accessed too late
            - **During Window**: Full access to exam content
            - **Grace Period**: May include small buffer for technical issues

            ### Performance Notes:
            - **Optimized Response**: Minimal data transfer for faster loading
            - **Caching**: Content may be cached for better performance
            - **Concurrent Access**: Supports multiple students accessing simultaneously
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy nội dung đề thi thành công",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 200,
                        "message": "Lấy nội dung đề thi thành công",
                        "data": {
                            "examInstanceId": "917e3cc3-6c84-40ba-a9cc-3a86c894e8b7",
                            "examName": "Template Hóa học - Lớp 10",
                            "subject": "Hóa học",
                            "grade": 12,
                            "durationMinutes": 45,
                            "school": "THPT Hong Thinh",
                            "examCode": "1234",
                            "atomicMasses": "H=1, C=12, O=16, N=14",
                            "totalScore": 7.75,
                            "contentJson": {
                                "parts": [
                                    {
                                        "part": "PHẦN I",
                                        "title": "Câu trắc nghiệm nhiều phương án lựa chọn",
                                        "questions": [
                                            {
                                                "id": "1",
                                                "questionNumber": 1,
                                                "question": "Câu hỏi mẫu?",
                                                "options": {
                                                    "A": "Đáp án A",
                                                    "B": "Đáp án B",
                                                    "C": "Đáp án C",
                                                    "D": "Đáp án D"
                                                }
                                            }
                                        ]
                                    }
                                ]
                            },
                            "startAt": "2025-07-24T14:51:01.385105",
                            "endAt": "2025-07-31T17:00:00",
                            "code": "DZ99YX"
                        }
                    }
                    """))),
        @ApiResponse(responseCode = "404", description = "Exam not found or not available"),
        @ApiResponse(responseCode = "400", description = "Exam not available (outside time window)")
    })
    public ResponseEntity<DataResponseDTO<ExamContentResponse>> getExamByCode(
            @Parameter(description = "Exam access code (e.g., ABC123)", example = "ABC123")
            @PathVariable String code) {
        
        log.info("Student accessing exam with code: {}", code);
        ExamContentResponse examContent = examInstanceService.getExamByCode(code);
        DataResponseDTO<ExamContentResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Lấy nội dung đề thi thành công",
            examContent
        );
        return ResponseEntity.ok(dataResponse);
    }
    
    @PostMapping("/code/{code}/submit")
    @Operation(
        summary = "Submit exam (Student API)",
        description = """
            ## Student Exam Submission

            **🎓 PUBLIC ENDPOINT - No Authentication Required**

            Students use this endpoint to submit their completed exam answers. The system automatically
            grades the submission and returns immediate results with score and detailed feedback.

            ### Features:
            - **Public Access**: No authentication required
            - **Instant Grading**: Automatic scoring with immediate results
            - **Duplicate Prevention**: Prevents multiple submissions from same student
            - **Detailed Results**: Complete breakdown of correct/incorrect answers
            - **Excel Generation**: Automatically updates teacher's Excel report

            ### Submission Requirements:
            - **Valid Code**: Must use correct exam access code
            - **Active Status**: Exam must be in ACTIVE status
            - **Time Window**: Must submit within exam time window
            - **Student Name**: Required for identification and results
            - **Answer Format**: Answers must match expected format

            ### Answer Format:
            ```json
            {
                "studentName": "John Doe",
                "answers": [
                    {
                        "questionId": "be06dfd5-e0dc-43ee-9ce1-d8f34607dc8a",
                        "answer": "A",
                        "questionType": "multiple_choice"
                    },
                    {
                        "questionId": "7992fce2-658a-4ed7-8883-e76fa60c7037",
                        "answer": "A",
                        "questionType": "true_false"
                    },
                    {
                        "questionId": "0a45811d-42b1-4a2e-8382-21987cfdeac5",
                        "answer": "1",
                        "questionType": "short_answer"
                    }
                ]
            }
            ```

            ### Grading Process:
            1. **Answer Validation**: Validates answer format and completeness
            2. **Content Matching**: Matches answers against correct solutions
            3. **Score Calculation**: Applies custom grading configuration
            4. **Result Generation**: Creates detailed result breakdown
            5. **Storage**: Saves submission and results to database
            6. **Report Update**: Updates Excel report with new submission

            ### Grading Features:
            - **Custom Scoring**: Uses template's grading configuration
            - **Part-Based Scoring**: Different point values for different sections
            - **Partial Credit**: May award partial credit for complex questions
            - **Immediate Results**: Returns score and feedback instantly

            ### Response Includes:
            - **Final Score**: Total points earned out of maximum possible
            - **Percentage**: Score as percentage for easy understanding
            - **Correct Count**: Number of questions answered correctly
            - **Total Questions**: Total number of questions in exam
            - **Submission Time**: When the exam was submitted
            - **Detailed Breakdown**: Per-question results (if enabled)

            ### Duplicate Prevention:
            - **Name-Based Check**: Prevents same student name from submitting twice
            - **Immediate Error**: Returns error if student already submitted
            - **Data Integrity**: Ensures one submission per student per exam

            ### Use Cases:
            - **Exam Completion**: Student finishes and submits exam
            - **Time Pressure**: Student submits before time runs out
            - **Partial Submission**: Student submits incomplete exam
            - **Technical Recovery**: Resubmission after technical issues (if allowed)

            ### Error Scenarios:
            - **Invalid Code**: Exam code doesn't exist
            - **Wrong Status**: Exam not in submittable status
            - **Time Expired**: Submission after exam end time
            - **Duplicate**: Student already submitted
            - **Invalid Format**: Answer format doesn't match requirements

            ### Student Experience:
            1. **Complete Exam**: Student answers all questions
            2. **Review Answers**: Optional review before submission
            3. **Submit**: Click submit button to send answers
            4. **Instant Results**: Receive immediate score and feedback
            5. **Result Review**: Review detailed breakdown of performance

            ### Status-Based Submission:
            - **DRAFT**: ❌ Cannot submit (exam not ready)
            - **SCHEDULED**: ❌ Cannot submit (exam not started)
            - **ACTIVE**: ✅ Can submit (exam is live)
            - **PAUSED**: ❌ Cannot submit (exam paused)
            - **COMPLETED**: ❌ Cannot submit (exam ended)
            - **CANCELLED**: ❌ Cannot submit (exam cancelled)

            ### Security Features:
            - **Answer Encryption**: Answers may be encrypted in transit
            - **Submission Validation**: Validates all submission data
            - **Time Stamps**: Records exact submission time
            - **Audit Trail**: Logs submission for audit purposes

            ### Performance Impact:
            - **Grading Speed**: Automatic grading is typically very fast
            - **Excel Update**: May take additional time for large classes
            - **Database Write**: Stores complete submission data
            - **Concurrent Handling**: Supports multiple simultaneous submissions
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nộp bài và chấm điểm thành công"),
        @ApiResponse(responseCode = "400", description = "Invalid submission"),
        @ApiResponse(responseCode = "404", description = "Exam not found")
    })
    public ResponseEntity<DataResponseDTO<SubmitExamResponse>> submitExam(
            @Parameter(description = "Exam access code", example = "ABC123")
            @PathVariable String code,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Student submission data",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Submit Exam Example",
                        value = """
                            {
                                "studentName": "Nguyen Van A",
                                "answers": [
                                    {
                                        "questionId": "be06dfd5-e0dc-43ee-9ce1-d8f34607dc8a",
                                        "answer": "A",
                                        "questionType": "multiple_choice"
                                    },
                                    {
                                        "questionId": "7992fce2-658a-4ed7-8883-e76fa60c7037",
                                        "answer": "A",
                                        "questionType": "true_false"
                                    },
                                    {
                                        "questionId": "0a45811d-42b1-4a2e-8382-21987cfdeac5",
                                        "answer": "1",
                                        "questionType": "short_answer"
                                    }
                                ]
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody SubmitExamRequest request) {
        
        log.info("Student {} submitting exam with code: {}", request.getStudentName(), code);
        SubmitExamResponse response = examInstanceService.submitExam(code, request);
        DataResponseDTO<SubmitExamResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Nộp bài thi thành công",
            response
        );
        return ResponseEntity.ok(dataResponse);
    }

    // Results Management APIs (Teacher Only)

    @GetMapping("/{instanceId}/submissions")
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Get exam submissions",
        description = """
            ## Get Exam Submissions and Results

            **🔒 TEACHER ONLY - Authentication Required**

            Retrieves all student submissions for a specific exam instance, including detailed results,
            statistics, and performance analytics. This is the primary endpoint for teachers to review exam outcomes.

            ### Features:
            - **Complete Results**: All student submissions with detailed scoring
            - **Performance Analytics**: Class statistics and performance metrics
            - **Detailed Breakdown**: Per-question analysis and common mistakes
            - **Sorting Options**: Results sorted by score, name, or submission time
            - **Export Ready**: Data formatted for easy export and reporting

            ### Response Includes:

            **Submission Data:**
            - **Student Information**: Name and submission timestamp
            - **Scores**: Total score, percentage, correct/incorrect counts
            - **Answer Details**: Complete answer breakdown per question
            - **Question-by-Question Results**: Detailed breakdown showing which questions each student got right/wrong
            - **Result Details**: For each question - student answer, correct answer, and correctness status
            - **Time Analysis**: Time taken to complete exam
            - **Status**: Submission status and validation results

            **Class Statistics:**
            - **Overall Performance**: Average score, median, standard deviation
            - **Score Distribution**: Grade distribution and percentile rankings
            - **Question Analysis**: Most missed questions and common errors
            - **Completion Rates**: Percentage of students who completed exam
            - **Time Statistics**: Average completion time and time distribution

            **Performance Metrics:**
            - **High Performers**: Top scoring students
            - **Struggling Students**: Students who may need additional support
            - **Question Difficulty**: Analysis of question difficulty based on results
            - **Learning Objectives**: Performance by learning objective or topic

            ### Data Organization:
            - **Student-Centric**: Organized by student with complete performance data
            - **Question-Centric**: Analysis by question showing class performance
            - **Statistical Summary**: Overall class performance metrics
            - **Trend Analysis**: Performance trends and patterns

            ### Use Cases:
            - **Grade Review**: Review and validate student grades
            - **Performance Analysis**: Analyze class and individual performance
            - **Feedback Preparation**: Prepare detailed feedback for students
            - **Curriculum Assessment**: Evaluate effectiveness of teaching materials
            - **Parent Conferences**: Data for parent-teacher discussions
            - **Academic Planning**: Plan remediation or advanced instruction

            ### Filtering and Sorting:
            - **By Score**: Highest to lowest or vice versa
            - **By Name**: Alphabetical order for easy lookup
            - **By Time**: Submission order or completion time
            - **By Status**: Filter by completion status
            - **By Performance**: Group by performance levels

            ### Security Features:
            - **Ownership Validation**: Only instance owner can access results
            - **Data Privacy**: Student data protected and isolated
            - **Access Control**: Comprehensive permission checking
            - **Audit Logging**: Access to results is logged

            ### Performance Considerations:
            - **Large Classes**: Optimized for classes with many students
            - **Detailed Data**: May take longer for very detailed analysis
            - **Caching**: Results may be cached for better performance
            - **Pagination**: Large result sets may be paginated

            ### Integration Features:
            - **Excel Export**: Data ready for Excel report generation
            - **Grade Book**: Compatible with grade book systems
            - **LMS Integration**: Can integrate with Learning Management Systems
            - **Analytics Tools**: Data formatted for analytics platforms

            ### Quality Assurance:
            - **Answer Validation**: Validates all student answers
            - **Score Verification**: Double-checks all score calculations
            - **Data Integrity**: Ensures all submission data is complete
            - **Error Detection**: Identifies potential grading errors

            ### Teacher Workflow:
            1. **Access Results**: View all student submissions
            2. **Review Performance**: Analyze class and individual performance
            3. **Identify Issues**: Spot students needing additional support
            4. **Prepare Feedback**: Use data to prepare student feedback
            5. **Export Data**: Generate reports for records or sharing
            6. **Plan Instruction**: Use results to plan future instruction

            ### Response Example with Detailed Results:
            ```json
            {
                "code": 200,
                "message": "Lấy danh sách bài nộp thành công",
                "data": [
                    {
                        "id": "uuid",
                        "studentName": "Nguyen Van A",
                        "score": 8.5,
                        "correctCount": 17,
                        "totalQuestions": 20,
                        "maxScore": 10.0,
                        "submittedAt": "2024-01-15T10:30:00",
                        "resultDetails": [
                            {
                                "questionId": "PHẦN I_Q1",
                                "studentAnswer": "A",
                                "correctAnswer": "B",
                                "isCorrect": false
                            },
                            {
                                "questionId": "PHẦN II_Q2_a",
                                "studentAnswer": "true",
                                "correctAnswer": "true",
                                "isCorrect": true
                            }
                        ]
                    }
                ]
            }
            ```
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách bài nộp thành công"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your exam instance"),
        @ApiResponse(responseCode = "404", description = "Exam instance not found")
    })
    public ResponseEntity<DataResponseDTO<List<ExamSubmissionResponse>>> getExamSubmissions(
            @Parameter(description = "Exam instance ID", example = "550e8400-e29b-41d4-a716-446655440003")
            @PathVariable UUID instanceId,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {

        log.info("Getting submissions for exam instance {} by teacher: {}", instanceId, teacherId);
        List<ExamSubmissionResponse> submissions = examInstanceService.getExamSubmissions(instanceId, teacherId);
        DataResponseDTO<List<ExamSubmissionResponse>> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Lấy danh sách bài nộp thành công",
            submissions
        );
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("/{instanceId}/excel")
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Download Excel report",
        description = """
            ## Download Excel Report

            **🔒 TEACHER ONLY - Authentication Required**

            Downloads a comprehensive Excel report containing detailed exam results, student performance data,
            and statistical analysis. This report is automatically generated and updated with each new submission.

            ### Features:
            - **Comprehensive Report**: Complete exam results in professional Excel format
            - **Multiple Worksheets**: Organized data across multiple sheets
            - **Statistical Analysis**: Built-in charts and statistical summaries
            - **Professional Format**: Ready for sharing with administrators or parents
            - **Auto-Generated**: Automatically created and updated with submissions

            ### Excel File Structure:

            **Sheet 1 - Student Results:**
            - Student names and submission timestamps
            - Total scores, percentages, and letter grades
            - Correct/incorrect answer counts
            - Time taken to complete exam
            - Individual question responses

            **Sheet 2 - Question Analysis:**
            - Question-by-question performance statistics
            - Percentage of students answering correctly
            - Most common incorrect answers
            - Question difficulty analysis
            - Learning objective alignment

            **Sheet 3 - Class Statistics:**
            - Overall class performance metrics
            - Score distribution and grade breakdown
            - Statistical measures (mean, median, mode, standard deviation)
            - Performance trends and patterns
            - Comparison with previous exams (if available)

            **Sheet 4 - Detailed Answers:**
            - Complete student answer breakdown
            - Correct answers for reference
            - Answer analysis and explanations
            - Partial credit assignments (if applicable)

            ### Report Features:
            - **Charts and Graphs**: Visual representation of performance data
            - **Color Coding**: Easy identification of performance levels
            - **Formulas**: Built-in Excel formulas for dynamic calculations
            - **Filtering**: Excel filters for easy data manipulation
            - **Sorting**: Pre-sorted data with custom sort options

            ### Use Cases:
            - **Grade Recording**: Import grades into grade book systems
            - **Parent Communication**: Share individual student results
            - **Administrative Reporting**: Provide data to school administration
            - **Curriculum Analysis**: Analyze effectiveness of teaching materials
            - **Student Conferences**: Data for student progress discussions
            - **Academic Records**: Permanent record of exam performance

            ### File Details:
            - **Format**: Microsoft Excel (.xlsx) format
            - **Compatibility**: Compatible with Excel 2010 and newer
            - **File Size**: Varies based on number of students and questions
            - **Naming**: Descriptive filename with exam and date information
            - **Storage**: Files stored securely on server

            ### Security Features:
            - **Access Control**: Only exam owner can download report
            - **Secure Download**: Files transmitted securely
            - **Temporary Storage**: Files may be cleaned up after download
            - **Data Privacy**: Student data protected according to privacy policies

            ### Performance Notes:
            - **Generation Time**: Large classes may take longer to generate
            - **File Size**: Complex exams create larger files
            - **Download Speed**: Depends on file size and connection
            - **Browser Compatibility**: Works with all modern browsers

            ### Integration Options:
            - **Grade Book Import**: Direct import into popular grade book systems
            - **LMS Integration**: Upload to Learning Management Systems
            - **Data Analysis**: Import into statistical analysis tools
            - **Backup Storage**: Save to cloud storage for backup

            ### Quality Assurance:
            - **Data Validation**: All data validated before report generation
            - **Format Checking**: Excel format validated for compatibility
            - **Error Handling**: Graceful handling of generation errors
            - **Version Control**: Reports versioned for consistency

            ### Teacher Workflow:
            1. **Request Download**: Click download button for exam instance
            2. **File Generation**: System generates comprehensive Excel report
            3. **Secure Download**: File downloaded securely to teacher's device
            4. **Data Analysis**: Use Excel features to analyze results
            5. **Share Results**: Share appropriate data with stakeholders
            6. **Record Keeping**: Save file for permanent records

            ### Troubleshooting:
            - **Large Files**: May take time to generate for large classes
            - **Browser Issues**: Try different browser if download fails
            - **File Corruption**: Re-download if file appears corrupted
            - **Access Issues**: Verify ownership of exam instance
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo và tải file Excel thành công",
            content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your exam instance"),
        @ApiResponse(responseCode = "404", description = "Exam instance not found")
    })
    public ResponseEntity<Resource> downloadExcelReport(
            @Parameter(description = "Exam instance ID", example = "550e8400-e29b-41d4-a716-446655440003")
            @PathVariable UUID instanceId,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {

        log.info("Generating Excel report for exam instance {} by teacher: {}", instanceId, teacherId);
        Resource resource = examInstanceService.generateExcelReport(instanceId, teacherId);

        String filename = String.format("exam_results_%s.xlsx", instanceId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PutMapping("/{instanceId}/status")
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Change exam instance status",
        description = """
            ## Overview
            Change the status of an exam instance to control the exam lifecycle. This endpoint provides teachers with full control over when students can access and submit exams.

            ## Status Definitions
            - **DRAFT**: Initial state, exam is being prepared (students cannot access)
            - **SCHEDULED**: Exam is scheduled for future start time (students cannot access yet)
            - **ACTIVE**: Exam is live and running (students can access and submit)
            - **PAUSED**: Exam is temporarily suspended (students cannot access)
            - **COMPLETED**: Exam has ended (students cannot submit, results available)
            - **CANCELLED**: Exam has been cancelled (students cannot access, no results)

            ## Valid Status Transitions

            ### From DRAFT:
            - → **SCHEDULED**: Set exam to wait for scheduled start time
            - → **ACTIVE**: Start exam immediately (bypass scheduling)
            - → **CANCELLED**: Cancel exam before it starts

            ### From SCHEDULED:
            - → **DRAFT**: Unschedule exam (back to preparation)
            - → **ACTIVE**: Start exam manually (before scheduled time) or automatically
            - → **CANCELLED**: Cancel scheduled exam

            ### From ACTIVE:
            - → **PAUSED**: Temporarily suspend exam (technical issues, etc.)
            - → **COMPLETED**: End exam early (all students finished, etc.)

            ### From PAUSED:
            - → **ACTIVE**: Resume exam after pause
            - → **COMPLETED**: End exam while paused
            - → **CANCELLED**: Cancel exam while paused

            ### Final States:
            - **COMPLETED** and **CANCELLED** are final states - no further transitions allowed

            ## Business Rules
            - Only the teacher who created the exam template can change status
            - Students can only access exam when status is **ACTIVE**
            - Students can only submit answers when status is **ACTIVE**
            - Time validation applies: exam must be within startAt-endAt window when ACTIVE
            - Status changes are logged with timestamp and reason for audit trail

            ## Common Use Cases
            1. **Early Start**: Change SCHEDULED → ACTIVE to start exam before scheduled time
            2. **Technical Pause**: Change ACTIVE → PAUSED for technical issues, then PAUSED → ACTIVE to resume
            3. **Early End**: Change ACTIVE → COMPLETED when all students finish early
            4. **Emergency Cancel**: Change any non-final status → CANCELLED for emergencies
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Thay đổi trạng thái thành công",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                            "statusCode": 200,
                            "message": "Thay đổi trạng thái đề thi thành Đang diễn ra - học sinh có thể làm bài thành công",
                            "data": {
                                "id": "550e8400-e29b-41d4-a716-446655440003",
                                "templateId": "550e8400-e29b-41d4-a716-446655440001",
                                "templateName": "Math Basic Test",
                                "code": "ABC123",
                                "description": "Math test for class 10A",
                                "startAt": "2024-01-15T08:00:00",
                                "endAt": "2024-01-15T09:00:00",
                                "excelUrl": null,
                                "createdAt": "2024-01-14T10:00:00",
                                "durationMinutes": 60,
                                "subject": "Mathematics",
                                "grade": 10,
                                "status": "ACTIVE",
                                "statusChangedAt": "2024-01-15T07:45:00",
                                "statusChangeReason": "Starting exam early due to schedule change"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status transition, validation error, or business rule violation",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Invalid Status Transition",
                        value = """
                            {
                                "statusCode": 400,
                                "message": "Cannot pause exam from status DRAFT. Current status must be ACTIVE."
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Time Validation Error",
                        value = """
                            {
                                "statusCode": 400,
                                "message": "Cannot schedule exam with start time in the past"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Invalid Status Value",
                        value = """
                            {
                                "statusCode": 400,
                                "message": "Field 'status' has invalid value 'INVALID'. Expected one of: DRAFT, SCHEDULED, ACTIVE, PAUSED, COMPLETED, CANCELLED"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Validation Error",
                        value = """
                            {
                                "statusCode": 400,
                                "message": "Validation failed: {status=Status is required}"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing or invalid authentication token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = """
                        {
                            "statusCode": 401,
                            "message": "Unauthenticated"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Access denied to this exam instance",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Access Denied",
                    value = """
                        {
                            "statusCode": 403,
                            "message": "Access denied to this exam instance"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Exam instance not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                        {
                            "statusCode": 404,
                            "message": "Exam instance not found"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                            "statusCode": 500,
                            "message": "An unexpected error occurred: Database connection failed"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<DataResponseDTO<ExamInstanceResponse>> changeExamStatus(
            @Parameter(
                description = "Unique identifier of the exam instance to change status",
                example = "550e8400-e29b-41d4-a716-446655440003",
                required = true
            )
            @PathVariable UUID instanceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = """
                    Status change request containing the new status and optional reason.

                    **Required Fields:**
                    - status: New status for the exam instance

                    **Optional Fields:**
                    - reason: Explanation for the status change (recommended for audit trail)

                    **Valid Status Values:**
                    - DRAFT: Exam is in preparation phase
                    - SCHEDULED: Exam is scheduled for future start
                    - ACTIVE: Exam is live and students can participate
                    - PAUSED: Exam is temporarily suspended
                    - COMPLETED: Exam has ended
                    - CANCELLED: Exam has been cancelled
                    """,
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    examples = {
                        @ExampleObject(
                            name = "Start Exam Early",
                            summary = "Start exam before scheduled time",
                            description = "Use this to start an exam early when all students are ready or due to schedule changes",
                            value = """
                                {
                                    "status": "ACTIVE",
                                    "reason": "All students are ready - starting 15 minutes early"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Schedule Exam",
                            summary = "Schedule a draft exam",
                            description = "Move exam from DRAFT to SCHEDULED status to wait for automatic start",
                            value = """
                                {
                                    "status": "SCHEDULED",
                                    "reason": "Exam is ready and scheduled for automatic start"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Pause Exam",
                            summary = "Temporarily pause active exam",
                            description = "Use this to pause exam due to technical issues or other interruptions",
                            value = """
                                {
                                    "status": "PAUSED",
                                    "reason": "Network connectivity issues - pausing temporarily"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Resume Exam",
                            summary = "Resume paused exam",
                            description = "Resume exam after resolving issues that caused the pause",
                            value = """
                                {
                                    "status": "ACTIVE",
                                    "reason": "Technical issues resolved - resuming exam"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Complete Exam Early",
                            summary = "End exam before scheduled end time",
                            description = "Use this when all students have finished or need to end exam early",
                            value = """
                                {
                                    "status": "COMPLETED",
                                    "reason": "All students have completed the exam"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Cancel Exam",
                            summary = "Cancel exam permanently",
                            description = "Use this to cancel exam due to unforeseen circumstances. This is irreversible.",
                            value = """
                                {
                                    "status": "CANCELLED",
                                    "reason": "Cancelled due to emergency situation"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Unschedule Exam",
                            summary = "Move scheduled exam back to draft",
                            description = "Use this to unschedule an exam and return it to draft for modifications",
                            value = """
                                {
                                    "status": "DRAFT",
                                    "reason": "Need to make changes to exam content"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Minimal Request",
                            summary = "Status change without reason",
                            description = "Minimum required fields - reason is optional but recommended",
                            value = """
                                {
                                    "status": "ACTIVE"
                                }
                                """
                        )
                    }
                )
            )
            @Valid @RequestBody ChangeExamStatusRequest request,
            @Parameter(
                description = """
                    Teacher ID extracted from JWT token by API Gateway.
                    This header is automatically added by the API Gateway after JWT validation.
                    Clients should NOT send this header manually.
                    """,
                hidden = true
            )
            @RequestHeader("X-User-Id") UUID teacherId) {

        log.info("Changing exam instance {} status to {} by teacher: {}", instanceId, request.getStatus(), teacherId);
        ExamInstanceResponse response = examInstanceService.changeExamStatus(instanceId, request, teacherId);
        DataResponseDTO<ExamInstanceResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            String.format("Thay đổi trạng thái đề thi thành %s thành công", request.getStatus().getDescription()),
            response
        );
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("/{instanceId}/status/transitions")
    @SecurityRequirement(name = "api")
    @Operation(
        summary = "Get valid status transitions",
        description = """
            Get all valid status transitions for the current exam instance status.
            This endpoint helps frontend applications show only valid status change options to teachers.

            Returns a list of status values that the exam can transition to from its current status,
            along with descriptions of what each transition means.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lấy danh sách chuyển đổi trạng thái hợp lệ thành công",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Valid Transitions for DRAFT Status",
                    value = """
                        {
                            "statusCode": 200,
                            "message": "Lấy danh sách chuyển đổi trạng thái hợp lệ thành công",
                            "data": {
                                "currentStatus": "DRAFT",
                                "currentStatusDescription": "Bản nháp - chưa được kích hoạt",
                                "validTransitions": [
                                    {
                                        "status": "SCHEDULED",
                                        "description": "Đã lên lịch - chờ đến giờ bắt đầu",
                                        "action": "Schedule exam for future start"
                                    },
                                    {
                                        "status": "ACTIVE",
                                        "description": "Đang diễn ra - học sinh có thể làm bài",
                                        "action": "Start exam immediately"
                                    },
                                    {
                                        "status": "CANCELLED",
                                        "description": "Đã hủy - không thể làm bài",
                                        "action": "Cancel exam permanently"
                                    }
                                ]
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your exam instance"),
        @ApiResponse(responseCode = "404", description = "Exam instance not found")
    })
    public ResponseEntity<DataResponseDTO<Map<String, Object>>> getValidStatusTransitions(
            @Parameter(
                description = "Unique identifier of the exam instance",
                example = "550e8400-e29b-41d4-a716-446655440003",
                required = true
            )
            @PathVariable UUID instanceId,
            @Parameter(
                description = "Teacher ID from JWT token (automatically added by API Gateway)",
                hidden = true
            )
            @RequestHeader("X-User-Id") UUID teacherId) {

        Map<String, Object> response = examInstanceService.getValidStatusTransitions(instanceId, teacherId);

        DataResponseDTO<Map<String, Object>> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Lấy danh sách chuyển đổi trạng thái hợp lệ thành công",
            response
        );
        return ResponseEntity.ok(dataResponse);
    }
}

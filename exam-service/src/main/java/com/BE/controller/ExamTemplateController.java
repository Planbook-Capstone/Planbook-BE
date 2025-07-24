package com.BE.controller;

import com.BE.model.request.CreateExamTemplateRequest;
import com.BE.model.request.UpdateExamTemplateRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ExamTemplateResponse;
import com.BE.service.interfaceService.IExamTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import java.util.UUID;

@RestController
@RequestMapping("/api/exam-templates")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Slf4j

public class ExamTemplateController {
    
    private final IExamTemplateService examTemplateService;
    
    @PostMapping
    @Operation(
        summary = "Create new exam template",
        description = """
            ## Create New Exam Template

            Creates a new exam template that can be reused multiple times to generate exam instances.
            Templates contain the exam structure, questions, answers, and grading configuration.

            ### Features:
            - **Reusable Design**: One template can create multiple exam instances
            - **Flexible Content**: Supports multiple question types and formats
            - **Custom Grading**: Configure different point values for different sections
            - **School Information**: Optional school name and exam code for identification
            - **Chemistry Support**: Atomic masses information for chemistry exams
            - **Version Control**: Templates are versioned for tracking changes
            - **Validation**: Content structure is validated before saving

            ### Content Structure:
            The `contentJson` field supports two formats:

            **Format 1 - Parts-based (Recommended):**
            ```json
            {
                "parts": [
                    {
                        "part": "PHẦN I",
                        "title": "Multiple Choice Questions",
                        "questions": [
                            {
                                "id": 1,
                                "question": "What is 2 + 2?",
                                "options": {"A": "3", "B": "4", "C": "5", "D": "6"},
                                "answer": "B"
                            }
                        ]
                    }
                ]
            }
            ```

            **Format 2 - Simple questions array:**
            ```json
            {
                "questions": [
                    {
                        "id": "1",
                        "question": "What is 2 + 2?",
                        "type": "multiple_choice",
                        "options": ["3", "4", "5", "6"],
                        "correctAnswer": "4"
                    }
                ]
            }
            ```

            ### Scoring Configuration:
            - **useStandardScoring**: Boolean to use standard scoring for Part II
            - **part1Score**: Score per question for Part I (multiple choice)
            - **part2ScoringType**: "standard", "auto", or "manual" for Part II scoring
            - **part2CustomScore**: Custom total score for Part II (used with "auto" type)
            - **part2ManualScores**: Manual score mapping for Part II (used with "manual" type)
            - **part3Score**: Score per question for Part III (essay questions)
            - **Example**: `{"useStandardScoring": false, "part1Score": 0.25, "part2ScoringType": "manual", "part2CustomScore": 4, "part2ManualScores": {"1": 0.1, "2": 0.25, "3": 3, "4": 10}, "part3Score": 0.25}`

            ### Business Rules:
            - Template name must be unique per teacher
            - Content structure is validated for completeness
            - Duration must be at least 1 minute
            - Grade level must be between 1-12
            - Created templates start at version 1
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Template created successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440001",
                        "name": "Math Basic Test",
                        "subject": "Mathematics",
                        "grade": 10,
                        "durationMinutes": 60,
                        "createdBy": "550e8400-e29b-41d4-a716-446655440000",
                        "contentJson": {
                            "questions": [
                                {
                                    "id": "1",
                                    "question": "What is 2 + 2?",
                                    "type": "multiple_choice",
                                    "options": ["3", "4", "5", "6"],
                                    "correctAnswer": "4"
                                }
                            ]
                        },
                        "version": 1,
                        "createdAt": "2024-01-15T10:30:00"
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication")
    })
    public ResponseEntity<DataResponseDTO<ExamTemplateResponse>> createExamTemplate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Exam template data",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Chemistry Test with Grading Config",
                        value = """
                            {
                                "name": "Chemistry Test - Atomic Structure",
                                "subject": "Chemistry",
                                "grade": 10,
                                "durationMinutes": 90,
                                "school": "THPT Hong Thinh",
                                "examCode": "1234",
                                "atomicMasses": "H=1, C=12, O=16, N=14",
                                "totalScore": 10.5,
                                "scoringConfig": {
                                    "useStandardScoring": false,
                                    "part1Score": 0.25,
                                    "part2ScoringType": "manual",
                                    "part2CustomScore": 4,
                                    "part2ManualScores": {
                                        "1": 0.1,
                                        "2": 0.25,
                                        "3": 3,
                                        "4": 10
                                    },
                                    "part3Score": 0.25
                                },
                                "contentJson": {
                                    "parts": [
                                        {
                                            "part": "PHẦN I",
                                            "title": "Câu trắc nghiệm nhiều phương án lựa chọn",
                                            "questions": [
                                                {
                                                    "id": 1,
                                                    "question": "Đơn vị đo khối lượng nguyên tử?",
                                                    "options": {
                                                        "A": "kg", "B": "g", "C": "amu", "D": "Å"
                                                    },
                                                    "answer": "C"
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody CreateExamTemplateRequest request,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {
        
        log.info("Creating exam template for teacher: {}", teacherId);
        ExamTemplateResponse response = examTemplateService.createExamTemplate(request, teacherId);
        DataResponseDTO<ExamTemplateResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.CREATED.value(),
            "Exam template created successfully",
            response
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dataResponse);
    }
    
    @GetMapping
    @Operation(
        summary = "Get exam templates by teacher",
        description = """
            ## Get All Exam Templates

            Retrieves all exam templates created by the authenticated teacher, sorted by creation date (newest first).
            This endpoint provides a complete list of templates that can be used to create exam instances.

            ### Features:
            - **Teacher Isolation**: Only returns templates created by the authenticated teacher
            - **Sorted Results**: Templates ordered by creation date (newest first)
            - **Complete Information**: Includes all template metadata and statistics
            - **Version Information**: Shows current version of each template
            - **Usage Statistics**: May include information about how many instances were created

            ### Response Data:
            Each template includes:
            - **Basic Info**: ID, name, subject, grade level, duration
            - **Content Summary**: Question count, total score, grading config
            - **Metadata**: Creation date, version, last modified
            - **Status**: Whether template is active and can be used

            ### Use Cases:
            - **Template Management**: View and manage all created templates
            - **Instance Creation**: Select template to create new exam instance
            - **Content Review**: Review existing templates before modification
            - **Statistics**: Track template usage and performance

            ### Sorting & Filtering:
            - **Default Sort**: Creation date descending (newest first)
            - **Future Enhancement**: May support filtering by subject, grade, or status

            ### Performance Notes:
            - Results are paginated for large datasets
            - Template content is not included in list view for performance
            - Use individual template endpoint to get full content
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Templates retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<DataResponseDTO<List<ExamTemplateResponse>>> getExamTemplatesByTeacher(
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {
        
        log.info("Getting exam templates for teacher: {}", teacherId);
        List<ExamTemplateResponse> templates = examTemplateService.getExamTemplatesByTeacher(teacherId);
        DataResponseDTO<List<ExamTemplateResponse>> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Templates retrieved successfully",
            templates
        );
        return ResponseEntity.ok(dataResponse);
    }
    
    @GetMapping("/{templateId}")
    @Operation(
        summary = "Get exam template by ID",
        description = """
            ## Get Specific Exam Template

            Retrieves a specific exam template by its unique identifier. Only the teacher who created
            the template can access it, ensuring data privacy and security.

            ### Features:
            - **Complete Template Data**: Returns full template with all content and metadata
            - **Security**: Only template owner can access (ownership validation)
            - **Full Content**: Includes complete question structure and answers
            - **Metadata**: Creation info, version history, usage statistics

            ### Response Includes:
            - **Template Information**: Name, subject, grade, duration, total score
            - **Complete Content**: Full question structure with answers and options
            - **Grading Configuration**: Custom scoring rules for different sections
            - **Version Information**: Current version and change history
            - **Usage Data**: Number of instances created from this template
            - **Timestamps**: Created date and last modified date

            ### Use Cases:
            - **Template Review**: View complete template before creating instance
            - **Content Editing**: Get current content before making updates
            - **Instance Creation**: Retrieve template data for instance creation
            - **Quality Assurance**: Review template structure and content
            - **Debugging**: Troubleshoot issues with template content

            ### Security Notes:
            - **Ownership Check**: Only template creator can access
            - **Data Privacy**: Templates are isolated between teachers
            - **Access Control**: Returns 403 if user doesn't own template

            ### Performance Considerations:
            - **Full Content Load**: This endpoint loads complete template content
            - **Large Templates**: May take longer for templates with many questions
            - **Caching**: Consider caching for frequently accessed templates
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your template"),
        @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<DataResponseDTO<ExamTemplateResponse>> getExamTemplateById(
            @PathVariable UUID templateId,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {
        
        log.info("Getting exam template {} for teacher: {}", templateId, teacherId);
        ExamTemplateResponse template = examTemplateService.getExamTemplateById(templateId, teacherId);
        DataResponseDTO<ExamTemplateResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Template retrieved successfully",
            template
        );
        return ResponseEntity.ok(dataResponse);
    }
    
    @PutMapping("/{templateId}")
    @Operation(
        summary = "Update exam template",
        description = """
            ## Update Exam Template

            Updates an existing exam template with new information. This endpoint supports partial updates,
            meaning only the fields provided in the request will be modified, leaving other fields unchanged.

            ### Features:
            - **Partial Updates**: Only provided fields are updated
            - **Version Control**: Template version is incremented on each update
            - **Content Validation**: New content structure is validated before saving
            - **Ownership Security**: Only template owner can make updates
            - **Atomic Operation**: All changes are applied together or none at all

            ### Updatable Fields:
            - **name**: Template name (must remain unique per teacher)
            - **subject**: Subject area of the exam
            - **grade**: Grade level (1-12)
            - **durationMinutes**: Exam duration in minutes
            - **school**: School name (optional)
            - **examCode**: Exam identification code (optional)
            - **atomicMasses**: Atomic masses information for chemistry exams (optional)
            - **contentJson**: Complete question structure and content
            - **scoringConfig**: Custom scoring configuration
            - **totalScore**: Maximum possible score

            ### Update Behavior:
            - **Incremental Version**: Version number increases by 1
            - **Timestamp Update**: Last modified timestamp is updated
            - **Content Validation**: New content is validated for structure
            - **Existing Instances**: Updates don't affect already created instances

            ### Business Rules:
            - **Template Name**: Must be unique among teacher's templates
            - **Content Structure**: Must maintain valid question format
            - **Grading Config**: Must match content structure if provided

            ### Use Cases:
            - **Content Refinement**: Fix typos or improve question wording
            - **Difficulty Adjustment**: Modify questions or scoring
            - **Format Updates**: Change from old to new content format
            - **Metadata Updates**: Update name, subject, or duration
            - **Grading Changes**: Adjust point values or scoring rules

            ### Version Management:
            - **Auto Increment**: Version automatically increases
            - **Change Tracking**: Previous versions are preserved
            - **Instance Isolation**: Existing instances use original version
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your template"),
        @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<DataResponseDTO<ExamTemplateResponse>> updateExamTemplate(
            @PathVariable UUID templateId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated template data (only provided fields will be updated)",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Update Template Example",
                        value = """
                            {
                                "name": "Updated Chemistry Test - Advanced",
                                "durationMinutes": 120,
                                "totalScore": 15.0
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody UpdateExamTemplateRequest request,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {
        
        log.info("Updating exam template {} for teacher: {}", templateId, teacherId);
        ExamTemplateResponse response = examTemplateService.updateExamTemplate(templateId, request, teacherId);
        DataResponseDTO<ExamTemplateResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.OK.value(),
            "Template updated successfully",
            response
        );
        return ResponseEntity.ok(dataResponse);
    }
    
    @DeleteMapping("/{templateId}")
    @Operation(
        summary = "Delete exam template",
        description = """
            ## Delete Exam Template

            Permanently deletes an exam template from the system. This is a destructive operation
            that cannot be undone, so use with caution.

            ### ⚠️ Warning:
            - **Irreversible Action**: Deleted templates cannot be recovered
            - **Data Loss**: All template content and metadata will be permanently lost
            - **Instance Impact**: Existing exam instances will lose reference to template

            ### Features:
            - **Ownership Validation**: Only template owner can delete
            - **Cascade Considerations**: Handles references from existing instances
            - **Audit Logging**: Deletion is logged for audit purposes
            - **Immediate Effect**: Template is removed immediately from system

            ### Pre-deletion Checks:
            - **Ownership Verification**: Confirms user owns the template
            - **Active Instance Check**: Warns if template has active exam instances
            - **Data Integrity**: Ensures deletion won't break system integrity

            ### What Gets Deleted:
            - **Template Record**: Complete template data and metadata
            - **Content Data**: All questions, answers, and configurations
            - **Version History**: All previous versions of the template
            - **Associated Files**: Any uploaded images or attachments

            ### What Remains:
            - **Exam Instances**: Existing instances keep their content copy
            - **Submissions**: Student submissions remain intact
            - **Results**: Exam results and statistics are preserved
            - **Audit Logs**: Deletion event is logged for compliance

            ### Use Cases:
            - **Template Cleanup**: Remove outdated or unused templates
            - **Content Management**: Clean up test or duplicate templates
            - **Privacy Compliance**: Remove templates containing sensitive data
            - **Storage Management**: Free up database space

            ### Best Practices:
            - **Backup First**: Export template content before deletion
            - **Check Usage**: Verify no active instances depend on template
            - **Team Communication**: Inform team members before deletion
            - **Alternative**: Consider archiving instead of deletion

            ### Recovery Options:
            - **No Recovery**: Deleted templates cannot be restored
            - **Backup Restore**: Only possible if backup was created beforehand
            - **Instance Copy**: Can recreate template from existing instance content

            ### Security Notes:
            - **Authorization Required**: Must be authenticated template owner
            - **Audit Trail**: Deletion is logged with user and timestamp
            - **Access Control**: Returns 403 if user doesn't own template
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Template deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your template"),
        @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<DataResponseDTO<Void>> deleteExamTemplate(
            @PathVariable UUID templateId,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {

        log.info("Deleting exam template {} for teacher: {}", templateId, teacherId);
        examTemplateService.deleteExamTemplate(templateId, teacherId);
        DataResponseDTO<Void> dataResponse = new DataResponseDTO<>(
            HttpStatus.NO_CONTENT.value(),
            "Template deleted successfully",
            null
        );
        return ResponseEntity.ok(dataResponse);
    }

    @PostMapping("/{templateId}/clone")
    @Operation(
        summary = "Clone exam template",
        description = """
            ## Clone Exam Template

            Creates an exact copy of an existing exam template, allowing teachers to reuse and modify
            successful exam structures without affecting the original template.

            ### Features:
            - **Complete Copy**: Duplicates all template content and configuration
            - **New Identity**: Clone gets new UUID and modified name
            - **Version Reset**: Clone starts at version 1
            - **Independent**: Changes to clone don't affect original template
            - **Ownership Transfer**: Clone belongs to the requesting teacher

            ### Clone Process:
            1. **Validation**: Verifies original template exists and user has access
            2. **Content Copy**: Duplicates all questions, answers, and structure
            3. **Metadata Reset**: Assigns new ID, resets version, updates timestamps
            4. **Name Modification**: Adds "(Copy)" suffix to distinguish from original
            5. **Ownership Assignment**: Sets requesting teacher as owner

            ### What Gets Cloned:
            - **Template Structure**: Name, subject, grade, duration
            - **Complete Content**: All questions, answers, options
            - **Grading Configuration**: Custom scoring rules and weights
            - **Total Score**: Maximum possible score setting
            - **Content Format**: Preserves original content structure

            ### What Changes:
            - **Template ID**: New unique identifier generated
            - **Template Name**: "(Copy)" suffix added to original name
            - **Version**: Reset to 1 (fresh start)
            - **Created Date**: Set to current timestamp
            - **Owner**: Set to requesting teacher
            - **Usage Stats**: Reset (no instances created yet)

            ### Use Cases:
            - **Template Variation**: Create similar exams with modifications
            - **Semester Reuse**: Reuse successful exam structure for new term
            - **Difficulty Levels**: Create easier/harder versions of same exam
            - **Subject Adaptation**: Adapt exam structure for different subjects
            - **Backup Creation**: Create backup before major modifications
            - **Team Sharing**: Share template structure between teachers

            ### Naming Convention:
            - **Original**: "Math Midterm Exam"
            - **Clone**: "Math Midterm Exam (Copy)"
            - **Multiple Clones**: System handles duplicate names automatically

            ### Business Rules:
            - **Access Control**: Only template owner can clone their templates
            - **Name Uniqueness**: Clone name must be unique (system handles conflicts)
            - **Content Validation**: Cloned content is validated for integrity
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Template cloned successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440002",
                        "name": "Math Basic Test (Copy)",
                        "subject": "Mathematics",
                        "grade": 10,
                        "durationMinutes": 60,
                        "createdBy": "550e8400-e29b-41d4-a716-446655440000",
                        "contentJson": {
                            "questions": [
                                {
                                    "id": "1",
                                    "question": "What is 2 + 2?",
                                    "type": "multiple_choice",
                                    "options": ["3", "4", "5", "6"],
                                    "correctAnswer": "4"
                                }
                            ]
                        },
                        "version": 1,
                        "createdAt": "2024-01-15T11:30:00"
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied - not your template"),
        @ApiResponse(responseCode = "404", description = "Template not found"),
        @ApiResponse(responseCode = "400", description = "Error cloning template")
    })
    public ResponseEntity<DataResponseDTO<ExamTemplateResponse>> cloneExamTemplate(
            @Parameter(description = "ID of the template to clone", example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID templateId,
            @Parameter(description = "Teacher ID from JWT token (automatically added by API Gateway)", hidden = true)
            @RequestHeader("X-User-Id") UUID teacherId) {

        log.info("Cloning exam template {} for teacher: {}", templateId, teacherId);
        ExamTemplateResponse response = examTemplateService.cloneExamTemplate(templateId, teacherId);
        DataResponseDTO<ExamTemplateResponse> dataResponse = new DataResponseDTO<>(
            HttpStatus.CREATED.value(),
            "Template cloned successfully",
            response
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dataResponse);
    }
}

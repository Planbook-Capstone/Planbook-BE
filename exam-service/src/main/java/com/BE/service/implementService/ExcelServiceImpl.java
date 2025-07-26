package com.BE.service.implementService;

import com.BE.model.entity.ExamInstance;
import com.BE.model.entity.ExamSubmission;
import com.BE.exception.ResourceNotFoundException;
import com.BE.repository.ExamInstanceRepository;
import com.BE.repository.ExamSubmissionRepository;
import com.BE.service.interfaceService.IExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelServiceImpl implements IExcelService {
    
    private final ExamInstanceRepository examInstanceRepository;
    private final ExamSubmissionRepository examSubmissionRepository;
    
    @Value("${app.excel.storage-path:./excel-reports/}")
    private String excelStoragePath;
    
    @Override
    @Transactional
    public Resource generateExcelReport(UUID examInstanceId) {
        ExamInstance instance = examInstanceRepository.findById(examInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên thi"));
        
        List<ExamSubmission> submissions = examSubmissionRepository
                .findByExamInstanceIdOrderBySubmittedAtDesc(examInstanceId);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Exam Results");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            
            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            
            // Create exam info section
            createExamInfoSection(sheet, instance, headerStyle, dataStyle);
            
            // Create results table
            createResultsTable(sheet, submissions, headerStyle, dataStyle, 6);
            
            // Auto-size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            // Update excel URL in database
            String fileName = String.format("exam_%s_%s.xlsx", 
                instance.getCode(), 
                System.currentTimeMillis());
            String excelUrl = "/api/exams/instances/" + examInstanceId + "/excel";
            instance.setExcelUrl(excelUrl);
            examInstanceRepository.save(instance);
            
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (IOException e) {
            log.error("Lỗi khi tạo báo cáo Excel: {}", e.getMessage());
            throw new RuntimeException("Không thể tạo báo cáo Excel", e);
        }
    }
    
    @Override
    @Transactional
    public void updateExcelUrl(UUID examInstanceId, String excelUrl) {
        ExamInstance instance = examInstanceRepository.findById(examInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên thi"));
        
        instance.setExcelUrl(excelUrl);
        examInstanceRepository.save(instance);
    }
    
    private void createExamInfoSection(Sheet sheet, ExamInstance instance, CellStyle headerStyle, CellStyle dataStyle) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // Row 0: Exam Name
        Row row0 = sheet.createRow(0);
        Cell cell0_0 = row0.createCell(0);
        cell0_0.setCellValue("Exam Name:");
        cell0_0.setCellStyle(headerStyle);
        Cell cell0_1 = row0.createCell(1);
        cell0_1.setCellValue(instance.getTemplate().getName());
        cell0_1.setCellStyle(dataStyle);
        
        // Row 1: Subject & Grade
        Row row1 = sheet.createRow(1);
        Cell cell1_0 = row1.createCell(0);
        cell1_0.setCellValue("Subject:");
        cell1_0.setCellStyle(headerStyle);
        Cell cell1_1 = row1.createCell(1);
        cell1_1.setCellValue(instance.getTemplate().getSubject());
        cell1_1.setCellStyle(dataStyle);
        Cell cell1_2 = row1.createCell(2);
        cell1_2.setCellValue("Grade:");
        cell1_2.setCellStyle(headerStyle);
        Cell cell1_3 = row1.createCell(3);
        cell1_3.setCellValue(instance.getTemplate().getGrade());
        cell1_3.setCellStyle(dataStyle);
        
        // Row 2: Code & Duration
        Row row2 = sheet.createRow(2);
        Cell cell2_0 = row2.createCell(0);
        cell2_0.setCellValue("Code:");
        cell2_0.setCellStyle(headerStyle);
        Cell cell2_1 = row2.createCell(1);
        cell2_1.setCellValue(instance.getCode());
        cell2_1.setCellStyle(dataStyle);
        Cell cell2_2 = row2.createCell(2);
        cell2_2.setCellValue("Duration:");
        cell2_2.setCellStyle(headerStyle);
        Cell cell2_3 = row2.createCell(3);
        cell2_3.setCellValue(instance.getTemplate().getDurationMinutes() + " minutes");
        cell2_3.setCellStyle(dataStyle);
        
        // Row 3: Time Range
        Row row3 = sheet.createRow(3);
        Cell cell3_0 = row3.createCell(0);
        cell3_0.setCellValue("Start Time:");
        cell3_0.setCellStyle(headerStyle);
        Cell cell3_1 = row3.createCell(1);
        cell3_1.setCellValue(instance.getStartAt().format(formatter));
        cell3_1.setCellStyle(dataStyle);
        Cell cell3_2 = row3.createCell(2);
        cell3_2.setCellValue("End Time:");
        cell3_2.setCellStyle(headerStyle);
        Cell cell3_3 = row3.createCell(3);
        cell3_3.setCellValue(instance.getEndAt().format(formatter));
        cell3_3.setCellStyle(dataStyle);
        
        // Row 4: Empty row
        sheet.createRow(4);
    }

    private void createResultsTable(Sheet sheet, List<ExamSubmission> submissions,
                                  CellStyle headerStyle, CellStyle dataStyle, int startRow) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Create header row
        Row headerRow = sheet.createRow(startRow);
        String[] headers = {"No.", "Student Name", "Score (%)", "Correct Answers", "Total Questions", "Submitted At", "Status"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = startRow + 1;
        for (int i = 0; i < submissions.size(); i++) {
            ExamSubmission submission = submissions.get(i);
            Row row = sheet.createRow(rowNum++);

            // No.
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(i + 1);
            cell0.setCellStyle(dataStyle);

            // Student Name
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(submission.getStudentName());
            cell1.setCellStyle(dataStyle);

            // Score
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(String.format("%.2f", submission.getScore()));
            cell2.setCellStyle(dataStyle);

            // Correct Answers
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(submission.getCorrectCount());
            cell3.setCellStyle(dataStyle);

            // Total Questions
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(submission.getTotalQuestions());
            cell4.setCellStyle(dataStyle);

            // Submitted At
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(submission.getSubmittedAt().format(formatter));
            cell5.setCellStyle(dataStyle);

            // Status
            Cell cell6 = row.createCell(6);
            String status = submission.getScore() >= 50 ? "PASS" : "FAIL";
            cell6.setCellValue(status);
            cell6.setCellStyle(dataStyle);
        }

        // Add summary row
        if (!submissions.isEmpty()) {
            Row summaryRow = sheet.createRow(rowNum + 1);
            Cell summaryCell = summaryRow.createCell(0);
            summaryCell.setCellValue("SUMMARY");
            summaryCell.setCellStyle(headerStyle);

            Cell totalCell = summaryRow.createCell(1);
            totalCell.setCellValue("Total Submissions: " + submissions.size());
            totalCell.setCellStyle(dataStyle);

            double avgScore = submissions.stream()
                    .mapToDouble(ExamSubmission::getScore)
                    .average()
                    .orElse(0.0);

            Cell avgCell = summaryRow.createCell(2);
            avgCell.setCellValue(String.format("Average Score: %.2f%%", avgScore));
            avgCell.setCellStyle(dataStyle);

            long passCount = submissions.stream()
                    .mapToLong(s -> s.getScore() >= 50 ? 1 : 0)
                    .sum();

            Cell passCell = summaryRow.createCell(3);
            passCell.setCellValue(String.format("Pass Rate: %d/%d (%.1f%%)",
                passCount, submissions.size(),
                submissions.size() > 0 ? (double) passCount / submissions.size() * 100 : 0));
            passCell.setCellStyle(dataStyle);
        }
    }
}

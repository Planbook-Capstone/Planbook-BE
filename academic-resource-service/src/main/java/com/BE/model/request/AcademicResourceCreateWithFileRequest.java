package com.BE.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcademicResourceCreateWithFileRequest {


    @Schema(description = "File to upload")
    @NotNull(message = "File is required")
    MultipartFile file;

    @Schema(description = "Metadata in JSON format", example = "{\"type\": \"image\", \"name\": \"Test\", \"description\": \"Test\",\"url\": \"null\", \"tags\": [\"1\", \"2\"]}")
    String metadataJson;



}

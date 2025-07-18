package com.BE.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AggregatedToolResponse {
     Page<ExternalToolConfigPublicResponse> externalTools;
     Page<BookTypeResponse> internalTools;
}


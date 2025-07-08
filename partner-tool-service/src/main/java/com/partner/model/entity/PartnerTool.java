package com.partner.model.entity;

import com.partner.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("partner_tools")  // <- Dùng annotation của R2DBC, không phải JPA
public class PartnerTool {

    @Id  // <- Của Spring Data, không phải jakarta.persistence
    Long id;

    String name;
    String description;

    @Column("token_url")
    String tokenUrl;

    @Column("api_url")
    String apiUrl;

    @Column("client_id")
    String clientId;

    @Column("client_secret")
    String clientSecret;

    StatusEnum status;

    @Column("input_payload")
    Map<String, Object> inputPayload; // Không cần converter, R2DBC lưu JSON bằng `CustomConverter`

    @Column("output")
    Map<String, Object> output;

    @Column("created_at")
    LocalDateTime createdAt;

    @Column("updated_at")
    LocalDateTime updatedAt;
}

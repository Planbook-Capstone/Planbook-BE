package com.partner;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
//@OpenAPIDefinition(info = @Info(title = "Planbook API", version = "2.0", description = "Information"))
//@OpenAPIDefinition(
//       servers = @Server(url = "https://planbook.vn"), // Đặt URL domain cuối cùng ở đây
//       info = @Info(
//               title = "Planbook API (Gateway)", // Đổi tên cho rõ ràng
//               version = "2.0",
//               description = "API Gateway for Planbook services" // Đổi mô tả
//       )
//)

@OpenAPIDefinition(
		info = @Info(title = "Planbook API", version = "2.0")
		,servers = {
				@Server(url = "https://api.planbook.vn/external-caller-service", description = "Production Server")
		}
)
@SecurityScheme(name = "api", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class PartnerToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerToolApplication.class, args);
    }

}

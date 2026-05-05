package com.library.library_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI libraryManagerOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Library Manager API")
						.description("API para gerenciamento de biblioteca")
						.version("v1")
						.contact(new Contact().name("Library Manager Team"))
						.license(new License().name("Apache 2.0")));
	}
}

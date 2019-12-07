package com.pkg.mgmt.Utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ServiceUtil {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	// TODO ADD SWAGGER DOCUMENTATION FOR API's
	/*
	 * @Bean public Docket productApi() { Docket docket = new
	 * Docket(DocumentationType.SWAGGER_2).select()
	 * .apis(RequestHandlerSelectors.basePackage("com.pkg.mgmt.Controller")).build()
	 * .apiInfo(metaData()); return docket; }
	 */

	private ApiInfo metaData() {
		ApiInfo apiInfo = new ApiInfoBuilder().contact(new Contact("Saurabh Godha", "", "godha89@gmail.com"))
				.description("This Service is used to Manage Pakages").title("Package Management").build();

		return apiInfo;
	}
}

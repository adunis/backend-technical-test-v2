package com.tui.proof;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.tui.proof.repository")
@EntityScan("com.tui.proof.entity")
@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {

		try {
			new SpringApplicationBuilder(MainApplication.class)
					.bannerMode(Banner.Mode.OFF)
					.web(WebApplicationType.REACTIVE)
					.build()
					.run(args);
		} catch (Throwable e){
			e.printStackTrace();
		}
	}
}

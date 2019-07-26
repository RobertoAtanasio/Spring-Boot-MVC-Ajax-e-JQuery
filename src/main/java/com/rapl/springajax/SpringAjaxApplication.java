package com.rapl.springajax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.rapl.springajax.service.SocialMetaTagService;

@SpringBootApplication
public class SpringAjaxApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringAjaxApplication.class, args);
	}

	@Autowired
	SocialMetaTagService service;
	
	@Override
	public void run(String... args) throws Exception {

//		SocialMetaTag tag = service.getSocialMetaTagByUrl("https://www.pichau.com.br/placa-mae-msi-meg-x570-ace-ddr4-socket-am4-chipset-amd-x570");
//		System.out.println(tag.toString());
		
		// os testes abaixo foram feitos quando seus métodos ainda eram public!
		
//		SocialMetaTag og = service.getOpenGraphByUrl("https://www.udemy.com/spring-boot-mvc-com-thymeleaf/");
//		System.out.println(og.toString());
//
//		SocialMetaTag twitter = service.getTwitterCardByUrl("https://www.udemy.com/spring-boot-mvc-com-thymeleaf/");
//		System.out.println(twitter.toString());
//		
//		og = service.getOpenGraphByUrl("https://www.pichau.com.br/placa-mae-msi-meg-x570-ace-ddr4-socket-am4-chipset-amd-x570");
//		System.out.println(og.toString());
//		
//		twitter = service.getTwitterCardByUrl("https://www.pichau.com.br/placa-mae-msi-meg-x570-ace-ddr4-socket-am4-chipset-amd-x570");
//		System.out.println(twitter.toString());
	}

}

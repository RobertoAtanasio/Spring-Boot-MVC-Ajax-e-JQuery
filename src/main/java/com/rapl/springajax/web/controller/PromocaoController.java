package com.rapl.springajax.web.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rapl.springajax.domain.Categoria;
import com.rapl.springajax.domain.Promocao;
import com.rapl.springajax.repository.CategoriaRepository;
import com.rapl.springajax.repository.PromocaoRepository;

@Controller
@RequestMapping("/promocao")
public class PromocaoController {

	private static Logger log = LoggerFactory.getLogger(PromocaoController.class);
	
	@Autowired
	private PromocaoRepository promocaoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@PostMapping("/save")
	public ResponseEntity<?> salvarPromocao (
			@Valid Promocao promocao, BindingResult result) {
		if (result.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			for (FieldError error : result.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			// 422 Unprocessable 
			return ResponseEntity.unprocessableEntity().body(errors);
		}
		promocao.setDtCadastro(LocalDateTime.now());
		log.info("> Promocao {}", promocao.toString());
		System.out.println("> Promocao {}" + promocao.toString());
		promocaoRepository.save(promocao);
		return ResponseEntity.ok().build();
	}
	
	@ModelAttribute("categorias")
	public List<Categoria> getCategorias() {
		return categoriaRepository.findAll(); 
	}
	
	@GetMapping("/add")
	public String abrirCadastro() {
		return "promo-add";
	}

	//	@GetMapping("/add")
//	public String abrirCadastro(ModelMap model) {
//		model.addAttribute("categorias", categoriaRepository.findAll());
//		return "promo-add";
//	}
}

package com.rapl.springajax.web.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rapl.springajax.domain.Categoria;
import com.rapl.springajax.domain.Promocao;
import com.rapl.springajax.repository.CategoriaRepository;
import com.rapl.springajax.repository.PromocaoRepository;
import com.rapl.springajax.service.PromocaoDataTableService;

@Controller
@RequestMapping("/promocao")
public class PromocaoController {

	private static Logger log = LoggerFactory.getLogger(PromocaoController.class);
	
	@Autowired
	private PromocaoRepository promocaoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	// ======================================DATATABLES==============================================
	
	@GetMapping("/tabela")
	public String showTabela( ) {
		return "promo-datatables";
	}
	
	@GetMapping("/datatables/server")
	public ResponseEntity<?> datatables(HttpServletRequest request) {
		Map<String, Object> data = new PromocaoDataTableService().execute(promocaoRepository, request);
		return ResponseEntity.ok(data);
	}
	
	@GetMapping("/delete/{id}")
	public ResponseEntity<?> excluirPromocao(@PathVariable("id") Long id) {
		promocaoRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/edit/{id}")
	public ResponseEntity<?> preEditarPromocao(@PathVariable("id") Long id) {
		Optional<Promocao> promo = promocaoRepository.findById(id);
		return promo.isPresent() ? ResponseEntity.ok(promo) : ResponseEntity.noContent().build();
//		return ResponseEntity.ok(promo);
//		return null;
	}
	
	// ======================================LISTAR OFERTAS==========================================
	
	@GetMapping("/list")
	public String listarOfertas(ModelMap model) {
		Sort sort = new Sort(Sort.Direction.DESC, "dtCadastro");
		PageRequest pageRequest = PageRequest.of(0, 8, sort);
		model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
		return "promo-list";
	}
	
	@GetMapping("/list/ajax")
	public String listarCard(@RequestParam(name = "page", defaultValue = "1") int page, ModelMap model) {
		Sort sort = new Sort(Sort.Direction.DESC, "dtCadastro");
		PageRequest pageRequest = PageRequest.of(page, 8, sort);
		model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
		// Retornará ao JS a página dos Cards rederizada com as promoções. Entretando, devemos enviar via JS
		// a página Card para o ser renderizado pelo comando th:insert="promo-card" que será feita via a 
		// função $(this).append(response); do JQuery
		return "promo-card";
	} 
	
	// ======================================ADD OFERTAS=============================================
	
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

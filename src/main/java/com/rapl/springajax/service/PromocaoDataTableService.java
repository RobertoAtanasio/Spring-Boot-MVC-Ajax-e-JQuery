package com.rapl.springajax.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.rapl.springajax.domain.Promocao;
import com.rapl.springajax.repository.PromocaoRepository;

public class PromocaoDataTableService {

	// As colunas abaixo devem estar na mesma ordem das definidas em promo-datatables.html e em 
	// promo-datatables.js.
	// Serve para relacionar as colunas d atabela com os atributos da classe Promocao que representam
	// as colunas que têm no banco de dados.
	private String[] cols = {
			"id", "titulo", "site", "linkPromocao", "descricao", "linkImagem",
			"preco", "likes", "dtCadastro", "categoria"
		};
	
	public Map<String, Object> execute(PromocaoRepository repository, HttpServletRequest request) {
		
		// ver em https://datatables.net/manual/server-side a área "Sent parameters"
		
		int start = Integer.parseInt(request.getParameter("start"));
		int lenght = Integer.parseInt(request.getParameter("length"));
		int draw = Integer.parseInt(request.getParameter("draw"));
		
		int current = currentPage(start, lenght);
		String column = columnName(request);
		Sort.Direction direction = orderBy(request);
		String search = searchBy(request);
		
		Pageable pageable = PageRequest.of(current, lenght, direction, column);
		
		Page<Promocao> page = queryBy(search, repository, pageable);
		
		// ver item 'Example data' no site de referência citado acima. Abaixo exemplo de modelo do site:
		/*
		 {
		    "draw": 1,
		    "recordsTotal": 57,
		    "recordsFiltered": 57,
		    "data": [
		        [
		            "Angelica",
		            "Ramos",
		            "System Architect",
		            "London",
		            "9th Oct 09",
		            "$2,875"
		        ],
		        [
		            "Ashton",
		            "Cox",
		            "Technical Author",
		            "San Francisco",
		            "12th Jan 09",
		            "$4,800"
		        ],
		        ...
		    ]
		 }
		 */
		
		Map<String, Object> json = new LinkedHashMap<>();
		json.put("draw", draw);
		json.put("recordsTotal", page.getTotalElements());
		json.put("recordsFiltered", page.getTotalElements());
		json.put("data", page.getContent());
		
		return json;
	}

	private Page<Promocao> queryBy(String search, PromocaoRepository repository, Pageable pageable) {
		if (search.isEmpty()) {
			return repository.findAll(pageable);
		}

		// Pesquisa apenas pelo preço
		if (search.matches("^[0-9]+([.,][0-9]{2})?$")) {
			search = search.replace(",", ".");
			return repository.findByPreco(new BigDecimal(search), pageable);
		}
		
		return repository.findByTituloOrSiteOrCategoria(search, pageable);
	}

	private String searchBy(HttpServletRequest request) {
		return request.getParameter("search[value]").isEmpty()
				? ""
				: request.getParameter("search[value]");
	}

	private Direction orderBy(HttpServletRequest request) {
		String order = request.getParameter("order[0][dir]");
		Sort.Direction sort = Sort.Direction.ASC;
		if (order.equalsIgnoreCase("desc")) {
			sort = Sort.Direction.DESC;
		}
		return sort;
	}

	private String columnName(HttpServletRequest request) {
		// Por padrão o primeiro array é o array da ordenação, daí a seleção do 0.
		// Este método servirá para a ordenação da página.
		int iCol = Integer.parseInt(request.getParameter("order[0][column]"));
		return cols[iCol];
	}

	private int currentPage(int start, int lenght) {
		// 0		1		  2			representam as páginas
		// 0-9 |	10-19 	| 20-29		linhas de cada página
		// O start equivale à linha início de cada página. Ex: 20 / 10 = 2 que é a página 2 (terceira página)
		// Nesse exemplo, temos 10 como o tamanho da página = lenght
		return start / lenght;
	}
}

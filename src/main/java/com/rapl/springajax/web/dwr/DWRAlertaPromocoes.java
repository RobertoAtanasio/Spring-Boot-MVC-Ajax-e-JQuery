package com.rapl.springajax.web.dwr;

import java.time.LocalDateTime;
import java.util.Timer;

import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.rapl.springajax.repository.PromocaoRepository;

@Component
@RemoteProxy
public class DWRAlertaPromocoes {

	@Autowired
	private PromocaoRepository repositorio;
	
	private Timer timer;
	
	private LocalDateTime getDtCadastroByUltimaPromocao() {
		PageRequest pageRequest = PageRequest.of(0, 1, Direction.DESC, "dtCadastro");
		return repositorio.findUltimaDataDePromocao(pageRequest)
				.getContent()
				.get(0);
	}
	
}

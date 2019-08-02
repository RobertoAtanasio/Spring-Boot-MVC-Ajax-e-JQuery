package com.rapl.springajax.web.dwr;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptSessions;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.rapl.springajax.repository.PromocaoRepository;

@Component
@RemoteProxy 	// configuração do proxy remoto que servirá de comunicação entre o servidor e o
				// cliente via DWR
public class DWRAlertaPromocoes {

	@Autowired
	private PromocaoRepository repositorio;

	private Timer timer;

	private LocalDateTime getDtCadastroByUltimaPromocao() {
		// 0 - primeira página selecionada
		// 1 - indica que terá apenas um elemento por página.
		// Sderá então apresentada a data mais recente da pesquisa.
		// O método 'getContent' retorna um objeto List<LocalDateTime>
		// Depois retornar o primeiro elemento
		PageRequest pageRequest = PageRequest.of(0, 1, Direction.DESC, "dtCadastro");
		return repositorio.findUltimaDataDePromocao(pageRequest).getContent().get(0);
	}

	@RemoteMethod	// faz a configuração de relação entre o este método init(), servidor, com o
					// método init que foi declarado no cliente (DWRAlertaPromocoes.init();) em promo-list.js
	public synchronized void init() {
		System.out.println(">> DWR está ativado!");

		LocalDateTime lastDate = getDtCadastroByUltimaPromocao();

		// variável da DWR
		WebContext contextDWR = WebContextFactory.get();

		timer = new Timer();
		// criar um agendamento de tarefa
		// 10000 em milisegundos, tempo de delay, 10s
		// 60000 em milisegundos, tempo de espera para a execução da tarefa, a cada 60 segundos
		timer.schedule(new AlertTask(contextDWR, lastDate), 10000, 60000);

	}

	class AlertTask extends TimerTask {

		private LocalDateTime lastDate;
		private WebContext context;
		private long count;
		
		public AlertTask(WebContext context, LocalDateTime lastDate) {
			super();
			this.lastDate = lastDate;
			this.context = context;
		}

		@Override
		public void run() {
			
			// Identifica a sessão que está sendo executada
			String session = context.getScriptSession().getId();
			
			Browser.withSession(context, session, new Runnable() {
				
				@Override
				public void run() {

					Map<String, Object> map = 
							repositorio.totalAndUltimaPromocaoByDataCadastro(lastDate);
					
					count = (Long) map.get("count");
					lastDate = map.get("lastDate") == null 
							? lastDate 
							: (LocalDateTime) map.get("lastDate");
					
					//--- Log para verificação apenas
					Calendar time = Calendar.getInstance();
					// mostrar o horário da última tentativa de acesso da DWR ao lado do cliente
					time.setTimeInMillis(context.getScriptSession().getLastAccessedTime());	
					System.out.println("count: " + count 
							+ ", lastDate: " + lastDate
							+ "<" + session + "> " + " <" + time.getTime() + ">");
					
					if (count > 0) {
						// Esta string "showButton" representa o nome da função que está definido no javascript
						// O segundo parâmetro equivale ao valor passado para a função
						ScriptSessions.addFunctionCall("showButton", count);
					}					
				}
			});			
		}	
	}
}

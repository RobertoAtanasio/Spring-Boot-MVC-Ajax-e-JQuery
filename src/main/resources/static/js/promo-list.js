let pageNumber = 0;

$(document).ready(function(){
	// Assim que a página for carregada, executará as instruções abaixo.
	$("#loader-img").hide();
	$("#fim-btn").hide();
});

// efeito infinite scroll
$(window).scroll(function() {
	
	var scrollTop = $(this).scrollTop();	// retorna a altura em que está a barra de rolagem no momento
	
	// calcular a altura da página de dados, excluindo o header e rodapé.
	var conteudo = $(document).height() - $(window).height();
	
//	console.log('scrollTop: ', scrollTop, ' | ', 'conteudo', conteudo, $(document).height(), $(window).height());
	
	if (scrollTop >= conteudo) {
		console.log('*** chegou ***');
		pageNumber++;
		setTimeout(function(){
			loadByScrollBar(pageNumber);
		}, 200);
	}
	
});

function loadByScrollBar(pageNumber) {
	
	// o parâmetro pageNumber é passado via o 'data:' ou poderi ser incluído junto ao string da URL 
	// via concatenação junto a ?
	$.ajax({
		method: "GET",
		url: "/promocao/list/ajax",
		data: {
			page: pageNumber
		},
		beforeSend: function() {
			// Apresentar a imagem do Loading. Após a remoção da classe do id = loader-img, o show abaixo
			// não será mais apresentado, pois todas as promoções já foram selecionadas.
			$("#loader-img").show();
		},
		success: function( response ) {
//			console.log("resposta > ", response);
			console.log("lista > ", response.length);
			
			if (response.length > 150) {
				// Esta classe .row é a classe da página promo-card.html
				$(".row").fadeIn(250, function() {
					// Adiciona à página o novo trecho de promoções selecionadas.
					$(this).append(response);
				});
			
			} else {
				$("#fim-btn").show();
				$("#loader-img").removeClass("loader");
			}
		},
		error: function(xhr) {
			alert("Ops, ocorreu um erro: " + xhr.status + " - " + xhr.statusText);
		},
		complete: function() {
			// Retirar a imagem do Loading ao final
			$("#loader-img").hide();
		}
	})  
}

//--- adicionar likes
//    o * em id* significa que o JQuery deve tratar o click em qualquer botão que tenha o id com likes-btn
$(document).on("click", "button[id*='likes-btn-']", function() {
	let id = $(this).attr("id").split("-")[2];	// o split("-") gera um array com 3 ocorrências
	
//	console.log("id: ", id);
	
	$.ajax({
		method: "POST",
		url: "/promocao/like/" + id,
		success: function(response) {
			$("#likes-count-" + id).text(response);
		},
		error: function(xhr) {
			alert("Ops, ocorreu um erro: " + xhr.status + ", " + xhr.statusText);
		}
	});
});


//--- AJAX REVERSE

let totalOfertas = 0;

$(document).ready(function() {
	init();
});

// Função responsável por iniciar o canal de comunicação entre o cliente e oservidor

function init() {
	console.log("dwr init...");
	
	dwr.engine.setActiveReverseAjax(true);	// habilitando o ajax reverse no lado cliente
	dwr.engine.setErrorHandler(error);		// para capturar mensagens de erro
	
	DWRAlertaPromocoes.init();				// declaração da classe do lado servidor com seu método de acesso
											// que será responsável por criar o canal de comunuicação entre o 
											// cliente e o servidor.
}

function error(excpetion) {
	console.log("dwr error: ", excpetion);
}

function showButton(count) {
	// a alteração do style faz com que o botão apareça na página.
	totalOfertas = totalOfertas + count;
	$("#btn-alert").show(function() {
		$(this)
			.attr("style", "display: block;")
			.text("Veja " + totalOfertas + " nova(s) oferta(s)!");
	});
}

$("#btn-alert").on("click", function() {
	
	$.ajax({
		method: "GET",
		url: "/promocao/list/ajax",
		data: {
			page : 0
		},
		beforeSend: function() {
			pageNumber = 0;
			totalOfertas = 0;
			$("#fim-btn").hide();
			$("#loader-img").addClass("loader");				// adicionar o loader
			$("#btn-alert").attr("style", "display: none;");	// retirar o botão clicado
			$(".row").fadeOut(400, function(){
				$(this).empty();
			});
		},
		success: function(response) {
			$("#loader-img").removeClass("loader");				// remover o loader da página
			$(".row").fadeIn(250, function(){
				$(this).append(response);
			});
		},
		error: function(xhr) {
			alert("Ops, algo deu errado: " + xhr.status + ", " + xhr.statusText);
		}
	});
});

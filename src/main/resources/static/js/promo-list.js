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
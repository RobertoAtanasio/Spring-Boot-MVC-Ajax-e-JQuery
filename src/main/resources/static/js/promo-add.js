// submit do formulario para o controller
$("#form-add-promo").submit(function(evt) {
	// bloquear o comportamento padrão do submit
	evt.preventDefault();
	
	// carregar o objeto com os valores infomados na tela.
	// Deve ter os mesmos nomes da classe Promocao.java 
	var promo = {};
	promo.linkPromocao = $("#linkPromocao").val();
	promo.descricao = $("#descricao").val();
	promo.preco = $("#preco").val();
	promo.titulo = $("#titulo").val();
	promo.categoria = $("#categoria").val();
	promo.linkImagem = $("#linkImagem").attr("src");
	promo.site = $("#site").text();
	
	console.log('> promo:', promo);
	
	$.ajax({
		method: "POST",
		url: "/promocao/save",
		data: promo,
		beforeSend: function() {
			// removendo todos os spans que contenham a classe .error-span retirando as mensagens
			$("span").closest('.error-span').remove();
			
			//remover as bordas vermelhas caso tenham sido lançadas
//			$("#categoria").removeClass("is-invalid");
//			$("#preco").removeClass("is-invalid");
//			$("#linkPromocao").removeClass("is-invalid");
//			$("#titulo").removeClass("is-invalid");
			$(".is-invalid").removeClass("is-invalid");
			
			//habilita o loading
			$("#form-add-promo").hide();
			$("#loader-form").addClass("loader").show();
		},
		success: function() {
			// Limpar cada uma das tag de entrada de dados
			$("#form-add-promo").each(function() {
				this.reset();
			});
			// Estes campos não são de entrada de dados
			$("#linkImagem").attr("src", "/images/promo-dark.png");
			$("#site").text("");
			// Mudar a cor do alerta de vermelho para verde, caso exista erro já lançado
			$("#alert")
				.removeClass("alert alert-danger")
				.addClass("alert alert-success")
				.text("OK! Promoção cadastrada com sucesso.");
		},
		statusCode: {
			422: function(xhr) {
				console.log('status error:', xhr.status);
				let errors = $.parseJSON(xhr.responseText)
				console.log('Error retorno:', errors);
				$.each(errors, function(key, val) {
					$("#" + key).addClass("is-invalid");
					$('#error-' + key)
						.addClass("invalid-feedback")
						.append("<span class='error-span'>" + val + "</span>")
				});
			}
		},
		error: function(xhr) {
			console.log("> error: ", xhr.responseText);
			$("#alert").addClass("alert alert-danger").text("Não foi possível salvar esta promoção.");
		},
		complete: function() {
			$("#loader-form").fadeOut(800, function() {
				$("#form-add-promo").fadeIn(250);
				$("#loader-form").removeClass("loader");
			});
		}
	});
});




// funcao para capturar as meta tags
$("#linkPromocao").on('change', function() {

	var url = $(this).val();

	if (url.length > 7) {			// http://... já se inicia pelo menos com 7 bytes
		
		$.ajax({
			method:"POST",
			url: "/meta/info?url=" + url,
			cache: false,
			beforeSend: function() {
				$("#alert").removeClass("alert alert-danger alert-success").text('');
				$("#titulo").val("");
				$("#site").text("");
				$("#linkImagem").attr("src", "");
				$("#loader-img").addClass("loader");
			},
			success: function( data ) {
				console.log(data);
				$("#titulo").val(data.title);
				$("#site").text(data.site.replace("@", ""));
				$("#linkImagem").attr("src", data.image);
			},
			statusCode: {
				404: function() {
					// via JQuery, adicionar a classe de mensagem de alerta do bootstrap 4
					$("#alert").addClass("alert alert-danger").text("Nenhuma informação pode ser recuperada dessa url.");
					$("#linkImagem").attr("src", "/images/promo-dark.png");
				}
			},
			error: function() {
				$("#alert").addClass("alert alert-danger").text("Ops... algo deu errado, tente mais tarde.");
				$("#linkImagem").attr("src", "/images/promo-dark.png");
			},
			complete: function() {
				$("#loader-img").removeClass("loader");
			}
		});
	}
});
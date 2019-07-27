$(document).ready(function(){
	
	// Comandos executados após a abertura da página html]
	// As colunas abaixo devem estar na mesma ordem das definidas em promo-datatables.html e em 
	// PromocaoDataTableService.java
	// Ver os vários tipos de formats para a data em https://momentjs.com/
	// Acessado o link de extensões na página da DataTables https://datatables.net/extensions/buttons/
	// referente aos botões.
	// Na página da datatables, https://datatables.net/extensions/buttons/, acessar o link de download no
	// canto superior direito, https://datatables.net/extensions/buttons/#Download e acessar o link
	// "individual files" e copiar as importações referentes aos botões do CSS e JS :
	// CSS: <link rel="stylesheet" href="https://cdn.datatables.net/buttons/1.5.6/css/buttons.dataTables.min.css"/>
	// JS: <script src="https://cdn.datatables.net/buttons/1.5.6/js/dataTables.buttons.min.js"></script>
	// e importá-los em layout.html
	
	moment.locale('pt-br');		// Faz com que Locate do Brasil fique ativo
	
	let table = $('#table-server').DataTable({
		processing: true,		// equivale a um loading
		serverSide: true,
		responsive: true,
		lengthMenu: [ 10, 15, 20, 25 ],
		ajax: {
			url: '/promocao/datatables/server',
			data: 'data'
		},
		columns: [
			{data: 'id'},
			{data: 'titulo'},
			{data: 'site'},
			{data: 'linkPromocao'},
			{data: 'descricao'},
			{data: 'linkImagem'},
			{data: 'preco', render: $.fn.dataTable.render.number('.', ',', 2, 'R$')},
			{data: 'likes'},
			{data: 'dtCadastro', render: 
				function(dtCadastro) {
					return moment( dtCadastro ).format('LLL'); 
				}
			},
			{data: 'categoria.titulo'}
		],
		dom: 'Bfrtip',
		buttons: [
			{
				text: 'Editar',
				attr: {
					id: 'btn-editar',
					type: 'button'
				},
				enabled: false
			},
			{
				text: 'Excluir',
				attr: {
					id: 'btn-excluir',
					type: 'button'
				},
				enabled: false
			}
		]
	});
	
	// Ação para marcar/desmarcar botões ao clicar na ordenação
	
	$("#table-server thead").on('click', 'tr', function() {
		table.buttons().disable();
	});
	
	// Ação para marcar/desmarcar linhas clicadas. Indicamos que o click será em uma tag 'tr'
	
	$("#table-server tbody").on('click', 'tr', function() {
		// Verifica se a linha já está selecionada, logo, vamos desmarcar.
		// 'selected' é umam classe JQuery que marcará a linha como selecionada.
		if ($(this).hasClass('selected')) {
			$(this).removeClass('selected');
			table.buttons().disable();
		} else {
			$('tr.selected').removeClass('selected');
			$(this).addClass('selected');
			table.buttons().enable();
		}
	});
	
	// Ação do botão de editar (abrir modal)
	
	$("#btn-editar").on('click', function() {
		
//		alert("Click no botão Editar de Id: " + id);
		
		if (isLinhaSelecionada()) {
			
			let id = getIdLinha();
			// Obs.: a variável 'data' em function abaixo, tem os dados retornados 
			$.ajax({
				method: "GET",
				url: "/promocao/edit/" + id,
				beforeSend: function() {
					// instrução 'modal' do bootstrap para abrir o mesmo
					$('#modal-form').modal('show');
				},
				success: function( data ) {
					console.log(">> ", data);
					// Preeche os campos do modal de edição. O valor já deve ser enviado com a formatação.
					$("#edt_id").val(data.id);
					$("#edt_site").text(data.site);
					$("#edt_titulo").val(data.titulo);
					$("#edt_descricao").val(data.descricao);
					$("#edt_preco").val(data.preco.toLocaleString('pt-BR', {
						minimumFractionDigits: 2,
						maximumFractionDigits: 2
					}));
					$("#edt_categoria").val(data.categoria.id);
					$("#edt_linkImagem").val(data.linkImagem);
					$("#edt_imagem").attr('src', data.linkImagem);
				},
				error: function() {
					alert("Ops... algum erro ocorreu, tente novamente.");
				}
			});
			
		} 
	});
	
	// Ação do botão de excluir (abrir modal)
	$("#btn-excluir").on('click', function() {
		if (isLinhaSelecionada()) {
			$('#modal-delete').modal('show');
//			alert("Click no botão Excluir.");
		}
	});
	
	// exclusão de uma promoção via click no botão do modal de excluir
	$("#btn-del-modal").on('click', function() {
		let id = getIdLinha();
		$.ajax({
			method: "GET",
			url: "/promocao/delete/" + id,
			success: function() {
				$('#modal-delete').modal('hide');	// fechar o modal
				table.buttons().disable();			// desabilitar os botões
				table.ajax.reload();				// atualiza a tabela
			},
			error: function() {
				alert("Ops... Ocorreu um erro, tente mais tarde.");
			}
		});
	});
	
	function getIdLinha() {
		// Obter o 'id' da linha selecionada. Obs: 'id', 'titulo',etc é o nome da coluna desejada
		// Obs: se a linha não estiver selecionada, a instrução abaixo dará erro.
		return table.row(table.$('tr.selected')).data().id;
	}
	
	function isLinhaSelecionada() {
		let trow = table.row(table.$('tr.selected'));
		return trow.data() !== undefined;
	}
	
});



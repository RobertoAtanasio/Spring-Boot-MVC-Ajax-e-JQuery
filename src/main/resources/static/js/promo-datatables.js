$(document).ready(function(){
	
	// Comandos executados após a abertura da página html]
	// As colunas abaixo devem estar na mesma ordem das definidas em promo-datatables.html e em 
	// PromocaoDataTableService.java
	
	$("#table-server").DataTable({
		processing: true,		// equivale a um loading
		serverSide: true,
		responsive: true,
		lengthMenu: [ 10, 15, 20, 25 ],
		ajax: {
			url: "/promocao/datatables/server",
			data: "data"
		},
		columns: [
			{data: "id"},
			{data: "titulo"},
			{data: "site"},
			{data: "linkPromocao"},
			{data: "descricao"},
			{data: "linkImagem"},
			{data: "preco", render: $.fn.dataTable.render.number('.', ',', 2, 'R$')},
			{data: 'likes'},
			{data: "dtCadastro"},
			{data: "categoria.titulo"}
		]
	});
});
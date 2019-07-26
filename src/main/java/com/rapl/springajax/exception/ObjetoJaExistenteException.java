package com.rapl.springajax.exception;

import java.io.Serializable;

public class ObjetoJaExistenteException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public ObjetoJaExistenteException (String mensagem) {
		super(mensagem);
	}

}

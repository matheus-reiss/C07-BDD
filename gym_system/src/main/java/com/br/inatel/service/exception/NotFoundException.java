package com.br.inatel.service.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String mensagem){
        super(mensagem);
    }
}

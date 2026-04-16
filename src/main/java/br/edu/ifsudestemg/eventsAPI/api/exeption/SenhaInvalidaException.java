package br.edu.ifsudestemg.eventsAPI.api.exeption;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException() {
        super("Senha inválida");
    }
}
package br.com.tiago.spring_security.dtos;

public record TokenResposeDTO(
        String token,
        Long expirationTime
){}

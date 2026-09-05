package br.com.tiago.spring_security.dtos;

import jakarta.persistence.Column;

public record UsuarioDTO(
        Integer id,
        String nome,
        String email
) {}

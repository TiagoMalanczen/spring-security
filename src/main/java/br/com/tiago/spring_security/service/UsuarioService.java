package br.com.tiago.spring_security.service;

import br.com.tiago.spring_security.config.TokenProvider;
import br.com.tiago.spring_security.database.model.RolesEntity;
import br.com.tiago.spring_security.database.model.UsuarioEntity;
import br.com.tiago.spring_security.database.repository.RolesRepository;
import br.com.tiago.spring_security.database.repository.UsuarioRepository;
import br.com.tiago.spring_security.dtos.LoginDTO;
import br.com.tiago.spring_security.dtos.RegisterDTO;
import br.com.tiago.spring_security.dtos.TokenResposeDTO;
import br.com.tiago.spring_security.dtos.UsuarioDTO;
import br.com.tiago.spring_security.enums.RolesTypeEnum;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public void register(RegisterDTO registerDTO){
        UsuarioEntity user = usuarioRepository.findByEmail(registerDTO.email())
                .orElse(null);

        if(user != null){
            throw new RuntimeException("Usuario com email ja cadastrado");
        }

        RolesEntity roles = rolesRepository.findByNome(RolesTypeEnum.ROLE_USUARIO.name())
                .orElseGet(() -> rolesRepository.save(RolesEntity.builder()
                        .nome(RolesTypeEnum.ROLE_USUARIO.name())
                        .build()));


        usuarioRepository.save(UsuarioEntity.builder()
                        .senha(passwordEncoder.encode(registerDTO.senha()))
                        .nome(registerDTO.nome())
                        .email(registerDTO.email())
                        .roles(Set.of(roles))
                .build());
    }

    public TokenResposeDTO login(LoginDTO loginDTO) throws BadRequestException {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.senha()));
            String token = tokenProvider.gerarToken(authentication);

            return new TokenResposeDTO(token, expirationTime);

        } catch (BadCredentialsException e) {
            throw new BadRequestException("Credencias invalidas");
        }
    }

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> new UsuarioDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail()
                ))
                .toList();
    }
}

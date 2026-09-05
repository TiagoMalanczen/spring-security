package br.com.tiago.spring_security.controller;

import br.com.tiago.spring_security.database.model.UsuarioEntity;
import br.com.tiago.spring_security.dtos.LoginDTO;
import br.com.tiago.spring_security.dtos.RegisterDTO;
import br.com.tiago.spring_security.dtos.TokenResposeDTO;
import br.com.tiago.spring_security.dtos.UsuarioDTO;
import br.com.tiago.spring_security.service.UserDetailsImp;
import br.com.tiago.spring_security.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value ="/v1/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping(value = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarNovoUsuario(@RequestBody RegisterDTO registerDTO){
        usuarioService.register(registerDTO);
    }

    @GetMapping(value = "/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResposeDTO login(@RequestBody LoginDTO loginDTO) throws BadRequestException {
        return usuarioService.login(loginDTO);
    }

    @GetMapping(value = "/perfil")
    public ResponseEntity<String> perfil(){
        return ResponseEntity.ok("Acesso autorizado");
    }

    @GetMapping(value = "/consultar")
    public List<UsuarioDTO> listarUsuario(){
        return usuarioService.listarUsuarios();
    }
}

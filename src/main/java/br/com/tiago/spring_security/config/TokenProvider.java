package br.com.tiago.spring_security.config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenProvider {
    @Value("${jwt.expiration}")

    private long expirationTime;
    @Value("$jwt.key")
    private String key;

    public String gerarToken(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return buildToken(user.getUsername());
    }
    private String buildToken(String username){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);
        return Jwts.builder()
                .subject(username) //pega o dado unico do usuario
                .issuedAt(now) //defina a data que o token foi gerado
                .expiration(expiration) //define a data que o token expira
                .signWith(getSingnedKey())//chave com a qual o token sera criado
                .compact(); //compacta tudo e retorna
    }

    private SecretKey getSingnedKey(){

        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

    }

    //Validar token
    public boolean isTokenValid(String token){
        try {
            getClaims(token);
            return true;
        }
        catch (Exception e){
            return  false;
        }
    }
    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(getSingnedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //extrair informacoes do token
    public String getUserName(String tokem){
        return getClaims(tokem).getSubject();
    }
}
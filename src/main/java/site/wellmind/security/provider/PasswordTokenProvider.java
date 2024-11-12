package site.wellmind.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

/**
 * PasswordTokenProvider
 * <p>비밀번호 설정을 위한 토큰 발급 로직을 처리하는 클래스</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.passwordSetup.expiration}")
    private long expirationTimeInSeconds;  //24시간

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(Base64.getUrlEncoder().encode(secretKey.getBytes()));
    }

    public String generatePasswordSetupToken(String employeeId){
        return Jwts.builder()
                .subject(employeeId)
                .claim("type","password-setup")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(expirationTimeInSeconds)))
                .signWith(getSecretKey(),Jwts.SIG.HS256)
                .compact();
    }

    public boolean isPasswordSetupTokenValid(String token){
        try{
            Claims claims=Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return "password-setup".equals(claims.get("type")) && !claims.getExpiration().before(new Date());
        }catch(SecurityException | MalformedJwtException e){
            log.info("Invalid Password Setup Token",e);
        }catch (ExpiredJwtException e){
            log.info("Expired Password Setup Token",e);
        }catch (UnsupportedJwtException e){
            log.info("Unsupported Password Setup Token",e);
        }catch (IllegalArgumentException e){
            log.info("Password Setup claims string is empty",e);
        }
        return false;
    }

    public String extractEmployeeId(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }

}

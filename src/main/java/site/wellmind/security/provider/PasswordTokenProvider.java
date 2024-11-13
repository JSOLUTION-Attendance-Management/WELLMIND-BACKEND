package site.wellmind.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.wellmind.security.domain.model.AccountTokenModel;
import site.wellmind.security.domain.model.PasswordSetupTokenModel;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;
import site.wellmind.security.domain.vo.TokenStatus;
import site.wellmind.security.repository.PasswordSetupTokenRepository;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

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

    private final PasswordSetupTokenRepository passwordSetupTokenRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.passwordSetup.expiration}")
    private long expirationTimeInSeconds;  //24시간

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(Base64.getUrlEncoder().encode(secretKey.getBytes()));
    }

    public String generatePasswordSetupToken(String employeeId){
        if (employeeId == null || employeeId.isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        String token=Jwts.builder()
                .subject(employeeId)
                .claim("type","password-setup")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(expirationTimeInSeconds)))
                .signWith(getSecretKey(),Jwts.SIG.HS256)
                .compact();

        passwordSetupTokenRepository.save(PasswordSetupTokenModel.builder()
                .employeeId(employeeId)
                .token(token)
                .tokenStatus(TokenStatus.VALID)
                .expirationTime(LocalDateTime.now().plusSeconds(expirationTimeInSeconds))
                .build());
        return token;
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

    public void invalidateToken(String token) {
        PasswordSetupTokenModel passwordToken = (PasswordSetupTokenModel) passwordSetupTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));

        passwordToken.setTokenStatus(TokenStatus.INVALID);
        passwordSetupTokenRepository.save(passwordToken);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void removeExpiredAndInvalidTokens(){
        List<PasswordSetupTokenModel> tokensToDelete=passwordSetupTokenRepository.findByTokenStatusIn(
                Arrays.asList(TokenStatus.EXPIRED,TokenStatus.INVALID)
        );

        if(!tokensToDelete.isEmpty()){
            passwordSetupTokenRepository.deleteAll(tokensToDelete);
            passwordSetupTokenRepository.flush();
            log.info("Removed {} expired or invalid tokens.", tokensToDelete.size());
        }else {
            log.info("No expired or invalid tokens found for cleanup.");
        }
    }

    @Scheduled(cron = "0 */15 * * * *") // 15분마다 실행
    public void removeExpiredTokens() {
        LocalDateTime now=LocalDateTime.now();
        List<PasswordSetupTokenModel> expiredTokens=passwordSetupTokenRepository.findByExpirationTimeBeforeAndTokenStatus(
                now,TokenStatus.VALID
        );

        for(PasswordSetupTokenModel token:expiredTokens){
            token.setTokenStatus(TokenStatus.EXPIRED);
        }

        passwordSetupTokenRepository.saveAll(expiredTokens);
        passwordSetupTokenRepository.flush();
    }
}

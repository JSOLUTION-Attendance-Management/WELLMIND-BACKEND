package site.wellmind.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.vo.AdminRole;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.Role;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.security.domain.model.AccountTokenModel;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;
import site.wellmind.security.domain.vo.TokenStatus;
import site.wellmind.security.handler.CustomAuthenticationFailureHandler;
import site.wellmind.security.handler.CustomAuthenticationSuccessHandler;
import site.wellmind.security.repository.AccountTokenRepository;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
/**
 * JwtTokenProvider
 * <p>Handles JWT token generation, validation, and management.</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see PrincipalUserDetails
 * @see PrincipalAdminDetails
 * @since 2024-11-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Getter
    @Value("${jwt.expired.access}")
    private Long accessTokenExpired;

    @Getter
    @Value("${jwt.expired.refresh}")
    private Long refreshTokenExpired;

    private final AccountTokenRepository accountTokenRepository;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(Base64.getUrlEncoder().encode(secretKey.getBytes()));
    }

    public String extractEmail(String jwt){
        return extractClaim(jwt, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String jwt){
        return extractClaim(jwt,i->i.get("roles",List.class));
    }

    Long extractId(String jwt){
        return extractClaim(jwt,i->i.get("id",Long.class));
    }
    private <T> T extractClaim(String jwt, Function<Claims,T> claimsResolver){
        return claimsResolver.apply(extractAllClaims(jwt));
    }

    private Claims extractAllClaims(String jwt){
        try{
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        }catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("extractAllClaims Error - Expired Token: {}", e.getMessage());
            throw new GlobalException(ExceptionStatus.EXPIRED_TOKEN, "Token has expired");
        } catch (io.jsonwebtoken.SignatureException e) {
            log.error("extractAllClaims Error - Invalid Signature: {}", e.getMessage());
            throw new GlobalException(ExceptionStatus.INVALID_SIGNATURE, "Invalid token signature");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("extractAllClaims Error - Malformed Token: {}", e.getMessage());
            throw new GlobalException(ExceptionStatus.MALFORMED_TOKEN, "Malformed token structure");
        }catch (Exception e){
            log.error("extractAllClaims Error : {} ",e.getMessage());
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED,"Invalid Token");
        }
    }

    public String generateToken(Object principal, boolean isRefreshToken) {
        return generateToken(Map.of(), principal, isRefreshToken);
    }
    private String generateToken(Map<String, Object> extractClaims, Object principal, boolean isRefreshToken){
        String roles;
        Long id;
        String username;

        if(principal instanceof PrincipalUserDetails principalUserDetails){
            roles="ROLE_USER";
            id=principalUserDetails.getUser().getId();
            username=principalUserDetails.getUsername();
        }else if (principal instanceof PrincipalAdminDetails principalAdminDetails){
            roles=principalAdminDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList().toString();
            id=principalAdminDetails.getAdmin().getId();
            username=principalAdminDetails.getUsername();
        }else{
            throw new GlobalException(ExceptionStatus.BAD_REQUEST, "Invalid principal type: unable to generate token");
        }

        String token=Jwts.builder()
                .claims(extractClaims)    // JWT 에 포함된 다양한 사용자 정보와 메타데이터를 담음
                .subject(username)  //사용자를 식별하는 주요 정보
                .issuer(issuer)
                .claim("roles",roles)
                .claim("id",id)
                .claim("type",isRefreshToken? "refresh":"access")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(isRefreshToken?refreshTokenExpired:accessTokenExpired)))
                .signWith(getSecretKey(),Jwts.SIG.HS256)
                .compact();

        if (isRefreshToken){
            accountTokenRepository.save(AccountTokenModel.builder()
                            .employeeId(username)
                            .token(token)
                            .tokenStatus(TokenStatus.VALID)
                            .roles("ROLE_USER".equals(roles) ? Role.ROLE_USER :
                                    "ROLE_ADMIN_UBL_55".equals(roles) ? Role.ROLE_ADMIN_UBL_55 :
                                    "ROLE_ADMIN_UBL_66".equals(roles) ? Role.ROLE_ADMIN_UBL_66 :
                                    Role.ROLE_ADMIN_UML_77)
                            .expirationTime(LocalDateTime.now().plusSeconds(refreshTokenExpired))
                    .build());
        }
        return token;
    }

    public Boolean isTokenValid(String token,Boolean isRefreshToken){
        try {
            if(isTokenExpired(token)){
                log.info("Token has expired.");
                return false;
            }
            return isTokenTypeEqual(token,isRefreshToken);
        }catch(SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token",e);
        }catch (ExpiredJwtException e){
            log.info("Expired JWT Token",e);
        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token",e);
        }catch (IllegalArgumentException e){
            log.info("JWT claims string is empty",e);
        }
        return false;
    }

    private Boolean isTokenExpired(String token){
        try{
            Date expiration=extractClaim(token,Claims::getExpiration);
            boolean isExpired=expiration.before(Date.from(Instant.now()));
            if(isExpired){
                expireToken(token);
            }
            return isExpired;
        }catch (ExpiredJwtException e){
            log.info("Token is already expired",e);
            return true;
        }

    }

    // token 타입이 refresh 면 true, access 면 false 반환
    private Boolean isTokenTypeEqual(String token,Boolean isRefreshToken){
        try {
            String tokenType=extractClaim(token,claims -> claims.get("type",String.class));
            return tokenType.equals(isRefreshToken ? "refresh" : "access");
        }catch (Exception e){
            log.info("Failed to determine token type",e);
            return false;
        }
    }

    public String removeBearer(String bearerToken){
        return bearerToken.startsWith("Bearer ") ? bearerToken.substring(7):"";
    }

    public Object extractPrincipalDetails(String jwt){
        String roles=extractRoles(jwt).get(0);
        Long id=extractId(jwt);
        String email = extractEmail(jwt);

        if("ROLE_USER".equals(roles)){
            return new PrincipalUserDetails(UserTopModel.builder()
                    .email(email)
                    .id(id)
                    .authType("N")
                    .build());
        }else{
            return new PrincipalAdminDetails(AdminTopModel.builder()
                    .email(email)
                    .id(id)
                    .authType("M")
                    .authAdminLevelCodeId( "ROLE_ADMIN_UBL_55".equals(roles) ? AdminRole.UBL_55 :
                            "ROLE_ADMIN_UBL_66".equals(roles) ? AdminRole.UBL_66 :
                                    AdminRole.UML_77)
                    .build());
        }
    }

    public Boolean isTokenExists(String employeeId,String token){
        Optional<AccountTokenModel> savedToken=accountTokenRepository.findByEmployeeIdAndTokenStatus(employeeId,TokenStatus.VALID);
        return savedToken.map(accountTokenModel -> token.equals(accountTokenModel.getToken())).orElse(false);
    }

    @Transactional
    public void expireToken(String token){
        Optional<AccountTokenModel> savedToken = accountTokenRepository.findByEmployeeIdAndTokenStatus(token, TokenStatus.VALID);
        if (savedToken.isPresent()) {
            AccountTokenModel accountToken = savedToken.get();
            accountToken.setTokenStatus(TokenStatus.EXPIRED);
            accountTokenRepository.save(accountToken); // 상태 업데이트
        }
    }
    @Transactional
    public Boolean invalidateToken(String employeeId){
        Optional<AccountTokenModel> savedToken=accountTokenRepository.findByEmployeeIdAndTokenStatus(employeeId,TokenStatus.VALID);

        if(savedToken.isPresent()){
            AccountTokenModel token=savedToken.get();
            token.setTokenStatus(TokenStatus.INVALID);
            accountTokenRepository.save(token);
            return true;
        }else{
            throw new GlobalException(ExceptionStatus.NO_VALID_TOKEN,ExceptionStatus.NO_VALID_TOKEN.getMessage());
        }
    }

    // 매일 새벽 3시에 실행 (cron 표현식: "초 분 시 일 월 요일")
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void removeExpiredAndInvalidTokens(){
        List<AccountTokenModel> tokensToDelete=accountTokenRepository.findByTokenStatusIn(
                Arrays.asList(TokenStatus.EXPIRED,TokenStatus.INVALID)
        );

        if(!tokensToDelete.isEmpty()){
            accountTokenRepository.deleteAll(tokensToDelete);
            accountTokenRepository.flush();
            log.info("Removed {} expired or invalid tokens.", tokensToDelete.size());
        }else {
            log.info("No expired or invalid tokens found for cleanup.");
        }
    }

    @Transactional
    public Boolean removeToken(String employeeId){
        Optional<AccountTokenModel> savedToken=accountTokenRepository.findByEmployeeIdAndTokenStatus(employeeId,TokenStatus.EXPIRED);
        if(savedToken.isPresent()){
            accountTokenRepository.delete(savedToken.get());
            accountTokenRepository.flush();
            return true;
        }else{
            throw new GlobalException(ExceptionStatus.NO_VALID_TOKEN,ExceptionStatus.NO_VALID_TOKEN.getMessage());
        }
    }
    public String getCookieValue(HttpServletRequest request, String cookieName){
        if(request.getCookies()!=null){
            return Arrays.stream(request.getCookies())
                    .filter(cookie->cookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }


}

package site.wellmind.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.PushBuilder;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import site.wellmind.security.repository.AccountTokenRepository;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import javax.crypto.SecretKey;
import javax.swing.text.html.Option;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

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
        return !isTokenExpired(token) && isTokenTypeEqual(token,isRefreshToken);
    }

    private Boolean isTokenExpired(String token){
        boolean isExpired = extractClaim(token, Claims::getExpiration).before(Date.from(Instant.now()));
        if (isExpired) {
            expireToken(token); // 만료 시 상태 업데이트
        }
        return isExpired;
    }

    // token 타입이 refresh 면 true, access 면 false 반환
    private Boolean isTokenTypeEqual(String token,Boolean isRefreshToken){
        return !isTokenExpired(token) &&
                extractClaim(token,i->i.get("type",String.class)).equals(isRefreshToken? "refresh":"access");
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
    public Boolean expireToken(String token){
        Optional<AccountTokenModel> savedToken = accountTokenRepository.findByEmployeeIdAndTokenStatus(token, TokenStatus.VALID);
        if (savedToken.isPresent()) {
            AccountTokenModel accountToken = savedToken.get();
            accountToken.setTokenStatus(TokenStatus.EXPIRED);
            accountTokenRepository.save(accountToken); // 상태 업데이트
            return true;
        }
        return false;
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
            throw new GlobalException(ExceptionStatus.NOT_FOUND,"No valid token found for the provided employee ID");
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
            throw new GlobalException(ExceptionStatus.NOT_FOUND,"No valid token found for the provided employee ID");
        }
    }

}

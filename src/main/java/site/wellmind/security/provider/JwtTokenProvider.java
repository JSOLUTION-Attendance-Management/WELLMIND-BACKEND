package site.wellmind.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.PushBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import javax.crypto.SecretKey;
import java.time.Instant;
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
        }catch (Exception e){
            log.error("extractAllClaims Error : {} ",e.getMessage());
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED,"Invalid Token");
        }
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

        return Jwts.builder()
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
    }

    public Boolean isTokenValid(String token,Boolean isRefreshToken){
        return !isTokenExpired(token) && isTokenTypeEqual(token,isRefreshToken);
    }

    private Boolean isTokenExpired(String token){
        return extractClaim(token,Claims::getExpiration).before(Date.from(Instant.now()));
    }

    private Boolean isTokenTypeEqual(String token,Boolean isRefreshToken){
        return extractClaim(token,i->i.get("type",String.class)).equals(isRefreshToken? "refresh":"access");
    }

    public String removeBearer(String bearerToken){
        return bearerToken.startsWith("Bearer ") ? bearerToken.substring(7):"";
    }

    public Object extractPrincipalUserDetails(String jwt){
        String roles=extractRoles(jwt).get(0);
        Long id=extractId(jwt);
        String email = extractEmail(jwt);

        if("ROLE_USER".equals(roles)){
            return new PrincipalUserDetails(UserTopModel.builder()
                    .email(email)
                    .id(id)
                    .build());
        }else{
            return new PrincipalAdminDetails(AdminTopModel.builder()
                    .email(email)
                    .id(id)
                    .build());
        }

    }
}

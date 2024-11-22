package site.wellmind.security.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.wellmind.security.handler.CustomAuthenticationFailureHandler;
import site.wellmind.security.handler.CustomAuthenticationSuccessHandler;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * PrincipalAdminDetails
 * <p>Represents an administrator’s authentication details, implementing Spring Security's UserDetails.</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@ToString
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j(topic = "PrincipalAdminDetails")
public class PrincipalAdminDetails implements UserDetails {
    private AdminTopModel admin;
    private Map<String,Object> attributes;

    public PrincipalAdminDetails(AdminTopModel admin){
        this.admin=admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String authLevel = admin.getAuthAdminLevelCodeId();
        authLevel = authLevel.replaceAll("[\\[\\]]", "");
        log.info("AuthAdminLevelCodeId without brackets: {}", authLevel);

        // 관리자 권한 추가
        switch (authLevel) {
            case "UML_77" -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_UML_77"));
            case "UBL_66" -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_UBL_66"));
            case "UBL_55" -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_UBL_55"));
            default -> log.warn("Unknown AuthAdminLevelCodeId: {}", authLevel);
        }

        log.info("getAuthAdminLevelCodeId authorities : {}",authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return admin.getEmployeeId();
    }
    public String getName(){
        return attributes.get(admin.getName()).toString();
    }
}

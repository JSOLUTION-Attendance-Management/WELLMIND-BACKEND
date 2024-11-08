package site.wellmind.security.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class PrincipalAdminDetails implements UserDetails {
    private AdminTopModel admin;
    private Map<String,Object> attributes;

    public PrincipalAdminDetails(AdminTopModel admin){
        this.admin=admin;
    }
    public PrincipalAdminDetails(AdminTopModel admin,Map<String,Object> attributes){
        this.admin=admin;
        this.attributes=attributes;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // 관리자 권한 추가
        switch (admin.getAuthAdminLevelCodeId()) {
            case UML_77 -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_UML_77"));
            case UBL_66 -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_UBL_66"));
            case UBL_55 -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_UBL_55"));
        }

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

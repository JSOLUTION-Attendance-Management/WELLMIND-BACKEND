package site.wellmind.security.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.wellmind.user.domain.model.AdminTopModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * CustomAdminDetails
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
@Slf4j(topic = "CustomAdminDetails")
public class CustomAdminDetails implements UserDetails {
    private AdminTopModel admin;
    private Map<String,Object> attributes;

    public CustomAdminDetails(AdminTopModel admin){
        this.admin=admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(admin.getAuthAdminLevelCodeId()));
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

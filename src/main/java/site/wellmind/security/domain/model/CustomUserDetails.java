package site.wellmind.security.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.wellmind.user.domain.model.UserTopModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * CustomUserDetails
 * <p>Represents a user's authentication details, implementing Spring Security's UserDetails interface.</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private UserTopModel user;
    private Map<String,Object> attributes;

    public CustomUserDetails(UserTopModel user){
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER_UGL_11"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmployeeId();
    }

    public String getName(){
        return attributes.get(user.getName()).toString();
    }
}

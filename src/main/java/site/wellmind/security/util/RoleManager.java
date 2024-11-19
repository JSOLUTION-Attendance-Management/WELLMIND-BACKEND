package site.wellmind.security.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;
import site.wellmind.user.repository.AccountRoleRepository;

import java.util.List;
/**
 * RoleManager
 * <p>AccountRole 테이블에서 role 추출을 위한 util component</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see AccountRoleRepository
 * @since 2024-11-18
 */
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class RoleManager {
    private final AccountRoleRepository accountRoleRepository;

    private List<String> roleIds;
    private List<String> roles;
    @PostConstruct
    private void initRoles(){
        this.roleIds=accountRoleRepository.findAllRoleIds();
        this.roles=accountRoleRepository.findAllRoles();
    }


}

package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.user.domain.model.AccountRoleModel;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRoleModel,Long> {

    AccountRoleModel findByRoleId(String s);
}

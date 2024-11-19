package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.wellmind.user.domain.model.AccountRoleModel;

import java.util.List;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRoleModel,Long> {

    AccountRoleModel findByRoleId(String s);

    @Query("SELECT ar.roleId FROM AccountRoleModel ar")
    List<String> findAllRoleIds();

    @Query("SELECT CONCAT(ar.roleGroupLevel,'_',ar.roleId) FROM AccountRoleModel ar")
    List<String> findAllRoles();
}

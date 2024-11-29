package site.wellmind.user.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;

import java.util.Optional;

/**
 * AccountService
 * <p>User Service Interface</p>
 *
 * @version 1.0
 * @see QueryService
 * @see CommandService
 * @see UserAllDto
 * @since 2024-10-08
 */
public interface AccountService extends CommandService<UserAllDto>, QueryService<UserAllDto> {

    Boolean existByEmail(String email);

    Boolean existByEmployeeId(AccountDto accountDto);

    Optional<UserTopModel> findUserByEmployeeId(String employeeId);

    Optional<AdminTopModel> findAdminByEmployeeId(String employeeId);

    Page<ProfileDto> findBy(String departName, String positionName, String name, Pageable pageable);

    ProfileDto findProfileById(Long currentAccountId, boolean isAdmin);

    UserDetailDto findDetailById(Long currentAccountId, boolean isAdmin);

    void registerProfile(RegProfileDto dto,AccountDto accountDto);

    void registerDetail(UserDetailDto dto, AccountDto accountDto);

    RegNumberDto findRegNumberById(String employeeId);
}


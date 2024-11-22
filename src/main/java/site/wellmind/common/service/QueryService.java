package site.wellmind.common.service;

import site.wellmind.user.domain.dto.AccountDto;

import java.util.List;
/**
 * Query Service
 * <p>Query Service Interface</p>
 * @since 2024-07-22
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
public interface QueryService<T> {
    Object findById(String employeeId, AccountDto dto);
    List<T> findAll();
    boolean existById(Long id);
    Long count();
}

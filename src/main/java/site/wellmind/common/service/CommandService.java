package site.wellmind.common.service;

import site.wellmind.user.domain.dto.AccountDto;

import java.util.List;

/**
 * Command Service
 * <p>Command Service Interface</p>
 * @since 2024-10-08
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
public interface CommandService<T> {
    Object save(T t);
    List<T> saveAll(List<T> entities);

    void deleteById(Object ob,AccountDto dto);

    T modify(T t, AccountDto dto);
}

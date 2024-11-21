package site.wellmind.common.service;

import java.util.List;
/**
 * Query Service
 * <p>Query Service Interface</p>
 * @since 2024-07-22
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
public interface QueryService<T> {
    T findById(Long id,Long currentAccountId, boolean isAdmin);
    List<T> findAll();
    boolean existById(Long id);
    Long count();
}

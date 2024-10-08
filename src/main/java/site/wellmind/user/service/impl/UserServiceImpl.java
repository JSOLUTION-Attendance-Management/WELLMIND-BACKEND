package site.wellmind.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.wellmind.user.domain.dto.UserDto;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.UserService;

import java.util.List;

/**
 * User Service Implementation
 * <p>User Service Implementation</p>
 * @since 2024-10-08
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 * @see UserService
 * @see UserTopRepository

 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    @Override
    public UserDto save(UserDto userDto) {
        return null;
    }

    @Override
    public List<UserDto> saveAll(List<UserDto> entities) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public UserDto update(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto findById(Long id) {
        return null;
    }

    @Override
    public List<UserDto> findAll() {
        return null;
    }

    @Override
    public boolean existById(Long id) {
        return false;
    }

    @Override
    public Long count() {
        return null;
    }
}

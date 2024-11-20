package site.wellmind.log.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.wellmind.log.domain.dto.LogViewDto;
import site.wellmind.log.service.LogViewService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogViewServiceImpl implements LogViewService {
    @Override
    public LogViewDto save(LogViewDto logViewDto) {
        return null;
    }

    @Override
    public List<LogViewDto> saveAll(List<LogViewDto> entities) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public LogViewDto update(LogViewDto logViewDto) {
        return null;
    }

    @Override
    public LogViewDto findById(Long id, Long currentAccountId, boolean isAdmin) {
        return null;
    }

    @Override
    public List<LogViewDto> findAll() {
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

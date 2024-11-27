package site.wellmind.log.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import site.wellmind.log.domain.dto.LogSaveDto;
import site.wellmind.log.domain.model.LogArchiveSaveModel;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.log.event.UserDeletedEvent;
import site.wellmind.log.event.UserSavedEvent;
import site.wellmind.log.repository.LogArchiveSaveRepository;
import site.wellmind.log.service.LogSaveService;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.model.AdminTopModel;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "LogDeleteServiceImpl")
public class LogSaveServiceImpl implements LogSaveService {
    private final LogArchiveSaveRepository logArchiveSaveRepository;
    @Override
    public Object save(LogSaveDto logSaveDto) {
        return null;
    }

    @Override
    public List<LogSaveDto> saveAll(List<LogSaveDto> entities) {
        return null;
    }

    @Override
    public void deleteById(Object ob, AccountDto dto) {

    }

    @Override
    public LogSaveDto modify(LogSaveDto logSaveDto, AccountDto dto) {
        return null;
    }

    @Override
    public Object findById(String employeeId, AccountDto dto) {
        return null;
    }

    @Override
    public List<LogSaveDto> findAll() {
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

    @Async
    @EventListener
    @Transactional
    public void handleUserSavedEvent(UserSavedEvent event) {
        String employeeId = event.getSavedEmployeeId();
        AdminTopModel admin = event.getAdmin();
        logArchiveSaveRepository.save(LogArchiveSaveModel.builder()
                .savedEmployeeId(employeeId)
                .saverId(admin).build());
    }
}
package site.wellmind.log.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.log.domain.dto.LogViewDto;
import site.wellmind.log.domain.model.LogArchiveViewModel;
import site.wellmind.log.repository.LogArchiveViewRepository;
import site.wellmind.log.service.LogViewService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogViewServiceImpl implements LogViewService {

    private final LogArchiveViewRepository viewRepository;

    @Override
    public LogViewDto save(LogViewDto logViewDto) {
        try{
            viewRepository.save(LogArchiveViewModel.builder()
                    .viewReason(logViewDto.getViewReason())
                    .userId(logViewDto.getUserId())
                    .adminId(logViewDto.getAdminId()).build());

            return LogViewDto.builder().build();
        }catch (Exception e){
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR,ExceptionStatus.INTERNAL_SERVER_ERROR.getMessage());
        }

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
    public LogViewDto findById(String employeeId, Long currentAccountId, boolean isAdmin,String role) {
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

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
import site.wellmind.user.domain.dto.AccountDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogViewServiceImpl implements LogViewService{

    private final LogArchiveViewRepository viewRepository;
     @Override
    public LogViewDto save(LogViewDto logViewDto) {
        try{
            viewRepository.save(LogArchiveViewModel.builder()
                    .viewReason(logViewDto.getViewReason())
                    .viewedEmployeeId(logViewDto.getViewedEmployeeId())
                    .viewerId(logViewDto.getViewerId()).build());

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
    public boolean deleteById(Object ob, AccountDto dto) {

        return false;
    }

    @Override
    public LogViewDto modify(LogViewDto logViewDto,AccountDto accountDto) {
        return null;
    }

    @Override
    public LogViewDto findById(String employeeId, AccountDto dto) {
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

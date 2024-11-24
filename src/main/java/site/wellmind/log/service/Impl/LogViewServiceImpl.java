package site.wellmind.log.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.log.domain.dto.LogViewDto;
import site.wellmind.log.domain.model.LogArchiveDeleteDetailModel;
import site.wellmind.log.domain.model.LogArchiveDeleteModel;
import site.wellmind.log.domain.model.LogArchiveViewModel;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.log.repository.LogArchiveDeleteDetailRepository;
import site.wellmind.log.repository.LogArchiveDeleteRepository;
import site.wellmind.log.repository.LogArchiveViewRepository;
import site.wellmind.log.service.LogViewService;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.dto.UserDeleteDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.service.AccountService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void deleteById(Object ob, AccountDto dto) {

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

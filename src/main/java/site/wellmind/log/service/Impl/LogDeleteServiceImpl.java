package site.wellmind.log.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import site.wellmind.log.domain.dto.LogDeleteDto;
import site.wellmind.log.domain.model.LogArchiveDeleteDetailModel;
import site.wellmind.log.domain.model.LogArchiveDeleteModel;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.log.event.UserDeletedEvent;
import site.wellmind.log.repository.LogArchiveDeleteDetailRepository;
import site.wellmind.log.repository.LogArchiveDeleteRepository;
import site.wellmind.log.service.LogDeleteService;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.dto.EducationDto;
import site.wellmind.user.domain.dto.UserDeleteDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.mapper.UserDtoEntityMapper;
import site.wellmind.user.mapper.UserEntityDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "LogDeleteServiceImpl")
public class LogDeleteServiceImpl implements LogDeleteService {
    private final LogArchiveDeleteRepository logArchiveDeleteRepository;
    private final LogArchiveDeleteDetailRepository logArchiveDeleteDetailRepository;
    private final ObjectMapper objectMapper;

    private final UserDtoEntityMapper userDtoEntityMapper;
    private final UserEntityDtoMapper userEntityDtoMapper;

    @Override
    public Object save(LogDeleteDto logDeleteDto) {
        return null;
    }

    @Override
    public List<LogDeleteDto> saveAll(List<LogDeleteDto> entities) {
        return null;
    }

    @Override
    public void deleteById(Object ob, AccountDto dto) {

    }

    @Override
    public LogDeleteDto modify(LogDeleteDto logDeleteDto, AccountDto dto) {
        return null;
    }

    @Override
    public Object findById(String employeeId, AccountDto dto) {
        return null;
    }

    @Override
    public List<LogDeleteDto> findAll() {
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

    @Override
    public void recovery(UserDeleteDto userDeleteDto, AccountDto accountDto) {


    }
    @Async
    @EventListener
    @Transactional
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        Object userModel = event.getUserModel();
        String reason = event.getReason();
        DeleteStatus deleteType = event.getDeleteType();
        AdminTopModel admin = event.getAdmin();

        LogArchiveDeleteModel masterLog = saveMasterDeleteLog(reason, deleteType, admin);

        try {
            if (userModel instanceof UserTopModel user) {
                saveDeleteDetailLog(masterLog, "jsol_usertop", user.getEmployeeId(), objectMapper.writeValueAsString(userEntityDtoMapper.entityToDtoUserTop(user)));

                if (user.getUserInfoModel() != null) {
                    saveDeleteDetailLog(masterLog, "jsol_userinfo", user.getEmployeeId(), objectMapper.writeValueAsString(userEntityDtoMapper.entityToDtoUserInfo(user.getUserInfoModel())));
                }

                if (user.getUserEduIds() != null) {
                    List<EducationDto> educationDtos = user.getUserEduIds().stream()
                            .map(edu -> userEntityDtoMapper.entityToDtoUserEdu(edu))
                            .collect(Collectors.toList());
                    saveDeleteDetailLog(masterLog, "jsol_user_education", user.getEmployeeId(), objectMapper.writeValueAsString(educationDtos));
                }
            } else if (userModel instanceof AdminTopModel adminUser) {
                saveDeleteDetailLog(masterLog, "jsol_admintop", adminUser.getEmployeeId(), objectMapper.writeValueAsString(userEntityDtoMapper.entityToDtoUserTop(adminUser)));

                if (adminUser.getUserInfoModel() != null) {
                    saveDeleteDetailLog(masterLog, "jsol_userinfo", adminUser.getEmployeeId(), objectMapper.writeValueAsString(userEntityDtoMapper.entityToDtoUserInfo(adminUser.getUserInfoModel())));
                }

                if (adminUser.getUserEduIds() != null) {
                    List<EducationDto> educationDtos = adminUser.getUserEduIds().stream()
                            .map(edu -> userEntityDtoMapper.entityToDtoUserEdu(edu))
                            .collect(Collectors.toList());

                    saveDeleteDetailLog(masterLog, "jsol_user_education", adminUser.getEmployeeId(), objectMapper.writeValueAsString(educationDtos));
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving delete log", e);
        }
    }
    private LogArchiveDeleteModel saveMasterDeleteLog(String reason, DeleteStatus deleteType, AdminTopModel admin) {
        LogArchiveDeleteModel masterLog = LogArchiveDeleteModel.builder()
                .deleteReason(reason)
                .deleteType(deleteType)
                .deleterId(admin)
                .build();
        return logArchiveDeleteRepository.save(masterLog);
    }

    private void saveDeleteDetailLog(LogArchiveDeleteModel masterLog, String tableName, String employeeId, String deletedValue) {
        LogArchiveDeleteDetailModel detailLog = LogArchiveDeleteDetailModel.builder()
                .masterDeleteLogId(masterLog)
                .tableName(tableName)
                .deletedEmployeeId(employeeId)
                .deletedValue(deletedValue)
                .build();
        logArchiveDeleteDetailRepository.save(detailLog);
    }
}

package site.wellmind.transfer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.wellmind.transfer.domain.dto.TransferDto;
import site.wellmind.user.domain.dto.AccountDto;

public interface TransferService {

    Page<TransferDto> findByEmployeeId(Pageable pageable, AccountDto accountDto);

    Page<TransferDto> findByAll(String departName, String positionName, String name, Pageable pageable, AccountDto accountDto);
}

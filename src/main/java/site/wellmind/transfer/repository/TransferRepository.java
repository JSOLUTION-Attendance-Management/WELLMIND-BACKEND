package site.wellmind.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.transfer.domain.model.TransferModel;

@Repository
public interface TransferRepository extends JpaRepository<TransferModel,Long> {
}

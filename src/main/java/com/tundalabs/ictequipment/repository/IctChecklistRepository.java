package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.IctChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IctChecklistRepository extends JpaRepository<IctChecklist, Long> {

    Optional<IctChecklist> findByTransactionId(Long transactionId);

    void deleteByTransactionId(Long transactionId);
}

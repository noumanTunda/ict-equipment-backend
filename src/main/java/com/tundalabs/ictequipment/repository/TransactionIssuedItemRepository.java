package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.TransactionIssuedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionIssuedItemRepository extends JpaRepository<TransactionIssuedItem, Long> {

    List<TransactionIssuedItem> findByTransactionId(Long transactionId);

    void deleteByTransactionId(Long transactionId);

    @Query("SELECT tii FROM TransactionIssuedItem tii JOIN tii.transaction t WHERE t.staffId = :staffId AND t.status = 'COMPLETED'")
    List<TransactionIssuedItem> findByStaffIdAndCompletedTransaction(@Param("staffId") Long staffId);
}

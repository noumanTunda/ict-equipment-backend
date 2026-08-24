package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.TransactionReturnedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionReturnedItemRepository extends JpaRepository<TransactionReturnedItem, Long> {

    List<TransactionReturnedItem> findByTransactionId(Long transactionId);

    void deleteByTransactionId(Long transactionId);
}

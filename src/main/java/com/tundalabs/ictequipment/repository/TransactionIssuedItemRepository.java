package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.TransactionIssuedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionIssuedItemRepository extends JpaRepository<TransactionIssuedItem, Long> {

    List<TransactionIssuedItem> findByTransactionId(Long transactionId);

    void deleteByTransactionId(Long transactionId);
}

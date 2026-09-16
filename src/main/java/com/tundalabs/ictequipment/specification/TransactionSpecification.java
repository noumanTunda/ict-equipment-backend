package com.tundalabs.ictequipment.specification;

import com.tundalabs.ictequipment.dto.ReportFilterDto;
import com.tundalabs.ictequipment.entity.EquipmentTransaction;
import com.tundalabs.ictequipment.entity.TransactionReturnedItem;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<EquipmentTransaction> buildFromFilter(ReportFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getEndDate()));
            }

            if (filter.getTransactionStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getTransactionStatus()));
            }

            if (filter.getStaffIds() != null && !filter.getStaffIds().isEmpty()) {
                predicates.add(root.get("staffId").in(filter.getStaffIds()));
            }

            if (filter.getItemCondition() != null) {
                Join<EquipmentTransaction, TransactionReturnedItem> returnedItemsJoin = 
                    root.join("returnedItems", JoinType.LEFT);
                predicates.add(cb.equal(returnedItemsJoin.get("itemCondition"), filter.getItemCondition()));
            }

            if (filter.getOverdueThresholdDays() != null) {
                LocalDateTime overdueDate = LocalDateTime.now().minusDays(filter.getOverdueThresholdDays());
                predicates.add(cb.and(
                    cb.equal(root.get("status"), EquipmentTransaction.TransactionStatus.PENDING_SIGNATURE),
                    cb.lessThan(root.get("createdAt"), overdueDate)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<EquipmentTransaction> byStatus(EquipmentTransaction.TransactionStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<EquipmentTransaction> byStaffId(Long staffId) {
        return (root, query, cb) -> cb.equal(root.get("staffId"), staffId);
    }

    public static Specification<EquipmentTransaction> byIssuingOfficerId(Long officerId) {
        return (root, query, cb) -> cb.equal(root.get("issuingOfficerId"), officerId);
    }

    public static Specification<EquipmentTransaction> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> cb.and(
            cb.greaterThanOrEqualTo(root.get("createdAt"), startDate),
            cb.lessThanOrEqualTo(root.get("createdAt"), endDate)
        );
    }

    public static Specification<EquipmentTransaction> isOverdue(int thresholdDays) {
        LocalDateTime overdueDate = LocalDateTime.now().minusDays(thresholdDays);
        return (root, query, cb) -> cb.and(
            cb.equal(root.get("status"), EquipmentTransaction.TransactionStatus.PENDING_SIGNATURE),
            cb.lessThan(root.get("createdAt"), overdueDate)
        );
    }
}

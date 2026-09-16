package com.tundalabs.ictequipment.specification;

import com.tundalabs.ictequipment.dto.ReportFilterDto;
import com.tundalabs.ictequipment.entity.Equipment;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EquipmentSpecification {

    public static Specification<Equipment> buildFromFilter(ReportFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getDepartment() != null) {
                predicates.add(cb.equal(root.get("department"), filter.getDepartment()));
            }

            if (filter.getEquipmentCategory() != null) {
                predicates.add(cb.like(
                    cb.lower(root.get("equipmentType")),
                    "%" + filter.getEquipmentCategory().toLowerCase() + "%"
                ));
            }

            if (filter.getEquipmentStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getEquipmentStatus()));
            }

            if (filter.getHasWarranty() != null) {
                predicates.add(cb.equal(root.get("hasWarranty"), filter.getHasWarranty()));
            }

            if (filter.getSearchQuery() != null && !filter.getSearchQuery().trim().isEmpty()) {
                String searchPattern = "%" + filter.getSearchQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("assetNumber")), searchPattern),
                    cb.like(cb.lower(root.get("serialNumber")), searchPattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Equipment> hasWarrantyExpiringBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (root.get("warrantyDurationMonths") == null) {
                return cb.disjunction();
            }
            return cb.and(
                cb.equal(root.get("hasWarranty"), true),
                cb.isNotNull(root.get("warrantyDurationMonths"))
            );
        };
    }

    public static Specification<Equipment> byStatus(Equipment.EquipmentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Equipment> byDepartment(Equipment.EquipmentDepartment department) {
        return (root, query, cb) -> cb.equal(root.get("department"), department);
    }
}

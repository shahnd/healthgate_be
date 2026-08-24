package com.kh.healthgate.employee.model.dao;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.kh.healthgate.attendance.model.vo.Timecards;
import com.kh.healthgate.employee.controller.EmployeeController.EmpSearchCondition;
import com.kh.healthgate.employee.model.vo.Employee;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class EmployeeSpecification {

    public static Specification<Employee> search(EmpSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(condition.employeeNumber())) {
                predicates.add(cb.like(root.get("employeeNumber"), "%" + condition.employeeNumber() + "%"));
            }

            if (StringUtils.hasText(condition.name())) {
                predicates.add(cb.like(root.get("name"), "%" + condition.name() + "%"));
            }

            if (condition.departmentId() != null) {
                predicates.add(cb.equal(root.get("departments").get("id"), condition.departmentId()));
            }

            if (condition.positionId() != null) {
                predicates.add(cb.equal(root.get("positions").get("id"), condition.positionId()));
            }

            if (condition.searchDate() != null || StringUtils.hasText(condition.status())) {

                Join<Employee, Timecards> timecardJoin = root.join("timecards", JoinType.LEFT);

                if (StringUtils.hasText(condition.status())) {
                    predicates.add(cb.equal(timecardJoin.get("status"), condition.status()));
                }

                if (condition.searchDate() != null) {
                    LocalDateTime startOfDay = condition.searchDate().atStartOfDay();
                    LocalDateTime endOfDay = condition.searchDate().atTime(LocalTime.MAX);

                    predicates.add(cb.between(timecardJoin.get("clockInAt"), startOfDay, endOfDay));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

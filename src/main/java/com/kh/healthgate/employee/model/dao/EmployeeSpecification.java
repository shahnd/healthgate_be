package com.kh.healthgate.employee.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.kh.healthgate.employee.controller.EmployeeController.EmpSearchCondition;
import com.kh.healthgate.employee.model.vo.Employee;

import jakarta.persistence.criteria.Predicate;

public class EmployeeSpecification {

    public static Specification<Employee> search(EmpSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(condition.id())) {
                predicates.add(cb.like(root.get("id"), "%" + condition.id() + "%"));
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

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

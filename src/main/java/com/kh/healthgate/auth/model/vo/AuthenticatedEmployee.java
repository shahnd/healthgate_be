package com.kh.healthgate.auth.model.vo;

import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.role;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class AuthenticatedEmployee {
    private Long id;
    private String name;
    private role role;

    public static AuthenticatedEmployee from(Employee employee) {
        return new AuthenticatedEmployee(
                employee.getId(),
                employee.getName(),
                employee.getRole());
    }
}

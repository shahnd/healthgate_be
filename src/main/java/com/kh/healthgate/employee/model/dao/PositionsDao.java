package com.kh.healthgate.employee.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.kh.healthgate.employee.model.vo.Positions;

public interface PositionsDao extends JpaRepository<Positions, Long> {

}

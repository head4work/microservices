package com.head4work.employeeservice.repositories;

import com.head4work.employeeservice.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByIdAndUserId(String employeeId, String userId);

    @Modifying
    @Transactional
    void deleteEmployeeByIdAndUserId(String id, String userId);

    @Query("SELECT e FROM Employee e WHERE e.userId = :userId")
    List<Employee> findEmployeesByUserId(@Param("userId") String userId);
}

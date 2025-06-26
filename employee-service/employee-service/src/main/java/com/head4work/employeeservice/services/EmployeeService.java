package com.head4work.employeeservice.services;

import com.head4work.employeeservice.dto.EmployeeDto;
import com.head4work.employeeservice.dto.EmployeeResponse;
import com.head4work.employeeservice.entities.Employee;
import com.head4work.employeeservice.exceptions.EmployeeNotFoundException;
import com.head4work.employeeservice.repositories.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.beans.PersistenceDelegate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee getByIdForUser(String id, String userId) {
        return employeeRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee updateEmployeeForUser(EmployeeDto employeeDto, String id, String userId) {
        Employee employee = getByIdForUser(id, userId);
        modelMapper.map(employeeDto, employee);
        return employeeRepository.save(employee);
    }

    public void deleteForUser(String id, String userId) {
        employeeRepository.deleteEmployeeByIdAndUserId(id, userId);
    }

    public List<Employee> getAllByUserId(String userId) {
        return employeeRepository.findEmployeesByUserId(userId);
    }

    public List<EmployeeResponse> findEmployeesByIds(List<String> ids, String userId) {
        List<Employee> employees = employeeRepository.findEmployeesByUserId(userId);
        return employees.stream()
                .filter(employee -> ids.contains(employee.getId()))
                .map(employee -> modelMapper.map(employee, EmployeeResponse.class))
                .toList();
    }
}

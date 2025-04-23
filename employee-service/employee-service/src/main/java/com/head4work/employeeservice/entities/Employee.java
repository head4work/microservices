package com.head4work.employeeservice.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private LocalDate hired;
    private LocalDate fired;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String address;
    private String phone;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    @JsonBackReference // Annotation to handle JSON serialization
    private String userId;

//    @Builder.Default
//    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
//    private Set<Task> tasks = new HashSet<>();
//
//    @Builder.Default
//    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Salary> salaries = new ArrayList<>();
//
//    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<TimeCard> timeCards;

//    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Payment> payments;

//    @ElementCollection
//    @CollectionTable(name = "employee_vacations", joinColumns = @JoinColumn(name = "employee_id"))
//    private Set<LocalDate> vacations;

}
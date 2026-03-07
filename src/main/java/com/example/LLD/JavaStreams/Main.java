package com.example.LLD.JavaStreams;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
            new Employee(1, "Alice", 30, "IT", "Mumbai", 60000, "Female"),
            new Employee(2, "Bob", 28, "HR", "Chennai", 50000, "Male"),
            new Employee(3, "Charlie", 35, "Finance", "Delhi", 70000, "Male"),
            new Employee(4, "David", 40, "IT", "Mumbai", 90000, "Male"),
            new Employee(5, "Eva", 25, "Sales", "Chennai", 45000, "Female"),
            new Employee(6, "Frank", 31, "HR", "Delhi", 52000, "Male"),
            new Employee(7, "Grace", 29, "IT", "Mumbai", 62000, "Female"),
            new Employee(8, "Hank", 33, "Sales", "Delhi", 48000, "Male"),
            new Employee(8, "Hank", 33, "Sales", "Delhi", 48000, "Male")
        );

        // BASIC Questions

        // 1 Print all employee names
        List<String> names = employees.stream().map(Employee::getName).collect(Collectors.toList());
        // System.out.println(names);

        // 2 Convert employee names to uppercase
        List<String> upperNames = employees.stream().map((Employee e)->{
            return e.getName().toUpperCase();
        }).collect(Collectors.toList());
        // System.out.println(upperNames);

        // 3 Filter employees older than 30
        List<String> over30Distinct = employees.stream().filter(e->e.getAge()>30).map(Employee::getName).distinct().collect(Collectors.toList());
        // System.out.println(over30Distinct);

        // 4 Count employees in a specific department (e.g., "IT")
        long ITEmployees = employees.stream().filter(e->{
            return e.department.equals("IT");
        }).count();
        // System.out.println(ITEmployees);

        //5 Find employees whose name starts with 'A'
        List<Employee> startsWithA = employees.stream().filter(e->e.getName().startsWith("A")).collect(Collectors.toList());
        // System.out.println(startsWithA);

        // 6 List all employee IDs
        List<Integer> Idsemployee = employees.stream().map(Employee::getId).collect(Collectors.toList());
        
        // 7 Create a list of employee names
        //  same as first question

        // 8 Increase all salaries by 10%
        List<Double> salaries = employees.stream().map(e->e.getSalary()*1.1).collect(Collectors.toList());
        // System.out.println(salaries);

        // 9 Check if any employee is from "Mumbai"
        Optional<Employee> oneFromMumbai = employees.stream().filter(e->e.getCity().equals("Mumbai")).findAny();
        // System.out.println(oneFromMumbai);

        // Sorting And Limiting

        // 11 Sort employees by salary
        List<Double> salarySort = employees.stream().map(Employee::getSalary).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        // System.out.println(salarySort);

        // 12 Sort by name and then by age
        List<String> sortedEmployees = employees.stream().sorted(Comparator.comparing(Employee::getName).thenComparing(Employee::getAge)).map(Employee::getName).collect(Collectors.toList());
        // System.out.println(sortedEmployees);

        // 13 Get top 3 highest-paid employees
        List<String> top3PaidEmp = employees.stream().sorted(Comparator.comparing(Employee::getSalary).reversed()).map(Employee::getName).distinct().limit(3).collect(Collectors.toList());
        // System.out.println(top3PaidEmp);

        // 14 Get second-highest salary employee
        List<String> secondHighestSalary = employees.stream().sorted(Comparator.comparing(Employee::getSalary).reversed()).map(Employee::getName).distinct().limit(2).skip(1).collect(Collectors.toList());
        System.out.println(secondHighestSalary);

        // 15 Find youngest employee
        List<String> youngestEmp = employees.stream().sorted(Comparator.comparing(Employee::getAge)).map(Employee::getName).limit(1).collect(Collectors.toList());
        Optional<Employee> youngest = employees.stream().min(Comparator.comparing(Employee::getAge));

        // 16. Get employee with longest name
        Optional<Employee> longestName = employees.stream().max(Comparator.comparingInt(e->e.getName().length()));

        //17 Find employees with duplicate names
        Set<String> seen = new HashSet<>();
        List<String> duplicateEmployee = employees.stream().map(Employee::getName).filter(name->!seen.add(name)).collect(Collectors.toList());

        // 18. Get the employee with minimum salary
        String minSalary = employees.stream().min(Comparator.comparing(Employee::getSalary)).map(Employee::getName).orElse(null);

        // 19. Skip first 2 by age
        employees.stream().sorted(Comparator.comparing(Employee::getAge)).skip(2)
                .forEach(e -> System.out.println(e.getName()));

        // 20. First 5 from IT
        employees.stream().filter(e -> e.getDepartment().equals("IT")).limit(5)
                .forEach(e -> System.out.println(e.getName()));
    }
}

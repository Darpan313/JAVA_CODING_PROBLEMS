package ImplementProblems.JavaStreams;

import java.util.*;
import java.util.stream.Collectors;

/*
    Using Collectors.collectingAndThen() to perform an additional transformation on the result of a doenstream collector.

    Collectors.maxBy() returns an Optional<Employee> because a department group might theoretically be empty.
    collectingAndThen unwraps the Optional immediately so the resulting map is Map<String, Employee> instead of
    Map<String, Optinal<Employee>>.
 */
public class HighestPaidByDepartment {
    private record Employee(String name, String department, double salary) {}

    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        Map<String, Employee> topEmployees = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::salary)),
                                Optional::get
                        )
                ));
    }
}

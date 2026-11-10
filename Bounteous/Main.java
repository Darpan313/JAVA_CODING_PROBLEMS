package Bounteous;

import DesginPatterns.CompositePattern.OrganizationHeirarchy.Employee;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
    Given a list of employees, create a Map<empId, Employee> where age of employee
    is greater than 40 using lambdas and streams

    Employee{
	int empId
	int age
	String name
}

Find the first pair of elements in an array with a given sum

arr -> 1,3,2,3,5,6 // O(n^2)
sorted -> 1,2,3,3,5,6 // O(nlogn)

sum = 7

map -> (num -> TreeSet<integer>)

1 -> [0]
3 -> [1, 3]
2 -> [2]


Output -> 1 and 6
 */
public class Main {
    public static void main(String[] args) {

    }

    Map<String, Employee> getEmployeeGT40(List<Employee> employees) {
        return employees.stream()
                .filter(e-> e.getSalary()>40)
                .collect(Collectors.toMap(
                        Employee::getName,
                        Function.identity()
                ));
    }

    // POST
    // body: {
        //timestamp:
        // email-id:
        // provider - mailchimp
    // }
    //______
    // persist in to database.., (if we find entry
    // Scheduler - Go over database.. events that needs to be triggered now
    // WHERE timestamp > now()
    // Schedule - 15 mins
    // REDIS - __, events  visibility timeout ___ LUA SCRIPT zset
    // Scheduler Nodes - taking event from redis - > it calls downstream service execute event.
    // reconcilation -
}

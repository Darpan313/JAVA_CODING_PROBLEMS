package ImplementProblems.RepeatedCustomer;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        List<String> customerName = new ArrayList<>();
        List<String> visitDate = new ArrayList<>();
        for(int i=0; i<n; i++) {
            customerName.add(sc.next());
            visitDate.add(sc.next());
        }

        int count = countRepeatedCustomer(customerName, visitDate);
        System.out.println(count);
    }

    private static int countRepeatedCustomer(List<String> names, List<String> visitDate) {
        Map<String, String> customerLastVisitDate = new HashMap<>();
        Set<String> repeatedCustomer = new HashSet<>();

        int n = names.size();
        for(int i=0; i<n; i++) {
            String name = names.get(i);
            String date = visitDate.get(i);

            if(customerLastVisitDate.containsKey(name)) {
                String lastVisitDate = customerLastVisitDate.get(name);
                if(!lastVisitDate.equals(date) && !repeatedCustomer.contains(name)) {
                    repeatedCustomer.add(name);
                }
            }
            else {
                customerLastVisitDate.put(name, date);
            }
        }
        return repeatedCustomer.size();
    }
}

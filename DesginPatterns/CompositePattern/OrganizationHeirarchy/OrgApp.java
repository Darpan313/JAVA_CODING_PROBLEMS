package DesginPatterns.CompositePattern.OrganizationHeirarchy;

public class OrgApp {
    public static void main(String[] args) {
        Employee dev1 = new Employee("Alice", "Senior Engineer", 120000);
        Employee dev2 = new Employee("Bob", "Engineer", 95000);
        Employee dev3 = new Employee("Charlie", "Engineer", 90000);
        Employee designer = new Employee("Diana", "Designer", 100000);

        Manager techLead = new Manager("Eve", "Tech Lead", 140000);
        techLead.addMember(dev1);
        techLead.addMember(dev2);

        Manager vpEng = new Manager("Frank", "VP Engineering", 200000);
        vpEng.addMember(techLead);
        vpEng.addMember(dev3);

        Manager vpProduct = new Manager("Grace", "VP Product", 190000);
        vpProduct.addMember(designer);

        Manager ceo = new Manager("Hank", "CEO", 300000);
        ceo.addMember(vpEng);
        ceo.addMember(vpProduct);

        System.out.println("---- Organization Chart ----");
        ceo.printHierarchy("");

        System.out.println("\nTotal Payroll: $" + ceo.getSalary());
        System.out.println("Total Headcount: " + ceo.getHeadCount());
        System.out.println("\nEngineering Payroll: $" + vpEng.getSalary());
        System.out.println("Engineering Headcount: " + vpEng.getHeadCount());
    }
}

package DesginPatterns.CompositePattern.OrganizationHeirarchy;

import java.util.ArrayList;
import java.util.List;

public class Manager implements OrgComponent {
    private final String name;
    private final String title;
    private final int salary;
    private final List<OrgComponent> members = new ArrayList<>();

    public Manager(String name, String title, int salary) {
        this.name = name;
        this.title = title;
        this.salary = salary;
    }

    public void addMember(OrgComponent member) {
        members.add(member);
    }
    public void removeMember(OrgComponent member) {
        members.remove(member);
    }

    @Override
    public int getSalary() {
        int total = salary;
        for(OrgComponent member : members) {
            total += member.getSalary();
        }
        return total;
    }

    @Override
    public int getHeadCount() {
        int count = 1;
        for(OrgComponent member : members) {
            count += member.getHeadCount();
        }
        return count;
    }

    @Override
    public void printHierarchy(String indent) {
        System.out.println(indent + "+ " + name + " (" + title + ", $" + salary + ")");
        for(OrgComponent member : members) {
            member.printHierarchy(indent + " ");
        }
    }
}

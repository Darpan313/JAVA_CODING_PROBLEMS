package DesginPatterns.CompositePattern.OrganizationHeirarchy;

public interface OrgComponent {
    int getSalary();
    int getHeadCount();
    void printHierarchy(String indent);
}

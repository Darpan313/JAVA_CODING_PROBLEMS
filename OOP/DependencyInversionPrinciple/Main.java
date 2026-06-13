package OOP.DependencyInversionPrinciple;

import OOP.DependencyInversionPrinciple.bad.ConnectToDatabase;
import OOP.DependencyInversionPrinciple.bad.PostgreSQLJdbcUrl;
import OOP.DependencyInversionPrinciple.good.MySQLJdbcUrl;

public class Main {
    public static void main(String[] args) {
        System.out.println("\nApproach that doesn't follow DIP:\n");

        PostgreSQLJdbcUrl p1 = new PostgreSQLJdbcUrl("my_db");
        ConnectToDatabase c1 = new ConnectToDatabase();

        c1.connect(p1);

        System.out.println("\nApproach tha follow DIP:\n");

        OOP.DependencyInversionPrinciple.good.PostgreSQLJdbcUrl p2 = new OOP.DependencyInversionPrinciple.good.PostgreSQLJdbcUrl("my_db");
        MySQLJdbcUrl p3 = new MySQLJdbcUrl("my_db");

        OOP.DependencyInversionPrinciple.good.ConnectToDatabase c2 = new OOP.DependencyInversionPrinciple.good.ConnectToDatabase();
        c2.connect(p2);
        c2.connect(p3);
    }
}

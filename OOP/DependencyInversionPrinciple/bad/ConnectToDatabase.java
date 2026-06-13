package OOP.DependencyInversionPrinciple.bad;

public class ConnectToDatabase {
//    If we create another type of JDBC URL (for example, MySQLJdbcUrl), then we cannot use connect(PostgreSQLJdbcUrl postgreSQL)
//    method. So, we have to drop this dependency on concrete and create a dependency on abstraction.
    public void connect(PostgreSQLJdbcUrl postgreSQLJdbcUrl) {
        System.out.println("Connecting to " + postgreSQLJdbcUrl.get());
    }
}

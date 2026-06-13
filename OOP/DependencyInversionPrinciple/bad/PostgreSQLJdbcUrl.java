package OOP.DependencyInversionPrinciple.bad;

public class PostgreSQLJdbcUrl {
    private final String dbName;

    public PostgreSQLJdbcUrl(String dbName) {
        this.dbName = dbName;
    }

    public String get() {
        return "jdbc:postgresql://..." + dbName;
    }
}

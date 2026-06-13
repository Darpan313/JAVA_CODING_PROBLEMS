package Concurrency.DesignProblem.MessageDispatcher;

public class SystemAlertMessage implements Message{
    private final String severity;

    public SystemAlertMessage(String severity) {
        this.severity = severity;
    }

    @Override
    public String getType() {
        return "SYSTEM_ALERT";
    }

    public String getSeverity() {
        return severity;
    }
}

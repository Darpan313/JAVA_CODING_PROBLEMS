package Concurrency.DesignProblem.MessageDispatcher;

public class Application {
    public static void main(String[] args) {
        MessageDispatcher dispatcher = new MessageDispatcher();

        MessageHandler<UserLoginMessage> loginHandler = message ->
                System.out.println("Processing login for: " + message.getUserName());

        MessageHandler<SystemAlertMessage> alertHandler = message ->
                System.out.println("CRITICAL ALERT! Severity: " + message.getSeverity());

        MessageHandler<UserLoginMessage> auditHandler = message ->
                System.out.println("Audit Log: User " + message.getUserName() +
                        " logged in at " + System.currentTimeMillis());

        dispatcher.register("USER_LOGIN", loginHandler);
        dispatcher.register("SYSTEM_ALERT", alertHandler);
        dispatcher.register("USER_LOGIN", auditHandler);

        Runnable task1 = () -> dispatcher.dispatch(new UserLoginMessage("Alice"));
        Runnable task2 = () -> dispatcher.dispatch(new SystemAlertMessage("HIGH"));

        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task2);

        t1.start();
        t2.start();
    }
}

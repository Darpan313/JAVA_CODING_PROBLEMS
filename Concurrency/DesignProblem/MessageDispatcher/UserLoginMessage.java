package Concurrency.DesignProblem.MessageDispatcher;

public class UserLoginMessage implements Message {
    private final String userName;

    public UserLoginMessage(String userName) {
        this.userName = userName;
    }

    @Override
    public String getType() {
        return "USER_LOGIN";
    }

    public String getUserName() {
        return userName;
    }
}

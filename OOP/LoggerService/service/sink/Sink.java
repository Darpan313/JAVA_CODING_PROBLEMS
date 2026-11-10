package OOP.LoggerService.service.sink;

public interface Sink {
    void write(String formatted) throws Exception;
}

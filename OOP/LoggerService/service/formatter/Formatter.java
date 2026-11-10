package OOP.LoggerService.service.formatter;

import OOP.LoggerService.entities.LogRecord;

public interface Formatter {
    String format(LogRecord record);
}

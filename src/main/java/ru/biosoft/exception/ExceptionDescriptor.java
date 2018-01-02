package ru.biosoft.exception;

import ru.biosoft.exception.LoggedException.LoggingLevel;

/**
 * Exception descriptor has following properties:
 * <ul> 
 *   <li>code - some formalized code. It can be used by user to describe the problem to vendor or find a solution in the product documentation.
 *   <li>logLevel - how the exception should be logged.
 *   <li>template - message template to log the exception. All substrings like <code>$some_key$</> will be replaced by corresponding values
 *   from LoggedException.properties.   
 * </ul>      
 *  
 */
public class ExceptionDescriptor
{
    private String code;
    private LoggingLevel logLevel;
    private String template;

    public ExceptionDescriptor(String code, LoggingLevel logLevel, String template)
    {
        this.code = code;
        this.logLevel = logLevel;
        this.template = template;
    }

    public String getCode()
    {
        return code;
    }

    public LoggingLevel getLogLevel()
    {
        return logLevel;
    }

    public String getTemplate()
    {
        return template;
    }

}

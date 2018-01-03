package ru.biosoft.exception;

/**
 * This exception is thrown when needed parameter is missing.
 */
@SuppressWarnings("serial")
public class MissingParameterException extends ParameterException
{
    public static final ExceptionDescriptor ED_ABSENT = new ExceptionDescriptor("MissingParameter", LoggingLevel.Summary,
            "Parameter $lc_parameter$ is missing, it should be specified.");
    
    public MissingParameterException(String parameterName)
    {
        super(ED_ABSENT, parameterName);
    }
}

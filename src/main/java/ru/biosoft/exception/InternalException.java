package ru.biosoft.exception;

/**
 * This exception is used to convert (wrap) other exceptions to {@link LoggedException}.
 */
@SuppressWarnings("serial")
public class InternalException extends LoggedException
{
    public static final String KEY_MESSAGE = "message";

    public static final ExceptionDescriptor ED_INTERNAL = new ExceptionDescriptor( "Internal", LoggingLevel.TraceIfNoCause,
            "Unexpected internal error occured: $message$.");

    private InternalException(Throwable t, ExceptionDescriptor ed, String message)
    {
        super(t, ed);
        
        if( message == null )
        {
        	if( t != null )
        		message = t.getClass().getSimpleName().toString() + " - " + t.getMessage();
        	else
        		message = "Unreachable state occurred.";
        }

        properties.put(KEY_MESSAGE, message);
    }
    
    public InternalException(Throwable t)
    {
        this(t, ED_INTERNAL, null);
    }
    
    public InternalException(Throwable t, String message)
    {
        this(t, ED_INTERNAL, message);
    }
    
    public InternalException(String message)
    {
        this(null, ED_INTERNAL, message);
    }
    
    public InternalException()
    {
        this(null, null);
    }
}
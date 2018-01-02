package ru.biosoft.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Extension of exception for more convenient logging.
 */
@SuppressWarnings("serial")
public abstract class LoggedException extends RuntimeException
{
	private final static Logger defaultLogger = Logger.getLogger("error.log");
    private static AtomicInteger lastExceptionId = new AtomicInteger();

	public enum LoggingLevel
    {
    	/**  Do not log this Exception even if requested.  */
    	None,
    	
    	/** Logs summary and properties. */ 
        Summary,
        
        /** Logs summary, properties and stack trace if no cause is available. */
        TraceIfNoCause,
        
        /** Logs summary, properties and stack trace. */
        Trace   
    }

    protected LoggedException(ExceptionDescriptor descriptor)
    {
        this(null, descriptor);
    }
    
    protected LoggedException(Throwable t, ExceptionDescriptor descriptor)
    {
        super(t);
        this.descriptor = descriptor;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Properties
    //

    private final int exceptionId = lastExceptionId.incrementAndGet();
    public String getId()
    {
        return "EX#" + exceptionId;
    }

    private final ExceptionDescriptor descriptor;
    public ExceptionDescriptor getDescriptor()
    {
    	return descriptor;
    }
    
    protected final HashMap<String, Object> properties = new HashMap<>();
    public Object getProperty(String key)
    {
        return properties.get(key);
    }

    protected boolean isDisplayCause()
    {
        return getCause() != null && getCause() != this && getCause() instanceof LoggedException;
    }
    
    private boolean logged = false;
    protected boolean isLogged()
    {
    	return logged;
    }
    
    // ////////////////////////////////////////////////////////////////////////
    // Logging
    //

    private static int lastLoggedStackTraceId;
    private static StackTraceElement[] lastStackTrace;

    private String getLog()
    {
        if(descriptor.getLogLevel() == LoggingLevel.None)
            return "";
        
        StringBuilder sb = new StringBuilder().append(this).append('\n');
        String template = descriptor.getTemplate();
        for(String key: properties.keySet() )
        {
            if( template.contains('$' + key +'$'))
                continue;
            
            sb.append("\t" + key + " = " + properties.get(key)+"\n");
        }
        
        if(descriptor.getLogLevel() == LoggingLevel.Summary)
            return sb.toString();
        
        if(descriptor.getLogLevel() == LoggingLevel.Trace || (descriptor.getLogLevel() == LoggingLevel.TraceIfNoCause && getCause() == null))
        {
            StringWriter sw = new StringWriter();
            logStackTrace(new PrintWriter(sw), this, exceptionId);
            sb.append(sw.toString());
        }
        
        return sb.toString();
    }

    public String log()
    {
    	return log(defaultLogger);
    }
    
    
    public String log(Logger log)
    {
        if( logged || descriptor.getLogLevel() == LoggingLevel.None ) 
        	return getMessage();
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.append(getLog());

        LoggedException ex = this;
        while(ex.getCause() != ex && ex.getCause() != null)
        {
            if(ex.getCause() instanceof LoggedException)
            {
                LoggedException cause = (LoggedException)ex.getCause();
                if( cause.descriptor.getLogLevel() != LoggingLevel.None )
                {
                    if(cause.logged)
                    {
                        pw.append("Caused by: " + cause + " (already logged)");
                        break;
                    }
                    
                    pw.append("Caused by: " + cause.getLog());
                }
                ex = cause;
            } 
            else
            {
                pw.append("Caused by: ");
                logStackTrace(pw, ex.getCause(), exceptionId);
                break;
            }
        }
        pw.flush();
        
        log.severe(sw.toString());
        logged = true;
        
        return getMessage();
    }
    
    private static void logStackTrace(PrintWriter pw, Throwable ex, int exceptionId)
    {
        StackTraceElement[] trace = ex.getStackTrace();
        synchronized(LoggedException.class)
        {
            if(Arrays.equals(lastStackTrace, trace))
            {
                if(!(ex instanceof LoggedException))
                    pw.println(ex.toString());
                pw.append("\t(see EX#" + lastLoggedStackTraceId + " for stack trace)");
                return;
            }
            lastStackTrace = trace;
            lastLoggedStackTraceId = exceptionId;
        }
        
        if(ex instanceof LoggedException)
        {
            for(StackTraceElement element: trace)
            {
                pw.append("\tat " + element+"\n");
            }
        }
        else
        {
            ex.printStackTrace(pw);
        }
    }

    public static String calculateTemplate(String template, HashMap<String, Object>properties)
    {
        try
        {
            StringBuilder result = new StringBuilder();

            String[] fields = split(template, '$');
            for( int i = 0; i < fields.length; i++ )
            {
                if( i % 2 == 0 )
                {
                    result.append(fields[i]);
                }
                else
                {
                	Object value = properties.get(fields[i]);
                	
                	if( value != null )
                		result.append(value.toString());
                }
            }
            return result.toString();
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public static String[] split(String string, char delimiter)
    {
        int n = 1;
        int i = 0;
        while(true)
        {
            i=string.indexOf(delimiter, i);
            if(i == -1) break;
            n++;
            i++;
        }
        if(n == 1) return new String[] {string};
        
        String[] result = new String[n];
        n = 0;
        i = 0;
        int start = 0;
        while(true)
        {
            i = string.indexOf(delimiter, start);
            if(i == -1) break;
            result[n++] = string.substring(start, i);
            start = i+1;
        }
        result[n] = string.substring(start);
        return result;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Override exception methods
    //

    private String message;
    
    @Override
    public String getMessage()
    {
        if(message == null)
        {
            String messageTemplate = descriptor.getTemplate();

            message = calculateTemplate(messageTemplate, properties);
            
            if( isDisplayCause() )
                message += "\nReason: " + getCause().getMessage();
        }
        
        return message;
    }
    
    @Override
    public String toString()
    {
        return getId() + '/' + descriptor.getCode() + ": " + calculateTemplate(descriptor.getTemplate(), properties)
                       + ( properties.isEmpty() ? "" : "\n properties=" + properties.hashCode() + "." );
    }
    
}

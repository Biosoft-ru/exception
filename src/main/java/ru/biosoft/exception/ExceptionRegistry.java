package ru.biosoft.exception;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

/**
 * Translates arbitrary exception into {@link LoggedException}.
 */
public class ExceptionRegistry
{
    public static LoggedException translateException(Throwable t)
    {
        if( translator != null )
        	return translator.translateException(t);
        
    	if(t == null)
            return null;

        if(t instanceof InvocationTargetException || t instanceof ExecutionException)
            t = t.getCause();

        if( t instanceof LoggedException )
            return (LoggedException)t;

        if( t instanceof ClassNotFoundException || t instanceof NoClassDefFoundError )
            return new LoggedClassNotFoundException (t);

        return new InternalException(t);
    }
    
    public static String log(Throwable t)
    {
        LoggedException ex = translateException(t);
        ex.log();
        
        return ex.getMessage();
    }

    private static ExceptionTranslator translator;
    public static void setExceptionRegistry(ExceptionTranslator translator)
    {
    	ExceptionRegistry.translator = translator;
    }

}

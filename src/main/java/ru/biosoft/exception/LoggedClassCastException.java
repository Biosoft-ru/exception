package ru.biosoft.exception;

/**
 * Logged version of {@link ClassCastException}.
 */
@SuppressWarnings("serial")
public class LoggedClassCastException extends LoggedException
{
    public static String KEY_CLASS = "class";
    public static String KEY_SUPER = "super";

    public static final ExceptionDescriptor ED_CLASS_CAST = new ExceptionDescriptor("ClassCast",
            LoggingLevel.TraceIfNoCause, "Internal error occured (Java class $class$ must subclass $super$).");
    
    public LoggedClassCastException(String className, String superName)
    {
        super(ED_CLASS_CAST);
        properties.put(KEY_CLASS, className);
        properties.put(KEY_SUPER, superName);
    }
}

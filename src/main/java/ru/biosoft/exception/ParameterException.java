package ru.biosoft.exception;

/**
 * Base class for LoggedExceptions related with incorrect parameters values.
 */
@SuppressWarnings("serial")
public abstract class ParameterException extends LoggedException
{
    public static String KEY_PARAMETER_NAME            = "parameter";
    public static String KEY_PARAMETER_NAME_LOWER_CASE = "lc_parameter";
    public static String KEY_PARAMETER_VALUE           = "value";
    
    public ParameterException(Throwable t, ExceptionDescriptor descriptor, String parameter, Object value)
    {
        super(ExceptionRegistry.translateException(t), descriptor);
        
        properties.put(KEY_PARAMETER_NAME, parameter);
        
        // changes first letter of the string to lower case if it's not abbreviation
        if(parameter != null && parameter.length() >= 2 && Character.isUpperCase(parameter.charAt(0)) && Character.isLowerCase(parameter.charAt(1)))
        	properties.put(KEY_PARAMETER_NAME_LOWER_CASE, parameter.substring(0,1).toLowerCase()+parameter.substring(1));
        else
        	properties.put(KEY_PARAMETER_NAME_LOWER_CASE, parameter);
        
        if( value != null )
        	properties.put(KEY_PARAMETER_VALUE, value);
    }

    public ParameterException(Throwable t, ExceptionDescriptor descriptor, String parameter)
    {
        this(t, descriptor, parameter, null);
    }
    
    public ParameterException(ExceptionDescriptor descriptor, String parameter)
    {
        this(null, descriptor, parameter);
    }
   
}

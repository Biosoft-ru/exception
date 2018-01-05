package ru.biosoft.exception;

/**
 * Logged version of {@link ClassNotFoundException}.
 */
@SuppressWarnings("serial")
public class LoggedClassNotFoundException extends LoggedException
{
    public static String KEY_CLASS      = "class";
    
    // keys for the case of environment with plug-ins
    
    public static String KEY_PLUGINS     = "plugins";
    public static String KEY_AVAILABLE  = "pluginAvailable";
    public static String KEY_REQUESTED  = "pluginRequested";
    public static String KEY_ADDITIONAL = "additional";
    
    public static final ExceptionDescriptor ED_NO_CLASS = new ExceptionDescriptor("NoClass", LoggingLevel.TraceIfNoCause,
            "Internal error occured: Java class $class$ not found.$additional$");
    public static final ExceptionDescriptor ED_CLASS_LOAD_ERROR = new ExceptionDescriptor("ClassLoadError", LoggingLevel.TraceIfNoCause,
            "Internal error occured during loading of Java class $class$.");


    public LoggedClassNotFoundException(Throwable t, String plugins)
    {
        this(t);
        properties.put(KEY_PLUGINS, plugins);
    }
    
    public LoggedClassNotFoundException(Throwable t, String clazz, String plugins)
    {
        super(t, defineDescriptor(t));
        properties.put(KEY_CLASS, clazz);

        if(plugins != null)
            properties.put(KEY_PLUGINS, plugins);
    }

    
    public LoggedClassNotFoundException(String clazz, String plugins)
    {
        super(ED_NO_CLASS);
        properties.put(KEY_CLASS, clazz);

        if(plugins != null)
            properties.put(KEY_PLUGINS, plugins);
    }

    public LoggedClassNotFoundException(Throwable t)
    {
        super(t, defineDescriptor(t));

        if(t instanceof ClassNotFoundException || t instanceof NoClassDefFoundError)
        {
            String className = t.getMessage().substring(t.getMessage().lastIndexOf(' ')+1).replace('/', '.');
            properties.put(KEY_CLASS, className);
            
            /** TODO - move to translate 
            String pluginForClass = ClassLoading.getPluginForClass( className );
            String requestedFromPlugin = ClassLoading.getPluginForClass( t.getStackTrace()[0].getClassName() );
            String additional = "";
            if(requestedFromPlugin != null)
            {
                properties.put(KEY_REQUESTED, requestedFromPlugin );
                if(SecurityManager.isAdmin())
                {
                    additional += "\nClass is requested from plugin: '"+requestedFromPlugin+"'.";
                }
            }
            if(pluginForClass != null)
            {
                properties.put(KEY_AVAILABLE, pluginForClass );
                if(SecurityManager.isAdmin())
                {
                    additional += "\nClass is available in plugin: '"+pluginForClass+"'.";
                }
            }
            
            properties.put(KEY_ADDITIONAL, additional);
            */
        }
    }
    

    private static ExceptionDescriptor defineDescriptor(Throwable t)
    {
        if(t instanceof ClassNotFoundException || t instanceof NoClassDefFoundError)
            return ED_NO_CLASS;
        
        return ED_CLASS_LOAD_ERROR;
    }
}

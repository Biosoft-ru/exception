package ru.biosoft.exception;

/**
 * Translates arbitrary exception into {@link LoggedException}.
 */
public interface ExceptionTranslator
{
    public LoggedException translateException(Throwable t);
}

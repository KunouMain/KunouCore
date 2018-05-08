package samophis.kunou.core.exceptions;

/**
 * Represents a generic exception thrown or caused by a {@link samophis.kunou.core.modules.Module Module} or an issue with one of them.
 *
 * @author SamOphis
 * @since 0.1
 */

public class ModuleException extends RuntimeException {
    public ModuleException(Throwable throwable) {
        super(throwable);
    }
    public ModuleException(String message) {
        super(message);
    }
}

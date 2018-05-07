package samophis.kunou.core.exceptions;

public class ModuleException extends RuntimeException {
    public ModuleException(Throwable throwable) {
        super(throwable);
    }
    public ModuleException(String message) {
        super(message);
    }
}

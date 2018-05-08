package samophis.kunou.core.modules;

/**
 * Represents the state of a {@link Module Module}.
 *
 * @author SamOphis
 * @since 0.1
 */

public enum State {
    /** Represents a state where a {@link samophis.kunou.core.modules.Module Module} is completely dead and has no active code running. */
    DEAD,
    /** Represents a state where a {@link samophis.kunou.core.modules.Module Module} is starting-up and initializing itself. */
    STARTING,
    /** Represents a state where a {@link samophis.kunou.core.modules.Module Module} has started and is about to become ready-to-use. */
    STARTED,
    /** Represents a state where a {@link samophis.kunou.core.modules.Module Module} is completely ready to accept events and function as intended. */
    READY,
    /** Represents a state where a {@link samophis.kunou.core.modules.Module Module} has been ordered to shut down and is stopping all of its active code. */
    SHUTTING_DOWN
}

package samophis.kunou.core.modules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A completely independent, asynchronous container with events to handle start-ups, shut-downs and incoming messages.
 * <br><p>Modules should usually have minimal dependencies and serve clear, explicitly-defined tasks to isolate an application's entry point and its "active parts".
 * If a module is too big, break it up into smaller modules dependent on the main one. These modules should all be contained inside the same {@link ModuleLoader ModuleLoader}
 * where they manage their state and react to events from the loader.
 *
 * <br>A helper class is present to take some weight off the backs of module developers: {@link AbstractModuleBase AbstractModuleBase}.
 * Additionally, a module is expected to <b>completely</b> manage its own state effectively and be fault-tolerant to some degree, usually in response to events from the loader.
 * Every single callback method is fired in different threads from a static, cached thread pool as defined in the {@link samophis.kunou.core.util.ModuleThreadExecutor ModuleThreadExecutor} class.</p>
 *
 * @author SamOphis
 * @since 0.1
 */

public interface Module {
    /**
     * Returns the {@link ModuleLoader ModuleLoader} this Module belongs to.
     * @return The {@link ModuleLoader ModuleLoader} instance that has control and owns this Module.
     */
    ModuleLoader getLoader();

    /**
     * Returns the name of this module (and what it's stored under in the internal module map}.
     * <br><p>Names of modules should be named as such: <code>(Some Name) Module</code> where the parentheses are missing. Audio Module would be a good name, for example.</p>
     * @return The name of this module.
     */
    String getName();

    /**
     * Returns the version of this module.
     * <br><p>The version of a module should follow the GitHub-style Semantic Versioning:  <code>v1.0-alpha</code> or <code>v0.3.12</code> are good examples.</p>
     * @return The version of this module.
     */
    String getVersion();

    /**
     * Returns the author(s) of this module -- who made it.
     * <br><p>Return a comma-separated list should there be more than one author.</p>
     * @return The author(s) who made this module.
     */
    String getAuthor();

    /**
     * Returns The homepage of this module.
     * <br><p>This does not have to be specified, although modules by nature should have some page attached to them, be it documentation, the source, introductions, etc.</p>
     * @return The homepage of this module.
     */
    String getUrl();

    /**
     * Returns the {@link State State} this module is currently in.
     * <br><p><b>Note: Please be aware that KunouCore will NOT manage the state of your modules. This is entirely dependent on the developer to set in response to events
     * in an asynchronous, modular environment.</b></p>
     * @return The current {@link State State} of this module.
     */
    State getState();

    /**
     * The method that fires (with <b>possibly-null or possibly-empty</b> arguments) in response to the {@link ModuleLoader ModuleLoader} requesting a start-up.
     * @param args <b>Possibly-null or possibly-empty</b> array of String arguments, which modules can react to as they wish.
     */
    void onStart(@Nullable String... args);

    /**
     * The method that fires (with <b>definitely-not-null</b> arguments) in response to a message from the {@link ModuleLoader ModuleLoader} being issued to it.
     * @param args <b>Definitely-not-null</b> array of String arguments, which modules can react to as they wish.
     */
    void onMessage(@Nonnull String... args);

    /**
     * The method that fires when the {@link ModuleLoader ModuleLoader} this module belongs to requests it to shutdown gracefully.
     * <br><p>No information is provided in this callback.
     * To get around this, send a message warning of an incoming death event and use the {@code andThen} follow-up code to shut down the module.</p>
     */
    void onDeath();
}
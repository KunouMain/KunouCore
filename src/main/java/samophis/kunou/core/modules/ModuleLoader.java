package samophis.kunou.core.modules;

import samophis.kunou.core.modules.internal.ModuleLoaderImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Represents the main module loader for Kunou's core.
 * <br><p>This is responsible for loading every other module available for Kunou, managing their states and managing the execution threads.
 * Other small features are also provides, but those are the main ones.
 * <br><br>This is the main entry point into any KunouCore application and is essentially a must to use (it is considered the "core" of the program).</p>
 *
 * @author SamOphis
 * @since 0.1
 */

public interface ModuleLoader {
    /**
     * Returns an unmodifiable view of the internal module map used to track {@link Module Modules}, verify ownership of them and more.
     * @return An unmodifiable view of the internal module map.
     */
    Map<String, Module> getModules();

    /**
     * Creates and returns an unmodifiable view of the values contained inside the internal module map.
     * <br><p>This isn't too intensive but it does use explicit casting for each call.
     * <br>However, since it's a view and backed internally by the value list, there's no need to make repeated calls.</p>
     * @return An unmodifiable view of the values contained inside the internal module map.
     */
    List<Module> getModulesAsList();

    /**
     * Returns a <b>possibly-null</b> {@link Module Module} contained in the internal module map.
     * <br><p>This operation is very fast -- it only null-checks a parameter and does an O(1) HashMap {@code get}.</p>
     * @param name The <b>non-null</b> name of the {@link Module Module}.
     * @throws NullPointerException If {@code name} is null.
     * @return A <b>possibly-null</b> module from the internal module map.
     */
    @Nullable Module getModuleByName(@Nonnull String name);

    /**
     * Adds a new {@link Module Module} to the internal module map, overriding a pre-existing one if it has the same name.
     * <br><p>Note: This method <b>does NOT</b> start the module; it only adds it to a HashMap.
     * <br>Module designers who actually care about tracking their own state in their callbacks should initialize
     * themselves as being {@link State#DEAD} at construction until their {@link Module#onStart(String...)} method is called.</p>
     * @param module The <b>not-null</b> {@link Module Module} object.
     * @throws NullPointerException If {@code module} is actually null.
     */
    void addModule(@Nonnull Module module);

    /**
     * Internally calls the sendMessage overload and provides {@code null} for its {@code andThen} parameter.
     * <br><p>Note: Modules are <b>expected to handle their own state for every event that occurs. The ModuleLoader has no obligation to do this automatically.</b></p>
     * @param module The <b>not-null</b> {@link Module Module} to send a message to. <b>MUST</b> be in the {@link State#READY READY} state to accept messages.
     * @param args The <b>not-null and not-empty</b> arguments to pass to the {@link Module Module} for it to handle.
     * @throws NullPointerException If {@code module} or {@code args} are null.
     * @throws IllegalArgumentException If {@code args} has a length of 0.
     * @throws samophis.kunou.core.exceptions.ModuleException If the module does not belong to this loader, if it's not dead or if an uncaught exception occurs in the Module Thread.
     * @see ModuleLoader#sendMessage(Module, BiConsumer, String...)
     */
    void sendMessage(@Nonnull Module module, @Nonnull String... args);

    /**
     * Sends a message to a {@link Module Module} with possible follow-up code (due to the asynchronous nature of modules).
     * <br><p>Note: Modules are <b>expected to handle their own state for every event that occurs. The ModuleLoader has no obligation to do this automatically.</b></p>
     * @param module The <b>non-null</b> {@link Module Module} to send a message to. <b>MUST</b> be in the {@link State#READY READY} state to accept messages.
     * @param andThen The <b>possibly-null</b> follow-up code, executing in the same thread right after. Set this value to null for no code to run.
     * @param args The <b>not-null and not-empty</b> arguments to pass to the {@link Module Module} for it to handle.
     * @throws NullPointerException If {@code module} or {@code args} are null.
     * @throws IllegalArgumentException If {@code args} has a length of 0.
     * @throws samophis.kunou.core.exceptions.ModuleException If the module does not belong to this loader, if it's not dead or if an uncaught exception occurs in the Module Thread.
     * @see ModuleLoader#sendMessage(Module, String...)
     */
    void sendMessage(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen, @Nonnull String... args);

    /**
     * Starts a {@link Module Module} with possible follow-up code (due to the asynchronous nature of modules) and arguments.
     * <br><p>Note: Modules are <b>expected to handle their own state for every event that occurs. The ModuleLoader has no obligation to do this automatically.</b></p>
     * @param module The <b>non-null</b> {@link Module Module} to start. <b>MUST</b> be in the {@link State#DEAD DEAD} state to start again.
     * @param andThen The <b>possibly-null</b> follow-up code, executing in the same thread right after. Set this value to null for no code to run.
     * @param args The <b>possibly-null and possibly-empty</b> arguments to pass to the {@link Module Module} for it to handle.
     * @throws NullPointerException If {@code module} is null.
     * @throws IllegalArgumentException If {@code args} has a length of 0.
     * @throws samophis.kunou.core.exceptions.ModuleException If the module does not belong to this loader, if it's not dead or if an uncaught exception occurs in the Module Thread.
     * @see ModuleLoader#startModule(Module, BiConsumer)
     * @see ModuleLoader#startModule(Module)
     */
    void startModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen, @Nullable String... args);

    /**
     * Starts a {@link Module Module} with possible follow-up code (due to the asynchronous nature of modules) and no additional arguments.
     * <br><p>Note: Modules are <b>expected to handle their own state for every event that occurs. The ModuleLoader has no obligation to do this automatically.</b></p>
     * @param module The <b>non-null</b> {@link Module Module} to start. <b>MUST</b> be in the {@link State#DEAD DEAD} state to start again.
     * @param andThen The <b>possibly-null</b> follow-up code, executing in the same thread right after. Set this value to null for no code to run.
     * @throws NullPointerException If {@code module} is null.
     * @throws samophis.kunou.core.exceptions.ModuleException If the module does not belong to this loader, if it's not dead or if an uncaught exception occurs in the Module Thread.
     * @see ModuleLoader#startModule(Module, BiConsumer, String...)
     * @see ModuleLoader#startModule(Module)
     */
    void startModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen);

    /**
     * Starts a {@link Module Module} with no follow-up code or additional arguments.
     * <br><p>Note: Modules are <b>expected to handle their own state for every event that occurs. The ModuleLoader has no obligation to do this automatically.</b></p>
     * @param module The <b>non-null</b> {@link Module Module} to start. <b>MUST</b> be in the {@link State#DEAD DEAD} state to start again.
     * @throws NullPointerException If {@code module} is null.
     * @throws samophis.kunou.core.exceptions.ModuleException If the module does not belong to this loader, if it's not dead or if an uncaught exception occurs in the Module Thread.
     * @see ModuleLoader#startModule(Module, BiConsumer)
     * @see ModuleLoader#startModule(Module, BiConsumer, String...)
     */
    void startModule(@Nonnull Module module);

    /**
     * Kills a {@link Module Module} with no follow-up code.
     * <br><p>Note: Modules, when dead or shutting down, should properly set their {@link State State} so that the loader can manage it properly. It has no obligation to do this automatically.</p>
     * @param module The <b>non-null</b> {@link Module Module} to kill. <b>MUST</b> be in the {@link State#READY READY} <b>ONLY (NOT {@link State#STARTED STARTED}</b> before it can be killed.
     * @throws NullPointerException If {@code module} is null.
     * @throws samophis.kunou.core.exceptions.ModuleException If the module does not belong to this loader, if it's not ready or if an uncaught exception occurs in the Module Thread.
     * @see ModuleLoader#killModule(Module, BiConsumer)
     */
    void killModule(@Nonnull Module module);

    /**
     * Kills a {@link Module Module} with possible follow-up code.
     * <br><p>Note: Modules, when dead or shutting down, should properly set their {@link State State} so that the loader can manage it properly. It has no obligation to do this automatically.</p>
     * @param module The <b>non-null</b> {@link Module Module} to kill. <b>MUST</b> be in the {@link State#READY} <b>ONLY (NOT {@link State#STARTED STARTED}</b> before it can be killed.
     * @param andThen The <b>possibly-null</b> follow-up code to run in the exact same thread after the shutdown. Set to null for no follow-up code.
     * @throws NullPointerException If {@code module} is null.
     * @throws samophis.kunou.core.exceptions.ModuleException If the module does not belong to this loader, if it's not ready or if an uncaught exception occurs in the Module Thread.
     * @see ModuleLoader#killModule(Module)
     */
    void killModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen);

    /**
     * Fetches a new ModuleLoader instance -- is directly equivalent to the code: {@code new ModuleLoaderImpl()}
     * <br><p>You should keep any new ModuleLoaders few. The whole point of a ModuleLoader is to track and manage modules in general, not to spawn one module and multiple "cores".</p>
     * @return A brand new ModuleLoader instance.
     */
    static ModuleLoader newInstance() {
        return new ModuleLoaderImpl();
    }
}
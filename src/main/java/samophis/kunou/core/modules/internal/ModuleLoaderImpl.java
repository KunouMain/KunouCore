package samophis.kunou.core.modules.internal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.kunou.core.exceptions.ModuleException;
import samophis.kunou.core.modules.ModuleLoader;
import samophis.kunou.core.modules.Module;
import samophis.kunou.core.modules.Status;
import samophis.kunou.core.util.ModuleThreadExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ModuleLoaderImpl implements ModuleLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleLoaderImpl.class);
    private final Map<String, Module> modules;
    public ModuleLoaderImpl() {
        this.modules = new Object2ObjectOpenHashMap<>();
    }
    @Override
    public Map<String, Module> getModules() {
        return Collections.unmodifiableMap(modules);
    }
    @Override
    public List<Module> getModulesAsList() {
        return Collections.unmodifiableList((List<Module>) modules.values());
    }
    @Nullable
    @Override
    public Module getModuleByName(@Nonnull String name) {
        return modules.get(Objects.requireNonNull(name));
    }
    @Override
    public void addModule(@Nonnull Module module) {
        Objects.requireNonNull(module);
        modules.put(module.getName(), module);
    }
    @Override
    public void startModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen, @Nullable String... args) {
        Objects.requireNonNull(module);
        String name = module.getName();
        String version = module.getVersion();
        String author = module.getAuthor();
        if (modules.get(name) == null) {
            LOGGER.warn("{} {} by {} does not belong to this loader!", name, version, author);
            throw new ModuleException("Module does not belong to this loader!");
        }
        Status status = module.getStatus();
        String sName = status.name();
        switch (status) {
            case DEAD:
                ModuleThreadExecutor.runModuleMethod(() -> {
                    module.onStart(args);
                    if (andThen != null)
                        andThen.accept(this, module);
                }, false);
                break;
            case READY:
            case STARTED:
            case STARTING:
                LOGGER.warn("{} {} by {} is already ready or starting up! State = {}", name, version, author, sName);
                throw new IllegalStateException(String.format("State of %s %s by %s = %s!", name, version, author, sName));
            case SHUTTING_DOWN:
                LOGGER.warn("{} {} by {} is shutting down. Wait for it to properly shut down before re-starting it!", name, version, author);
                throw new IllegalStateException(String.format("State of %s %s by %s = %s!", name, version, author, sName));
        }
    }
    @Override
    public void startModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen) {
        startModule(module, andThen, (String) null);
    }
    @Override
    public void startModule(@Nonnull Module module) {
        startModule(module, null);
    }
    @Override
    public void killModule(@Nonnull Module module) {
        killModule(module, null);
    }
    @Override
    public void killModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen) {
        Objects.requireNonNull(module);
        String name = module.getName();
        String version = module.getVersion();
        String author = module.getAuthor();
        if (modules.get(name) == null) {
            LOGGER.warn("{} {} by {} does not belong to this loader!", name, version, author);
            throw new ModuleException("Module does not belong to this loader!");
        }
        Status status = module.getStatus();
        String sName = status.name();
        switch (status) {
            case READY:
                ModuleThreadExecutor.runModuleMethod(() -> {
                    module.onDeath();
                    if (andThen != null)
                        andThen.accept(this, module);
                }, true);
                break;
            default:
                LOGGER.warn("{} {} by {} is already dead or not ready to shutdown yet! State = {}", name, version, author, sName);
                throw new IllegalStateException(String.format("State of %s %s by %s = %s!", name, version, author, sName));
        }
    }
    @Override
    public void sendMessage(@Nonnull Module module, @Nonnull String... args) {
        sendMessage(module, null, args);
    }
    @Override
    public void sendMessage(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen, @Nonnull String... args) {
        Objects.requireNonNull(module);
        Objects.requireNonNull(args);
        String name = module.getName();
        String version = module.getVersion();
        String author = module.getAuthor();
        if (modules.get(name) == null) {
            LOGGER.warn("{} {} by {} does not belong to this loader!", name, version, author);
            throw new ModuleException("Module does not belong to this loader!");
        }
        Status status = module.getStatus();
        String sName = status.name();
        switch (status) {
            case READY:
                ModuleThreadExecutor.runModuleMethod(() -> {
                    module.onMessage(args);
                    if (andThen != null)
                        andThen.accept(this, module);
                }, false);
                break;
            default:
                LOGGER.warn("{} {} by {} is not ready to accept new messages! State = {}", name, version, author, sName);
                throw new IllegalStateException(String.format("State of %s %s by %s = %s!", name, version, author, sName));
        }
    }
}

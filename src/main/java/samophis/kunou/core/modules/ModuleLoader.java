package samophis.kunou.core.modules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public interface ModuleLoader {
    Map<String, Module> getModules();
    List<Module> getModulesAsList();
    @Nullable Module getModuleByName(@Nonnull String name);
    void addModule(@Nonnull Module module);
    void sendMessage(@Nonnull Module module, @Nonnull String... args);
    void sendMessage(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen, @Nonnull String... args);

    void startModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen, @Nullable String... args);
    void startModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen);
    void startModule(@Nonnull Module module);

    void killModule(@Nonnull Module module);
    void killModule(@Nonnull Module module, @Nullable BiConsumer<ModuleLoader, Module> andThen);
}
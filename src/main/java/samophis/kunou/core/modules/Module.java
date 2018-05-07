package samophis.kunou.core.modules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Module {
    ModuleLoader getLoader();
    String getName();
    String getVersion();
    String getAuthor();
    String getUrl();
    Status getStatus();
    void onStart(@Nullable String... args);
    void onMessage(@Nonnull String... args);
    void onDeath();
}
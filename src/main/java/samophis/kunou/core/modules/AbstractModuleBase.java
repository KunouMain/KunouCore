package samophis.kunou.core.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractModuleBase implements Module {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModuleBase.class);
    private final ModuleLoader loader;
    @SuppressWarnings("WeakerAccess")
    protected volatile Status status;
    protected AbstractModuleBase(@Nonnull ModuleLoader loader) {
        this.loader = Objects.requireNonNull(loader);
        this.status = Status.DEAD;
    }
    @Override
    public ModuleLoader getLoader() {
        return loader;
    }
    @Override
    public Status getStatus() {
        return status;
    }
    @Override
    public void onStart(@Nullable String... args) {
        status = Status.STARTED;
        LOGGER.info("Starting EMPTY {} {} by {} with initial arguments - {} on Thread: {}", getName(), getVersion(), getAuthor(), Arrays.toString(args), Thread.currentThread().getName());
        status = Status.READY;
    }
    @Override
    public void onMessage(@Nonnull String... args) {
        LOGGER.info("Sending {} to EMPTY {} {} by {} on Thread: {}", Arrays.toString(args), getName(), getVersion(), getAuthor(), Thread.currentThread().getName());
    }
    @Override
    public void onDeath() {
        status = Status.DEAD;
        LOGGER.info("EMPTY {} {} by {} on Thread: {} is dead!", getName(), getVersion(), getAuthor(), Thread.currentThread().getName());
    }
}

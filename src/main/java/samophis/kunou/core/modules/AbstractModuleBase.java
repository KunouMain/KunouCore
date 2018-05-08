package samophis.kunou.core.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

/**
 * A default, helper implementation to serve as a very basic {@link Module Module} Base.
 *
 * @since 0.1
 * @author SamOphis
 */

public abstract class AbstractModuleBase implements Module {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModuleBase.class);
    private final ModuleLoader loader;
    @SuppressWarnings("WeakerAccess")
    protected volatile State state;
    protected AbstractModuleBase(@Nonnull ModuleLoader loader) {
        this.loader = Objects.requireNonNull(loader);
        this.state = State.DEAD;
    }
    @Override
    public ModuleLoader getLoader() {
        return loader;
    }
    @Override
    public State getState() {
        return state;
    }
    @Override
    public void onStart(@Nullable String... args) {
        /* -- No need to cover all states if there's no distinction between them, which is why I'm setting it to READY without STARTED first -- */
        state = State.STARTING;
        LOGGER.info("Starting EMPTY {} {} by {} with initial arguments - {} on Thread: {}", getName(), getVersion(), getAuthor(), Arrays.toString(args), Thread.currentThread().getName());
        state = State.READY;
    }
    @Override
    public void onMessage(@Nonnull String... args) {
        LOGGER.info("Sending {} to EMPTY {} {} by {} on Thread: {}", Arrays.toString(args), getName(), getVersion(), getAuthor(), Thread.currentThread().getName());
    }
    @Override
    public void onDeath() {
        state = State.SHUTTING_DOWN;
        LOGGER.info("EMPTY {} {} by {} on Thread: {} is dead!", getName(), getVersion(), getAuthor(), Thread.currentThread().getName());
        state = State.DEAD;
    }
}

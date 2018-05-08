# KunouCore
[![](https://jitpack.io/v/KunouMain/KunouCore.svg)](https://jitpack.io/#KunouMain/KunouCore)

KunouCore is a fully-documented, production-ready and easy-to-use module system for better project structure, independence, performance and easier contributions. Every module is loaded and executed asynchronously in a cached thread pool where it manages its own state.

### Why Modular?

Dividing a project into a core or many, separate cores (what this is), that oversee and track different, isolated modules does many things for a big project. 

* Firstly, it allows changes to be made very easily with no large changes that affect other parts of a project.
* Secondly, at any time a module can be killed, rebooted, communicated with or swapped out with another completely different module. This allows users to run their own configurations of a project super easily by modifying literally one or two lines of code.
* Finally, having isolated parts that all do their own things on different threads with no dependence on references in other parts ensures no issues with concurrency, improves development speed and productivity. Editing one module doesn't impact another, and adding or removing others, again, doesn't have impacts on any other parts of the code. Changes in one module stay in that module.

### Why KunouCore?

KunouCore, although originally made to help manage my Kunou project, is completely independent of any Kunou code, and thus can be used in any project. It was made to allow easier contributions and project management, as well as to aid productivity. It wasn't a random piece of junk I steadily developed -- it's a product I produced for large, production-ready projects with real needs and real goals.

KunouCore is, as its title implies, just a core. You can make many cores with many different modules which can all run independently of each other. It's lightweight, small-ish, efficient, fully-documented and asynchronous.

### Adding KunouCore

KunouCore is available on JitPack [with additional releases here](https://github.com/KunouMain/KunouCore/releases). In a standard GitHub KunouCore release, there is a sources .jar and a full .jar with dependencies bundled.

To add it via Gradle:
```gradle
repositories {
    jcenter()
    maven {url 'https://jitpack.io'}
}

dependencies {
    compile 'com.github.KunouMain:KunouCore:v0.1'
}
```

### Using KunouCore

Simply create a `ModuleLoader` instance by calling the static method `ModuleLoader.newInstance()`. You can then add modules simply by calling `ModuleLoader#addModule(Module)`. A "module" has to extend the `Module` interface, and a helper class is provided too (`AbstractModuleBase`).

### Creating Modules

Modules are expected to manage their `State` themselves, and a basic, self-explanatory example of this is provided in the `AbstractModuleBase` class and also documented in the `Module` interface. Here's an example:

```java
public class ExampleModule extends AbstractModuleBase {
    public static ExampleModule getInstance(@Nonnull ModuleLoader loader) {
        return new ExampleModule(loader); /* -- this is just a style guideline, doesn't have to be done -- */
    }
    private ExampleModule(@Nonnull ModuleLoader loader) {
        super(loader); /* -- calling AbstractModuleBase constructor, which sets state as DEAD, verifies loader isn't null and sets it -- */
        System.out.printf("Creation of %s %s by %s!\n", getName(), getVersion(), getAuthor());
        /* -- absolutely NO blocking or intensive or long code should go here. only logging or very simple, short actions -- */
        /* -- anything executed here is executed on the calling thread (usually by the core directly, defeating the modular purpose) -- */
    }
    @Override
    public String getName() {
        return "Example Module"; /* -- name ends with Module -- */
    }
    @Override
    public String getVersion() {
        return "v0.1-alpha"; /* -- using semantic versioning, github-style with preceding 'v' -- */
    }
    @Override
    public String getAuthor() {
        return "SamOphis"; /* -- author should be single or a space + comma separated list -- */
    }
    @Override
    public String getUrl() {
        return ""; /* -- you can leave this empty: it's never called defaultly in KunouCore and is just there to identify -- */
    }
    /* -- AbstractModuleBase already has default implementations for this. This is just to document. -- */
    @Override
    public void onStart(@Nullable String... args) {
        /* -- 'state' is a protected, volatile variable from AbstractModuleBase -- */
        /* -- STARTING means 'just started up' -- */
        /* -- we can skip certain stages if nothing happens between them! this purposely uses variables to demonstrate states. -- */
        state = State.STARTING;
        String arguments = Arrays.toString(args);
        String threadName = Thread.currentThread().getName();
        String moduleName = getName();
        String moduleVersion = getVersion();
        String moduleAuthor = getAuthor();
        /* -- STARTED means 'got initial info, nearly ready!' -- */
        state = State.STARTED;
        System.out.printf("Starting up %s %s by %s with Arguments: %s on Thread: %s\n", moduleName, moduleVersion, moduleAuthor, arguments, threadName);
        state = State.READY;
    }
    @Override
    public void onMessage(@Nonnull String... args) {
        /* -- at this point, module is guaranteed to be in the READY state with a not-null and not-zero-length array of args -- */
        /* -- remember, we're also in a multi-threaded environment. make sure any stored info or updates watch out for this! -- */
        System.out.printf("Received %s on %s, Thread: %s", Arrays.toString(args), getName(), Thread.currentThread().getName());
        throw new RuntimeException("just a test!");
        /* -- any uncaught exceptions are captured, wrapped in a ModuleException and thrown back to the caller after being logged -- */
        /* -- the module still continues though, as it's not DEAD yet and the code here is isolated from other parts in a different thread -- */
    }
    @Override
    public void onDeath() {
        /* -- this is where we gracefully shutdown ALL active parts of our module, ready to possibly be re-started later -- */
        /* -- if we have a connection, a loop, or something else, this is our chance to shut it down nicely before SIGKILL -- */
        state = State.SHUTTING_DOWN;
        System.out.printf("%s is now shutting down, Thread: %s", getName(), Thread.currentThread().getName());
        state = State.DEAD;
        /* -- at this point, this module is considered dead. nothing in this module is running at all, not active and gone. -- */
    }
}
```

After this, we can then run it as so:

```java
/* -- check out all the overloads for the startModule/sendMessage/killModule methods -- */
/* -- we should preferably use one core and add many modules! there's almost never a need for more than one moduleloader! save this variable! -- */
ModuleLoader loader = ModuleLoader.getInstance();
ExampleModule example = ExampleModule.getInstance(loader);
loader.addModule(example);
loader.startModule(example, (ldr, mod) -> {
    ldr.sendMessage(mod, (ignored, ignored2) -> ldr.killModule(mod), "now", "killing", "the", "module");
}
/* -- example of callbacks to ENSURE proper order, executed on same thread right after original module code -- */
/* -- remember, every event has the possibility of being executed on different threads. loader checks state and ownership of module before sending events! -- */
/* -- so, if no callback is used there's a chance "later" actions can be executed earlier, explaining the need for callbacks right here! -- */
```

And at **any** point, we can easily shutdown modules, replace them with others, add more, send messages to each and every one of them, etc.

# Contributions

Contributions are **very welcome** as long as they match my general coding style, have documentation provided and have been tested! More complex examples of real, production-quality modules are steadily being developed and uploaded as part of my Kunou Project. Check them out and possibly help make some more -- or even self-host a copy of Kunou with your own mix of modules! The choice is yours, and I hope you enjoy your time with this project.
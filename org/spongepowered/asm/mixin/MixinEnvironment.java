//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin;

import org.spongepowered.asm.util.perf.*;
import org.spongepowered.asm.obfuscation.*;
import org.spongepowered.asm.mixin.throwables.*;
import org.spongepowered.asm.launch.*;
import org.spongepowered.asm.mixin.extensibility.*;
import java.util.*;
import org.spongepowered.asm.mixin.transformer.*;
import org.spongepowered.asm.service.*;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.*;
import com.google.common.collect.*;
import org.spongepowered.asm.util.*;

public final class MixinEnvironment implements ITokenProvider
{
    private static final Set excludeTransformers;
    private static MixinEnvironment currentEnvironment;
    private static Phase currentPhase;
    private static CompatibilityLevel compatibility;
    private static boolean showHeader;
    private static final Logger logger;
    private static final Profiler profiler;
    private final IMixinService service;
    private final Phase phase;
    private final String configsKey;
    private final boolean[] options;
    private final Set tokenProviderClasses;
    private final List tokenProviders;
    private final Map internalTokens;
    private final RemapperChain remappers;
    private Side side;
    private List transformers;
    private String obfuscationContext;
    
    MixinEnvironment(final Phase phase) {
        this.tokenProviderClasses = new HashSet();
        this.tokenProviders = new ArrayList();
        this.internalTokens = new HashMap();
        this.remappers = new RemapperChain();
        this.obfuscationContext = null;
        this.service = MixinService.getService();
        this.phase = phase;
        this.configsKey = "mixin.configs." + this.phase.name.toLowerCase();
        final String version = this.getVersion();
        if (version == null || !"0.7.11".equals(version)) {
            throw new MixinException("Environment conflict, mismatched versions or you didn't call MixinBootstrap.init()");
        }
        this.service.checkEnv(this);
        this.options = new boolean[Option.values().length];
        for (final Option option : Option.values()) {
            this.options[option.ordinal()] = option.getBooleanValue();
        }
        if (MixinEnvironment.showHeader) {
            MixinEnvironment.showHeader = false;
            this.printHeader(version);
        }
    }
    
    private void printHeader(final Object o) {
        final String codeSource = this.getCodeSource();
        final String name = this.service.getName();
        final Side side = this.getSide();
        MixinEnvironment.logger.info("SpongePowered MIXIN Subsystem Version={} Source={} Service={} Env={}", new Object[] { o, codeSource, name, side });
        final boolean option = this.getOption(Option.DEBUG_VERBOSE);
        if (option || this.getOption(Option.DEBUG_EXPORT) || this.getOption(Option.DEBUG_PROFILER)) {
            final PrettyPrinter prettyPrinter = new PrettyPrinter(32);
            prettyPrinter.add("SpongePowered MIXIN%s", option ? " (Verbose debugging enabled)" : "").centre().hr();
            prettyPrinter.kv("Code source", (Object)codeSource);
            prettyPrinter.kv("Internal Version", o);
            prettyPrinter.kv("Java 8 Supported", CompatibilityLevel.JAVA_8.isSupported()).hr();
            prettyPrinter.kv("Service Name", (Object)name);
            prettyPrinter.kv("Service Class", (Object)this.service.getClass().getName()).hr();
            for (final Option option2 : Option.values()) {
                final StringBuilder sb = new StringBuilder();
                for (int j = 0; j < option2.depth; ++j) {
                    sb.append("- ");
                }
                prettyPrinter.kv(option2.property, "%s<%s>", sb, option2);
            }
            prettyPrinter.hr().kv("Detected Side", side);
            prettyPrinter.print(System.err);
        }
    }
    
    private String getCodeSource() {
        try {
            return this.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        }
        catch (Throwable t) {
            return "Unknown";
        }
    }
    
    public Phase getPhase() {
        return this.phase;
    }
    
    @Deprecated
    public List getMixinConfigs() {
        List list = (List)GlobalProperties.get(this.configsKey);
        if (list == null) {
            list = new ArrayList();
            GlobalProperties.put(this.configsKey, (Object)list);
        }
        return list;
    }
    
    @Deprecated
    public MixinEnvironment addConfiguration(final String s) {
        MixinEnvironment.logger.warn("MixinEnvironment::addConfiguration is deprecated and will be removed. Use Mixins::addConfiguration instead!");
        Mixins.addConfiguration(s, this);
        return this;
    }
    
    void registerConfig(final String s) {
        final List mixinConfigs = this.getMixinConfigs();
        if (!mixinConfigs.contains(s)) {
            mixinConfigs.add(s);
        }
    }
    
    @Deprecated
    public MixinEnvironment registerErrorHandlerClass(final String s) {
        Mixins.registerErrorHandlerClass(s);
        return this;
    }
    
    public MixinEnvironment registerTokenProviderClass(final String s) {
        if (!this.tokenProviderClasses.contains(s)) {
            try {
                this.registerTokenProvider(this.service.getClassProvider().findClass(s, true).newInstance());
            }
            catch (Throwable t) {
                MixinEnvironment.logger.error("Error instantiating " + s, t);
            }
        }
        return this;
    }
    
    public MixinEnvironment registerTokenProvider(final IEnvironmentTokenProvider environmentTokenProvider) {
        if (environmentTokenProvider != null && !this.tokenProviderClasses.contains(environmentTokenProvider.getClass().getName())) {
            final String name = environmentTokenProvider.getClass().getName();
            final TokenProviderWrapper tokenProviderWrapper = new TokenProviderWrapper(environmentTokenProvider, this);
            MixinEnvironment.logger.info("Adding new token provider {} to {}", new Object[] { name, this });
            this.tokenProviders.add(tokenProviderWrapper);
            this.tokenProviderClasses.add(name);
            Collections.sort((List<Comparable>)this.tokenProviders);
        }
        return this;
    }
    
    @Override
    public Integer getToken(String upperCase) {
        upperCase = upperCase.toUpperCase();
        final Iterator<TokenProviderWrapper> iterator = this.tokenProviders.iterator();
        while (iterator.hasNext()) {
            final Integer token = iterator.next().getToken(upperCase);
            if (token != null) {
                return token;
            }
        }
        return this.internalTokens.get(upperCase);
    }
    
    @Deprecated
    public Set getErrorHandlerClasses() {
        return Mixins.getErrorHandlerClasses();
    }
    
    public Object getActiveTransformer() {
        return GlobalProperties.get("mixin.transformer");
    }
    
    public void setActiveTransformer(final ITransformer transformer) {
        if (transformer != null) {
            GlobalProperties.put("mixin.transformer", (Object)transformer);
        }
    }
    
    public MixinEnvironment setSide(final Side side) {
        if (side != null && this.getSide() == Side.UNKNOWN && side != Side.UNKNOWN) {
            this.side = side;
        }
        return this;
    }
    
    public Side getSide() {
        if (this.side == null) {
            for (final Side side : Side.values()) {
                if (side.detect()) {
                    this.side = side;
                    break;
                }
            }
        }
        return (this.side != null) ? this.side : Side.UNKNOWN;
    }
    
    public String getVersion() {
        return (String)GlobalProperties.get("mixin.initialised");
    }
    
    public boolean getOption(final Option option) {
        return this.options[option.ordinal()];
    }
    
    public void setOption(final Option option, final boolean b) {
        this.options[option.ordinal()] = b;
    }
    
    public String getOptionValue(final Option option) {
        return option.getStringValue();
    }
    
    public Enum getOption(final Option option, final Enum enum1) {
        return option.getEnumValue(enum1);
    }
    
    public void setObfuscationContext(final String obfuscationContext) {
        this.obfuscationContext = obfuscationContext;
    }
    
    public String getObfuscationContext() {
        return this.obfuscationContext;
    }
    
    public String getRefmapObfuscationContext() {
        final String stringValue = Option.OBFUSCATION_TYPE.getStringValue();
        if (stringValue != null) {
            return stringValue;
        }
        return this.obfuscationContext;
    }
    
    public RemapperChain getRemappers() {
        return this.remappers;
    }
    
    public void audit() {
        final Object activeTransformer = this.getActiveTransformer();
        if (activeTransformer instanceof MixinTransformer) {
            ((MixinTransformer)activeTransformer).audit(this);
        }
    }
    
    public List getTransformers() {
        if (this.transformers == null) {
            this.buildTransformerDelegationList();
        }
        return Collections.unmodifiableList((List<?>)this.transformers);
    }
    
    public void addTransformerExclusion(final String s) {
        MixinEnvironment.excludeTransformers.add(s);
        this.transformers = null;
    }
    
    private void buildTransformerDelegationList() {
        MixinEnvironment.logger.debug("Rebuilding transformer delegation list:");
        this.transformers = new ArrayList();
        for (final ITransformer transformer : this.service.getTransformers()) {
            if (!(transformer instanceof ILegacyClassTransformer)) {
                continue;
            }
            final ILegacyClassTransformer legacyClassTransformer = (ILegacyClassTransformer)transformer;
            final String name = legacyClassTransformer.getName();
            boolean b = true;
            final Iterator<String> iterator2 = (Iterator<String>)MixinEnvironment.excludeTransformers.iterator();
            while (iterator2.hasNext()) {
                if (name.contains(iterator2.next())) {
                    b = false;
                    break;
                }
            }
            if (b && !legacyClassTransformer.isDelegationExcluded()) {
                MixinEnvironment.logger.debug("  Adding:    {}", new Object[] { name });
                this.transformers.add(legacyClassTransformer);
            }
            else {
                MixinEnvironment.logger.debug("  Excluding: {}", new Object[] { name });
            }
        }
        MixinEnvironment.logger.debug("Transformer delegation list created with {} entries", new Object[] { this.transformers.size() });
    }
    
    @Override
    public String toString() {
        return String.format("%s[%s]", this.getClass().getSimpleName(), this.phase);
    }
    
    private static Phase getCurrentPhase() {
        if (MixinEnvironment.currentPhase == Phase.NOT_INITIALISED) {
            init(Phase.PREINIT);
        }
        return MixinEnvironment.currentPhase;
    }
    
    public static void init(final Phase currentPhase) {
        if (MixinEnvironment.currentPhase == Phase.NOT_INITIALISED) {
            MixinEnvironment.currentPhase = currentPhase;
            getProfiler().setActive(getEnvironment(currentPhase).getOption(Option.DEBUG_PROFILER));
            MixinLogWatcher.begin();
        }
    }
    
    public static MixinEnvironment getEnvironment(final Phase phase) {
        if (phase == null) {
            return Phase.DEFAULT.getEnvironment();
        }
        return phase.getEnvironment();
    }
    
    public static MixinEnvironment getDefaultEnvironment() {
        return getEnvironment(Phase.DEFAULT);
    }
    
    public static MixinEnvironment getCurrentEnvironment() {
        if (MixinEnvironment.currentEnvironment == null) {
            MixinEnvironment.currentEnvironment = getEnvironment(getCurrentPhase());
        }
        return MixinEnvironment.currentEnvironment;
    }
    
    public static CompatibilityLevel getCompatibilityLevel() {
        return MixinEnvironment.compatibility;
    }
    
    @Deprecated
    public static void setCompatibilityLevel(final CompatibilityLevel compatibility) throws IllegalArgumentException {
        if (!"org.spongepowered.asm.mixin.transformer.MixinConfig".equals(Thread.currentThread().getStackTrace()[2].getClassName())) {
            MixinEnvironment.logger.warn("MixinEnvironment::setCompatibilityLevel is deprecated and will be removed. Set level via config instead!");
        }
        if (compatibility != MixinEnvironment.compatibility && compatibility.isAtLeast(MixinEnvironment.compatibility)) {
            if (!compatibility.isSupported()) {
                throw new IllegalArgumentException("The requested compatibility level " + compatibility + " could not be set. Level is not supported");
            }
            MixinEnvironment.compatibility = compatibility;
            MixinEnvironment.logger.info("Compatibility level set to {}", new Object[] { compatibility });
        }
    }
    
    public static Profiler getProfiler() {
        return MixinEnvironment.profiler;
    }
    
    static void gotoPhase(final Phase currentPhase) {
        if (currentPhase == null || currentPhase.ordinal < 0) {
            throw new IllegalArgumentException("Cannot go to the specified phase, phase is null or invalid");
        }
        if (currentPhase.ordinal > getCurrentPhase().ordinal) {
            MixinService.getService().beginPhase();
        }
        if (currentPhase == Phase.DEFAULT) {
            MixinLogWatcher.end();
        }
        MixinEnvironment.currentPhase = currentPhase;
        MixinEnvironment.currentEnvironment = getEnvironment(getCurrentPhase());
    }
    
    static {
        excludeTransformers = Sets.newHashSet((Object[])new String[] { "net.minecraftforge.fml.common.asm.transformers.EventSubscriptionTransformer", "cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer", "net.minecraftforge.fml.common.asm.transformers.TerminalTransformer", "cpw.mods.fml.common.asm.transformers.TerminalTransformer" });
        MixinEnvironment.currentPhase = Phase.NOT_INITIALISED;
        MixinEnvironment.compatibility = (CompatibilityLevel)Option.DEFAULT_COMPATIBILITY_LEVEL.getEnumValue(CompatibilityLevel.JAVA_6);
        MixinEnvironment.showHeader = true;
        logger = LogManager.getLogger("mixin");
        profiler = new Profiler();
    }
    
    static class MixinLogWatcher
    {
        static MixinAppender appender;
        static org.apache.logging.log4j.core.Logger log;
        static Level oldLevel;
        
        static void begin() {
            final Logger logger = LogManager.getLogger("FML");
            if (!(logger instanceof org.apache.logging.log4j.core.Logger)) {
                return;
            }
            MixinLogWatcher.log = (org.apache.logging.log4j.core.Logger)logger;
            MixinLogWatcher.oldLevel = MixinLogWatcher.log.getLevel();
            MixinLogWatcher.appender.start();
            MixinLogWatcher.log.addAppender((Appender)MixinLogWatcher.appender);
            MixinLogWatcher.log.setLevel(Level.ALL);
        }
        
        static void end() {
            if (MixinLogWatcher.log != null) {
                MixinLogWatcher.log.removeAppender((Appender)MixinLogWatcher.appender);
            }
        }
        
        static {
            MixinLogWatcher.appender = new MixinAppender();
            MixinLogWatcher.oldLevel = null;
        }
        
        static class MixinAppender extends AbstractAppender
        {
            MixinAppender() {
                super("MixinLogWatcherAppender", (Filter)null, (Layout)null);
            }
            
            public void append(final LogEvent logEvent) {
                if (logEvent.getLevel() != Level.DEBUG || !"Validating minecraft".equals(logEvent.getMessage().getFormattedMessage())) {
                    return;
                }
                MixinEnvironment.gotoPhase(Phase.INIT);
                if (MixinLogWatcher.log.getLevel() == Level.ALL) {
                    MixinLogWatcher.log.setLevel(MixinLogWatcher.oldLevel);
                }
            }
        }
    }
    
    public static final class Phase
    {
        static final Phase NOT_INITIALISED;
        public static final Phase PREINIT;
        public static final Phase INIT;
        public static final Phase DEFAULT;
        static final List phases;
        final int ordinal;
        final String name;
        private MixinEnvironment environment;
        
        private Phase(final int ordinal, final String name) {
            this.ordinal = ordinal;
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static Phase forName(final String s) {
            for (final Phase phase : Phase.phases) {
                if (phase.name.equals(s)) {
                    return phase;
                }
            }
            return null;
        }
        
        MixinEnvironment getEnvironment() {
            if (this.ordinal < 0) {
                throw new IllegalArgumentException("Cannot access the NOT_INITIALISED environment");
            }
            if (this.environment == null) {
                this.environment = new MixinEnvironment(this);
            }
            return this.environment;
        }
        
        static {
            NOT_INITIALISED = new Phase(-1, "NOT_INITIALISED");
            PREINIT = new Phase(0, "PREINIT");
            INIT = new Phase(1, "INIT");
            DEFAULT = new Phase(2, "DEFAULT");
            phases = (List)ImmutableList.of((Object)Phase.PREINIT, (Object)Phase.INIT, (Object)Phase.DEFAULT);
        }
    }
    
    static class TokenProviderWrapper implements Comparable
    {
        private static int nextOrder;
        private final int priority;
        private final int order;
        private final IEnvironmentTokenProvider provider;
        private final MixinEnvironment environment;
        
        public TokenProviderWrapper(final IEnvironmentTokenProvider provider, final MixinEnvironment environment) {
            this.provider = provider;
            this.environment = environment;
            this.order = TokenProviderWrapper.nextOrder++;
            this.priority = provider.getPriority();
        }
        
        public int compareTo(final TokenProviderWrapper tokenProviderWrapper) {
            if (tokenProviderWrapper == null) {
                return 0;
            }
            if (tokenProviderWrapper.priority == this.priority) {
                return tokenProviderWrapper.order - this.order;
            }
            return tokenProviderWrapper.priority - this.priority;
        }
        
        public IEnvironmentTokenProvider getProvider() {
            return this.provider;
        }
        
        Integer getToken(final String s) {
            return this.provider.getToken(s, this.environment);
        }
        
        @Override
        public int compareTo(final Object o) {
            return this.compareTo((TokenProviderWrapper)o);
        }
        
        static {
            TokenProviderWrapper.nextOrder = 0;
        }
    }
    
    public enum CompatibilityLevel
    {
        JAVA_6("JAVA_6", 0, 6, 50, false), 
        JAVA_7(7, 51, false) {
            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 1.7;
            }
        }, 
        JAVA_8(8, 52, true) {
            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 1.8;
            }
        }, 
        JAVA_9(9, 53, true) {
            @Override
            boolean isSupported() {
                return false;
            }
        };
        
        private static final int CLASS_V1_9 = 53;
        private final int ver;
        private final int classVersion;
        private final boolean supportsMethodsInInterfaces;
        private CompatibilityLevel maxCompatibleLevel;
        private static final CompatibilityLevel[] $VALUES;
        
        private CompatibilityLevel(final String s, final int n, final int ver, final int classVersion, final boolean supportsMethodsInInterfaces) {
            this.ver = ver;
            this.classVersion = classVersion;
            this.supportsMethodsInInterfaces = supportsMethodsInInterfaces;
        }
        
        private void setMaxCompatibleLevel(final CompatibilityLevel maxCompatibleLevel) {
            this.maxCompatibleLevel = maxCompatibleLevel;
        }
        
        boolean isSupported() {
            return true;
        }
        
        public int classVersion() {
            return this.classVersion;
        }
        
        public boolean supportsMethodsInInterfaces() {
            return this.supportsMethodsInInterfaces;
        }
        
        public boolean isAtLeast(final CompatibilityLevel compatibilityLevel) {
            return compatibilityLevel == null || this.ver >= compatibilityLevel.ver;
        }
        
        public boolean canElevateTo(final CompatibilityLevel compatibilityLevel) {
            return compatibilityLevel == null || this.maxCompatibleLevel == null || compatibilityLevel.ver <= this.maxCompatibleLevel.ver;
        }
        
        public boolean canSupport(final CompatibilityLevel compatibilityLevel) {
            return compatibilityLevel == null || compatibilityLevel.canElevateTo(this);
        }
        
        CompatibilityLevel(final String s, final int n, final int n2, final int n3, final boolean b, final MixinEnvironment$1 object) {
            this(s, n, n2, n3, b);
        }
        
        static {
            $VALUES = new CompatibilityLevel[] { CompatibilityLevel.JAVA_6, CompatibilityLevel.JAVA_7, CompatibilityLevel.JAVA_8, CompatibilityLevel.JAVA_9 };
        }
    }
    
    public enum Option
    {
        DEBUG_ALL("DEBUG_ALL", 0, "debug"), 
        DEBUG_EXPORT("DEBUG_EXPORT", 1, Option.DEBUG_ALL, "export"), 
        DEBUG_EXPORT_FILTER("DEBUG_EXPORT_FILTER", 2, Option.DEBUG_EXPORT, "filter", false), 
        DEBUG_EXPORT_DECOMPILE("DEBUG_EXPORT_DECOMPILE", 3, Option.DEBUG_EXPORT, Inherit.ALLOW_OVERRIDE, "decompile"), 
        DEBUG_EXPORT_DECOMPILE_THREADED("DEBUG_EXPORT_DECOMPILE_THREADED", 4, Option.DEBUG_EXPORT_DECOMPILE, Inherit.ALLOW_OVERRIDE, "async"), 
        DEBUG_EXPORT_DECOMPILE_MERGESIGNATURES("DEBUG_EXPORT_DECOMPILE_MERGESIGNATURES", 5, Option.DEBUG_EXPORT_DECOMPILE, Inherit.ALLOW_OVERRIDE, "mergeGenericSignatures"), 
        DEBUG_VERIFY("DEBUG_VERIFY", 6, Option.DEBUG_ALL, "verify"), 
        DEBUG_VERBOSE("DEBUG_VERBOSE", 7, Option.DEBUG_ALL, "verbose"), 
        DEBUG_INJECTORS("DEBUG_INJECTORS", 8, Option.DEBUG_ALL, "countInjections"), 
        DEBUG_STRICT("DEBUG_STRICT", 9, Option.DEBUG_ALL, Inherit.INDEPENDENT, "strict"), 
        DEBUG_UNIQUE("DEBUG_UNIQUE", 10, Option.DEBUG_STRICT, "unique"), 
        DEBUG_TARGETS("DEBUG_TARGETS", 11, Option.DEBUG_STRICT, "targets"), 
        DEBUG_PROFILER("DEBUG_PROFILER", 12, Option.DEBUG_ALL, Inherit.ALLOW_OVERRIDE, "profiler"), 
        DUMP_TARGET_ON_FAILURE("DUMP_TARGET_ON_FAILURE", 13, "dumpTargetOnFailure"), 
        CHECK_ALL("CHECK_ALL", 14, "checks"), 
        CHECK_IMPLEMENTS("CHECK_IMPLEMENTS", 15, Option.CHECK_ALL, "interfaces"), 
        CHECK_IMPLEMENTS_STRICT("CHECK_IMPLEMENTS_STRICT", 16, Option.CHECK_IMPLEMENTS, Inherit.ALLOW_OVERRIDE, "strict"), 
        IGNORE_CONSTRAINTS("IGNORE_CONSTRAINTS", 17, "ignoreConstraints"), 
        HOT_SWAP("HOT_SWAP", 18, "hotSwap"), 
        ENVIRONMENT("ENVIRONMENT", 19, Inherit.ALWAYS_FALSE, "env"), 
        OBFUSCATION_TYPE("OBFUSCATION_TYPE", 20, Option.ENVIRONMENT, Inherit.ALWAYS_FALSE, "obf"), 
        DISABLE_REFMAP("DISABLE_REFMAP", 21, Option.ENVIRONMENT, Inherit.INDEPENDENT, "disableRefMap"), 
        REFMAP_REMAP("REFMAP_REMAP", 22, Option.ENVIRONMENT, Inherit.INDEPENDENT, "remapRefMap"), 
        REFMAP_REMAP_RESOURCE("REFMAP_REMAP_RESOURCE", 23, Option.ENVIRONMENT, Inherit.INDEPENDENT, "refMapRemappingFile", ""), 
        REFMAP_REMAP_SOURCE_ENV("REFMAP_REMAP_SOURCE_ENV", 24, Option.ENVIRONMENT, Inherit.INDEPENDENT, "refMapRemappingEnv", "searge"), 
        REFMAP_REMAP_ALLOW_PERMISSIVE("REFMAP_REMAP_ALLOW_PERMISSIVE", 25, Option.ENVIRONMENT, Inherit.INDEPENDENT, "allowPermissiveMatch", true, "true"), 
        IGNORE_REQUIRED("IGNORE_REQUIRED", 26, Option.ENVIRONMENT, Inherit.INDEPENDENT, "ignoreRequired"), 
        DEFAULT_COMPATIBILITY_LEVEL("DEFAULT_COMPATIBILITY_LEVEL", 27, Option.ENVIRONMENT, Inherit.INDEPENDENT, "compatLevel"), 
        SHIFT_BY_VIOLATION_BEHAVIOUR("SHIFT_BY_VIOLATION_BEHAVIOUR", 28, Option.ENVIRONMENT, Inherit.INDEPENDENT, "shiftByViolation", "warn"), 
        INITIALISER_INJECTION_MODE("INITIALISER_INJECTION_MODE", 29, "initialiserInjectionMode", "default");
        
        private static final String PREFIX = "mixin";
        final Option parent;
        final Inherit inheritance;
        final String property;
        final String defaultValue;
        final boolean isFlag;
        final int depth;
        private static final Option[] $VALUES;
        
        private Option(final String s, final int n, final String s2) {
            this(s, n, null, s2, true);
        }
        
        private Option(final String s, final int n, final Inherit inherit, final String s2) {
            this(s, n, null, inherit, s2, true);
        }
        
        private Option(final String s, final int n, final String s2, final boolean b) {
            this(s, n, null, s2, b);
        }
        
        private Option(final String s, final int n, final String s2, final String s3) {
            this(s, n, null, Inherit.INDEPENDENT, s2, false, s3);
        }
        
        private Option(final String s, final int n, final Option option, final String s2) {
            this(s, n, option, Inherit.INHERIT, s2, true);
        }
        
        private Option(final String s, final int n, final Option option, final Inherit inherit, final String s2) {
            this(s, n, option, inherit, s2, true);
        }
        
        private Option(final String s, final int n, final Option option, final String s2, final boolean b) {
            this(s, n, option, Inherit.INHERIT, s2, b, null);
        }
        
        private Option(final String s, final int n, final Option option, final Inherit inherit, final String s2, final boolean b) {
            this(s, n, option, inherit, s2, b, null);
        }
        
        private Option(final String s, final int n, final Option option, final String s2, final String s3) {
            this(s, n, option, Inherit.INHERIT, s2, false, s3);
        }
        
        private Option(final String s, final int n, final Option option, final Inherit inherit, final String s2, final String s3) {
            this(s, n, option, inherit, s2, false, s3);
        }
        
        private Option(final String s, final int n, Option parent, final Inherit inheritance, final String s2, final boolean isFlag, final String defaultValue) {
            this.parent = parent;
            this.inheritance = inheritance;
            this.property = ((parent != null) ? parent.property : "mixin") + "." + s2;
            this.defaultValue = defaultValue;
            this.isFlag = isFlag;
            int depth;
            for (depth = 0; parent != null; parent = parent.parent, ++depth) {}
            this.depth = depth;
        }
        
        Option getParent() {
            return this.parent;
        }
        
        String getProperty() {
            return this.property;
        }
        
        @Override
        public String toString() {
            return this.isFlag ? String.valueOf(this.getBooleanValue()) : this.getStringValue();
        }
        
        private boolean getLocalBooleanValue(final boolean b) {
            return Boolean.parseBoolean(System.getProperty(this.property, Boolean.toString(b)));
        }
        
        final String getStringValue() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        org/spongepowered/asm/mixin/MixinEnvironment$Option.inheritance:Lorg/spongepowered/asm/mixin/MixinEnvironment$Option$Inherit;
            //     4: getstatic       org/spongepowered/asm/mixin/MixinEnvironment$Option$Inherit.INDEPENDENT:Lorg/spongepowered/asm/mixin/MixinEnvironment$Option$Inherit;
            //     7: if_acmpeq       24
            //    10: aload_0        
            //    11: getfield        org/spongepowered/asm/mixin/MixinEnvironment$Option.parent:Lorg/spongepowered/asm/mixin/MixinEnvironment$Option;
            //    14: ifnull          24
            //    17: aload_0        
            //    18: getfield        org/spongepowered/asm/mixin/MixinEnvironment$Option.parent:Lorg/spongepowered/asm/mixin/MixinEnvironment$Option;
            //    21: if_acmpne       38
            //    24: aload_0        
            //    25: getfield        org/spongepowered/asm/mixin/MixinEnvironment$Option.property:Ljava/lang/String;
            //    28: aload_0        
            //    29: getfield        org/spongepowered/asm/mixin/MixinEnvironment$Option.defaultValue:Ljava/lang/String;
            //    32: invokestatic    java/lang/System.getProperty:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
            //    35: goto            42
            //    38: aload_0        
            //    39: getfield        org/spongepowered/asm/mixin/MixinEnvironment$Option.defaultValue:Ljava/lang/String;
            //    42: areturn        
            // 
            // The error that occurred was:
            // 
            // java.lang.ArrayIndexOutOfBoundsException
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        Enum getEnumValue(final Enum enum1) {
            final String property = System.getProperty(this.property, enum1.name());
            try {
                return (Enum)Enum.valueOf(enum1.getClass(), property.toUpperCase());
            }
            catch (IllegalArgumentException ex) {
                return enum1;
            }
        }
        
        static {
            $VALUES = new Option[] { Option.DEBUG_ALL, Option.DEBUG_EXPORT, Option.DEBUG_EXPORT_FILTER, Option.DEBUG_EXPORT_DECOMPILE, Option.DEBUG_EXPORT_DECOMPILE_THREADED, Option.DEBUG_EXPORT_DECOMPILE_MERGESIGNATURES, Option.DEBUG_VERIFY, Option.DEBUG_VERBOSE, Option.DEBUG_INJECTORS, Option.DEBUG_STRICT, Option.DEBUG_UNIQUE, Option.DEBUG_TARGETS, Option.DEBUG_PROFILER, Option.DUMP_TARGET_ON_FAILURE, Option.CHECK_ALL, Option.CHECK_IMPLEMENTS, Option.CHECK_IMPLEMENTS_STRICT, Option.IGNORE_CONSTRAINTS, Option.HOT_SWAP, Option.ENVIRONMENT, Option.OBFUSCATION_TYPE, Option.DISABLE_REFMAP, Option.REFMAP_REMAP, Option.REFMAP_REMAP_RESOURCE, Option.REFMAP_REMAP_SOURCE_ENV, Option.REFMAP_REMAP_ALLOW_PERMISSIVE, Option.IGNORE_REQUIRED, Option.DEFAULT_COMPATIBILITY_LEVEL, Option.SHIFT_BY_VIOLATION_BEHAVIOUR, Option.INITIALISER_INJECTION_MODE };
        }
        
        private enum Inherit
        {
            INHERIT("INHERIT", 0), 
            ALLOW_OVERRIDE("ALLOW_OVERRIDE", 1), 
            INDEPENDENT("INDEPENDENT", 2), 
            ALWAYS_FALSE("ALWAYS_FALSE", 3);
            
            private static final Inherit[] $VALUES;
            
            private Inherit(final String s, final int n) {
            }
            
            static {
                $VALUES = new Inherit[] { Inherit.INHERIT, Inherit.ALLOW_OVERRIDE, Inherit.INDEPENDENT, Inherit.ALWAYS_FALSE };
            }
        }
    }
    
    public enum Side
    {
        UNKNOWN {
            @Override
            protected boolean detect() {
                return false;
            }
        }, 
        CLIENT {
            @Override
            protected boolean detect() {
                return "CLIENT".equals(MixinService.getService().getSideName());
            }
        }, 
        SERVER {
            @Override
            protected boolean detect() {
                final String sideName = MixinService.getService().getSideName();
                return "SERVER".equals(sideName) || "DEDICATEDSERVER".equals(sideName);
            }
        };
        
        private static final Side[] $VALUES;
        
        private Side(final String s, final int n) {
        }
        
        protected abstract boolean detect();
        
        Side(final String s, final int n, final MixinEnvironment$1 object) {
            this(s, n);
        }
        
        static {
            $VALUES = new Side[] { Side.UNKNOWN, Side.CLIENT, Side.SERVER };
        }
    }
}

//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.transformer;

import com.google.gson.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.launch.*;
import java.util.regex.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.util.*;
import org.spongepowered.asm.mixin.refmap.*;
import org.spongepowered.asm.mixin.transformer.throwables.*;
import org.spongepowered.asm.mixin.extensibility.*;
import org.spongepowered.asm.lib.tree.*;
import java.util.*;
import org.apache.logging.log4j.*;
import com.google.gson.*;
import org.spongepowered.asm.service.*;
import java.io.*;

final class MixinConfig implements Comparable, IMixinConfig
{
    private static int configOrder;
    private static final Set globalMixinList;
    private final Logger logger;
    private final transient Map mixinMapping;
    private final transient Set unhandledTargets;
    private final transient List mixins;
    private transient Config handle;
    @SerializedName("target")
    private String selector;
    @SerializedName("minVersion")
    private String version;
    @SerializedName("compatibilityLevel")
    private String compatibility;
    @SerializedName("required")
    private boolean required;
    @SerializedName("priority")
    private int priority;
    @SerializedName("mixinPriority")
    private int mixinPriority;
    @SerializedName("package")
    private String mixinPackage;
    @SerializedName("mixins")
    private List mixinClasses;
    @SerializedName("client")
    private List mixinClassesClient;
    @SerializedName("server")
    private List mixinClassesServer;
    @SerializedName("setSourceFile")
    private boolean setSourceFile;
    @SerializedName("refmap")
    private String refMapperConfig;
    @SerializedName("verbose")
    private boolean verboseLogging;
    private final transient int order;
    private final transient List listeners;
    private transient IMixinService service;
    private transient MixinEnvironment env;
    private transient String name;
    @SerializedName("plugin")
    private String pluginClassName;
    @SerializedName("injectors")
    private InjectorOptions injectorOptions;
    @SerializedName("overwrites")
    private OverwriteOptions overwriteOptions;
    private transient IMixinConfigPlugin plugin;
    private transient IReferenceMapper refMapper;
    private transient boolean prepared;
    private transient boolean visited;
    
    private MixinConfig() {
        this.logger = LogManager.getLogger("mixin");
        this.mixinMapping = new HashMap();
        this.unhandledTargets = new HashSet();
        this.mixins = new ArrayList();
        this.priority = 1000;
        this.mixinPriority = 1000;
        this.setSourceFile = false;
        this.order = MixinConfig.configOrder++;
        this.listeners = new ArrayList();
        this.injectorOptions = new InjectorOptions();
        this.overwriteOptions = new OverwriteOptions();
        this.prepared = false;
        this.visited = false;
    }
    
    private void initCompatibilityLevel() {
        if (this.compatibility == null) {
            return;
        }
        final MixinEnvironment.CompatibilityLevel value = MixinEnvironment.CompatibilityLevel.valueOf(this.compatibility.trim().toUpperCase());
        final MixinEnvironment.CompatibilityLevel compatibilityLevel = MixinEnvironment.getCompatibilityLevel();
        if (value == compatibilityLevel) {
            return;
        }
        if (compatibilityLevel.isAtLeast(value) && !compatibilityLevel.canSupport(value)) {
            throw new MixinInitialisationError("Mixin config " + this.name + " requires compatibility level " + value + " which is too old");
        }
        if (!compatibilityLevel.canElevateTo(value)) {
            throw new MixinInitialisationError("Mixin config " + this.name + " requires compatibility level " + value + " which is prohibited by " + compatibilityLevel);
        }
        MixinEnvironment.setCompatibilityLevel(value);
    }
    
    private MixinEnvironment parseSelector(final String s, final MixinEnvironment mixinEnvironment) {
        if (s != null) {
            final String[] split = s.split("[&\\| ]");
            for (int length = split.length, i = 0; i < length; ++i) {
                final Matcher matcher = Pattern.compile("^@env(?:ironment)?\\(([A-Z]+)\\)$").matcher(split[i].trim());
                if (matcher.matches()) {
                    return MixinEnvironment.getEnvironment(MixinEnvironment.Phase.forName(matcher.group(1)));
                }
            }
            final MixinEnvironment.Phase forName = MixinEnvironment.Phase.forName(s);
            if (forName != null) {
                return MixinEnvironment.getEnvironment(forName);
            }
        }
        return mixinEnvironment;
    }
    
    private void initInjectionPoints() {
        if (this.injectorOptions.injectionPoints == null) {
            return;
        }
        for (final String s : this.injectorOptions.injectionPoints) {
            try {
                final Class class1 = this.service.getClassProvider().findClass(s, true);
                if (InjectionPoint.class.isAssignableFrom(class1)) {
                    InjectionPoint.register(class1);
                }
                else {
                    this.logger.error("Unable to register injection point {} for {}, class must extend InjectionPoint", new Object[] { class1, this });
                }
            }
            catch (Throwable t) {
                this.logger.catching(t);
            }
        }
    }
    
    private boolean checkVersion() throws MixinInitialisationError {
        if (this.version == null) {
            this.logger.error("Mixin config {} does not specify \"minVersion\" property", new Object[] { this.name });
        }
        final VersionNumber parse = VersionNumber.parse(this.version);
        final VersionNumber parse2 = VersionNumber.parse(this.env.getVersion());
        if (parse.compareTo(parse2) <= 0) {
            return true;
        }
        this.logger.warn("Mixin config {} requires mixin subsystem version {} but {} was found. The mixin config will not be applied.", new Object[] { this.name, parse, parse2 });
        if (this.required) {
            throw new MixinInitialisationError("Required mixin config " + this.name + " requires mixin subsystem version " + parse);
        }
        return false;
    }
    
    void addListener(final IListener listener) {
        this.listeners.add(listener);
    }
    
    void onSelect() {
        if (this.pluginClassName != null) {
            try {
                this.plugin = this.service.getClassProvider().findClass(this.pluginClassName, true).newInstance();
                if (this.plugin != null) {
                    this.plugin.onLoad(this.mixinPackage);
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
                this.plugin = null;
            }
        }
        if (!this.mixinPackage.endsWith(".")) {
            this.mixinPackage += ".";
        }
        boolean b = false;
        if (this.refMapperConfig == null) {
            if (this.plugin != null) {
                this.refMapperConfig = this.plugin.getRefMapperConfig();
            }
            if (this.refMapperConfig == null) {
                b = true;
                this.refMapperConfig = "mixin.refmap.json";
            }
        }
        this.refMapper = (IReferenceMapper)ReferenceMapper.read(this.refMapperConfig);
        this.verboseLogging |= this.env.getOption(MixinEnvironment.Option.DEBUG_VERBOSE);
        if (!b && this.refMapper.isDefault() && !this.env.getOption(MixinEnvironment.Option.DISABLE_REFMAP)) {
            this.logger.warn("Reference map '{}' for {} could not be read. If this is a development environment you can ignore this message", new Object[] { this.refMapperConfig, this });
        }
        if (this.env.getOption(MixinEnvironment.Option.REFMAP_REMAP)) {
            this.refMapper = RemappingReferenceMapper.of(this.env, this.refMapper);
        }
    }
    
    void prepare() {
        if (this.prepared) {
            return;
        }
        this.prepared = true;
        this.prepareMixins(this.mixinClasses, false);
        switch (this.env.getSide()) {
            case CLIENT: {
                this.prepareMixins(this.mixinClassesClient, false);
                break;
            }
            case SERVER: {
                this.prepareMixins(this.mixinClassesServer, false);
                break;
            }
            default: {
                this.logger.warn("Mixin environment was unable to detect the current side, sided mixins will not be applied");
                break;
            }
        }
    }
    
    void postInitialise() {
        if (this.plugin != null) {
            this.prepareMixins(this.plugin.getMixins(), true);
        }
        final Iterator<MixinInfo> iterator = (Iterator<MixinInfo>)this.mixins.iterator();
        while (iterator.hasNext()) {
            final MixinInfo mixinInfo = iterator.next();
            try {
                mixinInfo.validate();
                final Iterator<IListener> iterator2 = this.listeners.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().onInit(mixinInfo);
                }
            }
            catch (InvalidMixinException ex) {
                this.logger.error(ex.getMixin() + ": " + ex.getMessage(), (Throwable)ex);
                this.removeMixin(mixinInfo);
                iterator.remove();
            }
            catch (Exception ex2) {
                this.logger.error(ex2.getMessage(), (Throwable)ex2);
                this.removeMixin(mixinInfo);
                iterator.remove();
            }
        }
    }
    
    private void removeMixin(final MixinInfo mixinInfo) {
        final Iterator<List<MixinInfo>> iterator = this.mixinMapping.values().iterator();
        while (iterator.hasNext()) {
            final Iterator<MixinInfo> iterator2 = iterator.next().iterator();
            while (iterator2.hasNext()) {
                if (mixinInfo == iterator2.next()) {
                    iterator2.remove();
                }
            }
        }
    }
    
    private void prepareMixins(final List list, final boolean b) {
        if (list == null) {
            return;
        }
        for (final String s : list) {
            final String string = this.mixinPackage + s;
            if (s != null) {
                if (MixinConfig.globalMixinList.contains(string)) {
                    continue;
                }
                Object o = null;
                try {
                    o = new MixinInfo(this.service, this, s, true, this.plugin, b);
                    if (((MixinInfo)o).getTargetClasses().size() <= 0) {
                        continue;
                    }
                    MixinConfig.globalMixinList.add(string);
                    final Iterator iterator2 = ((MixinInfo)o).getTargetClasses().iterator();
                    while (iterator2.hasNext()) {
                        final String replace = iterator2.next().replace('/', '.');
                        this.mixinsFor(replace).add(o);
                        this.unhandledTargets.add(replace);
                    }
                    final Iterator<IListener> iterator3 = this.listeners.iterator();
                    while (iterator3.hasNext()) {
                        iterator3.next().onPrepare((MixinInfo)o);
                    }
                    this.mixins.add(o);
                }
                catch (InvalidMixinException ex) {
                    if (this.required) {
                        throw ex;
                    }
                    this.logger.error(ex.getMessage(), (Throwable)ex);
                }
                catch (Exception ex2) {
                    if (this.required) {
                        throw new InvalidMixinException((IMixinInfo)o, "Error initialising mixin " + o + " - " + ex2.getClass() + ": " + ex2.getMessage(), ex2);
                    }
                    this.logger.error(ex2.getMessage(), (Throwable)ex2);
                }
            }
        }
    }
    
    void postApply(final String s, final ClassNode classNode) {
        this.unhandledTargets.remove(s);
    }
    
    public Config getHandle() {
        if (this.handle == null) {
            this.handle = new Config(this);
        }
        return this.handle;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public MixinEnvironment getEnvironment() {
        return this.env;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getMixinPackage() {
        return this.mixinPackage;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public int getDefaultMixinPriority() {
        return this.mixinPriority;
    }
    
    public int getDefaultRequiredInjections() {
        return this.injectorOptions.defaultRequireValue;
    }
    
    public String getDefaultInjectorGroup() {
        final String defaultGroup = this.injectorOptions.defaultGroup;
        return (defaultGroup != null && !defaultGroup.isEmpty()) ? defaultGroup : "default";
    }
    
    public boolean conformOverwriteVisibility() {
        return this.overwriteOptions.conformAccessModifiers;
    }
    
    public boolean requireOverwriteAnnotations() {
        return this.overwriteOptions.requireOverwriteAnnotations;
    }
    
    public int getMaxShiftByValue() {
        return Math.min(Math.max(this.injectorOptions.maxShiftBy, 0), 5);
    }
    
    public boolean select(final MixinEnvironment mixinEnvironment) {
        this.visited = true;
        return this.env == mixinEnvironment;
    }
    
    boolean isVisited() {
        return this.visited;
    }
    
    int getDeclaredMixinCount() {
        return getCollectionSize(this.mixinClasses, this.mixinClassesClient, this.mixinClassesServer);
    }
    
    int getMixinCount() {
        return this.mixins.size();
    }
    
    public List getClasses() {
        return Collections.unmodifiableList((List<?>)this.mixinClasses);
    }
    
    public boolean shouldSetSourceFile() {
        return this.setSourceFile;
    }
    
    public IReferenceMapper getReferenceMapper() {
        if (this.env.getOption(MixinEnvironment.Option.DISABLE_REFMAP)) {
            return (IReferenceMapper)ReferenceMapper.DEFAULT_MAPPER;
        }
        this.refMapper.setContext(this.env.getRefmapObfuscationContext());
        return this.refMapper;
    }
    
    String remapClassName(final String s, final String s2) {
        return this.getReferenceMapper().remap(s, s2);
    }
    
    public IMixinConfigPlugin getPlugin() {
        return this.plugin;
    }
    
    public Set getTargets() {
        return Collections.unmodifiableSet(this.mixinMapping.keySet());
    }
    
    public Set getUnhandledTargets() {
        return Collections.unmodifiableSet((Set<?>)this.unhandledTargets);
    }
    
    public Level getLoggingLevel() {
        return this.verboseLogging ? Level.INFO : Level.DEBUG;
    }
    
    public boolean packageMatch(final String s) {
        return s.startsWith(this.mixinPackage);
    }
    
    public boolean hasMixinsFor(final String s) {
        return this.mixinMapping.containsKey(s);
    }
    
    public List getMixinsFor(final String s) {
        return this.mixinsFor(s);
    }
    
    private List mixinsFor(final String s) {
        List list = this.mixinMapping.get(s);
        if (list == null) {
            list = new ArrayList();
            this.mixinMapping.put(s, list);
        }
        return list;
    }
    
    public List reloadMixin(final String s, final byte[] array) {
        for (final MixinInfo mixinInfo : this.mixins) {
            if (mixinInfo.getClassName().equals(s)) {
                mixinInfo.reloadMixin(array);
                return mixinInfo.getTargetClasses();
            }
        }
        return Collections.emptyList();
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public int compareTo(final MixinConfig mixinConfig) {
        if (mixinConfig == null) {
            return 0;
        }
        if (mixinConfig.priority == this.priority) {
            return this.order - mixinConfig.order;
        }
        return this.priority - mixinConfig.priority;
    }
    
    static Config create(final String s, final MixinEnvironment mixinEnvironment) {
        try {
            final MixinConfig mixinConfig = (MixinConfig)new Gson().fromJson((Reader)new InputStreamReader(MixinService.getService().getResourceAsStream(s)), (Class)MixinConfig.class);
            if (mixinEnvironment == 0) {
                return mixinConfig.getHandle();
            }
            return null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException(String.format("The specified resource '%s' was invalid or could not be read", s), ex);
        }
    }
    
    private static int getCollectionSize(final Collection... array) {
        int n = 0;
        for (final Collection collection : array) {
            if (collection != null) {
                n += collection.size();
            }
        }
        return n;
    }
    
    @Override
    public int compareTo(final Object o) {
        return this.compareTo((MixinConfig)o);
    }
    
    static {
        MixinConfig.configOrder = 0;
        globalMixinList = new HashSet();
    }
    
    interface IListener
    {
        void onPrepare(final MixinInfo p0);
        
        void onInit(final MixinInfo p0);
    }
    
    static class OverwriteOptions
    {
        @SerializedName("conformVisibility")
        boolean conformAccessModifiers;
        @SerializedName("requireAnnotations")
        boolean requireOverwriteAnnotations;
    }
    
    static class InjectorOptions
    {
        @SerializedName("defaultRequire")
        int defaultRequireValue;
        @SerializedName("defaultGroup")
        String defaultGroup;
        @SerializedName("injectionPoints")
        List injectionPoints;
        @SerializedName("maxShiftBy")
        int maxShiftBy;
        
        InjectorOptions() {
            this.defaultRequireValue = 0;
            this.defaultGroup = "default";
            this.maxShiftBy = 0;
        }
    }
}

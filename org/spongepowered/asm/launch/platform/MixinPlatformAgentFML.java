//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.launch.platform;

import java.net.*;
import org.spongepowered.asm.launch.*;
import java.io.*;
import net.minecraft.launchwrapper.*;
import java.lang.reflect.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.extensibility.*;
import org.apache.logging.log4j.*;
import java.util.*;

public class MixinPlatformAgentFML extends MixinPlatformAgentAbstract
{
    private static final String LOAD_CORE_MOD_METHOD = "loadCoreMod";
    private static final String GET_REPARSEABLE_COREMODS_METHOD = "getReparseableCoremods";
    private static final String CORE_MOD_MANAGER_CLASS = "net.minecraftforge.fml.relauncher.CoreModManager";
    private static final String CORE_MOD_MANAGER_CLASS_LEGACY = "cpw.mods.fml.relauncher.CoreModManager";
    private static final String GET_IGNORED_MODS_METHOD = "getIgnoredMods";
    private static final String GET_IGNORED_MODS_METHOD_LEGACY = "getLoadedCoremods";
    private static final String FML_REMAPPER_ADAPTER_CLASS = "org.spongepowered.asm.bridge.RemapperAdapterFML";
    private static final String FML_CMDLINE_COREMODS = "fml.coreMods.load";
    private static final String FML_PLUGIN_WRAPPER_CLASS = "FMLPluginWrapper";
    private static final String FML_CORE_MOD_INSTANCE_FIELD = "coreModInstance";
    private static final String MFATT_FORCELOADASMOD = "ForceLoadAsMod";
    private static final String MFATT_FMLCOREPLUGIN = "FMLCorePlugin";
    private static final String MFATT_COREMODCONTAINSMOD = "FMLCorePluginContainsFMLMod";
    private static final String FML_TWEAKER_DEOBF = "FMLDeobfTweaker";
    private static final String FML_TWEAKER_INJECTION = "FMLInjectionAndSortingTweaker";
    private static final String FML_TWEAKER_TERMINAL = "TerminalTweaker";
    private static final Set loadedCoreMods;
    private final ITweaker coreModWrapper;
    private final String fileName;
    private Class clCoreModManager;
    private boolean initInjectionState;
    
    public MixinPlatformAgentFML(final MixinPlatformManager mixinPlatformManager, final URI uri) {
        super(mixinPlatformManager, uri);
        this.fileName = this.container.getName();
        this.coreModWrapper = this.initFMLCoreMod();
    }
    
    private ITweaker initFMLCoreMod() {
        try {
            try {
                this.clCoreModManager = getCoreModManagerClass();
            }
            catch (ClassNotFoundException ex) {
                MixinPlatformAgentAbstract.logger.info("FML platform manager could not load class {}. Proceeding without FML support.", new Object[] { ex.getMessage() });
                return null;
            }
            if ("true".equalsIgnoreCase(this.attributes.get("ForceLoadAsMod"))) {
                MixinPlatformAgentAbstract.logger.debug("ForceLoadAsMod was specified for {}, attempting force-load", new Object[] { this.fileName });
                this.loadAsMod();
            }
            return this.injectCorePlugin();
        }
        catch (Exception ex2) {
            MixinPlatformAgentAbstract.logger.catching((Throwable)ex2);
            return null;
        }
    }
    
    private void loadAsMod() {
        try {
            getIgnoredMods(this.clCoreModManager).remove(this.fileName);
        }
        catch (Exception ex) {
            MixinPlatformAgentAbstract.logger.catching((Throwable)ex);
        }
        if (this.attributes.get("FMLCorePluginContainsFMLMod") != null) {
            if (this.isIgnoredReparseable()) {
                MixinPlatformAgentAbstract.logger.debug("Ignoring request to add {} to reparseable coremod collection - it is a deobfuscated dependency", new Object[] { this.fileName });
                return;
            }
            this.addReparseableJar();
        }
    }
    
    private boolean isIgnoredReparseable() {
        return this.container.toString().contains("deobfedDeps");
    }
    
    private void addReparseableJar() {
        try {
            final List list = (List)this.clCoreModManager.getDeclaredMethod(GlobalProperties.getString("mixin.launch.fml.reparseablecoremodsmethod", "getReparseableCoremods"), (Class[])new Class[0]).invoke(null, new Object[0]);
            if (!list.contains(this.fileName)) {
                MixinPlatformAgentAbstract.logger.debug("Adding {} to reparseable coremod collection", new Object[] { this.fileName });
                list.add(this.fileName);
            }
        }
        catch (Exception ex) {
            MixinPlatformAgentAbstract.logger.catching((Throwable)ex);
        }
    }
    
    private ITweaker injectCorePlugin() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final String value = this.attributes.get("FMLCorePlugin");
        if (value == null) {
            return null;
        }
        if (value != 0) {
            MixinPlatformAgentAbstract.logger.debug("{} has core plugin {}. Skipping because it was already injected.", new Object[] { this.fileName, value });
            return null;
        }
        MixinPlatformAgentAbstract.logger.debug("{} has core plugin {}. Injecting it into FML for co-initialisation:", new Object[] { this.fileName, value });
        final Method declaredMethod = this.clCoreModManager.getDeclaredMethod(GlobalProperties.getString("mixin.launch.fml.loadcoremodmethod", "loadCoreMod"), LaunchClassLoader.class, String.class, File.class);
        declaredMethod.setAccessible(true);
        final ITweaker tweaker = (ITweaker)declaredMethod.invoke(null, Launch.classLoader, value, this.container);
        if (tweaker == null) {
            MixinPlatformAgentAbstract.logger.debug("Core plugin {} could not be loaded.", new Object[] { value });
            return null;
        }
        this.initInjectionState = isTweakerQueued("FMLInjectionAndSortingTweaker");
        MixinPlatformAgentFML.loadedCoreMods.add(value);
        return tweaker;
    }
    
    public String getPhaseProvider() {
        return MixinPlatformAgentFML.class.getName() + "$PhaseProvider";
    }
    
    public void prepare() {
        this.initInjectionState |= isTweakerQueued("FMLInjectionAndSortingTweaker");
    }
    
    public void initPrimaryContainer() {
        if (this.clCoreModManager != null) {
            this.injectRemapper();
        }
    }
    
    private void injectRemapper() {
        try {
            MixinPlatformAgentAbstract.logger.debug("Creating FML remapper adapter: {}", new Object[] { "org.spongepowered.asm.bridge.RemapperAdapterFML" });
            MixinEnvironment.getDefaultEnvironment().getRemappers().add((IRemapper)Class.forName("org.spongepowered.asm.bridge.RemapperAdapterFML", true, (ClassLoader)Launch.classLoader).getDeclaredMethod("create", (Class<?>[])new Class[0]).invoke(null, new Object[0]));
        }
        catch (Exception ex) {
            MixinPlatformAgentAbstract.logger.debug("Failed instancing FML remapper adapter, things will probably go horribly for notch-obf'd mods!");
        }
    }
    
    public void inject() {
        if (this.coreModWrapper != null && this != 0) {
            MixinPlatformAgentAbstract.logger.debug("FML agent is co-initiralising coremod instance {} for {}", new Object[] { this.coreModWrapper, this.uri });
            this.coreModWrapper.injectIntoClassLoader(Launch.classLoader);
        }
    }
    
    public String getLaunchTarget() {
        return null;
    }
    
    private static Class getCoreModManagerClass() throws ClassNotFoundException {
        try {
            return Class.forName(GlobalProperties.getString("mixin.launch.fml.coremodmanagerclass", "net.minecraftforge.fml.relauncher.CoreModManager"));
        }
        catch (ClassNotFoundException ex) {
            return Class.forName("cpw.mods.fml.relauncher.CoreModManager");
        }
    }
    
    private static List getIgnoredMods(final Class clazz) throws IllegalAccessException, InvocationTargetException {
        Method method;
        try {
            method = clazz.getDeclaredMethod(GlobalProperties.getString("mixin.launch.fml.ignoredmodsmethod", "getIgnoredMods"), (Class[])new Class[0]);
        }
        catch (NoSuchMethodException ex2) {
            try {
                method = clazz.getDeclaredMethod("getLoadedCoremods", (Class[])new Class[0]);
            }
            catch (NoSuchMethodException ex) {
                MixinPlatformAgentAbstract.logger.catching(Level.DEBUG, (Throwable)ex);
                return Collections.emptyList();
            }
        }
        return (List)method.invoke(null, new Object[0]);
    }
    
    static {
        loadedCoreMods = new HashSet();
        for (final String s : System.getProperty("fml.coreMods.load", "").split(",")) {
            if (!s.isEmpty()) {
                MixinPlatformAgentAbstract.logger.debug("FML platform agent will ignore coremod {} specified on the command line", new Object[] { s });
                MixinPlatformAgentFML.loadedCoreMods.add(s);
            }
        }
    }
}

//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.points;

import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.refmap.*;
import org.spongepowered.asm.mixin.injection.struct.*;
import org.apache.logging.log4j.*;
import org.spongepowered.asm.mixin.*;
import java.util.*;
import org.spongepowered.asm.lib.tree.*;

@InjectionPoint.AtCode("INVOKE")
public class BeforeInvoke extends InjectionPoint
{
    protected final MemberInfo target;
    protected final boolean allowPermissive;
    protected final int ordinal;
    protected final String className;
    protected final IMixinContext context;
    protected final Logger logger;
    private boolean log;
    
    public BeforeInvoke(final InjectionPointData injectionPointData) {
        super(injectionPointData);
        this.logger = LogManager.getLogger("mixin");
        this.log = false;
        this.target = injectionPointData.getTarget();
        this.ordinal = injectionPointData.getOrdinal();
        this.log = injectionPointData.get("log", false);
        this.className = this.getClassName();
        this.context = injectionPointData.getContext();
        this.allowPermissive = (this.context.getOption(MixinEnvironment.Option.REFMAP_REMAP) && this.context.getOption(MixinEnvironment.Option.REFMAP_REMAP_ALLOW_PERMISSIVE) && !this.context.getReferenceMapper().isDefault());
    }
    
    private String getClassName() {
        final InjectionPoint.AtCode atCode = this.getClass().getAnnotation(InjectionPoint.AtCode.class);
        return String.format("@At(%s)", (atCode != null) ? atCode.value() : this.getClass().getSimpleName().toUpperCase());
    }
    
    public BeforeInvoke setLogging(final boolean log) {
        this.log = log;
        return this;
    }
    
    public boolean find(final String s, final InsnList list, final Collection collection) {
        this.log("{} is searching for an injection point in method with descriptor {}", this.className, s);
        this.target;
        if (SearchType.STRICT == null && this.target.desc != null && this.allowPermissive) {
            this.logger.warn("STRICT match for {} using \"{}\" in {} returned 0 results, attempting permissive search. To inhibit permissive search set mixin.env.allowPermissiveMatch=false", new Object[] { this.className, this.target, this.context });
            return this.find(s, list, collection, this.target, SearchType.PERMISSIVE);
        }
        return true;
    }
    
    protected boolean addInsn(final InsnList list, final Collection collection, final AbstractInsnNode abstractInsnNode) {
        collection.add(abstractInsnNode);
        return true;
    }
    
    protected boolean matchesInsn(final AbstractInsnNode abstractInsnNode) {
        return abstractInsnNode instanceof MethodInsnNode;
    }
    
    protected void inspectInsn(final String s, final InsnList list, final AbstractInsnNode abstractInsnNode) {
    }
    
    protected void log(final String s, final Object... array) {
        if (this.log) {
            this.logger.info(s, array);
        }
    }
    
    public enum SearchType
    {
        STRICT("STRICT", 0), 
        PERMISSIVE("PERMISSIVE", 1);
        
        private static final SearchType[] $VALUES;
        
        private SearchType(final String s, final int n) {
        }
        
        static {
            $VALUES = new SearchType[] { SearchType.STRICT, SearchType.PERMISSIVE };
        }
    }
}

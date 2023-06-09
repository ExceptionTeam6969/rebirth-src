//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.lib.*;
import org.spongepowered.asm.mixin.transformer.throwables.*;
import org.spongepowered.asm.mixin.extensibility.*;
import java.util.*;
import org.spongepowered.asm.mixin.transformer.meta.*;
import org.spongepowered.asm.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.lib.tree.*;

public final class InterfaceInfo
{
    private final MixinInfo mixin;
    private final String prefix;
    private final Type iface;
    private final boolean unique;
    private Set methods;
    
    private InterfaceInfo(final MixinInfo mixin, final String prefix, final Type iface, final boolean unique) {
        if (prefix == null || prefix.length() < 2 || !prefix.endsWith("$")) {
            throw new InvalidMixinException((IMixinInfo)mixin, String.format("Prefix %s for iface %s is not valid", prefix, iface.toString()));
        }
        this.mixin = mixin;
        this.prefix = prefix;
        this.iface = iface;
        this.unique = unique;
    }
    
    private void initMethods() {
        this.methods = new HashSet();
        this.readInterface(this.iface.getInternalName());
    }
    
    private void readInterface(final String s) {
        final ClassInfo forName = ClassInfo.forName(s);
        final Iterator iterator = forName.getMethods().iterator();
        while (iterator.hasNext()) {
            this.methods.add(iterator.next().toString());
        }
        final Iterator iterator2 = forName.getInterfaces().iterator();
        while (iterator2.hasNext()) {
            this.readInterface(iterator2.next());
        }
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public Type getIface() {
        return this.iface;
    }
    
    public String getName() {
        return this.iface.getClassName();
    }
    
    public String getInternalName() {
        return this.iface.getInternalName();
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public boolean renameMethod(final MethodNode methodNode) {
        if (this.methods == null) {
            this.initMethods();
        }
        if (!methodNode.name.startsWith(this.prefix)) {
            if (this.methods.contains(methodNode.name + methodNode.desc)) {
                this.decorateUniqueMethod(methodNode);
            }
            return false;
        }
        final String substring = methodNode.name.substring(this.prefix.length());
        if (!this.methods.contains(substring + methodNode.desc)) {
            throw new InvalidMixinException((IMixinInfo)this.mixin, String.format("%s does not exist in target interface %s", substring, this.getName()));
        }
        if ((methodNode.access & 0x1) == 0x0) {
            throw new InvalidMixinException((IMixinInfo)this.mixin, String.format("%s cannot implement %s because it is not visible", substring, this.getName()));
        }
        Annotations.setVisible(methodNode, MixinRenamed.class, "originalName", methodNode.name, "isInterfaceMember", true);
        this.decorateUniqueMethod(methodNode);
        methodNode.name = substring;
        return true;
    }
    
    private void decorateUniqueMethod(final MethodNode methodNode) {
        if (!this.unique) {
            return;
        }
        if (Annotations.getVisible(methodNode, Unique.class) == null) {
            Annotations.setVisible(methodNode, Unique.class, new Object[0]);
            this.mixin.getClassInfo().findMethod(methodNode).setUnique(true);
        }
    }
    
    static InterfaceInfo fromAnnotation(final MixinInfo mixinInfo, final AnnotationNode annotationNode) {
        final String s = (String)Annotations.getValue(annotationNode, "prefix");
        final Type type = (Type)Annotations.getValue(annotationNode, "iface");
        final Boolean b = (Boolean)Annotations.getValue(annotationNode, "unique");
        if (s == null || type == null) {
            throw new InvalidMixinException((IMixinInfo)mixinInfo, String.format("@Interface annotation on %s is missing a required parameter", mixinInfo));
        }
        return new InterfaceInfo(mixinInfo, s, type, b != null && b);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final InterfaceInfo interfaceInfo = (InterfaceInfo)o;
        return this.mixin.equals(interfaceInfo.mixin) && this.prefix.equals(interfaceInfo.prefix) && this.iface.equals((Object)interfaceInfo.iface);
    }
    
    @Override
    public int hashCode() {
        return 31 * (31 * this.mixin.hashCode() + this.prefix.hashCode()) + this.iface.hashCode();
    }
}

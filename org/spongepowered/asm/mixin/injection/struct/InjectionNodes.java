//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.struct;

import org.spongepowered.asm.lib.tree.*;
import java.util.*;
import org.spongepowered.asm.util.*;

public class InjectionNodes extends ArrayList
{
    private static final long serialVersionUID = 1L;
    
    public InjectionNode add(final AbstractInsnNode abstractInsnNode) {
        InjectionNode value = this.get(abstractInsnNode);
        if (value == null) {
            value = new InjectionNode(abstractInsnNode);
            this.add(value);
        }
        return value;
    }
    
    public InjectionNode get(final AbstractInsnNode abstractInsnNode) {
        for (final InjectionNode injectionNode : this) {
            if (injectionNode.matches(abstractInsnNode)) {
                return injectionNode;
            }
        }
        return null;
    }
    
    public boolean contains(final AbstractInsnNode abstractInsnNode) {
        return this.get(abstractInsnNode) != null;
    }
    
    public void replace(final AbstractInsnNode abstractInsnNode, final AbstractInsnNode abstractInsnNode2) {
        final InjectionNode value = this.get(abstractInsnNode);
        if (value != null) {
            value.replace(abstractInsnNode2);
        }
    }
    
    public void remove(final AbstractInsnNode abstractInsnNode) {
        final InjectionNode value = this.get(abstractInsnNode);
        if (value != null) {
            value.remove();
        }
    }
    
    public static class InjectionNode implements Comparable
    {
        private static int nextId;
        private final int id;
        private final AbstractInsnNode originalTarget;
        private AbstractInsnNode currentTarget;
        private Map decorations;
        
        public InjectionNode(final AbstractInsnNode abstractInsnNode) {
            this.originalTarget = abstractInsnNode;
            this.currentTarget = abstractInsnNode;
            this.id = InjectionNode.nextId++;
        }
        
        public int getId() {
            return this.id;
        }
        
        public AbstractInsnNode getOriginalTarget() {
            return this.originalTarget;
        }
        
        public AbstractInsnNode getCurrentTarget() {
            return this.currentTarget;
        }
        
        public InjectionNode replace(final AbstractInsnNode currentTarget) {
            this.currentTarget = currentTarget;
            return this;
        }
        
        public InjectionNode remove() {
            this.currentTarget = null;
            return this;
        }
        
        public boolean matches(final AbstractInsnNode abstractInsnNode) {
            return this.originalTarget == abstractInsnNode || this.currentTarget == abstractInsnNode;
        }
        
        public boolean isReplaced() {
            return this.originalTarget != this.currentTarget;
        }
        
        public boolean isRemoved() {
            return this.currentTarget == null;
        }
        
        public InjectionNode decorate(final String s, final Object o) {
            if (this.decorations == null) {
                this.decorations = new HashMap();
            }
            this.decorations.put(s, o);
            return this;
        }
        
        public boolean hasDecoration(final String s) {
            return this.decorations != null && this.decorations.get(s) != null;
        }
        
        public Object getDecoration(final String s) {
            return (this.decorations == null) ? null : this.decorations.get(s);
        }
        
        public int compareTo(final InjectionNode injectionNode) {
            return (injectionNode == null) ? Integer.MAX_VALUE : (this.hashCode() - injectionNode.hashCode());
        }
        
        @Override
        public String toString() {
            return String.format("InjectionNode[%s]", Bytecode.describeNode(this.currentTarget).replaceAll("\\s+", " "));
        }
        
        @Override
        public int compareTo(final Object o) {
            return this.compareTo((InjectionNode)o);
        }
        
        static {
            InjectionNode.nextId = 0;
        }
    }
}

//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.transformer;

import java.util.*;
import org.spongepowered.asm.transformers.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.util.*;
import org.spongepowered.asm.mixin.gen.*;
import org.spongepowered.asm.mixin.transformer.throwables.*;
import org.spongepowered.asm.lib.*;
import org.spongepowered.asm.lib.tree.*;

class MixinPostProcessor extends TreeTransformer implements MixinConfig.IListener
{
    private final Set syntheticInnerClasses;
    private final Map accessorMixins;
    private final Set loadable;
    
    MixinPostProcessor() {
        this.syntheticInnerClasses = new HashSet();
        this.accessorMixins = new HashMap();
        this.loadable = new HashSet();
    }
    
    public void onInit(final MixinInfo mixinInfo) {
        final Iterator<String> iterator = mixinInfo.getSyntheticInnerClasses().iterator();
        while (iterator.hasNext()) {
            this.registerSyntheticInner(iterator.next().replace('/', '.'));
        }
    }
    
    public void onPrepare(final MixinInfo mixinInfo) {
        final String className = mixinInfo.getClassName();
        if (mixinInfo.isLoadable()) {
            this.registerLoadable(className);
        }
        if (mixinInfo.isAccessor()) {
            this.registerAccessor(mixinInfo);
        }
    }
    
    void registerSyntheticInner(final String s) {
        this.syntheticInnerClasses.add(s);
    }
    
    void registerLoadable(final String s) {
        this.loadable.add(s);
    }
    
    void registerAccessor(final MixinInfo mixinInfo) {
        this.registerLoadable(mixinInfo.getClassName());
        this.accessorMixins.put(mixinInfo.getClassName(), mixinInfo);
    }
    
    boolean canTransform(final String s) {
        return this.syntheticInnerClasses.contains(s) || this.loadable.contains(s);
    }
    
    public String getName() {
        return this.getClass().getName();
    }
    
    public boolean isDelegationExcluded() {
        return true;
    }
    
    public byte[] transformClassBytes(final String s, final String s2, final byte[] array) {
        if (this.syntheticInnerClasses.contains(s2)) {
            return this.processSyntheticInner(array);
        }
        if (this.accessorMixins.containsKey(s2)) {
            return this.processAccessor(array, this.accessorMixins.get(s2));
        }
        return array;
    }
    
    private byte[] processSyntheticInner(final byte[] array) {
        final ClassReader classReader = new ClassReader(array);
        final MixinClassWriter mixinClassWriter = new MixinClassWriter(classReader, 0);
        classReader.accept((ClassVisitor)new ClassVisitor(327680, (ClassVisitor)mixinClassWriter) {
            final MixinPostProcessor this$0;
            
            public void visit(final int n, final int n2, final String s, final String s2, final String s3, final String[] array) {
                super.visit(n, n2 | 0x1, s, s2, s3, array);
            }
            
            public FieldVisitor visitField(int n, final String s, final String s2, final String s3, final Object o) {
                if ((n & 0x6) == 0x0) {
                    n |= 0x1;
                }
                return super.visitField(n, s, s2, s3, o);
            }
            
            public MethodVisitor visitMethod(int n, final String s, final String s2, final String s3, final String[] array) {
                if ((n & 0x6) == 0x0) {
                    n |= 0x1;
                }
                return super.visitMethod(n, s, s2, s3, array);
            }
        }, 8);
        return mixinClassWriter.toByteArray();
    }
    
    private byte[] processAccessor(final byte[] array, final MixinInfo mixinInfo) {
        if (!MixinEnvironment.getCompatibilityLevel().isAtLeast(MixinEnvironment.CompatibilityLevel.JAVA_8)) {
            return array;
        }
        boolean b = false;
        final MixinInfo.MixinClassNode classNode = mixinInfo.getClassNode(0);
        final ClassInfo classInfo = mixinInfo.getTargets().get(0);
        for (final MixinInfo.MixinMethodNode mixinMethodNode : classNode.mixinMethods) {
            if (!Bytecode.hasFlag((MethodNode)mixinMethodNode, 8)) {
                continue;
            }
            final AnnotationNode visibleAnnotation = mixinMethodNode.getVisibleAnnotation((Class)Accessor.class);
            final AnnotationNode visibleAnnotation2 = mixinMethodNode.getVisibleAnnotation((Class)Invoker.class);
            if (visibleAnnotation == null && visibleAnnotation2 == null) {
                continue;
            }
            createProxy((MethodNode)mixinMethodNode, classInfo, getAccessorMethod(mixinInfo, (MethodNode)mixinMethodNode, classInfo));
            b = true;
        }
        if (b) {
            return this.writeClass((ClassNode)classNode);
        }
        return array;
    }
    
    private static ClassInfo.Method getAccessorMethod(final MixinInfo mixinInfo, final MethodNode methodNode, final ClassInfo classInfo) throws MixinTransformerError {
        final ClassInfo.Method method = mixinInfo.getClassInfo().findMethod(methodNode, 10);
        if (!method.isRenamed()) {
            throw new MixinTransformerError("Unexpected state: " + mixinInfo + " loaded before " + classInfo + " was conformed");
        }
        return method;
    }
    
    private static void createProxy(final MethodNode methodNode, final ClassInfo classInfo, final ClassInfo.Method method) {
        methodNode.instructions.clear();
        final Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
        final Type returnType = Type.getReturnType(methodNode.desc);
        Bytecode.loadArgs(argumentTypes, methodNode.instructions, 0);
        methodNode.instructions.add((AbstractInsnNode)new MethodInsnNode(184, classInfo.getName(), method.getName(), methodNode.desc, false));
        methodNode.instructions.add((AbstractInsnNode)new InsnNode(returnType.getOpcode(172)));
        methodNode.maxStack = Bytecode.getFirstNonArgLocalIndex(argumentTypes, false);
        methodNode.maxLocals = 0;
    }
}

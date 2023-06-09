//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.gen;

import org.spongepowered.asm.lib.*;
import org.spongepowered.asm.util.*;
import org.spongepowered.asm.lib.tree.*;

public class AccessorGeneratorMethodProxy extends AccessorGenerator
{
    private final MethodNode targetMethod;
    private final Type[] argTypes;
    private final Type returnType;
    private final boolean isInstanceMethod;
    
    public AccessorGeneratorMethodProxy(final AccessorInfo accessorInfo) {
        super(accessorInfo);
        this.targetMethod = accessorInfo.getTargetMethod();
        this.argTypes = accessorInfo.getArgTypes();
        this.returnType = accessorInfo.getReturnType();
        this.isInstanceMethod = !Bytecode.hasFlag(this.targetMethod, 8);
    }
    
    public MethodNode generate() {
        final int n = Bytecode.getArgsSize(this.argTypes) + this.returnType.getSize() + (this.isInstanceMethod ? 1 : 0);
        final MethodNode method = this.createMethod(n, n);
        if (this.isInstanceMethod) {
            method.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
        }
        Bytecode.loadArgs(this.argTypes, method.instructions, this.isInstanceMethod ? 1 : 0);
        final boolean hasFlag = Bytecode.hasFlag(this.targetMethod, 2);
        method.instructions.add((AbstractInsnNode)new MethodInsnNode(this.isInstanceMethod ? (hasFlag ? 183 : 182) : 184, this.info.getClassNode().name, this.targetMethod.name, this.targetMethod.desc, false));
        method.instructions.add((AbstractInsnNode)new InsnNode(this.returnType.getOpcode(172)));
        return method;
    }
}

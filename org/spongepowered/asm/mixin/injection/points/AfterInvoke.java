//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.points;

import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.struct.*;
import java.util.*;
import org.spongepowered.asm.lib.*;
import org.spongepowered.asm.lib.tree.*;

@InjectionPoint.AtCode("INVOKE_ASSIGN")
public class AfterInvoke extends BeforeInvoke
{
    public AfterInvoke(final InjectionPointData injectionPointData) {
        super(injectionPointData);
    }
    
    @Override
    protected boolean addInsn(final InsnList list, final Collection collection, AbstractInsnNode abstractInsnNode) {
        if (Type.getReturnType(((MethodInsnNode)abstractInsnNode).desc) == Type.VOID_TYPE) {
            return false;
        }
        abstractInsnNode = InjectionPoint.nextNode(list, abstractInsnNode);
        if (abstractInsnNode instanceof VarInsnNode && abstractInsnNode.getOpcode() >= 54) {
            abstractInsnNode = InjectionPoint.nextNode(list, abstractInsnNode);
        }
        collection.add(abstractInsnNode);
        return true;
    }
}

//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.points;

import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.struct.*;
import org.spongepowered.asm.lib.tree.*;
import java.util.*;

@InjectionPoint.AtCode("JUMP")
public class JumpInsnPoint extends InjectionPoint
{
    private final int opCode;
    private final int ordinal;
    
    public JumpInsnPoint(final InjectionPointData injectionPointData) {
        this.opCode = injectionPointData.getOpcode(-1, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 198, 199, -1);
        this.ordinal = injectionPointData.getOrdinal();
    }
    
    public boolean find(final String s, final InsnList list, final Collection collection) {
        boolean b = false;
        int n = 0;
        for (final AbstractInsnNode abstractInsnNode : list) {
            if (abstractInsnNode instanceof JumpInsnNode && (this.opCode == -1 || abstractInsnNode.getOpcode() == this.opCode)) {
                if (this.ordinal == -1 || this.ordinal == n) {
                    collection.add(abstractInsnNode);
                    b = true;
                }
                ++n;
            }
        }
        return b;
    }
}

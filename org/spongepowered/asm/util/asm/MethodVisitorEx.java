//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.util.asm;

import org.spongepowered.asm.lib.*;
import org.spongepowered.asm.util.*;

public class MethodVisitorEx extends MethodVisitor
{
    public MethodVisitorEx(final MethodVisitor methodVisitor) {
        super(327680, methodVisitor);
    }
    
    public void visitConstant(final byte b) {
        if (b > -2 && b < 6) {
            this.visitInsn(Bytecode.CONSTANTS_INT[b + 1]);
            return;
        }
        this.visitIntInsn(16, (int)b);
    }
}

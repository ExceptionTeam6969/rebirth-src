//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.tree;

import org.spongepowered.asm.lib.*;
import java.util.*;

public class VarInsnNode extends AbstractInsnNode
{
    public int var;
    
    public VarInsnNode(final int n, final int var) {
        super(n);
        this.var = var;
    }
    
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }
    
    public int getType() {
        return 2;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitVarInsn(this.opcode, this.var);
        this.acceptAnnotations(methodVisitor);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new VarInsnNode(this.opcode, this.var).cloneAnnotations((AbstractInsnNode)this);
    }
}

//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.tree;

import org.spongepowered.asm.lib.*;
import java.util.*;

public class InvokeDynamicInsnNode extends AbstractInsnNode
{
    public String name;
    public String desc;
    public Handle bsm;
    public Object[] bsmArgs;
    
    public InvokeDynamicInsnNode(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        super(186);
        this.name = name;
        this.desc = desc;
        this.bsm = bsm;
        this.bsmArgs = bsmArgs;
    }
    
    public int getType() {
        return 6;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
        this.acceptAnnotations(methodVisitor);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs).cloneAnnotations((AbstractInsnNode)this);
    }
}

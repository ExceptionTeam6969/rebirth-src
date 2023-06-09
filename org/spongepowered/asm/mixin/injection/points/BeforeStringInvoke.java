//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.points;

import org.spongepowered.asm.mixin.injection.*;
import java.util.*;
import org.spongepowered.asm.lib.tree.*;
import org.spongepowered.asm.mixin.injection.struct.*;

@InjectionPoint.AtCode("INVOKE_STRING")
public class BeforeStringInvoke extends BeforeInvoke
{
    private static final String STRING_VOID_SIG = "(Ljava/lang/String;)V";
    private final String ldcValue;
    private boolean foundLdc;
    
    public BeforeStringInvoke(final InjectionPointData injectionPointData) {
        super(injectionPointData);
        this.ldcValue = injectionPointData.get("ldc", null);
        if (this.ldcValue == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires named argument \"ldc\" to specify the desired target");
        }
        if (!"(Ljava/lang/String;)V".equals(this.target.desc)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires target method with with signature " + "(Ljava/lang/String;)V");
        }
    }
    
    public boolean find(final String s, final InsnList list, final Collection collection) {
        this.foundLdc = false;
        return super.find(s, list, collection);
    }
    
    protected void inspectInsn(final String s, final InsnList list, final AbstractInsnNode abstractInsnNode) {
        if (abstractInsnNode instanceof LdcInsnNode) {
            final LdcInsnNode ldcInsnNode = (LdcInsnNode)abstractInsnNode;
            if (ldcInsnNode.cst instanceof String && this.ldcValue.equals(ldcInsnNode.cst)) {
                this.log("{} > found a matching LDC with value {}", new Object[] { this.className, ldcInsnNode.cst });
                this.foundLdc = true;
                return;
            }
        }
        this.foundLdc = false;
    }
    
    protected boolean matchesInsn(final MemberInfo memberInfo, final int n) {
        this.log("{} > > found LDC \"{}\" = {}", new Object[] { this.className, this.ldcValue, this.foundLdc });
        return this.foundLdc && super.matchesInsn(memberInfo, n);
    }
}

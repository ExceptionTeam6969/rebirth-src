//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.extensibility;

import org.spongepowered.asm.lib.tree.*;
import java.util.*;
import org.spongepowered.asm.mixin.*;

public interface IMixinInfo
{
    IMixinConfig getConfig();
    
    String getName();
    
    String getClassName();
    
    String getClassRef();
    
    byte[] getClassBytes();
    
    boolean isDetachedSuper();
    
    ClassNode getClassNode(final int p0);
    
    List getTargetClasses();
    
    int getPriority();
    
    MixinEnvironment.Phase getPhase();
}

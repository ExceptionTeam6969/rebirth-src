//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.launch.platform;

public interface IMixinPlatformAgent
{
    String getPhaseProvider();
    
    void prepare();
    
    void initPrimaryContainer();
    
    void inject();
    
    String getLaunchTarget();
}
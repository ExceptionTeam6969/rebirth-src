//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.launch.platform;

import java.net.*;

public class MixinPlatformAgentDefault extends MixinPlatformAgentAbstract
{
    public MixinPlatformAgentDefault(final MixinPlatformManager mixinPlatformManager, final URI uri) {
        super(mixinPlatformManager, uri);
    }
    
    public void prepare() {
        final String value = this.attributes.get("MixinCompatibilityLevel");
        if (value != null) {
            this.manager.setCompatibilityLevel(value);
        }
        final String value2 = this.attributes.get("MixinConfigs");
        if (value2 != null) {
            final String[] split = value2.split(",");
            for (int length = split.length, i = 0; i < length; ++i) {
                this.manager.addConfig(split[i].trim());
            }
        }
        final String value3 = this.attributes.get("MixinTokenProviders");
        if (value3 != null) {
            final String[] split2 = value3.split(",");
            for (int length2 = split2.length, j = 0; j < length2; ++j) {
                this.manager.addTokenProvider(split2[j].trim());
            }
        }
    }
    
    public void initPrimaryContainer() {
    }
    
    public void inject() {
    }
    
    public String getLaunchTarget() {
        return this.attributes.get("Main-Class");
    }
}

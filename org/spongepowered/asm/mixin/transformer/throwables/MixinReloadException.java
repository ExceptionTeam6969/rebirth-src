//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.transformer.throwables;

import org.spongepowered.asm.mixin.throwables.*;
import org.spongepowered.asm.mixin.extensibility.*;

public class MixinReloadException extends MixinException
{
    private static final long serialVersionUID = 2L;
    private final IMixinInfo mixinInfo;
    
    public MixinReloadException(final IMixinInfo mixinInfo, final String s) {
        super(s);
        this.mixinInfo = mixinInfo;
    }
    
    public IMixinInfo getMixinInfo() {
        return this.mixinInfo;
    }
}

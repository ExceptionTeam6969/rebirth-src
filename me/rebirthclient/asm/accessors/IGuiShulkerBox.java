//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package me.rebirthclient.asm.accessors;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.inventory.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ GuiShulkerBox.class })
public interface IGuiShulkerBox
{
    @Accessor("inventory")
    IInventory getInventory();
}

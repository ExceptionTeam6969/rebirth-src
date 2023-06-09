//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package me.rebirthclient.api.util.shaders.impl.outline;

import me.rebirthclient.api.util.shaders.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.gui.*;
import java.util.*;

public final class RainbowCubeOutlineShader extends FramebufferShader
{
    public float time;
    public static final RainbowCubeOutlineShader INSTANCE;
    
    public void stopDraw(final Color color, final float n, final float n2, final boolean b, final int n3, final float n4, final Color color2, final int n5, final int n6, final int n7, final int n8) {
        this.mc.gameSettings.entityShadows = this.entityShadows;
        this.framebuffer.unbindFramebuffer();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        this.startShader(color, n, n2, b, n3, n4, color2, n5, n6, n7, n8);
        this.mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
    
    public void update(final double n) {
        this.time += (float)n;
    }
    
    static {
        INSTANCE = new RainbowCubeOutlineShader();
    }
    
    public void updateUniforms(final Color color, final float n, final float n2, final boolean b, final int n3, final float n4, final Color color2, final int n5, final int n6, final int n7, final int n8) {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / this.mc.displayWidth * (n * n2), 1.0f / this.mc.displayHeight * (n * n2));
        GL20.glUniform1f(this.getUniform("divider"), 140.0f);
        GL20.glUniform1f(this.getUniform("radius"), n);
        GL20.glUniform1f(this.getUniform("maxSample"), 10.0f);
        GL20.glUniform1f(this.getUniform("alpha0"), b ? -1.0f : (n3 / 255.0f));
        GL20.glUniform2f(this.getUniform("resolution"), new ScaledResolution(this.mc).getScaledWidth() / n4, new ScaledResolution(this.mc).getScaledHeight() / n4);
        GL20.glUniform1f(this.getUniform("time"), this.time);
        GL20.glUniform1f(this.getUniform("alpha"), color2.getAlpha() / 255.0f);
        GL20.glUniform1f(this.getUniform("WAVELENGTH"), (float)n5);
        GL20.glUniform1i(this.getUniform("R"), color2.getRed());
        GL20.glUniform1i(this.getUniform("G"), color2.getGreen());
        GL20.glUniform1i(this.getUniform("B"), color2.getBlue());
        GL20.glUniform1i(this.getUniform("RSTART"), n6);
        GL20.glUniform1i(this.getUniform("GSTART"), n7);
        GL20.glUniform1i(this.getUniform("BSTART"), n8);
    }
    
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("alpha0");
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("WAVELENGTH");
        this.setupUniform("R");
        this.setupUniform("G");
        this.setupUniform("B");
        this.setupUniform("RSTART");
        this.setupUniform("GSTART");
        this.setupUniform("BSTART");
        this.setupUniform("alpha");
    }
    
    public void startShader(final Color color, final float n, final float n2, final boolean b, final int n3, final float n4, final Color color2, final int n5, final int n6, final int n7, final int n8) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(color, n, n2, b, n3, n4, color2, n5, n6, n7, n8);
    }
    
    public RainbowCubeOutlineShader() {
        super("rainbowCubeOutline.frag");
        this.time = 0.0f;
    }
}

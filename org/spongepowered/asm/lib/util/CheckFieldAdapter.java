//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.util;

import org.spongepowered.asm.lib.*;

public class CheckFieldAdapter extends FieldVisitor
{
    private boolean end;
    
    public CheckFieldAdapter(final FieldVisitor fieldVisitor) {
        this(327680, fieldVisitor);
        if (this.getClass() != CheckFieldAdapter.class) {
            throw new IllegalStateException();
        }
    }
    
    protected CheckFieldAdapter(final int n, final FieldVisitor fieldVisitor) {
        super(n, fieldVisitor);
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        this.checkEnd();
        CheckMethodAdapter.checkDesc(s, false);
        return (AnnotationVisitor)new CheckAnnotationAdapter(super.visitAnnotation(s, b));
    }
    
    public AnnotationVisitor visitTypeAnnotation(final int n, final TypePath typePath, final String s, final boolean b) {
        this.checkEnd();
        final int n2 = n >>> 24;
        if (n2 != 19) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(n2));
        }
        CheckClassAdapter.checkTypeRefAndPath(n, typePath);
        CheckMethodAdapter.checkDesc(s, false);
        return (AnnotationVisitor)new CheckAnnotationAdapter(super.visitTypeAnnotation(n, typePath, s, b));
    }
    
    public void visitAttribute(final Attribute attribute) {
        this.checkEnd();
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid attribute (must not be null)");
        }
        super.visitAttribute(attribute);
    }
    
    public void visitEnd() {
        this.checkEnd();
        this.end = true;
        super.visitEnd();
    }
    
    private void checkEnd() {
        if (this.end) {
            throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
        }
    }
}

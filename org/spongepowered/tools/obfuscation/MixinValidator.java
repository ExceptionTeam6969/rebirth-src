//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.tools.obfuscation;

import javax.annotation.processing.*;
import org.spongepowered.tools.obfuscation.interfaces.*;
import org.spongepowered.tools.obfuscation.mirror.*;
import java.util.*;
import javax.lang.model.element.*;
import javax.tools.*;
import javax.lang.model.type.*;

public abstract class MixinValidator implements IMixinValidator
{
    protected final ProcessingEnvironment processingEnv;
    protected final Messager messager;
    protected final IOptionProvider options;
    protected final IMixinValidator.ValidationPass pass;
    
    public MixinValidator(final IMixinAnnotationProcessor mixinAnnotationProcessor, final IMixinValidator.ValidationPass pass) {
        this.processingEnv = mixinAnnotationProcessor.getProcessingEnvironment();
        this.messager = (Messager)mixinAnnotationProcessor;
        this.options = (IOptionProvider)mixinAnnotationProcessor;
        this.pass = pass;
    }
    
    public final boolean validate(final IMixinValidator.ValidationPass validationPass, final TypeElement typeElement, final AnnotationHandle annotationHandle, final Collection collection) {
        return validationPass != this.pass || this.validate(typeElement, annotationHandle, collection);
    }
    
    protected abstract boolean validate(final TypeElement p0, final AnnotationHandle p1, final Collection p2);
    
    protected final void note(final String s, final Element element) {
        this.messager.printMessage(Diagnostic.Kind.NOTE, s, element);
    }
    
    protected final void warning(final String s, final Element element) {
        this.messager.printMessage(Diagnostic.Kind.WARNING, s, element);
    }
    
    protected final void error(final String s, final Element element) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, s, element);
    }
    
    protected final Collection getMixinsTargeting(final TypeMirror typeMirror) {
        return AnnotatedMixins.getMixinsForEnvironment(this.processingEnv).getMixinsTargeting(typeMirror);
    }
}

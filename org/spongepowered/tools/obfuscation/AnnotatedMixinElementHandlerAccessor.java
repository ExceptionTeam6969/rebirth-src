//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.tools.obfuscation;

import org.spongepowered.tools.obfuscation.interfaces.*;
import org.spongepowered.asm.mixin.extensibility.*;
import org.spongepowered.asm.mixin.transformer.ext.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.lib.tree.*;
import javax.tools.*;
import javax.annotation.processing.*;
import org.spongepowered.asm.mixin.gen.*;
import java.util.*;
import com.google.common.base.*;
import org.spongepowered.asm.mixin.refmap.*;
import org.spongepowered.tools.obfuscation.mirror.*;
import javax.lang.model.element.*;
import org.spongepowered.asm.mixin.injection.struct.*;
import javax.lang.model.type.*;

public class AnnotatedMixinElementHandlerAccessor extends AnnotatedMixinElementHandler implements IMixinContext
{
    public AnnotatedMixinElementHandlerAccessor(final IMixinAnnotationProcessor mixinAnnotationProcessor, final AnnotatedMixin annotatedMixin) {
        super(mixinAnnotationProcessor, annotatedMixin);
    }
    
    public ReferenceMapper getReferenceMapper() {
        return null;
    }
    
    public String getClassName() {
        return this.mixin.getClassRef().replace('/', '.');
    }
    
    public String getClassRef() {
        return this.mixin.getClassRef();
    }
    
    public String getTargetClassRef() {
        throw new UnsupportedOperationException("Target class not available at compile time");
    }
    
    public IMixinInfo getMixin() {
        throw new UnsupportedOperationException("MixinInfo not available at compile time");
    }
    
    public Extensions getExtensions() {
        throw new UnsupportedOperationException("Mixin Extensions not available at compile time");
    }
    
    public boolean getOption(final MixinEnvironment.Option option) {
        throw new UnsupportedOperationException("Options not available at compile time");
    }
    
    public int getPriority() {
        throw new UnsupportedOperationException("Priority not available at compile time");
    }
    
    public Target getTargetMethod(final MethodNode methodNode) {
        throw new UnsupportedOperationException("Target not available at compile time");
    }
    
    public void registerAccessor(final AnnotatedElementAccessor annotatedElementAccessor) {
        if (annotatedElementAccessor.getAccessorType() == null) {
            annotatedElementAccessor.printMessage((Messager)this.ap, Diagnostic.Kind.WARNING, (CharSequence)"Unsupported accessor type");
            return;
        }
        final String accessorTargetName = this.getAccessorTargetName(annotatedElementAccessor);
        if (accessorTargetName == null) {
            annotatedElementAccessor.printMessage((Messager)this.ap, Diagnostic.Kind.WARNING, (CharSequence)"Cannot inflect accessor target name");
            return;
        }
        annotatedElementAccessor.setTargetName(accessorTargetName);
        for (final TypeHandle typeHandle : this.mixin.getTargets()) {
            if (annotatedElementAccessor.getAccessorType() == AccessorInfo.AccessorType.METHOD_PROXY) {
                this.registerInvokerForTarget((AnnotatedElementInvoker)annotatedElementAccessor, typeHandle);
            }
            else {
                this.registerAccessorForTarget(annotatedElementAccessor, typeHandle);
            }
        }
    }
    
    private void registerAccessorForTarget(final AnnotatedElementAccessor annotatedElementAccessor, final TypeHandle typeHandle) {
        FieldHandle field = typeHandle.findField(annotatedElementAccessor.getTargetName(), annotatedElementAccessor.getTargetTypeName(), false);
        if (field == null) {
            if (!typeHandle.isImaginary()) {
                annotatedElementAccessor.printMessage((Messager)this.ap, Diagnostic.Kind.ERROR, (CharSequence)("Could not locate @Accessor target " + annotatedElementAccessor + " in target " + typeHandle));
                return;
            }
            field = new FieldHandle(typeHandle.getName(), annotatedElementAccessor.getTargetName(), annotatedElementAccessor.getDesc());
        }
        if (!annotatedElementAccessor.shouldRemap()) {
            return;
        }
        final ObfuscationData obfField = this.obf.getDataProvider().getObfField(field.asMapping(false).move(typeHandle.getName()));
        if (obfField.isEmpty()) {
            annotatedElementAccessor.printMessage((Messager)this.ap, Diagnostic.Kind.WARNING, (CharSequence)("Unable to locate obfuscation mapping" + (this.mixin.isMultiTarget() ? (" in target " + typeHandle) : "") + " for @Accessor target " + annotatedElementAccessor));
            return;
        }
        final ObfuscationData stripOwnerData = AnnotatedMixinElementHandler.stripOwnerData(obfField);
        try {
            this.obf.getReferenceManager().addFieldMapping(this.mixin.getClassRef(), annotatedElementAccessor.getTargetName(), annotatedElementAccessor.getContext(), stripOwnerData);
        }
        catch (ReferenceManager.ReferenceConflictException ex) {
            annotatedElementAccessor.printMessage((Messager)this.ap, Diagnostic.Kind.ERROR, (CharSequence)("Mapping conflict for @Accessor target " + annotatedElementAccessor + ": " + ex.getNew() + " for target " + typeHandle + " conflicts with existing mapping " + ex.getOld()));
        }
    }
    
    private void registerInvokerForTarget(final AnnotatedElementInvoker annotatedElementInvoker, final TypeHandle typeHandle) {
        MethodHandle method = typeHandle.findMethod(annotatedElementInvoker.getTargetName(), annotatedElementInvoker.getTargetTypeName(), false);
        if (method == null) {
            if (!typeHandle.isImaginary()) {
                annotatedElementInvoker.printMessage((Messager)this.ap, Diagnostic.Kind.ERROR, (CharSequence)("Could not locate @Invoker target " + annotatedElementInvoker + " in target " + typeHandle));
                return;
            }
            method = new MethodHandle(typeHandle, annotatedElementInvoker.getTargetName(), annotatedElementInvoker.getDesc());
        }
        if (!annotatedElementInvoker.shouldRemap()) {
            return;
        }
        final ObfuscationData obfMethod = this.obf.getDataProvider().getObfMethod(method.asMapping(false).move(typeHandle.getName()));
        if (obfMethod.isEmpty()) {
            annotatedElementInvoker.printMessage((Messager)this.ap, Diagnostic.Kind.WARNING, (CharSequence)("Unable to locate obfuscation mapping" + (this.mixin.isMultiTarget() ? (" in target " + typeHandle) : "") + " for @Accessor target " + annotatedElementInvoker));
            return;
        }
        final ObfuscationData stripOwnerData = AnnotatedMixinElementHandler.stripOwnerData(obfMethod);
        try {
            this.obf.getReferenceManager().addMethodMapping(this.mixin.getClassRef(), annotatedElementInvoker.getTargetName(), annotatedElementInvoker.getContext(), stripOwnerData);
        }
        catch (ReferenceManager.ReferenceConflictException ex) {
            annotatedElementInvoker.printMessage((Messager)this.ap, Diagnostic.Kind.ERROR, (CharSequence)("Mapping conflict for @Invoker target " + annotatedElementInvoker + ": " + ex.getNew() + " for target " + typeHandle + " conflicts with existing mapping " + ex.getOld()));
        }
    }
    
    private String getAccessorTargetName(final AnnotatedElementAccessor annotatedElementAccessor) {
        final String annotationValue = annotatedElementAccessor.getAnnotationValue();
        if (Strings.isNullOrEmpty(annotationValue)) {
            return this.inflectAccessorTarget(annotatedElementAccessor);
        }
        return annotationValue;
    }
    
    private String inflectAccessorTarget(final AnnotatedElementAccessor annotatedElementAccessor) {
        return AccessorInfo.inflectTarget(annotatedElementAccessor.getSimpleName(), annotatedElementAccessor.getAccessorType(), "", (IMixinContext)this, false);
    }
    
    public IReferenceMapper getReferenceMapper() {
        return (IReferenceMapper)this.getReferenceMapper();
    }
    
    static class AnnotatedElementInvoker extends AnnotatedElementAccessor
    {
        public AnnotatedElementInvoker(final ExecutableElement executableElement, final AnnotationHandle annotationHandle, final boolean b) {
            super(executableElement, annotationHandle, b);
        }
        
        @Override
        public String getAccessorDesc() {
            return TypeUtils.getDescriptor((ExecutableElement)this.getElement());
        }
        
        @Override
        public AccessorInfo.AccessorType getAccessorType() {
            return AccessorInfo.AccessorType.METHOD_PROXY;
        }
        
        @Override
        public String getTargetTypeName() {
            return TypeUtils.getJavaSignature(this.getElement());
        }
    }
    
    static class AnnotatedElementAccessor extends AnnotatedMixinElementHandler.AnnotatedElement
    {
        private final boolean shouldRemap;
        private final TypeMirror returnType;
        private String targetName;
        
        public AnnotatedElementAccessor(final ExecutableElement executableElement, final AnnotationHandle annotationHandle, final boolean shouldRemap) {
            super((Element)executableElement, annotationHandle);
            this.shouldRemap = shouldRemap;
            this.returnType = ((ExecutableElement)this.getElement()).getReturnType();
        }
        
        public boolean shouldRemap() {
            return this.shouldRemap;
        }
        
        public String getAnnotationValue() {
            return (String)this.getAnnotation().getValue();
        }
        
        public TypeMirror getTargetType() {
            switch (this.getAccessorType()) {
                case FIELD_GETTER: {
                    return this.returnType;
                }
                case FIELD_SETTER: {
                    return ((VariableElement)((ExecutableElement)this.getElement()).getParameters().get(0)).asType();
                }
                default: {
                    return null;
                }
            }
        }
        
        public String getTargetTypeName() {
            return TypeUtils.getTypeName(this.getTargetType());
        }
        
        public String getAccessorDesc() {
            return TypeUtils.getInternalName(this.getTargetType());
        }
        
        public MemberInfo getContext() {
            return new MemberInfo(this.getTargetName(), (String)null, this.getAccessorDesc());
        }
        
        public AccessorInfo.AccessorType getAccessorType() {
            return (this.returnType.getKind() == TypeKind.VOID) ? AccessorInfo.AccessorType.FIELD_SETTER : AccessorInfo.AccessorType.FIELD_GETTER;
        }
        
        public void setTargetName(final String targetName) {
            this.targetName = targetName;
        }
        
        public String getTargetName() {
            return this.targetName;
        }
        
        public String toString() {
            return (this.targetName != null) ? this.targetName : "<invalid>";
        }
    }
}

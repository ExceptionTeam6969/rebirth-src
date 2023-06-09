//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.tools.obfuscation.mirror;

import javax.lang.model.element.*;
import org.spongepowered.asm.mixin.injection.struct.*;
import org.spongepowered.asm.obfuscation.mapping.common.*;
import org.spongepowered.asm.util.*;
import javax.lang.model.type.*;
import java.util.*;

public class TypeHandleSimulated extends TypeHandle
{
    private final TypeElement simulatedType;
    
    public TypeHandleSimulated(final String s, final TypeMirror typeMirror) {
        this(TypeUtils.getPackage(typeMirror), s, typeMirror);
    }
    
    public TypeHandleSimulated(final PackageElement packageElement, final String s, final TypeMirror typeMirror) {
        super(packageElement, s);
        this.simulatedType = (TypeElement)((DeclaredType)typeMirror).asElement();
    }
    
    protected TypeElement getTargetElement() {
        return this.simulatedType;
    }
    
    public boolean isPublic() {
        return true;
    }
    
    public boolean isImaginary() {
        return false;
    }
    
    public boolean isSimulated() {
        return true;
    }
    
    public AnnotationHandle getAnnotation(final Class clazz) {
        return null;
    }
    
    public TypeHandle getSuperclass() {
        return null;
    }
    
    public String findDescriptor(final MemberInfo memberInfo) {
        return (memberInfo != null) ? memberInfo.desc : null;
    }
    
    public FieldHandle findField(final String s, final String s2, final boolean b) {
        return new FieldHandle((String)null, s, s2);
    }
    
    public MethodHandle findMethod(final String s, final String s2, final boolean b) {
        return new MethodHandle((TypeHandle)null, s, s2);
    }
    
    public MappingMethod getMappingMethod(final String s, final String s2) {
        final String descriptor = new SignaturePrinter(s, s2).setFullyQualified(true).toDescriptor();
        final MethodHandle methodRecursive = findMethodRecursive(this, s, descriptor, TypeUtils.stripGenerics(descriptor), true);
        return (methodRecursive != null) ? methodRecursive.asMapping(true) : super.getMappingMethod(s, s2);
    }
    
    private static MethodHandle findMethodRecursive(final TypeHandle typeHandle, final String s, final String s2, final String s3, final boolean b) {
        final TypeElement targetElement = typeHandle.getTargetElement();
        if (targetElement == null) {
            return null;
        }
        final MethodHandle method = TypeHandle.findMethod(typeHandle, s, s2, s3, b);
        if (method != null) {
            return method;
        }
        final Iterator<? extends TypeMirror> iterator = targetElement.getInterfaces().iterator();
        while (iterator.hasNext()) {
            final MethodHandle methodRecursive = findMethodRecursive((TypeMirror)iterator.next(), s, s2, s3, b);
            if (methodRecursive != null) {
                return methodRecursive;
            }
        }
        final TypeMirror superclass = targetElement.getSuperclass();
        if (superclass == null || superclass.getKind() == TypeKind.NONE) {
            return null;
        }
        return findMethodRecursive(superclass, s, s2, s3, b);
    }
    
    private static MethodHandle findMethodRecursive(final TypeMirror typeMirror, final String s, final String s2, final String s3, final boolean b) {
        if (!(typeMirror instanceof DeclaredType)) {
            return null;
        }
        return findMethodRecursive(new TypeHandle((TypeElement)((DeclaredType)typeMirror).asElement()), s, s2, s3, b);
    }
}

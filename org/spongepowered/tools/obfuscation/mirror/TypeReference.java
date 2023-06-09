//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.tools.obfuscation.mirror;

import java.io.*;
import javax.annotation.processing.*;
import javax.lang.model.element.*;

public class TypeReference implements Serializable, Comparable
{
    private static final long serialVersionUID = 1L;
    private final String name;
    private transient TypeHandle handle;
    
    public TypeReference(final TypeHandle handle) {
        this.name = handle.getName();
        this.handle = handle;
    }
    
    public TypeReference(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getClassName() {
        return this.name.replace('/', '.');
    }
    
    public TypeHandle getHandle(final ProcessingEnvironment processingEnvironment) {
        if (this.handle == null) {
            final TypeElement typeElement = processingEnvironment.getElementUtils().getTypeElement(this.getClassName());
            try {
                this.handle = new TypeHandle(typeElement);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return this.handle;
    }
    
    @Override
    public String toString() {
        return String.format("TypeReference[%s]", this.name);
    }
    
    public int compareTo(final TypeReference typeReference) {
        return (typeReference == null) ? -1 : this.name.compareTo(typeReference.name);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof TypeReference && this.compareTo((TypeReference)o) == 0;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public int compareTo(final Object o) {
        return this.compareTo((TypeReference)o);
    }
}

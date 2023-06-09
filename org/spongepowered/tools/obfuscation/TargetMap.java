//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.tools.obfuscation;

import org.spongepowered.tools.obfuscation.mirror.*;
import javax.lang.model.element.*;
import java.util.*;
import java.nio.charset.*;
import com.google.common.io.*;
import java.io.*;

public final class TargetMap extends HashMap
{
    private static final long serialVersionUID = 1L;
    private final String sessionId;
    
    private TargetMap() {
        this(String.valueOf(System.currentTimeMillis()));
    }
    
    private TargetMap(final String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getSessionId() {
        return this.sessionId;
    }
    
    public void registerTargets(final AnnotatedMixin annotatedMixin) {
        this.registerTargets(annotatedMixin.getTargets(), annotatedMixin.getHandle());
    }
    
    public void registerTargets(final List list, final TypeHandle typeHandle) {
        final Iterator<TypeHandle> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addMixin(iterator.next(), typeHandle);
        }
    }
    
    public void addMixin(final TypeHandle typeHandle, final TypeHandle typeHandle2) {
        this.addMixin(typeHandle.getReference(), typeHandle2.getReference());
    }
    
    public void addMixin(final String s, final String s2) {
        this.addMixin(new TypeReference(s), new TypeReference(s2));
    }
    
    public void addMixin(final TypeReference typeReference, final TypeReference typeReference2) {
        this.getMixinsFor(typeReference).add(typeReference2);
    }
    
    public Collection getMixinsTargeting(final TypeElement typeElement) {
        return this.getMixinsTargeting(new TypeHandle(typeElement));
    }
    
    public Collection getMixinsTargeting(final TypeHandle typeHandle) {
        return this.getMixinsTargeting(typeHandle.getReference());
    }
    
    public Collection getMixinsTargeting(final TypeReference typeReference) {
        return Collections.unmodifiableCollection((Collection<?>)this.getMixinsFor(typeReference));
    }
    
    private Set getMixinsFor(final TypeReference typeReference) {
        Set set = this.get(typeReference);
        if (set == null) {
            set = new HashSet();
            this.put(typeReference, set);
        }
        return set;
    }
    
    public void readImports(final File file) throws IOException {
        if (!file.isFile()) {
            return;
        }
        final Iterator<String> iterator = Files.readLines(file, Charset.defaultCharset()).iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().split("\t");
            if (split.length == 2) {
                this.addMixin(split[1], split[0]);
            }
        }
    }
    
    public void write(final boolean b) {
        ObjectOutputStream objectOutputStream = null;
        try {
            final File sessionFile = getSessionFile(this.sessionId);
            if (b) {
                sessionFile.deleteOnExit();
            }
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(sessionFile, true));
            objectOutputStream.writeObject(this);
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return;
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                }
                catch (IOException ex3) {
                    ex3.printStackTrace();
                }
            }
            return;
        }
        throw;
    }
    
    private static TargetMap read(final File file) {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(file));
            final TargetMap targetMap = (TargetMap)objectInputStream.readObject();
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return targetMap;
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                }
                catch (IOException ex3) {
                    ex3.printStackTrace();
                }
                return null;
            }
            return null;
        }
        throw;
    }
    
    public static TargetMap create(final String s) {
        if (s != null) {
            final File sessionFile = getSessionFile(s);
            if (sessionFile.exists()) {
                final TargetMap read = read(sessionFile);
                if (read != null) {
                    return read;
                }
            }
        }
        return new TargetMap();
    }
    
    private static File getSessionFile(final String s) {
        return new File(new File(System.getProperty("java.io.tmpdir")), String.format("mixin-targetdb-%s.tmp", s));
    }
}

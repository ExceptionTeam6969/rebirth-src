//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.tree;

import org.spongepowered.asm.lib.*;
import java.util.*;

public class FrameNode extends AbstractInsnNode
{
    public int type;
    public List local;
    public List stack;
    
    private FrameNode() {
        super(-1);
    }
    
    public FrameNode(final int type, final int n, final Object[] array, final int n2, final Object[] array2) {
        super(-1);
        switch (this.type = type) {
            case -1:
            case 0: {
                this.local = asList(n, array);
                this.stack = asList(n2, array2);
                break;
            }
            case 1: {
                this.local = asList(n, array);
                break;
            }
            case 2: {
                this.local = Arrays.asList(new Object[n]);
            }
            case 4: {
                this.stack = asList(1, array2);
                break;
            }
        }
    }
    
    public int getType() {
        return 14;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        switch (this.type) {
            case -1:
            case 0: {
                methodVisitor.visitFrame(this.type, this.local.size(), asArray(this.local), this.stack.size(), asArray(this.stack));
                break;
            }
            case 1: {
                methodVisitor.visitFrame(this.type, this.local.size(), asArray(this.local), 0, (Object[])null);
                break;
            }
            case 2: {
                methodVisitor.visitFrame(this.type, this.local.size(), (Object[])null, 0, (Object[])null);
                break;
            }
            case 3: {
                methodVisitor.visitFrame(this.type, 0, (Object[])null, 0, (Object[])null);
                break;
            }
            case 4: {
                methodVisitor.visitFrame(this.type, 0, (Object[])null, 1, asArray(this.stack));
                break;
            }
        }
    }
    
    public AbstractInsnNode clone(final Map map) {
        final FrameNode frameNode = new FrameNode();
        frameNode.type = this.type;
        if (this.local != null) {
            frameNode.local = new ArrayList();
            for (int i = 0; i < this.local.size(); ++i) {
                Object o = this.local.get(i);
                if (o instanceof LabelNode) {
                    o = map.get(o);
                }
                frameNode.local.add(o);
            }
        }
        if (this.stack != null) {
            frameNode.stack = new ArrayList();
            for (int j = 0; j < this.stack.size(); ++j) {
                Object o2 = this.stack.get(j);
                if (o2 instanceof LabelNode) {
                    o2 = map.get(o2);
                }
                frameNode.stack.add(o2);
            }
        }
        return frameNode;
    }
    
    private static List asList(final int n, final Object[] array) {
        return Arrays.asList(array).subList(0, n);
    }
    
    private static Object[] asArray(final List list) {
        final Object[] array = new Object[list.size()];
        for (int i = 0; i < array.length; ++i) {
            Object o = list.get(i);
            if (o instanceof LabelNode) {
                o = ((LabelNode)o).getLabel();
            }
            array[i] = o;
        }
        return array;
    }
}

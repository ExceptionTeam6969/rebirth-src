//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.tree;

import org.spongepowered.asm.lib.*;
import java.util.*;

public class TableSwitchInsnNode extends AbstractInsnNode
{
    public int min;
    public int max;
    public LabelNode dflt;
    public List labels;
    
    public TableSwitchInsnNode(final int min, final int max, final LabelNode dflt, final LabelNode... array) {
        super(170);
        this.min = min;
        this.max = max;
        this.dflt = dflt;
        this.labels = new ArrayList();
        if (array != null) {
            this.labels.addAll(Arrays.asList(array));
        }
    }
    
    public int getType() {
        return 11;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        final Label[] array = new Label[this.labels.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ((LabelNode)this.labels.get(i)).getLabel();
        }
        methodVisitor.visitTableSwitchInsn(this.min, this.max, this.dflt.getLabel(), array);
        this.acceptAnnotations(methodVisitor);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new TableSwitchInsnNode(this.min, this.max, clone(this.dflt, map), clone(this.labels, map)).cloneAnnotations((AbstractInsnNode)this);
    }
}

//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\23204\Desktop\cn��ǿ��������\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.tree.analysis;

import org.spongepowered.asm.lib.*;
import org.spongepowered.asm.lib.tree.*;
import java.util.*;

public class SourceInterpreter extends Interpreter implements Opcodes
{
    public SourceInterpreter() {
        super(327680);
    }
    
    protected SourceInterpreter(final int n) {
        super(n);
    }
    
    public SourceValue newValue(final Type type) {
        if (type == Type.VOID_TYPE) {
            return null;
        }
        return new SourceValue((type == null) ? 1 : type.getSize());
    }
    
    public SourceValue newOperation(final AbstractInsnNode abstractInsnNode) {
        int size = 0;
        switch (abstractInsnNode.getOpcode()) {
            case 9:
            case 10:
            case 14:
            case 15: {
                size = 2;
                break;
            }
            case 18: {
                final Object cst = ((LdcInsnNode)abstractInsnNode).cst;
                size = ((cst instanceof Long || cst instanceof Double) ? 2 : 1);
                break;
            }
            case 178: {
                size = Type.getType(((FieldInsnNode)abstractInsnNode).desc).getSize();
                break;
            }
            default: {
                size = 1;
                break;
            }
        }
        return new SourceValue(size, abstractInsnNode);
    }
    
    public SourceValue copyOperation(final AbstractInsnNode abstractInsnNode, final SourceValue sourceValue) {
        return new SourceValue(sourceValue.getSize(), abstractInsnNode);
    }
    
    public SourceValue unaryOperation(final AbstractInsnNode abstractInsnNode, final SourceValue sourceValue) {
        int size = 0;
        switch (abstractInsnNode.getOpcode()) {
            case 117:
            case 119:
            case 133:
            case 135:
            case 138:
            case 140:
            case 141:
            case 143: {
                size = 2;
                break;
            }
            case 180: {
                size = Type.getType(((FieldInsnNode)abstractInsnNode).desc).getSize();
                break;
            }
            default: {
                size = 1;
                break;
            }
        }
        return new SourceValue(size, abstractInsnNode);
    }
    
    public SourceValue binaryOperation(final AbstractInsnNode abstractInsnNode, final SourceValue sourceValue, final SourceValue sourceValue2) {
        int n = 0;
        switch (abstractInsnNode.getOpcode()) {
            case 47:
            case 49:
            case 97:
            case 99:
            case 101:
            case 103:
            case 105:
            case 107:
            case 109:
            case 111:
            case 113:
            case 115:
            case 121:
            case 123:
            case 125:
            case 127:
            case 129:
            case 131: {
                n = 2;
                break;
            }
            default: {
                n = 1;
                break;
            }
        }
        return new SourceValue(n, abstractInsnNode);
    }
    
    public SourceValue ternaryOperation(final AbstractInsnNode abstractInsnNode, final SourceValue sourceValue, final SourceValue sourceValue2, final SourceValue sourceValue3) {
        return new SourceValue(1, abstractInsnNode);
    }
    
    public SourceValue naryOperation(final AbstractInsnNode abstractInsnNode, final List list) {
        final int opcode = abstractInsnNode.getOpcode();
        int size;
        if (opcode == 197) {
            size = 1;
        }
        else {
            size = Type.getReturnType((opcode == 186) ? ((InvokeDynamicInsnNode)abstractInsnNode).desc : ((MethodInsnNode)abstractInsnNode).desc).getSize();
        }
        return new SourceValue(size, abstractInsnNode);
    }
    
    public void returnOperation(final AbstractInsnNode abstractInsnNode, final SourceValue sourceValue, final SourceValue sourceValue2) {
    }
    
    public SourceValue merge(final SourceValue sourceValue, final SourceValue sourceValue2) {
        if (sourceValue.insns instanceof SmallSet && sourceValue2.insns instanceof SmallSet) {
            final Set union = ((SmallSet)sourceValue.insns).union((SmallSet)sourceValue2.insns);
            if (union == sourceValue.insns && sourceValue.size == sourceValue2.size) {
                return sourceValue;
            }
            return new SourceValue(Math.min(sourceValue.size, sourceValue2.size), union);
        }
        else {
            if (sourceValue.size != sourceValue2.size || !sourceValue.insns.containsAll(sourceValue2.insns)) {
                final HashSet set = new HashSet();
                set.addAll(sourceValue.insns);
                set.addAll(sourceValue2.insns);
                return new SourceValue(Math.min(sourceValue.size, sourceValue2.size), set);
            }
            return sourceValue;
        }
    }
    
    public Value merge(final Value value, final Value value2) {
        return this.merge((SourceValue)value, (SourceValue)value2);
    }
    
    public void returnOperation(final AbstractInsnNode abstractInsnNode, final Value value, final Value value2) throws AnalyzerException {
        this.returnOperation(abstractInsnNode, (SourceValue)value, (SourceValue)value2);
    }
    
    public Value naryOperation(final AbstractInsnNode abstractInsnNode, final List list) throws AnalyzerException {
        return this.naryOperation(abstractInsnNode, list);
    }
    
    public Value ternaryOperation(final AbstractInsnNode abstractInsnNode, final Value value, final Value value2, final Value value3) throws AnalyzerException {
        return this.ternaryOperation(abstractInsnNode, (SourceValue)value, (SourceValue)value2, (SourceValue)value3);
    }
    
    public Value binaryOperation(final AbstractInsnNode abstractInsnNode, final Value value, final Value value2) throws AnalyzerException {
        return this.binaryOperation(abstractInsnNode, (SourceValue)value, (SourceValue)value2);
    }
    
    public Value unaryOperation(final AbstractInsnNode abstractInsnNode, final Value value) throws AnalyzerException {
        return this.unaryOperation(abstractInsnNode, (SourceValue)value);
    }
    
    public Value copyOperation(final AbstractInsnNode abstractInsnNode, final Value value) throws AnalyzerException {
        return this.copyOperation(abstractInsnNode, (SourceValue)value);
    }
    
    public Value newOperation(final AbstractInsnNode abstractInsnNode) throws AnalyzerException {
        return this.newOperation(abstractInsnNode);
    }
    
    public Value newValue(final Type type) {
        return this.newValue(type);
    }
}

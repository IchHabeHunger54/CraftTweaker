package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.data.visitor.DataVisitor;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.ByteTag;
import org.jetbrains.annotations.NotNull;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Objects;

/**
 * {@code BoolData} represents a boolean NBT data value.
 *
 * <p>Note that while this will work flawlessly with JSON, NBT actually uses {@link ByteData} objects with the values {@code 1b} (for {@code true}) and {@code 0b} (for {@code false}) instead. This is because the NBT specification does not include boolean values.</p>
 *
 * @docParam this (true as IData)
 */
@ZenCodeType.Name("crafttweaker.api.data.BoolData")
@ZenRegister
@Document("vanilla/api/data/BoolData")
public class BoolData implements IData {
    
    public static final BoolData TRUE = new BoolData(true);
    public static final BoolData FALSE = new BoolData(false);
    
    private final boolean internalValue;
    private final ByteData internalData;
    
    @ZenCodeType.Constructor
    public BoolData(boolean internalValue) {
        
        this.internalValue = internalValue;
        this.internalData = new ByteData(ByteTag.valueOf(asBool()));
    }

    /**
     * @return This object's {@link ByteData} equivalent.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    public ByteData getByteData() {
        
        return new ByteData(ByteTag.valueOf(asBool()));
    }
    
    @Override
    public ByteTag getInternal() {
        
        return internalData.getInternal();
    }
    
    @Override
    public IData or(IData other) {
        
        return of(asBool() | other.asBool());
    }
    
    @Override
    public IData and(IData other) {
        
        return of(asBool() & other.asBool());
    }
    
    @Override
    public IData xor(IData other) {
        
        return of(asBool() ^ other.asBool());
    }
    
    @Override
    public IData not() {
        
        return of(!asBool());
    }
    
    @Override
    public boolean contains(IData other) {
        
        return other.asBool() == asBool();
    }
    
    @Override
    public int compareTo(@NotNull IData other) {
        
        return Boolean.compare(asBool(), other.asBool());
    }
    
    @Override
    public boolean equalTo(IData other) {
        
        return asBool() == other.asBool();
    }
    
    @Override
    public byte asByte() {
        
        return (byte) asInt();
    }
    
    @Override
    public short asShort() {
        
        return (short) asInt();
    }
    
    @Override
    public int asInt() {
        
        return asBool() ? 1 : 0;
    }
    
    @Override
    public long asLong() {
        
        return asInt();
    }
    
    @Override
    public float asFloat() {
        
        return (float) asInt();
    }
    
    @Override
    public double asDouble() {
        
        return asInt();
    }
    
    @Override
    @ZenCodeType.Caster(implicit = true)
    public boolean asBool() {
        
        return internalValue;
    }
    
    @Override
    public IData copy() {
        
        return of(asBool());
    }
    
    @Override
    public IData copyInternal() {
        
        return copy();
    }
    
    @Override
    public <T> T accept(DataVisitor<T> visitor) {
        
        return visitor.visitBool(this);
    }
    
    @Override
    public Type getType() {
        
        return Type.BOOL;
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        BoolData boolData = (BoolData) o;
        return internalValue == boolData.internalValue;
    }
    
    @Override
    public int hashCode() {
        
        return Objects.hash(internalValue);
    }
    
    private BoolData of(boolean value) {
        
        return value ? TRUE : FALSE;
    }
    
    @Override
    public String toString() {
        
        return getAsString();
    }
    
}

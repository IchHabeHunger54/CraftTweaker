package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.data.converter.tag.TagToDataConverter;
import com.blamejared.crafttweaker.api.data.visitor.DataToStringVisitor;
import com.blamejared.crafttweaker.api.data.visitor.DataToTextComponentVisitor;
import com.blamejared.crafttweaker.api.data.visitor.DataVisitor;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.Util;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.DataVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zenscript.codemodel.OperatorType;
import org.openzen.zenscript.codemodel.type.BasicTypeID;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


/**
 * An {@code IData} object represents Minecraft NBT data that can be attached to blocks, items, entities etc. All other classes in this package inherit from this class.
 *
 * @docParam this (1 as IData)
 */
@ZenRegister(loaders = {CraftTweakerConstants.DEFAULT_LOADER_NAME, CraftTweakerConstants.TAGS_LOADER_NAME})
@ZenCodeType.Name("crafttweaker.api.data.IData")
@Document("vanilla/api/data/IData")
public interface IData extends Comparable<IData>, Iterable<IData> {
    
    /**
     * Creates a collection of the given IData members.
     *
     * <p>If all contents are numbers, the most fitting {@code ArrayData} type will be used. For example, if all the members are {@link ByteData}, then a {@link ByteArrayData} will be returned, or if all members are of {@link ByteData} or {@link IntData}, then an {@link IntArrayData} will be returned. However, if types are mixed or do not have an {@code ArrayData} counterpart, then a {@link ListData} is used instead.</p>
     *
     * @param members The members to put in the list.
     *
     * @return A list of the given members.
     *
     * @docParam members 1, 2, 3
     */
    @ZenCodeType.Method
    static IData listOf(IData... members) {
        
        if(members == null) {
            return new ListData();
        }
        
        int type = 0;
        final int byteIndex = 1;
        final int intIndex = 2;
        final int longIndex = 4;
        final int otherIndex = 8;
        
        for(IData member : members) {
            if(member instanceof ByteData) {
                type |= byteIndex;
            } else if(member instanceof IntData || member instanceof ShortData) {
                type |= intIndex;
            } else if(member instanceof LongData) {
                type |= longIndex;
            } else {
                type |= otherIndex;
            }
        }
        
        if((type & otherIndex) != 0) {
            return new ListData(members);
        } else if((type & longIndex) != 0) {
            long[] result = new long[members.length];
            for(int i = 0; i < members.length; i++) {
                result[i] = members[i].asLong();
            }
            return new LongArrayData(result);
        } else if((type & intIndex) != 0) {
            int[] result = new int[members.length];
            for(int i = 0; i < members.length; i++) {
                result[i] = members[i].asInt();
            }
            return new IntArrayData(result);
        } else if((type & byteIndex) != 0) {
            byte[] result = new byte[members.length];
            for(int i = 0; i < members.length; i++) {
                result[i] = members[i].asByte();
            }
            return new ByteArrayData(result);
        }
        
        return new ListData();
    }
    
    
    /**
     * Adds the given {@code IData} to this {@code IData}.
     *
     * @param other The {@code IData} to add.
     *
     * @return A new {@code IData}, containing the result of adding the other {@code IData} to this {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.ADD)
    @ZenCodeType.Method
    default IData add(IData other) {
        
        return notSupportedOperator(OperatorType.ADD);
    }
    
    /**
     * Subtracts the given {@code IData} from this {@code IData}.
     *
     * @param other The {@code IData} to subtract.
     *
     * @return A new {@code IData}, containing the result of subtracting the other {@code IData} from this {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.SUB)
    default IData sub(IData other) {
        
        return notSupportedOperator(OperatorType.SUB);
    }
    
    /**
     * Multiplies the given {@code IData} with this {@code IData}.
     *
     * @param other The {@code IData} to multiply with.
     *
     * @return A new {@code IData}, containing the result of multiplying the other {@code IData} with this {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MUL)
    default IData mul(IData other) {
        
        return notSupportedOperator(OperatorType.MUL);
    }
    
    /**
     * Divides this {@code IData} by the given {@code IData}.
     *
     * @param other The {@code IData} to divide by.
     *
     * @return A new {@code IData}, containing the result of dividing this {@code IData} by the other {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.DIV)
    default IData div(IData other) {
        
        return notSupportedOperator(OperatorType.DIV);
    }
    
    /**
     * Applies a modulo operation to this {@code IData} against the given {@code IData}.
     *
     * @param other The {@code IData} to modulo against.
     *
     * @return A new {@code IData}, containing the result of applying the modulo operation to this {@code IData} against the other {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MOD)
    default IData mod(IData other) {
        
        return notSupportedOperator(OperatorType.MOD);
    }
    
    /**
     * Concatenates the given {@code IData} to this {@code IData}.
     *
     * @param other The {@code IData} to concatenate.
     *
     * @return A new {@code IData}, containing the result of concatenating the other {@code IData} to this {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.CAT)
    default IData cat(IData other) {
        
        return notSupportedOperator(OperatorType.CAT);
    }
    
    /**
     * Applies a bitwise OR (|) operation to this {@code IData} against the given {@code IData}.
     *
     * @param other The {@code IData} to OR against.
     *
     * @return A new {@code IData}, containing the result of applying the bitwise OR operation to this {@code IData} against the other {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.OR)
    default IData or(IData other) {
        
        return notSupportedOperator(OperatorType.OR);
    }
    
    /**
     * Applies a bitwise AND (&) operation to this {@code IData} against the given {@code IData}.
     *
     * @param other The {@code IData} to AND against.
     *
     * @return A new {@code IData}, containing the result of applying the bitwise AND operation to this {@code IData} against the other {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.AND)
    default IData and(IData other) {
        
        return notSupportedOperator(OperatorType.AND);
    }
    
    /**
     * Applies a bitwise XOR (^) operation to this {@code IData} against the given {@code IData}.
     *
     * @param other The {@code IData} to XOR against.
     *
     * @return A new {@code IData}, containing the result of applying the bitwise XOR operation to this {@code IData} against the other {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.XOR)
    default IData xor(IData other) {
        
        return notSupportedOperator(OperatorType.XOR);
    }
    
    /**
     * Negates this {@code IData}.
     *
     * @return The negation of this {@code IData}.
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.NEG)
    default IData neg() {
        
        return notSupportedOperator(OperatorType.NEG);
    }
    
    //TODO ZC bug thinks it is cat instead of invert
    ///**
    // * Inverts this {@code IData}.
    // *
    // * @return The invertion of this {@code IData}.
    // */
    //@ZenCodeType.Operator(ZenCodeType.OperatorType.INVERT)
    //default IData operatorInvert() {
    //
    //    return notSupportedOperator(OperatorType.INVERT);
    //}
    
    /**
     * Applies a NOT (!) operation to this {@code IData}.
     *
     * @return The result of the NOT operation.
     *
     * @docParam this true
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.NOT)
    default IData not() {
        
        return notSupportedOperator(OperatorType.NOT);
    }
    
    /**
     * Puts the given value inside this {@code IData} at the given index. Will only work for {@link ListData}, {@link MapData} and the various {@code ArrayData} types.
     *
     * @param index The key to store the data at.
     * @param value The data to store.
     *
     * @docParam index "key"
     * @docParam value "value"
     * @docParam this new MapData()
     */
    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.INDEXSET)
    default void put(String index, @ZenCodeType.Nullable IData value) {
        
        notSupportedOperator(OperatorType.INDEXSET);
    }
    
    /**
     * Gets the data from this {@code IData} at the given index. Will only work for {@link ListData} and the various {@code ArrayData} types.
     *
     * @param index The index to get the data from.
     *
     * @return The data at the index.
     *
     * @docParam index 0
     * @docParam this [1, 2, 3] as IData
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.INDEXGET)
    default IData getAt(int index) {
        
        return notSupportedOperator(OperatorType.INDEXGET);
    }
    
    /**
     * Gets the data from this {@code IData} at the given key. Will only work for {@link MapData}.
     *
     * @param key The key to get the data from.
     *
     * @return The data at the key.
     *
     * @docParam index "key"
     * @docParam this {key: "value"}
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.INDEXGET)
    default IData getAt(String key) {
        
        return notSupportedOperator(OperatorType.INDEXGET);
    }
    
    /**
     * Checks if this {@code IData} contains the other {@code IData}.
     *
     * <p>For most data types, this will check equality of the data. For {@link MapData}, it will check if the other data is a string, and then check if it contains a key with that name.</p>
     *
     * @param other The other {@code IData} to check.
     *
     * @return {@code true} if this {@code IData} contains the other {@code IData}, {@code false} if not.
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.CONTAINS)
    default boolean contains(IData other) {
        
        return notSupportedOperator(OperatorType.CONTAINS);
    }
    
    /**
     * Compares this {@code IData} with the other {@code IData}.
     *
     * @param other The {@code IData} to compare with.
     *
     * @return The comparison result: {@code -1} if this {@code IData} is considered lesser than the other {@code IData}, {@code 1} if this {@code IData} is considered greater than the other {@code IData}, and {@code 0} if they are considered equal.
     *
     * @docParam other 5
     */
    @Override
    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.COMPARE)
    default int compareTo(@NotNull IData other) {
        
        return notSupportedOperator(OperatorType.COMPARE);
    }
    
    /**
     * Removes the stored data at the given index.
     *
     * @param index The index to remove.
     *
     * @docParam index 0
     * @docParam this [1, 2, 3] as IData
     */
    @ZenCodeType.Method
    default void remove(int index) {
        
        doesNot("support removal by index");
    }
    
    /**
     * Removes the stored data at the given key.
     *
     * @param key The key to remove.
     *
     * @docParam key "key"
     * @docParam this {key: "value"} as IData
     */
    @ZenCodeType.Method
    default void remove(String key) {
        
        doesNot("support removal by key");
    }
    
    /**
     * Sets the given value inside this {@code IData} at the given index.
     *
     * @param name The key to store the data at.
     * @param data The data to store.
     *
     * @docParam index "key"
     * @docParam value "value"
     * @docParam this new MapData()
     */
    @ZenCodeType.Method
    default void setAt(String name, @ZenCodeType.Nullable IData data) {
        
        put(name, data);
    }
    
    /**
     * Checks if this {@code IData} is equal to the other {@code IData}.
     *
     * @param other The other {@code IData} to check equality of.
     *
     * @return {@code true} if this {@code IData} is equal to the other {@code IData}, {@code false} otherwise.
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.EQUALS)
    default boolean equalTo(IData other) {
        
        return notSupportedOperator(OperatorType.EQUALS);
    }
    
    /**
     * Applies a SHL (<<) operation to this {@code IData} by the given {@code IData}.
     *
     * @param other The {@code IData} to SHL by.
     *
     * @return A new {@code IData}, containing the result of applying the SHL operation to this {@code IData} by the other {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.SHL)
    default IData shl(IData other) {
        
        return notSupportedOperator(OperatorType.SHL);
    }

    /**
     * Applies a SHR (>>) operation to this {@code IData} by the given {@code IData}.
     *
     * @param other The {@code IData} to SHR by.
     *
     * @return A new {@code IData}, containing the result of applying the SHR operation to this {@code IData} by the other {@code IData}.
     *
     * @docParam other 2
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.SHR)
    default IData shr(IData other) {
        
        return notSupportedOperator(OperatorType.SHR);
    }
    
    /**
     * Casts this {@code IData} to a {@code bool}.
     *
     * @return This {@code IData} as a {@code bool}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default boolean asBool() {
        
        return notSupportedCast(BasicTypeID.BOOL);
    }
    
    /**
     * Casts this {@code IData} to a {@code byte}.
     *
     * @return This {@code IData} as a {@code byte}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default @ZenCodeType.Unsigned byte asByte() {
        
        return notSupportedCast(BasicTypeID.BYTE);
    }
    
    /**
     * Casts this {@code IData} to a {@code short}.
     *
     * @return This {@code IData} as a {@code short}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default short asShort() {
        
        return notSupportedCast(BasicTypeID.SHORT);
    }
    
    /**
     * Casts this {@code IData} to an {@code int}.
     *
     * @return This {@code IData} as an {@code int}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default int asInt() {
        
        return notSupportedCast(BasicTypeID.INT);
    }
    
    /**
     * Casts this {@code IData} to a {@code long}.
     *
     * @return This {@code IData} as a {@code long}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default long asLong() {
        
        return notSupportedCast(BasicTypeID.LONG);
    }
    
    /**
     * Casts this {@code IData} to a {@code float}.
     *
     * @return This {@code IData} as a {@code float}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default float asFloat() {
        
        return notSupportedCast(BasicTypeID.FLOAT);
    }
    
    /**
     * Casts this {@code IData} to a {@code double}.
     *
     * @return This {@code IData} as a {@code double}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default double asDouble() {
        
        return notSupportedCast(BasicTypeID.DOUBLE);
    }
    
    /**
     * Casts this {@code IData} to a {@code string}. Note: This version includes escaped quotes around the result, use {@link IData#getAsString()} instead if you do not need the quotes.
     *
     * @return This {@code IData} as a {@code string}.
     */
    @ZenCodeType.Method
    default String asString() {
        
        return this.accept(DataToStringVisitor.ESCAPE);
    }
    
    /**
     * Casts this {@code IData} to a {@code string}. Note: This version does not include escaped quotes around the result, use {@link IData#asString()} instead if you need quotes.
     *
     * @return This {@code IData} as a {@code string}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default String getAsString() {
        
        return this.accept(DataToStringVisitor.PLAIN);
    }
    
    /**
     * Casts this {@code IData} to a {@code List}.
     *
     * @return This {@code IData} as a {@code List}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default List<IData> asList() {
        
        return notSupportedCast("IData[]");
    }
    
    /**
     * Casts this {@code IData} to a {@code Map}.
     *
     * @return This {@code IData} as a {@code Map}.
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default Map<String, IData> asMap() {
        
        return notSupportedCast("IData[string]");
    }
    
    /**
     * Casts this {@code IData} to a {@code byte} array (list).
     *
     * @return This {@code IData} as a {@code byte} array (list).
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default byte[] asByteArray() {
        //TODO this is actually sbyte[], but @Unsigned doesn't work on arrays right now
        return notSupportedCast("byte[]");
    }
    
    /**
     * Casts this {@code IData} to an {@code int} array (list).
     *
     * @return This {@code IData} as an {@code int} array (list).
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default int[] asIntArray() {
        
        return notSupportedCast("int[]");
    }
    
    /**
     * Casts this {@code IData} to a {@code long} array (list).
     *
     * @return This {@code IData} as a {@code long} array (list).
     */
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default long[] asLongArray() {
        
        return notSupportedCast("long[]");
    }
    
    /**
     * Returns the length of this {@code IData}.
     *
     * @return The length of this {@code IData}.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("length")
    default int length() {
        
        return doesNot("have a length");
    }
    
    /**
     * Returns the keys of this {@code IData}.
     *
     * @return The keys of this {@code IData}.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("keys")
    default Set<String> getKeys() {
        
        return doesNot("is not indexable by keys");
    }
    
    @NotNull
    @Override
    default Iterator<IData> iterator() {
        
        return doesNot("support iteration");
    }
    
    /**
     * Merges the given {@code IData} with this {@code IData}.
     *
     * @param other The {@code IData} to merge with.
     *
     * @return The result of merging the {@code IData}s.
     *
     * @docParam this {}
     */
    @ZenCodeType.Method
    default IData merge(IData other) {
        
        return doesNot("support merging");
    }
    
    /**
     * Checks if this {@code IData} is empty.
     *
     * @return {@code true} if this {@code IData} is empty, {@code false} otherwise.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("isEmpty")
    default boolean isEmpty() {
        
        return length() == 0;
    }
    
    /**
     * Returns the internal ID of this {@code IData}.
     *
     * @return the internal ID of this {@code IData}.
     */
    @ZenCodeType.Method
    default byte getId() {
        
        return getInternal().getId();
    }
    
    /**
     * Returns the internal {@link Tag} stored in this {@code IData}.
     *
     * @return The internal {@link Tag} that this {@code IData} represents.
     */
    Tag getInternal();
    
    @ZenCodeType.Method
    IData copy();
    
    IData copyInternal();
    
    <T> T accept(DataVisitor<T> visitor);
    
    /**
     * Maps this {@code IData} to another {@code IData}, based on the given function.
     *
     * @param operation The function to apply to this {@code IData}.
     *
     * @return A new {@code IData}, creating from applying the given function to this {@code IData}.
     *
     * @docParam operation (data) => 3
     */
    @ZenCodeType.Method
    default IData map(Function<IData, IData> operation) {
        
        return operation.apply(this);
    }
    
    /**
     * Returns the {@link Type} of this {@code IData}.
     *
     * @return The {@link Type} of this {@code IData}.
     */
    Type getType();
    
    private <T> T doesNot(String message) {
        
        throw new UnsupportedOperationException("Data type: '%s' does not %s!".formatted(getType(), message));
    }
    
    private <T> T notSupportedOperator(OperatorType operator) {
        
        return doesNot("support the '%s' ('%s') operator".formatted(operator.compiledName, operator.operator));
    }
    
    private <T> T notSupportedCast(String toType) {
        
        return doesNot("support being cast to '%s'".formatted(toType));
    }
    
    private <T> T notSupportedCast(BasicTypeID toType) {
        
        return notSupportedCast(toType.name);
    }
    
    /**
     * Checks if this {@code IData} supports being cast to a {@code List}.
     *
     * @return {@code true} if this {@code IData} can be cast to a {@code List}, {@code false} otherwise.
     */
    @ApiStatus.Internal
    default boolean isListable() {
        
        return false;
    }
    
    /**
     * Checks if this {@code IData} supports being cast to a {@code Map}.
     *
     * @return {@code true} if this {@code IData} can be cast to a {@code Map}, {@code false} otherwise.
     */
    @ApiStatus.Internal
    default boolean isMappable() {
        
        return false;
    }
    
    default boolean containsList(List<IData> dataValues) {
        
        if(getInternal() instanceof CollectionTag<?> internal) {
            outer:
            for(IData dataValue : dataValues) {
                for(Tag value : internal) {
                    if(TagToDataConverter.convert(value).contains(dataValue)) {
                        continue outer;
                    }
                }
        
                return false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * Used to specify what "type" of {@code IData} this is. This is primarily to make it easier to have a {@code Map} with an {@code IData}-based key.
     *
     * <p>See also: {@link DataToTextComponentVisitor#DATA_TO_COMPONENT}</p>
     */
    enum Type {
        BOOL, BYTE_ARRAY, BYTE, DOUBLE, FLOAT, INT_ARRAY, INT, LIST, LONG_ARRAY, LONG, MAP, SHORT, STRING;
    }
    
}

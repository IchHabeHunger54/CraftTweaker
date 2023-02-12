package com.blamejared.crafttweaker.api.data.converter.tag;

import com.blamejared.crafttweaker.api.data.IData;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

public class TagToDataConverter {
    
    /**
     * Converts the given {@link Tag} to its {@link IData} representation.
     *
     * @param tag The {@link Tag} to convert.
     *
     * @return The {@link IData} representation of the {@link Tag}.
     */
    @Nullable
    public static IData convert(Tag tag) {
        
        if(tag == null) {
            return null;
        }
        TagToDataVisitor visitor = new TagToDataVisitor();
        return visitor.visit(tag);
    }
    
}

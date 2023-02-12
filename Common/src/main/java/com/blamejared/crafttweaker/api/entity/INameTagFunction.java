package com.blamejared.crafttweaker.api.entity;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.world.entity.Entity;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@FunctionalInterface
@Document("vanilla/api/entity/INameTagFunction")
@ZenCodeType.Name("crafttweaker.api.entity.INameTagFunction")
public interface INameTagFunction {
    /**
     * Applies the function with the given parameters.
     *
     * @param entity The {@link Entity} to use.
     * @param result The {@link NameTagResult} to use.
     */
    @ZenCodeType.Method
    void apply(Entity entity, NameTagResult result);
    
}

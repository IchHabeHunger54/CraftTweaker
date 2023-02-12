package com.blamejared.crafttweaker.api.entity;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.world.entity.Entity;
import org.openzen.zencode.java.ZenCodeType;

/**
 * An {@code INameTagFunction} is a function that determines an entity's name tag properties.
 *
 * <p>The function takes two parameters: an {@link Entity}, which the name tag will be applied for, and a {@link NameTagResult}. This {@link NameTagResult} contains the default values the game would end up using. By modifying the values in this {@link NameTagResult}, the desired behavior can be achieved.</p>
 *
 * <p>This is a functional interface. A functional interface can be written using lambda syntax, for example like this: {@code (entity, result) => { result.setContent(Component.literal("Barack Obama, President of the Villagers")); }}</p>
 */
@ZenRegister
@FunctionalInterface
@Document("vanilla/api/entity/INameTagFunction")
@ZenCodeType.Name("crafttweaker.api.entity.INameTagFunction")
public interface INameTagFunction {
    /**
     * Applies the function with the given parameters. Typically, this is called by the game for you.
     *
     * @param entity The {@link Entity} to use.
     * @param result The {@link NameTagResult} to use.
     */
    @ZenCodeType.Method
    void apply(Entity entity, NameTagResult result);
    
}

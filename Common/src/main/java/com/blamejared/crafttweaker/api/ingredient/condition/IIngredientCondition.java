package com.blamejared.crafttweaker.api.ingredient.condition;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.ingredient.condition.serializer.IIngredientConditionSerializer;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("crafttweaker.api.ingredient.condition.IIngredientCondition")
@Document("vanilla/api/ingredient/condition/IIngredientCondition")
public interface IIngredientCondition<T extends IIngredient> {
    /**
     * Checks if the given {@link IItemStack} matches this {@link IIngredient}.
     *
     * @param stack The {@link IItemStack} to check.
     *
     * @return {@code true} if the given {@link IItemStack} matches this {@link IIngredient}, {@code false} otherwise.
     */
    @ZenCodeType.Method
    boolean matches(IItemStack stack);

    /**
     * Returns the string equivalent of this ingredient condition.
     */
    @ZenCodeType.Method
    String getCommandString(T ingredient);
    
    boolean ignoresDamage();
    
    IIngredientConditionSerializer getSerializer();
    
    default void write(FriendlyByteBuf buffer) {
        
        getSerializer().toNetwork(buffer, this);
    }
    
    default JsonObject toJson() {
        
        return getSerializer().toJson(this);
    }
    
    default ResourceLocation getType() {
        
        return getSerializer().getType();
    }
    
}

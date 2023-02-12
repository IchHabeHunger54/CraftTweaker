package com.blamejared.crafttweaker.api.ingredient;


import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.bracket.CommandStringDisplayable;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import org.openzen.zencode.java.ZenCodeType;

/**
 * An {@link IIngredient} with additional amount information. Typically used in modded machines, there are no vanilla recipes that make use of this.
 */
@ZenRegister
@Document("vanilla/api/ingredient/IIngredientWithAmount")
@ZenCodeType.Name("crafttweaker.api.ingredient.IIngredientWithAmount")
public interface IIngredientWithAmount extends CommandStringDisplayable {
    
    /**
     * Returns the backing {@link IIngredient}.
     *
     * @return The backing {@link IIngredient}.
     */
    @ZenCodeType.Getter("ingredient")
    IIngredient getIngredient();
    
    /**
     * Returns the amount of items in this {@code IIngredientWithAmount}.
     *
     * @return The amount of items in this {@code IIngredientWithAmount}.
     */
    @ZenCodeType.Getter("amount")
    int getAmount();
    
}

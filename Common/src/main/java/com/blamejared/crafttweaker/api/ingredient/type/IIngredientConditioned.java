package com.blamejared.crafttweaker.api.ingredient.type;


import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.MapData;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.ingredient.condition.IIngredientCondition;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.platform.Services;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.world.item.crafting.Ingredient;
import org.openzen.zencode.java.ZenCodeType;

/**
 * An {@link IIngredient} with a condition applied.
 *
 * <p>If multiple conditions are applied, these are placed in layers of base ingredients. This means that e.g. for two conditions, this ingredient's {@link IIngredientConditioned#base} is another {@link IIngredientConditioned}, whose base is a normal {@link IIngredient}.</p>
 *
 * @param <T> The {@link IIngredient} class, or a subclass thereof, of which {@link IIngredientConditioned#base} is an instance.
 */
@ZenRegister
@ZenCodeType.Name("crafttweaker.api.ingredient.type.IngredientConditioned")
@Document("vanilla/api/ingredient/type/IngredientConditioned")
public class IIngredientConditioned<T extends IIngredient> implements IIngredient {
    
    private final T base;
    private final IIngredientCondition<T> condition;
    
    public IIngredientConditioned(T base, IIngredientCondition<T> condition) {
        
        this.base = base;
        this.condition = condition;
    }
    
    @Override
    public Ingredient asVanillaIngredient() {
        
        return Services.REGISTRY.getIngredientConditioned(this);
    }
    
    @ZenCodeType.Getter("condition")
    public IIngredientCondition<T> getCondition() {
        
        return condition;
    }
    
    @Override
    public String getCommandString() {
        
        return condition.getCommandString(base);
    }
    
    @ZenCodeType.Getter("baseIngredient")
    public T getBaseIngredient() {
        
        return base;
    }
    
    @Override
    @ZenCodeType.Method
    public boolean matches(IItemStack stack, boolean ignoreDamage) {
        
        return base.matches(stack, condition.ignoresDamage()) && condition.matches(stack);
    }
    
    @Override
    @ZenCodeType.Getter("items")
    public IItemStack[] getItems() {
        
        return base.getItems();
    }
    
    @Override
    @ZenCodeType.Caster(implicit = true)
    public MapData asMapData() {
        
        return base.asMapData();
    }
    
    @Override
    @ZenCodeType.Caster(implicit = true)
    public IData asIData() {
        
        return base.asIData();
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        
        IIngredientConditioned<?> that = (IIngredientConditioned<?>) o;
        
        if(!base.equals(that.base)) {
            return false;
        }
        return condition.equals(that.condition);
    }
    
    @Override
    public int hashCode() {
        
        int result = base.hashCode();
        result = 31 * result + condition.hashCode();
        return result;
    }
    
}

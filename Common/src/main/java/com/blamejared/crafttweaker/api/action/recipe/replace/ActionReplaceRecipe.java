package com.blamejared.crafttweaker.api.action.recipe.replace;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.base.IRuntimeAction;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipeByName;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.NameUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ActionReplaceRecipe<T extends Recipe<?>> implements IRuntimeAction {
    
    private static final String REPLACER_DISCRIMINATOR = "replacer";
    
    private final ResourceLocation oldName;
    private final ResourceLocation newName;
    private final IRecipeManager<T> manager;
    private final Supplier<ActionAddRecipe<T>> addRecipe;
    private final ActionRemoveRecipeByName<T> removeRecipe;
    
    ActionReplaceRecipe(final ResourceLocation name, final IRecipeManager<T> manager, final Function<ResourceLocation, T> recipeCreator) {
        
        this.oldName = name;
        this.newName = this.createNewName();
        this.manager = manager;
        this.addRecipe = () -> new ActionAddRecipe<>(manager, recipeCreator.apply(this.newName));
        this.removeRecipe = new ActionRemoveRecipeByName<>(this.manager, this.oldName);
    }
    
    @Override
    public void apply() {
        
        CraftTweakerAPI.apply(this.removeRecipe);
        CraftTweakerAPI.apply(this.addRecipe.get());
    }
    
    @Override
    public String describe() {
        
        return "Replacing recipe %s in manager %s (new name: %s)".formatted(
                this.oldName,
                this.manager.getCommandString(),
                this.newName
        );
    }
    
    private ResourceLocation createNewName() {
        
        return NameUtil.isAutogeneratedName(this.oldName) ? this.tweakAutogenerated() : this.makeNewAutogenerated();
    }
    
    private ResourceLocation tweakAutogenerated() {
        
        final String oldPath = this.oldName.getPath();
        final int dot = oldPath.lastIndexOf('.');
        final int number = Integer.parseInt(oldPath.substring(dot + 1));
        final String newPath = oldPath.substring(0, dot) + '.' + (number + 1);
        return new ResourceLocation(this.oldName.getNamespace(), newPath);
    }
    
    private ResourceLocation makeNewAutogenerated() {
        
        final String tweakedRecipeName = this.oldName.getNamespace() + '.' + this.oldName.getPath() + '.' + 1;
        return NameUtil.generateNameFrom(REPLACER_DISCRIMINATOR, tweakedRecipeName);
    }
    
}

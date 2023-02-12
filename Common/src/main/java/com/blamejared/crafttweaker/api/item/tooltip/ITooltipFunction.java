package com.blamejared.crafttweaker.api.item.tooltip;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.entity.NameTagResult;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;

/**
 * An {@code INameTagFunction} is a function that can modify an {@link IItemStack}'s tooltips in great detail.
 *
 * <p>The function takes three parameters: an {@link IItemStack}, which the tooltips will be modified for, a list of tooltip {@link Component}s, which is what the game itself is set to render, and a {@link TooltipFlag} that determines whether advanced tooltips (F3+H) are active or not. The desired effects can be achieved by adding or removing {@link Component}s to or from the list.</p>
 *
 * <p>This is a functional interface. A functional interface can be written using lambda syntax, for example like this: {@code (stack, tooltip, flag) => { if flag.isAdvanced() tooltip.add(Component.literal("Peek-a-boo!")); }}</p>
 */
@ZenRegister
@FunctionalInterface
@Document("vanilla/api/item/tooltip/ITooltipFunction")
@ZenCodeType.Name("crafttweaker.api.item.tooltip.ITooltipFunction")
public interface ITooltipFunction {
    /**
     * Applies the function with the given parameters.
     *
     * @param stack The {@link IItemStack} to use.
     * @param tooltip The tooltip list to use.
     * @param flag The {@link TooltipFlag} to use.
     */
    @ZenCodeType.Method
    void apply(IItemStack stack, List<Component> tooltip, TooltipFlag flag);
    
}

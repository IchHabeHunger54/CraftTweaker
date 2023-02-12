package com.blamejared.crafttweaker.api.ingredient;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.item.ActionModifyAttribute;
import com.blamejared.crafttweaker.api.action.item.ActionSetBurnTime;
import com.blamejared.crafttweaker.api.action.item.tooltip.ActionAddShiftedTooltip;
import com.blamejared.crafttweaker.api.action.item.tooltip.ActionAddTooltip;
import com.blamejared.crafttweaker.api.action.item.tooltip.ActionClearTooltip;
import com.blamejared.crafttweaker.api.action.item.tooltip.ActionModifyShiftedTooltip;
import com.blamejared.crafttweaker.api.action.item.tooltip.ActionModifyTooltip;
import com.blamejared.crafttweaker.api.action.item.tooltip.ActionRemoveRegexTooltip;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.bracket.CommandStringDisplayable;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.MapData;
import com.blamejared.crafttweaker.api.data.converter.JSONConverter;
import com.blamejared.crafttweaker.api.ingredient.condition.IIngredientCondition;
import com.blamejared.crafttweaker.api.ingredient.condition.type.ConditionAnyDamage;
import com.blamejared.crafttweaker.api.ingredient.condition.type.ConditionCustom;
import com.blamejared.crafttweaker.api.ingredient.condition.type.ConditionDamaged;
import com.blamejared.crafttweaker.api.ingredient.condition.type.ConditionDamagedAtLeast;
import com.blamejared.crafttweaker.api.ingredient.condition.type.ConditionDamagedAtMost;
import com.blamejared.crafttweaker.api.ingredient.transform.IIngredientTransformer;
import com.blamejared.crafttweaker.api.ingredient.transform.type.TransformCustom;
import com.blamejared.crafttweaker.api.ingredient.transform.type.TransformDamage;
import com.blamejared.crafttweaker.api.ingredient.transform.type.TransformReplace;
import com.blamejared.crafttweaker.api.ingredient.transform.type.TransformReuse;
import com.blamejared.crafttweaker.api.ingredient.type.IIngredientConditioned;
import com.blamejared.crafttweaker.api.ingredient.type.IIngredientList;
import com.blamejared.crafttweaker.api.ingredient.type.IIngredientTransformed;
import com.blamejared.crafttweaker.api.ingredient.type.IngredientWithAmount;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.item.tooltip.ITooltipFunction;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 * The CraftTweaker ingredient class, which is used to power recipes and item stack matching.
 *
 * <p>The main difference to {@link IItemStack} is that this class also includes item tags. Typically, a recipe will have {@code IIngredient} inputs (as any valid item can be used) and {@link IItemStack} outputs (as recipes can only have one defined output, not a range of outputs to choose from).</p>
 *
 * @docParam this <tag:items:minecraft:wool>
 */
@ZenRegister
@ZenCodeType.Name("crafttweaker.api.ingredient.IIngredient")
@Document("vanilla/api/ingredient/IIngredient")
public interface IIngredient extends CommandStringDisplayable {
    
    /**
     * Checks if this {@code IIngredient} matches the given {@link IItemStack}. This version is damage-sensitive, use {@link IIngredient#matches(IItemStack, boolean)} to disable damage sensitivity.
     *
     * @param stack The {@link IItemStack} to check.
     *
     * @return {@code true} if this {@code IIngredient} matches the given {@link IItemStack}, {@code false} if not.
     *
     * @docParam stack <item:minecraft:iron_ingot>
     */
    @ZenCodeType.Method
    default boolean matches(IItemStack stack) {
        
        return matches(stack, false);
    }
    
    /**
     * Checks if this {@code IIngredient} matches the given {@link IItemStack}.
     *
     * @param stack        The {@link IItemStack} to check.
     * @param ignoreDamage Whether damage should be checked or not.
     *
     * @return {@code true} if this {@code IIngredient} matches the given {@link IItemStack}, {@code false} if not.
     *
     * @docParam stack <item:minecraft:iron_ingot>
     * @docParam ignoreDamage true
     */
    @ZenCodeType.Method
    boolean matches(IItemStack stack, boolean ignoreDamage);
    
    /**
     * Checks if this {@code IIngredient} is empty.
     *
     * @return {@code true} if this {@code IIngredient} is empty, {@code false} if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("empty")
    default boolean isEmpty() {
        
        return asVanillaIngredient().isEmpty();
    }
    
    /**
     * Checks if this {@code IIngredient} contains the given {@code IIngredient}.
     *
     * @param ingredient The {@code IIngredient} to check.
     *
     * @return {@code true} if this {@code IIngredient} contains the given {@code IIngredient}, {@code false} if not.
     *
     * @docParam ingredient (<item:minecraft:iron_ingot> | <item:minecraft:gold_ingot>)
     */
    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.CONTAINS)
    default boolean contains(IIngredient ingredient) {
        
        return Arrays.stream(ingredient.getItems()).allMatch(this::matches);
    }
    
    /**
     * Creates a vanilla {@link Ingredient} matching this one.
     *
     * @return A new vanilla {@link Ingredient} matching this one.
     */
    @ZenCodeType.Method
    @ZenCodeType.Caster(implicit = true)
    Ingredient asVanillaIngredient();
    
    /**
     * Returns the crafting remainder item of this {@code IIngredient}. Relevant e.g. for various bucket- or bowl-based items.
     *
     * @param stack The {@link IItemStack} to get the crafting remainder for.
     *
     * @return The crafting remainder item of this {@code IIngredient}.
     *
     * @docParam stack <item:minecraft:iron_ingot>
     */
    @ZenCodeType.Method
    default IItemStack getRemainingItem(IItemStack stack) {
        
        Item remainingItem = stack.getInternal()
                .getItem()
                .getCraftingRemainingItem();
        if(remainingItem != null) {
            
            return IItemStack.of(remainingItem.getDefaultInstance());
        }
        return IItemStack.empty();
    }
    
    @ZenCodeType.Getter("commandString")
    String getCommandString();

    /**
     * Returns a list of valid {@link IItemStack}s for this {@code IIngredient}.
     *
     * @return A list of valid {@link IItemStack}s for this {@code IIngredient}.
     */
    @ZenCodeType.Getter("items")
    IItemStack[] getItems();
    
    /**
     * Sets the burn time of this {@code IIngredient}, for use in furnaces and other machines.
     *
     * @param time The burn time to set.
     *
     * @docParam time 500
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("burnTime")
    default void setBurnTime(int time) {
        
        CraftTweakerAPI.apply(new ActionSetBurnTime(this, time));
    }

    /**
     * Clears any and all tooltips this {@code IIngredient} has. This includes shift-only tooltips, advanced-only tooltips, NBT-dependant tooltips, etc.
     *
     * @param leaveName By default, this method will also clear the item name itself. Set this to {@code true} to leave the name.
     */
    @ZenCodeType.Method
    default void clearTooltip(@ZenCodeType.OptionalBoolean() boolean leaveName) {
        
        CraftTweakerAPI.apply(new ActionClearTooltip(this, leaveName));
    }

    /**
     * Adds a new tooltip line to this {@code IIngredient}.
     *
     * @param content The contents of the new line.
     *
     * @docParam content Component.literal("I am a tooltip. I give tips on tools.");
     */
    @ZenCodeType.Method
    default void addTooltip(Component content) {
        
        CraftTweakerAPI.apply(new ActionAddTooltip(this, content));
    }

    /**
     * Adds a new tooltip line to this {@code IIngredient} that only shows when Shift is held down.
     *
     * @param content     The contents of the new line.
     * @param showMessage The message that will be shown when Shift is not held down, and will be hidden when Shift is held down. This is useful for messages like "Hold Shift for more information".
     *
     * @docParam content Component.literal("I am a hidden tooltip. I give hidden tips on tools.");
     * @docParam showMessage Component.literal("Hold Shift for more information");
     */
    @ZenCodeType.Method
    default void addShiftTooltip(Component content, @ZenCodeType.Optional Component showMessage) {
        
        CraftTweakerAPI.apply(new ActionAddShiftedTooltip(this, content, showMessage));
    }

    /**
     * Applies the given {@link ITooltipFunction} to this {@code IIngredient}'s tooltip. See the documentation on {@link ITooltipFunction} for details on how to use this.
     *
     * @param function The {@link ITooltipFunction} to apply.
     */
    @ZenCodeType.Method
    default void modifyTooltip(ITooltipFunction function) {
        
        CraftTweakerAPI.apply(new ActionModifyTooltip(this, function));
    }

    /**
     * Applies the given {@link ITooltipFunction} to this {@code IIngredient}'s tooltip. The function will only apply when Shift is held down. See the documentation on {@link ITooltipFunction} for details on how to use this.
     *
     * @param shiftedFunction   The {@link ITooltipFunction} to apply.
     * @param unshiftedFunction The {@link ITooltipFunction} to apply when Shift is not held down. This will be hidden when Shift is held down. This is useful for messages like "Hold Shift for more information".
     */
    @ZenCodeType.Method
    default void modifyShiftTooltip(ITooltipFunction shiftedFunction, @ZenCodeType.Optional ITooltipFunction unshiftedFunction) {
        
        CraftTweakerAPI.apply(new ActionModifyShiftedTooltip(this, shiftedFunction, unshiftedFunction));
    }

    /**
     * Removes all tooltip lines that match the given regular expression (regex) from this {@code IIngredient}.
     *
     * @param regex The regular expression to match against. If you don't know what a regular expression is, please consult Google.
     *
     * @docParam regex ".* Attack Damage"
     */
    @ZenCodeType.Method
    default void removeTooltip(String regex) {
        
        CraftTweakerAPI.apply(new ActionRemoveRegexTooltip(this, Pattern.compile(regex)));
    }
    
    /**
     * Adds an {@link AttributeModifier} to this {@code IIngredient}. Unlike other overloads, which take a separate UUID parameter, this one creates a random UUID based on the given {@code name}.
     *
     * <p>Note: This method will add the modifier to all {@link IItemStack}s that match this {@code IIngredient}. If you want to apply a modifier to a specific stack only, you should use {@link IItemStack#withAttributeModifier(Attribute, String, double, AttributeModifier.Operation, EquipmentSlot[], boolean)} instead.</p>
     *
     * @param attribute The {@link Attribute} of the modifier.
     * @param name      The name of the modifier.
     * @param value     The value of the modifier.
     * @param operation The operation of the modifier.
     * @param slotTypes What slots the modifier is valid for.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam name "Extra Power"
     * @docParam value 10
     * @docParam operation AttributeOperation.ADDITION
     * @docParam slotTypes [<constant:minecraft:equipmentslot:mainhand>]
     */
    @ZenCodeType.Method
    default void addGlobalAttributeModifier(Attribute attribute, String name, double value, AttributeModifier.Operation operation, EquipmentSlot[] slotTypes) {
        
        AttributeModifier modifier = new AttributeModifier(name, value, operation);
        addModifier(attribute, slotTypes, modifier);
    }
    
    /**
     * Adds an {@link AttributeModifier} to this {@code IIngredient}, using a specific UUID.
     *
     * <p>The UUID can be used to override existing attributes of an {@code IIngredient} with this new modifier. Use {@code /ct hand attributes} to get the attribute UUIDs on an IItemStack. If you do not want to override an attribute modifier, but want to add one instead, you can use an online UUID generator of your choice.</p>
     *
     * <p>Note: This method will add the modifier to all {@link IItemStack}s that match this {@code IIngredient}. If you want to apply a modifier to a specific stack only, you should use {@link IItemStack#withAttributeModifier(Attribute, String, double, AttributeModifier.Operation, EquipmentSlot[], boolean)} instead.</p>
     *
     * @param attribute The {@link Attribute} of the modifier.
     * @param uuid      The UUID of the modifier.
     * @param name      The name of the modifier.
     * @param value     The value of the modifier.
     * @param operation The operation of the modifier.
     * @param slotTypes What slots the modifier is valid for.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam uuid "8c1b5535-9f79-448b-87ae-52d81480aaa3"
     * @docParam name "Extra Power"
     * @docParam value 10
     * @docParam operation AttributeOperation.ADDITION
     * @docParam slotTypes [<constant:minecraft:equipmentslot:mainhand>]
     */
    @ZenCodeType.Method
    default void addGlobalAttributeModifier(Attribute attribute, String uuid, String name, double value, AttributeModifier.Operation operation, EquipmentSlot[] slotTypes) {
        
        addGlobalAttributeModifier(attribute, UUID.fromString(uuid), name, value, operation, slotTypes);
    }
    
    /**
     * Adds an {@link AttributeModifier} to this {@code IIngredient}, using a specific UUID.
     *
     * <p>The UUID can be used to override existing attributes of an {@code IIngredient} with this new modifier. Use {@code /ct hand attributes} to get the attribute UUIDs on an IItemStack. If you do not want to override an attribute modifier, but want to add one instead, you can use an online UUID generator of your choice.</p>
     *
     * <p>Note: This method will add the modifier to all {@link IItemStack}s that match this {@code IIngredient}. If you want to apply a modifier to a specific stack only, you should use {@link IItemStack#withAttributeModifier(Attribute, String, double, AttributeModifier.Operation, EquipmentSlot[], boolean)} instead.</p>
     *
     * @param attribute The {@link Attribute} of the modifier.
     * @param uuid      The UUID of the modifier.
     * @param name      The name of the modifier.
     * @param value     The value of the modifier.
     * @param operation The operation of the modifier.
     * @param slotTypes What slots the modifier is valid for.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam uuid "8c1b5535-9f79-448b-87ae-52d81480aaa3"
     * @docParam name "Extra Power"
     * @docParam value 10
     * @docParam operation AttributeOperation.ADDITION
     * @docParam slotTypes [<constant:minecraft:equipmentslot:mainhand>]
     */
    @ZenCodeType.Method
    default void addGlobalAttributeModifier(Attribute attribute, UUID uuid, String name, double value, AttributeModifier.Operation operation, EquipmentSlot[] slotTypes) {
        
        AttributeModifier modifier = new AttributeModifier(uuid, name, value, operation);
        addModifier(attribute, slotTypes, modifier);
    }
    
    private void addModifier(Attribute attribute, EquipmentSlot[] slotTypes, AttributeModifier modifier) {
        
        final Set<EquipmentSlot> validSlots = new HashSet<>(Arrays.asList(slotTypes));
        CraftTweakerAPI.apply(new ActionModifyAttribute(this, event -> {
            if(validSlots.contains(event.getSlotType())) {
                if(event.getModifiers().containsEntry(attribute, modifier)) {
                    event.removeModifier(attribute, modifier);
                }
                event.addModifier(attribute, modifier);
            }
        }));
    }
    
    /**
     * Removes all {@link AttributeModifier}s that use the given {@link Attribute} from this {@code IIngredient}. This method can only remove default {@link Attribute}s, custom {@link Attribute}s may still be added by other means.
     *
     * <p>Note: This method will remove the modifier from all {@link IItemStack}s that match this {@code IIngredient}. If you want to remove a modifier from a specific stack only, you should use {@link IItemStack#withoutAttributeModifier(Attribute, EquipmentSlot[])} instead.</p>//TODO doc
     *
     * @param attribute The {@link Attribute} to remove.
     * @param slotTypes The {@link EquipmentSlot}s to remove the {@link AttributeModifier}s from.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam slotTypes [<constant:minecraft:equipmentslot:chest>]
     */
    @ZenCodeType.Method
    default void removeGlobalAttribute(Attribute attribute, EquipmentSlot[] slotTypes) {
        
        final Set<EquipmentSlot> validSlots = new HashSet<>(Arrays.asList(slotTypes));
        
        CraftTweakerAPI.apply(new ActionModifyAttribute(this, event -> {
            if(validSlots.contains(event.getSlotType())) {
                event.removeAttribute(attribute);
            }
        }));
    }
    
    /**
     * Removes all {@link AttributeModifier}s that use the given UUID from this {@code IIngredient}. Unlike the other overload, this method can also remove non-default {@link Attribute}s.
     *
     * <p>Note: This method will remove the modifier from all {@link IItemStack}s that match this {@code IIngredient}. If you want to remove a modifier from a specific stack only, you should use {@link IItemStack#withoutAttributeModifier(Attribute, EquipmentSlot[])} instead.</p>//TODO doc
     *
     * @param uuid      The UUID to remove.
     * @param slotTypes The {@link EquipmentSlot}s to remove the {@link AttributeModifier}s from.
     *
     * @docParam uuid "8c1b5535-9f79-448b-87ae-52d81480aaa3"
     * @docParam slotTypes [<constant:minecraft:equipmentslot:chest>]
     */
    @ZenCodeType.Method
    default void removeGlobalAttributeModifier(String uuid, EquipmentSlot[] slotTypes) {
        
        removeGlobalAttributeModifier(UUID.fromString(uuid), slotTypes);
    }
    
    /**
     * Removes all {@link AttributeModifier}s that use the given UUID from this {@code IIngredient}. Unlike the other overload, this method can also remove non-default {@link Attribute}s.
     *
     * <p>Note: This method will remove the modifier from all {@link IItemStack}s that match this {@code IIngredient}. If you want to remove a modifier from a specific stack only, you should use {@link IItemStack#withoutAttributeModifier(Attribute, EquipmentSlot[])} instead.</p>//TODO doc
     *
     * @param uuid      The UUID to remove.
     * @param slotTypes The {@link EquipmentSlot}s to remove the {@link AttributeModifier}s from.
     *
     * @docParam uuid IItemStack.BASE_ATTACK_DAMAGE_UUID
     * @docParam slotTypes [<constant:minecraft:equipmentslot:chest>]
     */
    @ZenCodeType.Method
    default void removeGlobalAttributeModifier(UUID uuid, EquipmentSlot[] slotTypes) {
        
        final Set<EquipmentSlot> validSlots = new HashSet<>(Arrays.asList(slotTypes));
        CraftTweakerAPI.apply(new ActionModifyAttribute(this, event -> {
            if(validSlots.contains(event.getSlotType())) {
                event.getModifiers()
                        .entries()
                        .stream()
                        .filter(entry -> entry.getValue().getId().equals(uuid))
                        .forEach(entry -> event.removeModifier(entry.getKey(), entry.getValue()));
            }
        }));
    }

    /**
     * Converts a vanilla {@link Ingredient} to a CraftTweaker {@code IIngredient}.
     *
     * @param ingredient The {@link Ingredient} to convert.
     *
     * @return A new {@code IIngredient}, created from the given vanilla {@link Ingredient}.
     */
    static IIngredient fromIngredient(Ingredient ingredient) {
        
        return IngredientConverter.fromIngredient(ingredient);
    }

    /**
     * Returns the {@link MapData} representation of this {@code IIngredient}, or an empty {@link MapData} if no representation could be created.
     *
     * @return A potentially empty {@link MapData} representation of this {@code IIngredient}.
     */
    @ZenCodeType.Caster(implicit = true)
    default MapData asMapData() {
        
        final IData data = this.asIData();
        return data instanceof MapData ? ((MapData) data) : new MapData();
    }

    /**
     * Returns the {@link IData} representation of this {@code IIngredient}.
     *
     * @return The {@link IData} representation of this {@code IIngredient}.
     */
    @ZenCodeType.Caster(implicit = true)
    default IData asIData() {
        
        return JSONConverter.convert(this.asVanillaIngredient().toJson());
    }

    /**
     * Creates a new {@link IIngredientList} that contains this {@code IIngredient} and the given {@code IIngredient}. The resulting {@link IIngredientList} can then be used to allow both {@code IIngredients} to be accepted in a recipe.
     *
     * @param other The other {@code IIngredient} to use.
     *
     * @return An {@link IIngredientList} that contains this {@code IIngredient} and the given {@code IIngredient}.
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.OR)
    default IIngredientList or(IIngredient other) {
        
        return new IIngredientList(new IIngredient[] {this, other});
    }
    
    /**
     * Sets an amount on this {@code IIngredient}. This is mainly used in modded machines that allow multiple inputs (all vanilla recipes ignore input counts).
     *
     * @param amount The amount to set.
     *
     * @return An {@link IIngredientWithAmount}, representing this {@code IIngredient} with the given amount.
     */
    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MUL)
    default IIngredientWithAmount mul(int amount) {
        
        return new IngredientWithAmount(this, amount);
    }
    
    /**
     * Casts this {@code IIngredient} to an {@link IIngredientWithAmount}. This is synonymous to calling {@link IIngredient#mul(int)} with a parameter value of 1. Used implicitly when providing regular {@code IIngredient}s to modded machines that allow multiple inputs.
     *
     * @return An {@link IIngredientWithAmount}, representing this {@code IIngredient} with an amount of 1.
     */
    @ZenCodeType.Method
    @ZenCodeType.Caster(implicit = true)
    default IIngredientWithAmount asIIngredientWithAmount() {
        
        if(this instanceof IIngredientWithAmount) {
            return (IIngredientWithAmount) this;
        }
        return mul(1);
    }
    
    // <editor-fold desc="Transformers">
    /**
     * Adds a replacement transformer to this {@code IIngredient}. Upon crafting, the {@code IIngredient} will not be used, instead, it will be replaced by the given {@link IItemStack}.
     *
     * @param replaceWith The {@link IItemStack} to replace this {@code IIngredient} with.
     *
     * @return This {@code IIngredient}, with the transformer information applied.
     */
    @ZenCodeType.Method
    default IIngredientTransformed<IIngredient> transformReplace(IItemStack replaceWith) {
        
        return new IIngredientTransformed<>(this, new TransformReplace<>(replaceWith));
    }

    /**
     * Adds a damage transformer to this {@code IIngredient}. Upon crafting, the {@code IIngredient} will not be used, instead, it will be damaged by the given amount.
     *
     * @param amount The amount to damage the {@code IIngredient} by.
     *
     * @return This {@code IIngredient}, with the transformer information applied.
     */
    @ZenCodeType.Method
    default IIngredientTransformed<IIngredient> transformDamage(@ZenCodeType.OptionalInt(1) int amount) {
        
        return new IIngredientTransformed<>(this, new TransformDamage<>(amount));
    }

    /**
     * Adds a custom transformer to this {@code IIngredient}. Upon crafting, the {@code IIngredient} will not be used, instead, the given function will be applied.
     *
     * @param uid      A string identification. This allows for safely adding multiple custom transformers to this {@code IIngredient}.
     * @param function The function to apply.
     *
     * @return This {@code IIngredient}, with the transformer information applied.
     */
    @ZenCodeType.Method
    default IIngredientTransformed<IIngredient> transformCustom(String uid, @ZenCodeType.Optional Function<IItemStack, IItemStack> function) {
        
        return new IIngredientTransformed<>(this, new TransformCustom<>(uid, function));
    }

    /**
     * Adds a reusing transformer to this {@code IIngredient}. Upon crafting, the {@code IIngredient} will not be used, instead, it will remain untouched.
     *
     * @return This {@code IIngredient}, with the transformer information applied.
     */
    @ZenCodeType.Method
    default IIngredientTransformed<IIngredient> reuse() {
        
        return new IIngredientTransformed<>(this, new TransformReuse<>());
    }
    
    /**
     * Applies an already existing transformer (e.g. from another {@code IIngredient}) to this {@code IIngredient}.
     *
     * @return This {@code IIngredient}, with the transformer information applied.
     */
    @ZenCodeType.Method
    default IIngredientTransformed<IIngredient> transform(IIngredientTransformer<IIngredient> transformer) {
        
        return new IIngredientTransformed<>(this, transformer);
    }
    
    // </editor-fold>
    
    // <editor-fold desc="conditions">
    /**
     * Adds a damage condition to this {@code IIngredient}. The resulting {@code IIngredient} now requires the input to be damaged in order to be valid.
     *
     * @return This {@code IIngredient}, with the damage condition applied.
     */
    @ZenCodeType.Method
    default IIngredientConditioned<IIngredient> onlyDamaged() {
        
        return new IIngredientConditioned<>(this, new ConditionDamaged<>());
    }

    /**
     * Adds a damage condition to this {@code IIngredient}. The resulting {@code IIngredient} now requires the input to have at least the given durability damage in order to be valid.
     *
     * @return This {@code IIngredient}, with the damage condition applied.
     */
    @ZenCodeType.Method
    default IIngredientConditioned<IIngredient> onlyDamagedAtLeast(int minDamage) {
        
        return new IIngredientConditioned<>(this, new ConditionDamagedAtLeast<>(minDamage));
    }

    /**
     * Adds a damage condition to this {@code IIngredient}. The resulting {@code IIngredient} now requires the input to have at most the given durability damage in order to be valid.
     *
     * @return This {@code IIngredient}, with the damage condition applied.
     */
    @ZenCodeType.Method
    default IIngredientConditioned<IIngredient> onlyDamagedAtMost(int maxDamage) {
        
        return new IIngredientConditioned<>(this, new ConditionDamagedAtMost<>(maxDamage));
    }

    /**
     * Adds a damage condition to this {@code IIngredient}. The resulting {@code IIngredient} now accepts any amount of durability damage, including no damage at all.
     *
     * @return This {@code IIngredient}, with the damage condition applied.
     */
    @ZenCodeType.Method
    default IIngredientConditioned<IIngredient> anyDamage() {
        
        return new IIngredientConditioned<>(this, new ConditionAnyDamage<>());
    }

    /**
     * Adds a custom condition to this {@code IIngredient}. The resulting {@code IIngredient} now requires potential inputs to pass the given function in order to be valid.
     *
     * @param uid      A string identification. This allows for safely adding multiple custom conditions to this {@code IIngredient}.
     * @param function The predicate to check.
     *
     * @return This {@code IIngredient}, with the custom condition applied.
     */
    @ZenCodeType.Method
    default IIngredientConditioned<IIngredient> onlyIf(String uid, @ZenCodeType.Optional Predicate<IItemStack> function) {
        
        return new IIngredientConditioned<>(this, new ConditionCustom<>(uid, function));
    }
    
    /**
     * Applies an already existing condition (e.g. from another {@code IIngredient}) to this {@code IIngredient}.
     *
     * @return This {@code IIngredient}, with the condition applied.
     */
    @ZenCodeType.Method
    default IIngredientConditioned<IIngredient> only(IIngredientCondition<IIngredient> condition) {
        
        return new IIngredientConditioned<>(this, condition);
    }
    // </editor-fold>
    
}

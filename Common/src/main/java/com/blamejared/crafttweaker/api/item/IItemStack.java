package com.blamejared.crafttweaker.api.item;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.item.ActionSetFood;
import com.blamejared.crafttweaker.api.action.item.ActionSetItemProperty;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.IntData;
import com.blamejared.crafttweaker.api.data.MapData;
import com.blamejared.crafttweaker.api.data.converter.tag.TagToDataConverter;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import com.blamejared.crafttweaker.api.util.AttributeUtil;
import com.blamejared.crafttweaker.api.util.EnchantmentUtil;
import com.blamejared.crafttweaker.api.util.ItemStackUtil;
import com.blamejared.crafttweaker.api.util.random.Percentaged;
import com.blamejared.crafttweaker.mixin.common.access.item.AccessItem;
import com.blamejared.crafttweaker.platform.Services;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents an in-game item stack. This is what you want 99% of the time when dealing with recipes.
 *
 * IItemStacks can be obtained through bracket handlers prefixed with `item`. For example, `<item:minecraft:diamond>` represents an item stack containing one diamond.
 *
 * Note that some methods, such as `setMaxStackSize()`, will affect the item of this IItemStack (and thus work for all stacks), while others, such as `addAttributeModifier()`, actually work only for that particular stack. Make sure to read the descriptions carefully!
 */
@ZenRegister
@ZenCodeType.Name("crafttweaker.api.item.IItemStack")
@Document("vanilla/api/item/IItemStack")
public interface IItemStack extends IIngredient, IIngredientWithAmount {
    
    @ZenCodeType.Field
    String CRAFTTWEAKER_DATA_KEY = "CraftTweakerData";
    
    @ZenCodeType.Field
    UUID BASE_ATTACK_DAMAGE_UUID = AccessItem.crafttweaker$getBASE_ATTACK_DAMAGE_UUID();
    
    @ZenCodeType.Field
    UUID BASE_ATTACK_SPEED_UUID = AccessItem.crafttweaker$getBASE_ATTACK_SPEED_UUID();
    
    static IItemStack empty() {
        
        return Services.PLATFORM.getEmptyItemStack();
    }
    
    static IItemStack of(final ItemStack stack) {
        
        return Services.PLATFORM.createItemStack(stack);
    }
    
    static IItemStack of(final ItemStack stack, final boolean mutable) {
        
        return mutable ? ofMutable(stack) : of(stack);
    }
    
    static IItemStack ofMutable(final ItemStack stack) {
        
        return Services.PLATFORM.createItemStackMutable(stack);
    }
    
    /**
     * Creates a copy of this IItemStack.
     */
    @ZenCodeType.Method
    IItemStack copy();
    
    /**
     * Returns the registry name for this IItemStack's item. For example, `<item:minecraft:diamond>.getRegistryName()` will return a {@link ResourceLocation} with the string contents `"minecraft:diamond"`.
     *
     * @return The registry name of this IItemStack's item.
     */
    @ZenCodeType.Getter("registryName")
    default ResourceLocation getRegistryName() {
        
        return Registry.ITEM.getKey(getInternal().getItem());
    }
    
    /**
     * Returns the owning mod (a.k.a. mod id) for this IItemStack's item. For example, `<item:minecraft:diamond>.getOwner()` will return `"minecraft"`, and `<item:create:zinc_ingot>` will return `"create"`.
     *
     * @return The owning mod of this IItemStack's item.
     */
    @ZenCodeType.Getter("owner")
    default String getOwner() {
        
        return getRegistryName().getNamespace();
    }
    
    /**
     * Returns whether this IItemStack is empty or not. The item of empty IItemStacks typically is `"minecraft:air"`, which means that `<item:minecraft:air>.isEmpty()` will always return `true`.
     *
     * @return `true` if this IItemStack is empty, `false` if not.
     */
    @Override
    default boolean isEmpty() {
        
        return getInternal().isEmpty();
    }
    
    /**
     * Returns the max stack size of this IItemStack's item. For example, `<item:minecraft:diamond>.getMaxStackSize()` will return `64`, and `<item:minecraft:diamond_sword>` will return `1`.
     *
     * @return The max stack size of this IItemStack's item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("maxStackSize")
    default int getMaxStackSize() {
        
        return getInternal().getItem().getMaxStackSize();
    }
    
    /**
     * Sets the max stack size of this IItemStack's item. Values greater than 64 are generally not recommended. For example, `<item:minecraft:egg>.setMaxStackSize(64)` will allow eggs to stack to 64.
     *
     * @param newMaxStackSize The new max stack size of this IItemStack's item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("maxStackSize")
    default void setMaxStackSize(int newMaxStackSize) {
        
        CraftTweakerAPI.apply(new ActionSetItemProperty<>(this, "Max Stack Size", newMaxStackSize, this.getInternal()
                .getItem().getMaxStackSize(), ((AccessItem) this.getInternal()
                .getItem())::crafttweaker$setMaxStackSize));
    }
    
    /**
     * Returns the {@link Rarity} of this IItemStack's item. An item's {@link Rarity} mainly determines its name color. For example, `<item:minecraft:diamond>.getRarity()` will return `<constant:minecraft:item/rarity:common>`, and `<item:minecraft:command_block>.getRarity()` will return `<constant:minecraft:item/rarity:epic>`.
     *
     * @return The {@link Rarity} of this IItemStack's item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("rarity")
    default Rarity getRarity() {
        
        return getInternal().getRarity();
    }
    
    /**
     * Sets the {@link Rarity} of this IItemStack's item. An item's {@link Rarity} mainly determines its name color. For example, `<item:minecraft:diamond>.setRarity(<constant:minecraft:item/rarity:uncommon>)` will set the rarity of diamonds to `uncommon` (yellow name).
     *
     * @param newRarity The new {@link Rarity} of this IItemStack's item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("rarity")
    default void setRarity(Rarity newRarity) {
        
        CraftTweakerAPI.apply(new ActionSetItemProperty<>(this, "Rarity", newRarity, this.getInternal()
                .getRarity(), ((AccessItem) this.getInternal()
                .getItem())::crafttweaker$setRarity));
    }
    
    /**
     * Sets the lore of this IItemStack. Lore is basically an extra tooltip that is stored in NBT rather than being added through code. The parameter is a vararg, every argument will result in one line of lore.
     *
     * Example usage:
     * ```zenscript
     * <item:minecraft:oak_log>.withLore(Component.literal("I am the lore. I speak for the trees."));
     * <item:minecraft:spruce_log>.withLore(Component.literal("I am the lore.", "I speak for the trees.", "I do so in multiple lines."));
     * ```
     *
     * @param lore The new Lore of this IItemStack. This is a vararg, so you can add as many arguments as you need.
     */
    @ZenCodeType.Method
    default IItemStack withLore(@ZenCodeType.Nullable Component... lore) {
        
        return modify(itemStack -> {
            CompoundTag tag = itemStack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
            if(lore != null && lore.length != 0) {
                ListTag listtag = new ListTag();
                for(Component component : lore) {
                    listtag.add(StringTag.valueOf(Component.Serializer.toJson(component)));
                }
                tag.put("Lore", listtag);
            } else {
                tag.remove(ItemStack.TAG_LORE);
            }
        });
    }
    
    /**
     * Returns the formatted display name of this IItemStack. This is what the player sees as the item's name when hovering over it. Note that the output of this method may be influenced by active resource packs, and will not produce reliable results on servers.
     *
     * @return The formatted display name of this IItemStack.
     */
    @ZenCodeType.Getter("displayName")
    default Component getDisplayName() {
        
        return getInternal().getDisplayName();
    }
    
    /**
     * Sets the display name of this IItemStack. For example, `<item:minecraft:grass_block>.withDisplayName(Component.literal("Premium Dirt"))` will use the display name `Premium Dirt` instead of `Grass Block`.
     *
     * @param name The new display name of this IItemStack.
     */
    @ZenCodeType.Method
    default IItemStack withDisplayName(Component name) {
        
        return modify(itemStack -> itemStack.setHoverName(name));
    }
    
    /**
     * Returns the hover name of this IItemStack. This will give the raw name, without the formatting that `getDisplayName()` applies.
     *
     * @return The hover name of this IItemStack.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("hoverName")
    default Component getHoverName() {
        
        return getInternal().getHoverName();
    }
    
    /**
     * Clears any custom name set for this IItemStack.
     */
    @ZenCodeType.Method
    default void resetHoverName() {
        
        getInternal().resetHoverName();
    }
    
    /**
     * Returns whether this IItemStack has a custom display name or not.
     *
     * @return `true` if this IItemStack has a custom display name, `false` if not.
     */
    @ZenCodeType.Getter("hasCustomHoverName")
    default boolean hasDisplayName() {
        
        return getInternal().hasCustomHoverName();
    }
    
    /**
     * Returns whether this IItemStack has a foil effect (enchantment glint) or not. Mostly identical with `isEnchanted()`, however, some items have a foil effect on their own (for example enchanted golden apples). For example, `<item:minecraft:enchanted_book>.hasFoil()` will return `true`.
     *
     * @return `true` if this IItemStack has a foil effect, `false` if not.
     */
    @ZenCodeType.Getter("hasFoil")
    default boolean hasFoil() {
        
        return getInternal().hasFoil();
    }
    
    /**
     * Returns whether this IItemStack can be enchanted or not. For example, `<item:minecraft:diamond_sword>.isEnchantable()` will return `true`.
     *
     * @return `true` if this IItemStack has a custom display name, `false` if not.
     */
    @ZenCodeType.Getter("isEnchantable")
    default boolean isEnchantable() {
        
        return getInternal().isEnchantable();
    }
    
    /**
     * Returns whether this IItemStack is enchanted or not. For example, `<item:minecraft:enchanted_book>.isEnchanted()` will return `true`.
     *
     * @return `true` if this IItemStack has a foil effect, `false` if not.
     */
    @ZenCodeType.Getter("isEnchanted")
    default boolean isEnchanted() {
        
        return getInternal().isEnchanted();
    }
    
    /**
     * Returns the base repair cost of this IItemStack, or 0 if no base repair cost is set. The base repair cost is used in anvil repair calculations and has the value 0 for all vanilla items, but can be overridden by the `RepairCost` NBT tag. For example, `<item:minecraft:diamond_sword>.getBaseRepairCost()` will return `0`.
     *
     * @return The base repair cost of this IItemStack, or 0 if no base repair cost is set.
     */
    @ZenCodeType.Getter("baseRepairCost")
    default int getBaseRepairCost() {
        
        return getInternal().getBaseRepairCost();
    }
    
    /**
     * Returns the amount of this IItemStack. For example, `(<item:minecraft:diamond> * 8).getAmount()` will return `8`.
     *
     * @return The amount of this IItemStack.
     */
    @ZenCodeType.Getter("amount")
    default int getAmount() {
        
        return getInternal().getCount();
    }
    
    /**
     * Sets the amount of this IItemStack. For example, `<item:minecraft:diamond>.setAmount(8)` will return a stack of 8 diamonds.
     *
     * Note: This method is equivalent to using the * operator. This means that, for example, `<item:minecraft:diamond>.setAmount(8)` and `<item:minecraft:diamond> * 8` will have the same effect.
     *
     * @param amount The new amount of this IItemStack.
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MUL)
    default IItemStack setAmount(int amount) {
        
        return modify(itemStack -> itemStack.setCount(amount));
    }
    
    /**
     * Grows this IItemStack's amount by the given amount, or 1 if no amount is given. For example, `<item:minecraft:diamond>.grow(3)` will return a stack of 4 diamonds.
     *
     * @param amount The amount to grow this IItemStack by.
     *
     * @return This IItemStack if mutable, a new IItemStack with the new amount otherwise.
     */
    @ZenCodeType.Method
    default IItemStack grow(@ZenCodeType.OptionalInt(1) int amount) {
        
        return setAmount(getAmount() + amount);
    }
    
    /**
     * Shrinks this IItemStack's amount by the given amount, or 1 if no amount is given. For example, `<item:minecraft:diamond>.shrink(1)` will return an empty item stack.
     *
     * @param amount The amount to shrink this IItemStack by.
     *
     * @return This IItemStack if mutable, a new IItemStack with the new amount otherwise.
     */
    @ZenCodeType.Method
    default IItemStack shrink(@ZenCodeType.OptionalInt(1) int amount) {
        
        return setAmount(getAmount() - amount);
    }
    
    /**
     * Returns whether this IItemStack is stackable, meaning whether it have a stack size greater than 1 or not. For example, `<item:minecraft:diamond>.isStackable()` will return `true`, and `<item:minecraft:diamond_sword>.isStackable()` will return `false`.
     *
     * @return `true` if this IItemStack is stackable, `false` if not.
     */
    @ZenCodeType.Getter("stackable")
    default boolean isStackable() {
        
        return getInternal().isStackable();
    }
    
    /**
     * Sets the durability damage of this IItemStack. Note that this represents the uses done so far, not the uses left. For example, `<item:minecraft:diamond_sword>.withDamage(150)` will return a diamond sword with 1411 uses left (diamond swords have 1561 uses by default).
     *
     * @param damage The new durability damage of this IItemStack.
     */
    @ZenCodeType.Method
    default IItemStack withDamage(int damage) {
        
        return modify(itemStack -> itemStack.setDamageValue(damage));
    }
    
    /**
     * Adds an {@link AttributeModifier} to this IItemStack, using a specific UUID.
     *
     * The UUID can be used to override existing attributes of an IItemStack with this new modifier. Use `/ct hand attributes` to get the attribute UUIDs on an IItemStack. If you do not want to override an attribute modifier, but want to add one instead, you can use an online UUID generator of your choice.
     *
     * By default, attribute modifiers for an attribute already present on this IItemStack will replace any attribute modifiers for that attribute. This can be prevented by setting the optional `preserveDefaults` flag to `true`.
     *
     * Example usages:
     * ```zenscript
     * <item:minecraft:diamond_sword>.addAttributeModifier(<attribute:minecraft:generic.attack_damage>, "8c1b5535-9f79-448b-87ae-52d81480aaa3", "Extra Power", 10, AttributeOperation.ADDITION, [<constant:minecraft:equipmentslot:mainhand>]);
     * ```The above example will remove the default attack damage of diamond swords and any other modifiers, leaving only this modifier.
     * ```zenscript
     * <item:minecraft:diamond_sword>.addAttributeModifier(<attribute:minecraft:generic.attack_damage>, "8c1b5535-9f79-448b-87ae-52d81480aaa3", "Extra Power", 10, AttributeOperation.ADDITION, [<constant:minecraft:equipmentslot:chest>], true);
     * ```The above example will not remove the default attack damage of diamond swords, instead adding this modifier on top of the modifiers already present.
     *
     * @param uuid             The UUID of the modifier.
     * @param attribute        The {@link Attribute} of the modifier.
     * @param name             The name of the modifier.
     * @param value            The value of the modifier.
     * @param operation        The operation of the modifier.
     * @param slotTypes        What slots the modifier is valid for.
     * @param preserveDefaults Whether the modifiers that are already present on this IItemStack should be preserved or not.
     */
    @ZenCodeType.Method
    default IItemStack withAttributeModifier(Attribute attribute, String uuid, String name, double value, AttributeModifier.Operation operation, EquipmentSlot[] slotTypes, @ZenCodeType.OptionalBoolean boolean preserveDefaults) {
        
        return withAttributeModifier(attribute, UUID.fromString(uuid), name, value, operation, slotTypes, preserveDefaults);
    }
    
    /**
     * Adds an {@link AttributeModifier} to this IItemStack, using a specific UUID.
     *
     * The UUID can be used to override existing attributes of an IItemStack with this new modifier. Use `/ct hand attributes` to get the attribute UUIDs on an IItemStack. If you do not want to override an attribute modifier, but want to add one instead, you can use an online UUID generator of your choice.
     *
     * By default, attribute modifiers for an attribute already present on this IItemStack will replace any attribute modifiers for that attribute. This can be prevented by setting the optional `preserveDefaults` flag to `true`.
     *
     * Example usages:
     * ```zenscript
     * <item:minecraft:diamond_sword>.addAttributeModifier(<attribute:minecraft:generic.attack_damage>, "8c1b5535-9f79-448b-87ae-52d81480aaa3", "Extra Power", 10, AttributeOperation.ADDITION, [<constant:minecraft:equipmentslot:mainhand>]);
     * ```The above example will remove the default attack damage of diamond swords and any other modifiers, leaving only this modifier.
     * ```zenscript
     * <item:minecraft:diamond_sword>.addAttributeModifier(<attribute:minecraft:generic.attack_damage>, "8c1b5535-9f79-448b-87ae-52d81480aaa3", "Extra Power", 10, AttributeOperation.ADDITION, [<constant:minecraft:equipmentslot:chest>], true);
     * ```The above example will not remove the default attack damage of diamond swords, instead adding this modifier on top of the modifiers already present.
     *
     * @param uuid             The UUID of the modifier.
     * @param attribute        The {@link Attribute} of the modifier.
     * @param name             The name of the modifier.
     * @param value            The value of the modifier.
     * @param operation        The operation of the modifier.
     * @param slotTypes        What slots the modifier is valid for.
     * @param preserveDefaults Whether the modifiers that are already present on this IItemStack should be preserved or not.
     */
    @ZenCodeType.Method
    default IItemStack withAttributeModifier(Attribute attribute, UUID uuid, String name, double value, AttributeModifier.Operation operation, EquipmentSlot[] slotTypes, @ZenCodeType.OptionalBoolean boolean preserveDefaults) {
        
        return modify(itemStack -> {
            for(EquipmentSlot slotType : slotTypes) {
                if(preserveDefaults) {
                    AttributeUtil.addAttributeModifier(itemStack, attribute, new AttributeModifier(uuid, name, value, operation), slotType);
                } else {
                    itemStack.addAttributeModifier(attribute, new AttributeModifier(uuid, name, value, operation), slotType);
                }
            }
        });
    }
    
    /**
     * Adds an {@link AttributeModifier} to this IItemStack. Unlike other overloads, which take a separate UUID parameter, this one creates a random UUID based on the given `name`.
     *
     * By default, attribute modifiers for an attribute already present on this IItemStack will replace any attribute modifiers for that attribute. This can be prevented by setting the optional `preserveDefaults` flag to `true`.
     *
     * Example usages:
     * ```zenscript
     * <item:minecraft:diamond_sword>.addAttributeModifier(<attribute:minecraft:generic.attack_damage>, "Extra Power", 10, AttributeOperation.ADDITION, [<constant:minecraft:equipmentslot:mainhand>]);
     * ```The above example will remove the default attack damage of diamond swords and any other modifiers, leaving only this modifier.
     * ```zenscript
     * <item:minecraft:diamond_sword>.addAttributeModifier(<attribute:minecraft:generic.attack_damage>, "Extra Power", 10, AttributeOperation.ADDITION, [<constant:minecraft:equipmentslot:chest>], true);
     * ```The above example will not remove the default attack damage of diamond swords, instead adding this modifier on top of the modifiers already present.
     *
     * @param attribute        The {@link Attribute} of the modifier.
     * @param name             The name of the modifier.
     * @param value            The value of the modifier.
     * @param operation        The operation of the modifier.
     * @param slotTypes        What slots the modifier is valid for.
     * @param preserveDefaults Whether the modifiers that are already present on this IItemStack should be preserved or not.
     */
    @ZenCodeType.Method
    default IItemStack withAttributeModifier(Attribute attribute, String name, double value, AttributeModifier.Operation operation, EquipmentSlot[] slotTypes, @ZenCodeType.OptionalBoolean boolean preserveDefaults) {
        
        return modify(itemStack -> {
            for(EquipmentSlot slotType : slotTypes) {
                if(preserveDefaults) {
                    AttributeUtil.addAttributeModifier(itemStack, attribute, new AttributeModifier(name, value, operation), slotType);
                } else {
                    itemStack.addAttributeModifier(attribute, new AttributeModifier(name, value, operation), slotType);
                }
            }
        });
    }
    
    /**
     * Returns the attributes and attribute modifiers on this IItemStack for the given {@link EquipmentSlot}. Example usage: `<item:minecraft:diamond_chestplate>.getAttributes(<constant:minecraft:equipmentslot:chest>);`
     *
     * @param slotType The slot to get the attributes for.
     *
     * @return The attribute => attribute modifier map for the given {@link EquipmentSlot}.
     */
    @ZenCodeType.Method
    default Map<Attribute, List<AttributeModifier>> getAttributes(EquipmentSlot slotType) {
        
        // I don't think we expose Collection, so just convert it to a list.
        return getInternal().getAttributeModifiers(slotType)
                .asMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, attributeAttributeModifierEntry -> new ArrayList<>(attributeAttributeModifierEntry
                        .getValue())));
    }
    
    /**
     * Returns whether this IItemStack is damageable or not. For example, `<item:minecraft:diamond>.isDamageable()` will return `false`, and `<item:minecraft:diamond_sword>.isDamageable()` will return `true`.
     *
     * @return `true` if this IItemStack is damageable, `false` if not.
     */
    @ZenCodeType.Getter("damageableItem")
    default boolean isDamageableItem() {
        
        return getInternal().isDamageableItem();
    }
    
    /**
     * Returns whether this IItemStack is damaged or not. Generally, the result of this method is equivalent to `getDamage() > 0`.
     *
     * @return `true` if this IItemStack is damaged, `false` if not.
     */
    @ZenCodeType.Getter("damaged")
    default boolean isDamaged() {
        
        return getInternal().isDamaged();
    }
    
    /**
     * Returns the max damage (a.k.a. max durability) of this IItemStack. For example, `<item:minecraft:diamond_sword>.getMaxDamage()` will return `1561`.
     *
     * @return The max damage of this IItemStack.
     */
    @ZenCodeType.Getter("maxDamage")
    default int getMaxDamage() {
        
        return getInternal().getMaxDamage();
    }
    
    /**
     * Sets the max damage (a.k.a. max durability) of this IItemStack's item. Using `0` will make the item unbreakable. For example, `<item:minecraft:diamond_sword>.setMaxDamage(1300)` will cause all diamond swords to have a max durability of `1300` instead of the default `1561`.
     *
     * @param newMaxDamage The new max damage of this IItemStack's item.
     */
    @ZenCodeType.Setter("maxDamage")
    default void setMaxDamage(int newMaxDamage) {
        
        CraftTweakerAPI.apply(new ActionSetItemProperty<>(this, "Max Damage", newMaxDamage, this.getInternal()
                .getMaxDamage(), ((AccessItem) this.getInternal()
                .getItem())::crafttweaker$setMaxDamage));
    }
    
    /**
     * Returns the unlocalized name of this IItemStack's item. This is typically the item's translation key. For example, `<item:minecraft:diamond>.getDescriptionId()` will return `"item.minecraft.diamond"`.
     *
     * @return The unlocalized name of this IItemStack's item.
     */
    @ZenCodeType.Getter("descriptionId")
    default String getDescriptionId() {
        
        return getInternal().getDescriptionId();
    }
    
    /**
     * Sets the NBT tag for this IItemStack. For example, `<item:minecraft:potion>.withTag({Potion: "minecraft:night_vision"})` returns a 3-minute night vision potion.
     *
     * @param tag The new tag of this IItemStack.
     *
     * @return This IItemStack if mutable, a new IItemStack with the new tag otherwise.
     */
    @ZenCodeType.Method
    default IItemStack withTag(MapData tag) {
        
        return modify(itemStack -> itemStack.setTag(tag.getInternal()));
    }
    
    /**
     * Removes the NBT tag from this IItemStack.
     *
     * @return This IItemStack if mutable, a new IItemStack with the tag removed otherwise.
     */
    @ZenCodeType.Method
    default IItemStack withoutTag() {
        
        return modify(itemStack -> itemStack.setTag(null));
    }
    
    /**
     * Returns whether this IItemStack has NBT tags or not. Generally, the result of this method is equivalent to `getTag() != null`.
     *
     * @return `true` if this IItemStack has NBT tags, `false` if not.
     */
    @ZenCodeType.Getter("hasTag")
    default boolean hasTag() {
        
        return getInternal().hasTag();
    }
    
    /**
     * Returns the NBT tag of this IItemStack. If no NBT tags are present, `null` is returned.
     *
     * @return The NBT tag of this IItemStack, or `null` if there are no NBT tags present.
     */
    @ZenCodeType.Nullable
    @ZenCodeType.Getter("tag")
    default IData getTag() {
        
        return TagToDataConverter.convert(getInternal().getTag());
    }
    
    /**
     * Returns the NBT tag of this IItemStack, or creates a new one if absent. Generally, the result of this method is equivalent to `getTag() == null ? new MapData() : getTag()`.
     *
     * @return The NBT tag of this IItemStack, or an empty tag if there are no NBT tags present.
     */
    @ZenCodeType.Method
    default IData getOrCreateTag() {
        
        if(getInternal().getTag() == null) {
            getInternal().setTag(new CompoundTag());
        }
        return getTag();
    }
    
    @Override
    default boolean matches(IItemStack stack, boolean ignoreDamage) {
        
        return ItemStackUtil.areStacksTheSame(this.getInternal(), stack.getInternal(), ignoreDamage, true);
    }
    
    @Override
    default String getCommandString() {
        
        return ItemStackUtil.getCommandString(this.getInternal(), this.isMutable());
    }
    
    /**
     * Returns the use duration of this IItemStack, in ticks. `72000` (1 hour) is used for items that can be used indefinitely, such as bows being drawn. For example, `<item:minecraft:bow>.getUseDuration()` will return `72000`.
     *
     * @return The use duration of this IItemStack.
     */
    @ZenCodeType.Getter("useDuration")
    default int getUseDuration() {
        
        return getInternal().getUseDuration();
    }
    
    /**
     * Returns whether this IItemStack's use logic is fired on release, rather than when beginning to draw. In vanilla, this will return `true` for `<item:minecraft:crossbow>.useOnRelease()` and `false` for all other items.
     *
     * @return `true` if this IItemStack's use logic is fired on release, `false` if not.
     */
    @ZenCodeType.Getter("useOnRelease")
    default boolean useOnRelease() {
        
        return getInternal().useOnRelease();
    }
    
    /**
     * Returns the {@link FoodProperties} of this IItemStack's item, or `null` if no {@link FoodProperties} are present. For example, `<item:minecraft:apple>.getFood()` will return food properties with a nutrition value of `4` and a saturation modifier of `0.3`.
     *
     * @return The {@link FoodProperties} of this IItemStack's item, or `null` if no {@link FoodProperties} are present.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("food")
    @ZenCodeType.Nullable
    default FoodProperties getFood() {
        
        return getInternal().getItem().getFoodProperties();
    }
    
    /**
     * Sets the {@link FoodProperties} for this IItemStack's item. For example, `<item:minecraft:apple>.setFood(new FoodProperties(8, 0.8))` will make apples as nutritious as steaks.
     *
     * @param food The new {@link FoodProperties} for this IItemStack's item. If this is `null`, the item will no longer be edible.
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("food")
    default void setFood(@ZenCodeType.Nullable FoodProperties food) {
        
        CraftTweakerAPI.apply(new ActionSetFood(this, food, this.getInternal()
                .getItem()
                .getFoodProperties()));
    }
    
    /**
     * Returns whether this IItemStack's item is edible or not. Generally, the result of this method is equivalent to `getFood() != null`.
     *
     * @return `true` if this IItemStack's item is edible, `false` if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("isEdible")
    default boolean isEdible() {
        
        return getInternal().isEdible();
    }
    
    /**
     * Returns the burn time of this IItemStack, in ticks. For example, `<item:minecraft:oak_planks>.getBurnTime()` will return `300`.
     *
     * @return The burn time of this IItemStack.
     */
    @ZenCodeType.Getter("burnTime")
    default int getBurnTime() {
        
        return Services.EVENT.getBurnTime(this);
    }
    
    /**
     * Returns whether this IItemStack's item is immune to fire or not. For example, `<item:minecraft:diamond>.isFireResistant()` will return `false`, and `<item:minecraft:netherite_ingot>.isFireResistant()` will return `true`.
     *
     * @return `true` if this IItemStack's item is immune to fire, `false` if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("fireResistant")
    default boolean isFireResistant() {
        
        return getInternal().getItem().isFireResistant();
    }
    
    /**
     * Sets whether this IItemStack's item should be immune to fire or not. For example, `<item:minecraft:diamond>.setFireResistant(true)` will make diamonds immune to fire, like netherite items are.
     *
     * @param fireResistant Whether the IItemStack's item should be immune to fire or not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("fireResistant")
    default void setFireResistant(boolean fireResistant) {
        
        CraftTweakerAPI.apply(new ActionSetItemProperty<>(this, "Fire Resistant", fireResistant, this.getInternal()
                .getItem().isFireResistant(), ((AccessItem) this.getInternal()
                .getItem())::crafttweaker$setFireResistant));
    }
    
    /**
     * Returns a {@link Percentaged} (weighted) IItemStack with the given percentage.
     *
     * Note: This method is equivalent to using the % operator. This means that, for example, `<item:minecraft:diamond>.percent(50)` and `<item:minecraft:diamond> % 50` will have the same effect.
     *
     * @param percentage The percentage of the new IItemStack.
     * @return A {@link Percentaged} (weighted) IItemStack with the given percentage.
     */
    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MOD)
    default Percentaged<IItemStack> percent(double percentage) {
        
        return new Percentaged<>(this, percentage / 100.0D, iItemStack -> iItemStack.getCommandString() + " % " + percentage);
    }
    
    //    @ZenCodeType.Method
    //    default WeightedEntry.Wrapper<IItemStack> weight(double weight) {
    //
    //        return new WeightedEntry.Wrapper<>(this, Weight.of(weight));
    //    }
    
    /**
     * Returns a {@link Percentaged} (weighted) instance of this IItemStack. Generally, the result of this method is equivalent to `percent(100)`.
     *
     * @return A {@link Percentaged} (weighted) instance of this IItemStack.
     */
    @ZenCodeType.Caster(implicit = true)
    default Percentaged<IItemStack> asWeightedItemStack() {
        
        return percent(100.0D);
    }
    
    /**
     * Returns the ItemDefinition for this IItemStack's item.
     *
     * @return The ItemDefinition for this IItemStack's item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("definition")
    @ZenCodeType.Caster(implicit = true)
    default Item getDefinition() {
        
        return getInternal().getItem();
    }
    
    /**
     * Returns a mutable instance of this IItemStack.
     *
     * @return A mutable instance of this IItemStack.
     */
    @ZenCodeType.Method
    IItemStack asMutable();
    
    /**
     * Returns an immutable instance of this IItemStack.
     *
     * @return An immutable instance of this IItemStack.
     */
    @ZenCodeType.Method
    IItemStack asImmutable();
    
    /**
     * Returns whether this IItemStack is immutable or not. Generally, the result of this method is equivalent to `!isMutable()`.
     *
     * @return `true` if this IItemStack is immutable, `false` if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("isImmutable")
    boolean isImmutable();
    
    /**
     * Returns whether this IItemStack is mutable or not. Generally, the result of this method is equivalent to `!isImmutable()`.
     *
     * @return `true` if this IItemStack is mutable, `false` if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("isMutable")
    default boolean isMutable() {
        
        return !isImmutable();
    }
    
    /**
     * Returns the damage value of this IItemStack. The damage value determines haw many times an item stack has already been used.
     *
     * @return The damage value of this IItemStack.
     */
    @ZenCodeType.Getter("damage")
    default int getDamage() {
        
        return getInternal().getDamageValue();
    }
    
    //    @ZenCodeType.Getter("toolTypes")
    //    default ToolType[] getToolTypes() {
    //
    //        return getInternal().getToolTypes().toArray(new ToolType[0]);
    //    }

    /**
     * Returns the {@link Enchantment}s on this IItemStack and their respective levels.
     *
     * @return An {@link Enchantment} => int map, where the map's value for an enchantment represents the enchantment's level on this IItemStack.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("enchantments")
    default Map<Enchantment, Integer> getEnchantments() {
        
        return EnchantmentHelper.getEnchantments(getInternal());
    }
    
    /**
     * Sets {@link Enchantment}s on this IItemStack. For example, `<item:minecraft:diamond_sword>.setEnchantments({<enchantment:minecraft:sharpness>: 5, <enchantment:minecraft:looting>: 1})` will return a diamond sword with Sharpness V and Looting I.
     *
     * @param enchantments The {@link Enchantment} to set on this IItemStack. Must be an enchantment => int map, where the map's value for an enchantment represents the enchantment level that should be set.
     *
     * @return This IItemStack if mutable, a new IItemStack with the enchantments added otherwise.
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("enchantments")
    default IItemStack setEnchantments(Map<Enchantment, Integer> enchantments) {
        
        return modify(newStack -> EnchantmentUtil.setEnchantments(enchantments, newStack));
    }
    
    /**
     * Returns the level of the given {@link Enchantment} on this IItemStack, or 0 if the enchantment if this IItemStack does not have the given enchantment.
     *
     * @return The level of the given {@link Enchantment} on this IItemStack, or 0 if the enchantment if this IItemStack does not have the given enchantment.
     */
    @ZenCodeType.Method
    default int getEnchantmentLevel(Enchantment enchantment) {
        
        return getEnchantments().getOrDefault(enchantment, 0);
    }
    
    /**
     * Enchants this IItemStack with the given {@link Enchantment}. For example, `<item:minecraft:diamond_sword>.withEnchantment(<enchantment:minecraft:sharpness>, 3)` will return a diamond sword with Sharpness III.
     *
     * @param enchantment The {@link Enchantment} to add.
     * @param level       The level of the {@link Enchantment} to add.
     *
     * @return This IItemStack if mutable, a new IItemStack with the enchantment added otherwise.
     */
    @ZenCodeType.Method
    default IItemStack withEnchantment(Enchantment enchantment, @ZenCodeType.OptionalInt(1) int level) {
        
        return modify(itemStack -> {
            Map<Enchantment, Integer> enchantments = getEnchantments();
            enchantments.put(enchantment, level);
            EnchantmentUtil.setEnchantments(enchantments, itemStack);
        });
    }
    
    /**
     * Removes the given {@link Enchantment} from this IItemStack.
     *
     * @param enchantment The {@link Enchantment} to remove.
     *
     * @return This IItemStack if mutable, a new IItemStack with the enchantment added otherwise.
     */
    @ZenCodeType.Method
    default IItemStack removeEnchantment(Enchantment enchantment) {
        
        return modify(itemStack -> {
            Map<Enchantment, Integer> enchantments = getEnchantments();
            enchantments.remove(enchantment);
            EnchantmentUtil.setEnchantments(enchantments, itemStack);
        });
    }
    
    /**
     * Returns the internal {@link ItemStack} for this IItemStack.
     *
     * @return The internal {@link ItemStack} for this IItemStack.
     */
    @ZenCodeType.Method
    @ZenCodeType.Caster(implicit = true)
    ItemStack getInternal();
    
    @Override
    default Ingredient asVanillaIngredient() {
        
        if(getInternal().isEmpty()) {
            return Ingredient.EMPTY;
        }
        
        if(!getInternal().hasTag()) {
            return Ingredient.of(getImmutableInternal());
        }
        return Services.REGISTRY.getIngredientPartialTag(getImmutableInternal());
    }

    /**
     * Returns an immutable internal {@link ItemStack} for this IItemStack.
     *
     * @return An immutable internal {@link ItemStack} for this IItemStack.
     */
    @ZenCodeType.Method
    default ItemStack getImmutableInternal() {
        
        return getInternal().copy();
    }

    /**
     * Returns this IItemStack, cast to {@link IIngredientWithAmount}.
     * @return This IItemStack, cast to {@link IIngredientWithAmount}.
     */
    @ZenCodeType.Method
    @ZenCodeType.Caster(implicit = true)
    default IIngredientWithAmount asIIngredientWithAmount() {
        
        return this;
    }
    
    @Override
    default IItemStack getIngredient() {
        
        return this;
    }
    
    @Override
    default IData asIData() {
        
        final IData data = IIngredient.super.asIData();
        assert data instanceof MapData;
        data.put("count", new IntData(this.getAmount()));
        return data;
    }
    
    IItemStack modify(Consumer<ItemStack> stackModifier);
    
}

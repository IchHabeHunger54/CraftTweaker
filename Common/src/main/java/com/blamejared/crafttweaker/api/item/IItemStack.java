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
 * Represents an in-game item stack. This is what you want most of the time when dealing with recipes.
 *
 * <p>{@code IItemStack}s can be obtained through bracket handlers prefixed with {@code item}. For example, {@code <item:minecraft:diamond>} represents an item stack containing one diamond.</p>
 *
 * <p>Note that some methods, such as {@code setMaxStackSize()}, will affect the item of this {@code IItemStack} (and thus work for all stacks), while others, such as {@link IItemStack#addGlobalAttributeModifier(Attribute, String, double, AttributeModifier.Operation, EquipmentSlot[])}, only work for that particular stack. Make sure to read the descriptions carefully!</p>
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
     * Creates a copy of this {@code IItemStack}.
     */
    @ZenCodeType.Method
    IItemStack copy();
    
    /**
     * Returns the registry name for this {@code IItemStack}'s item. This is the item's id that shows up when advanced tooltips (F3+H) are enabled.
     *
     * @return The registry name of this {@code IItemStack}'s item.
     */
    @ZenCodeType.Getter("registryName")
    default ResourceLocation getRegistryName() {
        
        return Registry.ITEM.getKey(getInternal().getItem());
    }
    
    /**
     * Returns the owning mod (a.k.a. mod id) for this {@code IItemStack}'s item.
     *
     * @return The owning mod of this {@code IItemStack}'s item.
     */
    @ZenCodeType.Getter("owner")
    default String getOwner() {
        
        return getRegistryName().getNamespace();
    }
    
    /**
     * Returns whether this {@code IItemStack} is empty or not. Empty {@code IItemStack}s have the item {@code "minecraft:air"}.
     *
     * @return {@code true} if this {@code IItemStack} is empty, {@code false} if not.
     */
    @Override
    default boolean isEmpty() {
        
        return getInternal().isEmpty();
    }
    
    /**
     * Returns the max stack size of this {@code IItemStack}'s item.
     *
     * @return The max stack size of this {@code IItemStack}'s item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("maxStackSize")
    default int getMaxStackSize() {
        
        return getInternal().getItem().getMaxStackSize();
    }
    
    /**
     * Sets the max stack size of this {@code IItemStack}'s item. Values greater than 64 are generally not recommended.
     *
     * @param newMaxStackSize The new max stack size of this {@code IItemStack}'s item.
     *
     * @docParam newMaxStackSize 16
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("maxStackSize")
    default void setMaxStackSize(int newMaxStackSize) {
        
        CraftTweakerAPI.apply(new ActionSetItemProperty<>(this, "Max Stack Size", newMaxStackSize, this.getInternal()
                .getItem().getMaxStackSize(), ((AccessItem) this.getInternal()
                .getItem())::crafttweaker$setMaxStackSize));
    }
    
    /**
     * Returns the {@link Rarity} of this {@code IItemStack}'s item. This mainly determines an item's name color, see the documentation on {@link Rarity} for more information.
     *
     * @return The {@link Rarity} of this {@code IItemStack}'s item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("rarity")
    default Rarity getRarity() {
        
        return getInternal().getRarity();
    }
    
    /**
     * Sets the {@link Rarity} of this {@code IItemStack}'s item. This mainly determines an item's name color.
     *
     * @param newRarity The new {@link Rarity} of this {@code IItemStack}'s item.
     * 
     * @docParam newRarity <constant:minecraft:item/rarity:uncommon>
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("rarity")
    default void setRarity(Rarity newRarity) {
        
        CraftTweakerAPI.apply(new ActionSetItemProperty<>(this, "Rarity", newRarity, this.getInternal()
                .getRarity(), ((AccessItem) this.getInternal()
                .getItem())::crafttweaker$setRarity));
    }
    
    /**
     * Sets the lore of this {@code IItemStack}. Lore is basically an extra tooltip that is stored in NBT rather than being added through code. The parameter is a vararg, every argument will result in one line of lore.
     *
     * @param lore The new Lore of this {@code IItemStack}. This is a vararg, so you can add as many arguments as you need.
     * 
     * @docParam lore Component.literal("I am the lore.", "I speak for the trees.", "I do so in multiple lines.")
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
     * Returns the formatted display name of this {@code IItemStack}. This is what the player sees as the item's name when hovering over it. Note that the output of this method may be influenced by active resource packs, and will not produce reliable results on servers.
     *
     * @return The formatted display name of this {@code IItemStack}.
     */
    @ZenCodeType.Getter("displayName")
    default Component getDisplayName() {
        
        return getInternal().getDisplayName();
    }
    
    /**
     * Sets the display name of this {@code IItemStack}. This is what the player will see as the item's name when hovering over it.
     *
     * @param name The new display name of this {@code IItemStack}.
     * 
     * @docParam name Component.literal("Premium Item")
     */
    @ZenCodeType.Method
    default IItemStack withDisplayName(Component name) {
        
        return modify(itemStack -> itemStack.setHoverName(name));
    }
    
    /**
     * Returns the hover name of this {@code IItemStack}. This will give the raw name, without the formatting that {@code getDisplayName()} applies.
     *
     * @return The hover name of this {@code IItemStack}.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("hoverName")
    default Component getHoverName() {
        
        return getInternal().getHoverName();
    }
    
    /**
     * Clears any custom name set for this {@code IItemStack}.
     */
    @ZenCodeType.Method
    default void resetHoverName() {
        
        getInternal().resetHoverName();
    }
    
    /**
     * Returns whether this {@code IItemStack} has a custom display name or not.
     *
     * @return {@code true} if this {@code IItemStack} has a custom display name, {@code false} if not.
     */
    @ZenCodeType.Getter("hasCustomHoverName")
    default boolean hasDisplayName() {
        
        return getInternal().hasCustomHoverName();
    }
    
    /**
     * Returns whether this {@code IItemStack} has a foil effect (enchantment glint) or not. Mostly identical with {@code isEnchanted()}, however, some items have a foil effect on their own (such as enchanted golden apples).
     *
     * @return {@code true} if this {@code IItemStack} has a foil effect, {@code false} if not.
     */
    @ZenCodeType.Getter("hasFoil")
    default boolean hasFoil() {
        
        return getInternal().hasFoil();
    }
    
    /**
     * Returns whether this {@code IItemStack} can be enchanted or not.
     *
     * @return {@code true} if this {@code IItemStack} has a custom display name, {@code false} if not.
     */
    @ZenCodeType.Getter("isEnchantable")
    default boolean isEnchantable() {
        
        return getInternal().isEnchantable();
    }
    
    /**
     * Returns whether this {@code IItemStack} is enchanted or not.
     *
     * @return {@code true} if this {@code IItemStack} is enchanted, {@code false} if not.
     */
    @ZenCodeType.Getter("isEnchanted")
    default boolean isEnchanted() {
        
        return getInternal().isEnchanted();
    }
    
    /**
     * Returns the base repair cost of this {@code IItemStack}, or 0 if no base repair cost is set. The base repair cost is used in anvil repair calculations and has the value 0 for all vanilla items, but can be overridden by the {@code RepairCost} NBT tag.
     *
     * @return The base repair cost of this {@code IItemStack}, or 0 if no base repair cost is set.
     */
    @ZenCodeType.Getter("baseRepairCost")
    default int getBaseRepairCost() {
        
        return getInternal().getBaseRepairCost();
    }
    
    /**
     * Returns the amount of this {@code IItemStack}.
     *
     * @return The amount of this {@code IItemStack}.
     */
    @ZenCodeType.Getter("amount")
    default int getAmount() {
        
        return getInternal().getCount();
    }
    
    /**
     * Sets the amount of this {@code IItemStack}.
     *
     * <p>Note: This method is equivalent to using the * operator. This means that, for example, {@code <item:minecraft:diamond>.setAmount(8)} and {@code <item:minecraft:diamond> * 8} will have the same effect.</p>
     *
     * @param amount The new amount of this {@code IItemStack}.
     *
     * @docParam amount 8
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MUL)
    default IItemStack setAmount(int amount) {
        
        return modify(itemStack -> itemStack.setCount(amount));
    }
    
    /**
     * Grows this {@code IItemStack}'s amount by the given amount.
     *
     * @param amount The amount to grow this {@code IItemStack} by.
     *
     * @return This {@code IItemStack} if mutable, a new {@code IItemStack} with the new amount otherwise.
     */
    @ZenCodeType.Method
    default IItemStack grow(@ZenCodeType.OptionalInt(1) int amount) {
        
        return setAmount(getAmount() + amount);
    }
    
    /**
     * Shrinks this {@code IItemStack}'s amount by the given amount.
     *
     * @param amount The amount to shrink this {@code IItemStack} by.
     *
     * @return This {@code IItemStack} if mutable, a new {@code IItemStack} with the new amount otherwise.
     */
    @ZenCodeType.Method
    default IItemStack shrink(@ZenCodeType.OptionalInt(1) int amount) {
        
        return setAmount(getAmount() - amount);
    }
    
    /**
     * Returns whether this {@code IItemStack} is stackable, meaning whether it have a stack size greater than 1 or not.
     *
     * @return {@code true} if this {@code IItemStack} is stackable, {@code false} if not.
     */
    @ZenCodeType.Getter("stackable")
    default boolean isStackable() {
        
        return getInternal().isStackable();
    }
    
    /**
     * Sets the durability damage of this {@code IItemStack}. Note that this represents the uses done so far, not the uses left.
     *
     * @param damage The new durability damage of this {@code IItemStack}.
     *
     * @docParam damage 50
     */
    @ZenCodeType.Method
    default IItemStack withDamage(int damage) {
        
        return modify(itemStack -> itemStack.setDamageValue(damage));
    }
    
    /**
     * Adds an {@link AttributeModifier} to this {@code IItemStack}, using a specific UUID.
     *
     * <p>The UUID can be used to override existing attributes of an {@code IItemStack} with this new modifier. Use {@code /ct hand attributes} to get the attribute UUIDs on an IItemStack. If you do not want to override an attribute modifier, but want to add one instead, you can use an online UUID generator of your choice.</p>
     *
     * <p>By default, attribute modifiers for an attribute already present on this {@code IItemStack} will replace any attribute modifiers for that attribute. This can be prevented by setting the optional {@code preserveDefaults} flag to {@code true}.</p>
     *
     * @param attribute        The {@link Attribute} of the modifier.
     * @param uuid             The UUID of the modifier.
     * @param name             The name of the modifier.
     * @param value            The value of the modifier.
     * @param operation        The operation of the modifier.
     * @param slotTypes        What slots the modifier is valid for.
     * @param preserveDefaults Whether the modifiers that are already present on this {@code IItemStack} should be preserved or not.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam uuid "8c1b5535-9f79-448b-87ae-52d81480aaa3"
     * @docParam name "Extra Power"
     * @docParam value 10
     * @docParam operation AttributeOperation.ADDITION
     * @docParam slotTypes [<constant:minecraft:equipmentslot:mainhand>]
     * @docParam preserveDefaults true
     */
    @ZenCodeType.Method
    default IItemStack withAttributeModifier(Attribute attribute, String uuid, String name, double value, AttributeModifier.Operation operation, EquipmentSlot[] slotTypes, @ZenCodeType.OptionalBoolean boolean preserveDefaults) {
        
        return withAttributeModifier(attribute, UUID.fromString(uuid), name, value, operation, slotTypes, preserveDefaults);
    }

    /**
     * Adds an {@link AttributeModifier} to this {@code IItemStack}, using a specific UUID.
     *
     * <p>The UUID can be used to override existing attributes of an {@code IItemStack} with this new modifier. Use {@code /ct hand attributes} to get the attribute UUIDs on an IItemStack. If you do not want to override an attribute modifier, but want to add one instead, you can use an online UUID generator of your choice.</p>
     *
     * <p>By default, attribute modifiers for an attribute already present on this {@code IItemStack} will replace any attribute modifiers for that attribute. This can be prevented by setting the optional {@code preserveDefaults} flag to {@code true}.</p>
     *
     * @param attribute        The {@link Attribute} of the modifier.
     * @param uuid             The UUID of the modifier.
     * @param name             The name of the modifier.
     * @param value            The value of the modifier.
     * @param operation        The operation of the modifier.
     * @param slotTypes        What slots the modifier is valid for.
     * @param preserveDefaults Whether the modifiers that are already present on this {@code IItemStack} should be preserved or not.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam uuid "8c1b5535-9f79-448b-87ae-52d81480aaa3"
     * @docParam name "Extra Power"
     * @docParam value 10
     * @docParam operation AttributeOperation.ADDITION
     * @docParam slotTypes [<constant:minecraft:equipmentslot:mainhand>]
     * @docParam preserveDefaults true
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
     * Adds an {@link AttributeModifier} to this {@code IItemStack}. Unlike other overloads, which take a separate UUID parameter, this one creates a random UUID based on the given {@code name}.
     *
     * <p>By default, attribute modifiers for an attribute already present on this {@code IItemStack} will replace any attribute modifiers for that attribute. This can be prevented by setting the optional {@code preserveDefaults} flag to {@code true}.</p>
     *
     * @param attribute        The {@link Attribute} of the modifier.
     * @param name             The name of the modifier.
     * @param value            The value of the modifier.
     * @param operation        The operation of the modifier.
     * @param slotTypes        What slots the modifier is valid for.
     * @param preserveDefaults Whether the modifiers that are already present on this {@code IItemStack} should be preserved or not.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam name "Extra Power"
     * @docParam value 10
     * @docParam operation AttributeOperation.ADDITION
     * @docParam slotTypes [<constant:minecraft:equipmentslot:mainhand>]
     * @docParam preserveDefaults true
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
     * Returns the attributes and attribute modifiers on this {@code IItemStack} for the given {@link EquipmentSlot}.
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
     * Returns whether this {@code IItemStack} is damageable or not.
     *
     * @return {@code true} if this {@code IItemStack} is damageable, {@code false} if not.
     */
    @ZenCodeType.Getter("damageableItem")
    default boolean isDamageableItem() {
        
        return getInternal().isDamageableItem();
    }
    
    /**
     * Returns whether this {@code IItemStack} is damaged or not. The result of this method is equivalent to {@code getDamage() > 0}.
     *
     * @return {@code true} if this {@code IItemStack} is damaged, {@code false} if not.
     */
    @ZenCodeType.Getter("damaged")
    default boolean isDamaged() {
        
        return getInternal().isDamaged();
    }
    
    /**
     * Returns the max damage (a.k.a. max durability) of this {@code IItemStack}.
     *
     * @return The max damage of this {@code IItemStack}.
     */
    @ZenCodeType.Getter("maxDamage")
    default int getMaxDamage() {
        
        return getInternal().getMaxDamage();
    }
    
    /**
     * Sets the max damage (a.k.a. max durability) of this {@code IItemStack}'s item. Using {@code 0} will make the item unbreakable.
     *
     * @param newMaxDamage The new max damage of this {@code IItemStack}'s item.
     *
     * @docParam newMaxDamage 2000
     */
    @ZenCodeType.Setter("maxDamage")
    default void setMaxDamage(int newMaxDamage) {
        
        CraftTweakerAPI.apply(new ActionSetItemProperty<>(this, "Max Damage", newMaxDamage, this.getInternal()
                .getMaxDamage(), ((AccessItem) this.getInternal()
                .getItem())::crafttweaker$setMaxDamage));
    }
    
    /**
     * Returns the unlocalized name (a.k.a. translation key) of this {@code IItemStack}.
     *
     * @return The unlocalized name of this {@code IItemStack}.
     */
    @ZenCodeType.Getter("descriptionId")
    default String getDescriptionId() {
        
        return getInternal().getDescriptionId();
    }
    
    /**
     * Sets the NBT tag for this {@code IItemStack}.
     *
     * @param tag The new tag of this {@code IItemStack}.
     *
     * @return This {@code IItemStack} if mutable, a new {@code IItemStack} with the new tag otherwise.
     *
     * @docParam tag {Potion: "minecraft:night_vision"}
     */
    @ZenCodeType.Method
    default IItemStack withTag(MapData tag) {
        
        return modify(itemStack -> itemStack.setTag(tag.getInternal()));
    }
    
    /**
     * Removes the NBT tag from this {@code IItemStack}.
     *
     * @return This {@code IItemStack} if mutable, a new {@code IItemStack} with the tag removed otherwise.
     */
    @ZenCodeType.Method
    default IItemStack withoutTag() {
        
        return modify(itemStack -> itemStack.setTag(null));
    }
    
    /**
     * Returns whether this {@code IItemStack} has NBT tags or not. The result of this method is equivalent to {@code getTag() != null}.
     *
     * @return {@code true} if this {@code IItemStack} has NBT tags, {@code false} if not.
     */
    @ZenCodeType.Getter("hasTag")
    default boolean hasTag() {
        
        return getInternal().hasTag();
    }
    
    /**
     * Returns the NBT tag of this {@code IItemStack}.
     *
     * @return The NBT tag of this {@code IItemStack}, or {@code null} if there are no NBT tags present.
     */
    @ZenCodeType.Nullable
    @ZenCodeType.Getter("tag")
    default IData getTag() {
        
        return TagToDataConverter.convert(getInternal().getTag());
    }
    
    /**
     * Returns the NBT tag of this {@code IItemStack}, or creates a new one if absent. The result of this method is equivalent to {@code getTag() == null ? new MapData() : getTag()}.
     *
     * @return The NBT tag of this {@code IItemStack}, or an empty tag if there are no NBT tags present.
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
     * Returns the use duration of this {@code IItemStack}, in ticks. {@code 72000} (1 hour) is used for items that can be used indefinitely, such as bows being drawn.
     *
     * @return The use duration of this {@code IItemStack}.
     */
    @ZenCodeType.Getter("useDuration")
    default int getUseDuration() {
        
        return getInternal().getUseDuration();
    }
    
    /**
     * Returns whether this {@code IItemStack}'s use logic is fired on release, rather than when beginning to draw. In vanilla, this will return {@code true} for crossbows and {@code false} for all other items.
     *
     * @return {@code true} if this {@code IItemStack}'s use logic is fired on release, {@code false} if not.
     */
    @ZenCodeType.Getter("useOnRelease")
    default boolean useOnRelease() {
        
        return getInternal().useOnRelease();
    }
    
    /**
     * Returns the {@link FoodProperties} of this {@code IItemStack}'s item.
     *
     * @return The {@link FoodProperties} of this {@code IItemStack}'s item, or {@code null} if no {@link FoodProperties} are present.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("food")
    @ZenCodeType.Nullable
    default FoodProperties getFood() {
        
        return getInternal().getItem().getFoodProperties();
    }
    
    /**
     * Sets the {@link FoodProperties} for this {@code IItemStack}'s item.
     *
     * @param food The new {@link FoodProperties} for this {@code IItemStack}'s item. If this is {@code null}, the item will no longer be edible.
     *
     * @docParam food new FoodProperties(8, 0.8)
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("food")
    default void setFood(@ZenCodeType.Nullable FoodProperties food) {
        
        CraftTweakerAPI.apply(new ActionSetFood(this, food, this.getInternal()
                .getItem()
                .getFoodProperties()));
    }
    
    /**
     * Returns whether this {@code IItemStack}'s item is edible or not. The result of this method is equivalent to {@code getFood() != null}.
     *
     * @return {@code true} if this {@code IItemStack}'s item is edible, {@code false} if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("isEdible")
    default boolean isEdible() {
        
        return getInternal().isEdible();
    }
    
    /**
     * Returns the burn time of this {@code IItemStack}, in ticks.
     *
     * @return The burn time of this {@code IItemStack}.
     */
    @ZenCodeType.Getter("burnTime")
    default int getBurnTime() {
        
        return Services.EVENT.getBurnTime(this);
    }
    
    /**
     * Returns whether this {@code IItemStack}'s item is immune to fire or not.
     *
     * @return {@code true} if this {@code IItemStack}'s item is immune to fire, {@code false} if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("fireResistant")
    default boolean isFireResistant() {
        
        return getInternal().getItem().isFireResistant();
    }
    
    /**
     * Sets whether this {@code IItemStack}'s item should be immune to fire or not.
     *
     * @param fireResistant Whether the {@code IItemStack}'s item should be immune to fire or not.
     *
     * @docParam fireResistant true
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("fireResistant")
    default void setFireResistant(boolean fireResistant) {
        
        CraftTweakerAPI.apply(new ActionSetItemProperty<>(this, "Fire Resistant", fireResistant, this.getInternal()
                .getItem().isFireResistant(), ((AccessItem) this.getInternal()
                .getItem())::crafttweaker$setFireResistant));
    }
    
    /**
     * Returns a {@link Percentaged} (weighted) {@code IItemStack} with the given percentage.
     *
     * <p>Note: This method is equivalent to using the % operator. This means that, for example, {@code <item:minecraft:diamond>.percent(50)} and {@code <item:minecraft:diamond> % 50} will have the same effect.</p>
     *
     * @param percentage The percentage of the new {@code IItemStack}.
     *
     * @return A {@link Percentaged} (weighted) {@code IItemStack} with the given percentage.
     *
     * @docParam percentage 80
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
     * Returns a {@link Percentaged} (weighted) instance of this {@code IItemStack}. The result of this method is equivalent to {@code percent(100)}.
     *
     * @return A {@link Percentaged} (weighted) instance of this {@code IItemStack}.
     */
    @ZenCodeType.Caster(implicit = true)
    default Percentaged<IItemStack> asWeightedItemStack() {
        
        return percent(100.0D);
    }
    
    /**
     * Returns the {@link Item} for this {@code IItemStack}'s item.
     *
     * @return The {@link Item} for this {@code IItemStack}'s item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("definition")
    @ZenCodeType.Caster(implicit = true)
    default Item getDefinition() {
        
        return getInternal().getItem();
    }
    
    /**
     * Returns a mutable instance of this {@code IItemStack}.
     *
     * @return A mutable instance of this {@code IItemStack}.
     */
    @ZenCodeType.Method
    IItemStack asMutable();
    
    /**
     * Returns an immutable instance of this {@code IItemStack}.
     *
     * @return An immutable instance of this {@code IItemStack}.
     */
    @ZenCodeType.Method
    IItemStack asImmutable();
    
    /**
     * Returns whether this {@code IItemStack} is immutable or not. The result of this method is equivalent to {@code !isMutable()}.
     *
     * @return {@code true} if this {@code IItemStack} is immutable, {@code false} if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("isImmutable")
    boolean isImmutable();
    
    /**
     * Returns whether this {@code IItemStack} is mutable or not. The result of this method is equivalent to {@code !isImmutable()}.
     *
     * @return {@code true} if this {@code IItemStack} is mutable, {@code false} if not.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("isMutable")
    default boolean isMutable() {
        
        return !isImmutable();
    }
    
    /**
     * Returns the damage value of this {@code IItemStack}. The damage value determines haw many times an item stack has already been used.
     *
     * @return The damage value of this {@code IItemStack}.
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
     * Returns the {@link Enchantment}s on this {@code IItemStack} and their respective levels.
     *
     * @return An {@link Enchantment} => int map, where the map's value for an enchantment represents the enchantment's level on this {@code IItemStack}.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("enchantments")
    default Map<Enchantment, Integer> getEnchantments() {
        
        return EnchantmentHelper.getEnchantments(getInternal());
    }
    
    /**
     * Sets {@link Enchantment}s on this {@code IItemStack}.
     *
     * @param enchantments The {@link Enchantment} to set on this {@code IItemStack}. Must be an enchantment => int map, where the map's value for an enchantment represents the enchantment level that should be set.
     *
     * @return This {@code IItemStack} if mutable, a new {@code IItemStack} with the enchantments added otherwise.
     *
     * @docParam enchantments {<enchantment:minecraft:sharpness>: 5, <enchantment:minecraft:looting>: 1}
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("enchantments")
    default IItemStack setEnchantments(Map<Enchantment, Integer> enchantments) {
        
        return modify(newStack -> EnchantmentUtil.setEnchantments(enchantments, newStack));
    }
    
    /**
     * Returns the level of the given {@link Enchantment} on this {@code IItemStack}, or 0 if this {@code IItemStack} does not have the given enchantment.
     *
     * @return The level of the given {@link Enchantment} on this {@code IItemStack}, or 0 if this {@code IItemStack} does not have the given enchantment.
     */
    @ZenCodeType.Method
    default int getEnchantmentLevel(Enchantment enchantment) {
        
        return getEnchantments().getOrDefault(enchantment, 0);
    }
    
    /**
     * Enchants this {@code IItemStack} with the given {@link Enchantment}.
     *
     * @param enchantment The {@link Enchantment} to add.
     * @param level       The level of the {@link Enchantment} to add.
     *
     * @return This {@code IItemStack} if mutable, a new {@code IItemStack} with the enchantment added otherwise.
     *
     * @docParam enchantment <enchantment:minecraft:sharpness>
     * @docParam level 3
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
     * Removes the given {@link Enchantment} from this {@code IItemStack}.
     *
     * @param enchantment The {@link Enchantment} to remove.
     *
     * @return This {@code IItemStack} if mutable, a new {@code IItemStack} with the enchantment added otherwise.
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
     * Returns the internal {@link ItemStack} for this {@code IItemStack}.
     *
     * @return The internal {@link ItemStack} for this {@code IItemStack}.
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
     * Returns an immutable internal {@link ItemStack} for this {@code IItemStack}.
     *
     * @return An immutable internal {@link ItemStack} for this {@code IItemStack}.
     */
    @ZenCodeType.Method
    default ItemStack getImmutableInternal() {
        
        return getInternal().copy();
    }

    /**
     * Returns this {@code IItemStack}, cast to {@link IIngredientWithAmount}.
     * @return This {@code IItemStack}, cast to {@link IIngredientWithAmount}.
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

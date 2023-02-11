package com.blamejared.crafttweaker.api.bracket;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.BracketResolver;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.bracket.custom.RecipeTypeBracketHandler;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.recipe.replacement.ITargetingStrategy;
import com.blamejared.crafttweaker.natives.block.ExpandBlockState;
import com.blamejared.crafttweaker.natives.block.material.ExpandMaterial;
import com.blamejared.crafttweaker.natives.world.damage.ExpandDamageSource;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * This class contains helpers for getting most bracket handlers.
 *
 * <p>Some bracket handlers, such as the ones for recipe types, tags and tag managers, are not shown here as they use a different internal structure.</p>
 */
@ZenRegister
@ZenCodeType.Name("crafttweaker.api.bracket.BracketHandlers")
@Document("vanilla/api/BracketHandlers")
public class BracketHandlers {
    /**
     * Returns the {@link Attribute} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link Attribute} for.
     *
     * @return The {@link Attribute} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:generic.max_health"
     */
    @ZenCodeType.Method
    @BracketResolver("attribute")
    public static Attribute getAttribute(String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("Attribute BEP <attribute:{}> does not seem to be lower-cased!", tokens);
        }
        
        final String[] split = tokens.split(":");
        if(split.length != 2) {
            throw new IllegalArgumentException("Could not get attribute with name: <attribute:" + tokens + ">! Syntax is <attribute:modid:name>");
        }
        ResourceLocation key = new ResourceLocation(split[0], split[1]);
        
        return Registry.ATTRIBUTE.getOptional(key)
                .orElseThrow(() -> new IllegalArgumentException("Could not get attribute with name: <attribute:" + tokens + ">! Attribute does not appear to exist!"));
    }

    /**
     * Returns the {@link Block} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link Block} for.
     *
     * @return The {@link Block} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:dirt"
     */
    @ZenCodeType.Method
    @BracketResolver("block")
    public static Block getBlock(String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("Block BEP <block:{}> does not seem to be lower-cased!", tokens);
        }
        
        final String[] split = tokens.split(":");
        if(split.length != 2) {
            throw new IllegalArgumentException("Could not get block with name: <block:" + tokens + ">! Syntax is <block:modid:itemname>");
        }
        ResourceLocation key = new ResourceLocation(split[0], split[1]);
        return Registry.BLOCK.getOptional(key)
                .orElseThrow(() -> new IllegalArgumentException("Could not get block with name: <block:" + tokens + ">! Block does not appear to exist!"));
    }

    /**
     * Returns the {@link Material} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link Material} for.
     *
     * @return The {@link Material} bracket handler associated with the given name.
     *
     * @docParam tokens "earth"
     */
    @ZenCodeType.Method
    @BracketResolver("material")
    public static Material getMaterial(String tokens) {
        
        // 1.16 did look at the Material class to see its fields,
        // but we can just add a test to make sure that ExpandMaterial.VANILLA_MATERIALS always contains the most upto date values
        return ExpandMaterial.getOptionalMaterial(tokens)
                .orElseThrow(() -> new IllegalArgumentException("Could not find material <material:" + tokens + ">!"));
    }

    /**
     * Returns a {@link BlockState} bracket handler created from the given input. Returns {@code null} if no {@link BlockState} could be created.
     *
     * @param tokens The input to create the {@link BlockState} from.
     *
     * @return A {@link BlockState} bracket handler created from the given input.
     *
     * @docParam tokens "minecraft:acacia_planks"
     * @docParam tokens "minecraft:furnace:facing=north,lit=false"
     */
    @ZenCodeType.Method
    @BracketResolver("blockstate")
    public static BlockState getBlockState(String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("BlockState BEP <blockstate:{}> does not seem to be lower-cased!", tokens);
        }
        String[] split = tokens.split(":", 4);
        
        if(split.length > 1) {
            String blockName = split[0] + ":" + split[1];
            String properties = split.length > 2 ? split[2] : "";
            
            Optional<Block> found = Registry.BLOCK.getOptional(new ResourceLocation(blockName));
            if(found.isEmpty()) {
                CraftTweakerAPI.LOGGER.error("Error creating BlockState!", new IllegalArgumentException("Could not get BlockState from: <blockstate:" + tokens + ">! The block does not appear to exist!"));
            } else {
                return getBlockState(found.get(), blockName, properties);
            }
        }
        CraftTweakerAPI.LOGGER.error("Error creating BlockState!", new IllegalArgumentException("Could not get BlockState from: <blockstate:" + tokens + ">!"));
        return null;
    }

    /**
     * Returns a {@link BlockState} bracket handler created from the given inputs. Returns {@code null} if no {@link BlockState} could be created.
     *
     * @param name       The name of the {@link Block} to create the {@link BlockState} from.
     * @param properties The block state properties to apply to the {@link BlockState}.
     *
     * @return A {@link BlockState} bracket handler created from the given inputs.
     *
     * @docParam name "minecraft:furnace"
     * @docParam tokens "facing=north,lit=false"
     */
    public static BlockState getBlockState(String name, String properties) {
        
        return getBlockState(Registry.BLOCK.get(new ResourceLocation(name)), name, properties);
    }

    /**
     * Returns a {@link BlockState} bracket handler created from the given inputs. Returns {@code null} if no {@link BlockState} could be created.
     *
     * @param block      The {@link Block} to create the {@link BlockState} from.
     * @param name       The name of the {@link Block} to create the {@link BlockState} from.
     * @param properties The block state properties to apply to the {@link BlockState}.
     *
     * @return A {@link BlockState} bracket handler created from the given inputs.
     *
     * @docParam block <item:minecraft:furnace>
     * @docParam name "minecraft:furnace"
     * @docParam tokens "facing=north,lit=false"
     */
    public static BlockState getBlockState(Block block, String name, String properties) {
        
        BlockState blockState = block.defaultBlockState();
        if(properties != null && !properties.isEmpty()) {
            for(String propertyPair : properties.split(",")) {
                String[] splitPair = propertyPair.split("=");
                if(splitPair.length != 2) {
                    CraftTweakerAPI.LOGGER.warn("Invalid blockstate property format '{}'. Using default property value.", propertyPair);
                    continue;
                }
                blockState = ExpandBlockState.withProperty(blockState, splitPair[0], splitPair[1]);
            }
        }
        
        return blockState;
    }

    /**
     * Returns the {@link MobEffect} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link MobEffect} for.
     *
     * @return The {@link MobEffect} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:haste"
     */
    @BracketResolver("mobeffect")
    @ZenCodeType.Method
    public static MobEffect getMobEffect(String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("MobEffect BEP <mobeffect:{}> does not seem to be lower-cased!", tokens);
        }
        
        final String[] split = tokens.split(":");
        if(split.length != 2) {
            throw new IllegalArgumentException("Could not get effect with name: <mobeffect:" + tokens + ">! Syntax is <effect:modid:mobeffect>");
        }
        return Registry.MOB_EFFECT.getOptional(new ResourceLocation(split[0], split[1]))
                .orElseThrow(() -> new IllegalArgumentException("Could not get effect with name: <mobeffect:" + tokens + ">! Effect does not appear to exist!"));
    }

    /**
     * Returns the {@link Enchantment} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link Enchantment} for.
     *
     * @return The {@link Enchantment} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:riptide"
     */
    @ZenCodeType.Method
    @BracketResolver("enchantment")
    public static Enchantment getEnchantment(String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("Enchantment BEP <enchantment:{}> does not seem to be lower-case!", tokens);
        }
        
        final String[] split = tokens.split(":");
        if(split.length != 2) {
            throw new IllegalArgumentException("Could not get enchantment '" + tokens + "': not a valid bracket handler, syntax is <enchantment:modid:name>");
        }
        
        final ResourceLocation key = new ResourceLocation(split[0], split[1]);
        Optional<Enchantment> found = Registry.ENCHANTMENT.getOptional(key);
        if(found.isEmpty()) {
            throw new IllegalArgumentException("Could not get enchantment '" + tokens + "': the enchantment does not appear to exist");
        }
        
        return found.get();
    }

    /**
     * Returns the {@link EntityType} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link EntityType} for.
     *
     * @return The {@link EntityType} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:pig"
     */
    @ZenCodeType.Method
    @BracketResolver("entitytype")
    public static EntityType getEntityType(String tokens) {
        
        final int length = tokens.split(":").length;
        if(length == 0 || length > 2) {
            throw new IllegalArgumentException("Could not get entitytype <entitytype:" + tokens + ">");
        }
        final ResourceLocation resourceLocation = new ResourceLocation(tokens);
        
        return Registry.ENTITY_TYPE.getOptional(resourceLocation)
                .orElseThrow(() -> new IllegalArgumentException("Could not get entitytype <entitytype:" + tokens + ">"));
    }


    /**
     * Returns the {@link IItemStack} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link IItemStack} for.
     *
     * @return The {@link IItemStack} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:dirt"
     */
    @BracketResolver("item")
    @ZenCodeType.Method
    public static IItemStack getItem(String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("Item BEP <item:{}> does not seem to be lower-cased!", tokens);
        }
        
        final String[] split = tokens.split(":");
        if(split.length != 2) {
            throw new IllegalArgumentException("Could not get item with name: <item:" + tokens + ">! Syntax is <item:modid:itemname>");
        }
        ResourceLocation key = new ResourceLocation(split[0], split[1]);
        
        ItemStack stack = Registry.ITEM.getOptional(key)
                .map(ItemStack::new)
                .orElseThrow(() -> new IllegalArgumentException("Could not get item with name: <item:" + tokens + ">! Item does not appear to exist!"));
        return IItemStack.of(stack);
    }


    /**
     * Returns the {@link Potion} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link Potion} for.
     *
     * @return The {@link Potion} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:long_night_vision"
     */
    @BracketResolver("potion")
    @ZenCodeType.Method
    public static Potion getPotion(String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("Potion BEP <potion:{}> does not seem to be lower-cased!", tokens);
        }
        
        final String[] split = tokens.split(":");
        if(split.length != 2) {
            throw new IllegalArgumentException("Could not get potion with name: <potion:" + tokens + ">! Syntax is <potion:modid:potionname>");
        }
        ResourceLocation key = new ResourceLocation(split[0], split[1]);
        return Registry.POTION.getOptional(key)
                .orElseThrow(() -> new IllegalArgumentException("Could not get potion with name: <potion:" + tokens + ">! Potion does not appear to exist!"));
    }


    /**
     * Returns the {@link IRecipeManager} bracket handler associated with the given name. Throws an exception if nothing is found. The bracket handler for {@code <recipetype:crafttweaker:scripts>} cannot be accessed this way.
     *
     * @param tokens The name to get the {@link IRecipeManager} for.
     *
     * @return The {@link IRecipeManager} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:crafting"
     */
    @ZenCodeType.Method
    public static IRecipeManager<?> getRecipeManager(String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("RecipeType BEP <recipetype:{}> does not seem to be lower-cased!", tokens);
        }
        if(tokens.equalsIgnoreCase("crafttweaker:scripts")) {
            // This is bound to cause issues, like: <recipetype:crafttweaker:scripts>.removeAll(); Best to just fix it now
            throw new IllegalArgumentException("Nice try, but there's no reason you need to access the <recipetype:crafttweaker:scripts> recipe manager!");
        }
        final ResourceLocation key = new ResourceLocation(tokens);
        
        final IRecipeManager<?> result = RecipeTypeBracketHandler.getOrDefault(key);
        
        if(result != null) {
            return result;
        } else {
            throw new IllegalArgumentException("Could not get RecipeType with name: <recipetype:" + tokens + ">! RecipeType does not appear to exist!");
        }
    }

    /**
     * Creates a {@link ResourceLocation} from the given inputs. Throws an exception if the inputs are invalid.
     *
     * @param tokens The name to create the {@link ResourceLocation} from.
     *
     * @return A {@link ResourceLocation} created from the given inputs.
     *
     * @docParam tokens "minecraft:dirt"
     * @deprecated Use {@link ResourceLocationBracketHandler#getResourceLocation(String)} instead.
     */
    @Deprecated(forRemoval = true)
    @ZenCodeType.Method
    public static ResourceLocation getResourceLocation(String tokens) {
        
        return ResourceLocationBracketHandler.getResourceLocation(tokens);
    }

    /**
     * Returns the {@link VillagerProfession} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link VillagerProfession} for.
     *
     * @return The {@link VillagerProfession} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:armorer"
     */
    @ZenCodeType.Method
    @BracketResolver("profession")
    public static VillagerProfession getProfession(String tokens) {
        
        final int length = tokens.split(":").length;
        if(length == 0 || length > 2) {
            throw new IllegalArgumentException("Could not get profession <profession:" + tokens + ">");
        }
        final ResourceLocation resourceLocation = new ResourceLocation(tokens);
        
        return Registry.VILLAGER_PROFESSION.getOptional(resourceLocation)
                .orElseThrow(() -> new IllegalArgumentException("Could not get profession with name: <profession:" + tokens + ">! Profession does not appear to exist!"));
    }

    /**
     * Returns the {@link DamageSource} bracket handler associated with the given name. If no {@link DamageSource} with the given name is found, a new one will be created and returned.
     *
     * @param tokens The name to get the {@link DamageSource} for.
     *
     * @return The {@link DamageSource} bracket handler associated with the given name.
     *
     * @docParam tokens "magic"
     */
    @ZenCodeType.Method
    @BracketResolver("damagesource")
    public static DamageSource getDamageSource(String tokens) {
        
        return ExpandDamageSource.PRE_REGISTERED_DAMAGE_SOURCES.getOrDefault(tokens, new DamageSource(tokens));
    }

    /**
     * Returns the {@link CreativeModeTab} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link CreativeModeTab} for.
     *
     * @return The {@link CreativeModeTab} bracket handler associated with the given name.
     *
     * @docParam tokens "misc"
     */
    @ZenCodeType.Method
    @BracketResolver("creativemodetab")
    public static CreativeModeTab getCreativeModeTab(String tokens) {
        
        return Arrays.stream(CreativeModeTab.TABS)
                .filter(g -> g.getRecipeFolderName().equals(tokens))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not find creativemodetab for '<creativemodetab:" + tokens + ">'!"));
    }

    /**
     * Returns the {@link SoundEvent} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link SoundEvent} for.
     *
     * @return The {@link SoundEvent} bracket handler associated with the given name.
     *
     * @docParam tokens "minecraft:ambient.cave"
     */
    @ZenCodeType.Method
    @BracketResolver("soundevent")
    public static SoundEvent getSoundEvent(String tokens) {
        
        final int length = tokens.split(":").length;
        if(length == 0 || length > 2) {
            throw new IllegalArgumentException("Could not get sound event <soundevent:" + tokens + ">");
        }
        final ResourceLocation resourceLocation = new ResourceLocation(tokens);
        
        return Registry.SOUND_EVENT.getOptional(resourceLocation)
                .orElseThrow(() -> new IllegalArgumentException("Could not get sound event with name: <soundevent:" + tokens + ">! Sound event does not appear to exist!"));
    }

    /**
     * Returns the {@link ITargetingStrategy} bracket handler associated with the given name. Throws an exception if nothing is found.
     *
     * @param tokens The name to get the {@link ITargetingStrategy} for.
     *
     * @return The {@link ITargetingStrategy} bracket handler associated with the given name.
     *
     * @docParam tokens "earth"
     */
    @ZenCodeType.Method
    @BracketResolver("targetingstrategy")
    public static ITargetingStrategy getTargetingStrategy(final String tokens) {
        
        if(!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
            CraftTweakerAPI.LOGGER.warn("Targeting strategy BEP <targetingstrategy:{}> does not seem to be lower-cased!", tokens);
        }
        
        final String[] split = tokens.split(":");
        if(split.length != 2) {
            throw new IllegalArgumentException("Could not get targeting strategy with <targetingstrategy:" + tokens + ">: syntax is <targetingstrategy:modid:name>");
        }
        
        final ResourceLocation key = new ResourceLocation(split[0], split[1]);
        return ITargetingStrategy.find(key);
    }
    
}

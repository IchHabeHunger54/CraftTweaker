package com.blamejared.crafttweaker.api.bracket;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.annotation.BracketResolver;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

/**
 * This class contains helper specifically for {@link ResourceLocation}s. This is because they are used in multiple loader stages.
 */
@ZenRegister(loaders = {CraftTweakerConstants.DEFAULT_LOADER_NAME, CraftTweakerConstants.TAGS_LOADER_NAME})
@ZenCodeType.Name("crafttweaker.api.bracket.ResourceLocationBracketHandler")
@Document("vanilla/api/ResourceLocationBracketHandler")
public class ResourceLocationBracketHandler {

    /**
     * Creates a {@link ResourceLocation} from the given inputs. Throws an exception if the inputs are invalid.
     *
     * @param tokens The name to create the {@link ResourceLocation} from.
     *
     * @return A {@link ResourceLocation} created from the given inputs.
     *
     * @docParam tokens "minecraft:dirt"
     */
    @ZenCodeType.Method
    @BracketResolver("resource")
    public static ResourceLocation getResourceLocation(String tokens) {
        
        return new ResourceLocation(tokens);
    }
    
}

package com.blamejared.crafttweaker.api.bracket;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import org.openzen.zencode.java.ZenCodeType;

/**
 * This is a helper interface for every object that can be represented by a bracket handler.
 *
 * @docParam this <item:minecraft:dirt>
 */
@ZenRegister(loaders = {CraftTweakerConstants.DEFAULT_LOADER_NAME, CraftTweakerConstants.TAGS_LOADER_NAME})
@ZenCodeType.Name("crafttweaker.api.bracket.CommandStringDisplayable")
@Document("vanilla/api/bracket/CommandStringDisplayable")
public interface CommandStringDisplayable {
    
    /**
     * Returns the string equivalent of this bracket handler.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("commandString")
    String getCommandString();
    
}

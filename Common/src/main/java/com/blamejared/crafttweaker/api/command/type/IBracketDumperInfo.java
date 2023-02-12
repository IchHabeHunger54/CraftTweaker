package com.blamejared.crafttweaker.api.command.type;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

import java.util.stream.Stream;

/**
 * Information related to a bracket dumper.
 *
 * @see com.blamejared.crafttweaker.api.annotation.BracketDumper
 * @since 9.1.0
 */
public interface IBracketDumperInfo extends Command<CommandSourceStack> {
    
    /**
     * Returns the name of the sub-command used in {@code ct dump} to obtain all values of a specific bracket handler.
     *
     * @return The name of the sub-command used in {@code ct dump} to obtain all values of a specific bracket handler.
     *
     * @since 9.1.0
     */
    String subCommandName();
    
    /**
     * Returns a human-readable representation of what the dumped values represent.
     *
     * @return A human-readable representation of what the dumped values represent.
     *
     * @since 9.1.0
     */
    MutableComponent description();
    
    /**
     * Returns the name of the file all dumped values will be written to.
     *
     * @return The name of the file all dumped values will be written to.
     *
     * @since 9.1.0
     */
    String dumpedFileName();
    
    /**
     * Returns a {@link Stream} containing all values that the bracket handler can recognize.
     *
     * <p>In case returning all values is impossible, a valid and complete subset of them can be returned instead. This
     * should be kept to a minimum to ensure complete information wherever possible.</p>
     *
     * @return The values accepted by the bracket handler.
     *
     * @since 9.1.0
     */
    Stream<String> values();
    
}

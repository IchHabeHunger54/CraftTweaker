package com.blamejared.crafttweaker.impl.entity;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import net.minecraft.entity.EntityClassification;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("crafttweaker.api.entity.MCEntityClassification")
public class MCEntityClassification {
    
    private EntityClassification internal;
    
    public MCEntityClassification(EntityClassification internal) {
        this.internal = internal;
    }
    
    @ZenCodeType.Getter("name")
    public String getName() {
        return internal.func_220363_a();
    }
    
    @ZenCodeType.Getter("maxNumberOfEntity")
    public int getMaxNumberOfCreature() {
        return internal.getMaxNumberOfCreature();
    }
    
    @ZenCodeType.Getter("isPeaceful")
    public boolean isPeacefulCreature() {
        return internal.getPeacefulCreature();
    }
    
    @ZenCodeType.Getter("isAnimal")
    public boolean isAnimal() {
        return internal.getAnimal();
    }
    
    public EntityClassification getInternal() {
        return internal;
    }
}

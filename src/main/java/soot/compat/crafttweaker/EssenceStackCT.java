package soot.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import soot.brewing.EssenceStack;
import stanhebben.zenscript.annotations.*;

@ZenClass("mods.soot.IEssenceStack")
@ZenRegister
public class EssenceStackCT {
    final EssenceStack internal;

    public EssenceStackCT(EssenceStack internal) {
        this.internal = internal;
    }

    public EssenceStack getInternal() {
        return internal;
    }

    @ZenGetter("type")
    String getType() {
        return internal.getEssence().getName();
    }

    @ZenGetter("amount")
    int getAmount() {
        return internal.getAmount();
    }

    @ZenGetter("isEmpty")
    boolean isEmpty() {
        return internal.isEmpty();
    }

    @ZenOperator(OperatorType.MUL)
    @ZenMethod
    EssenceStackCT withAmount(int amount) {
        EssenceStack copy = internal.copy();
        copy.setAmount(amount);
        return new EssenceStackCT(copy);
    }
}

package soot.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.formatting.IFormatter;
import soot.recipe.RecipeStillModifier;
import soot.recipe.breweffects.*;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.soot.StillModifierBuilder")
@ZenRegister
public class StillModifierBuilder {
    RecipeStillModifier recipe;

    public StillModifierBuilder(RecipeStillModifier recipe) {
        this.recipe = recipe;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder add(String modifier, float amount, @Optional boolean hidden) {
        recipe.addEffect(new EffectAdd(modifier, amount, hidden));
        return this;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder add(String modifier, float amount, float limit, @Optional boolean hidden) {
        recipe.addEffect(new EffectAdd(modifier, amount, limit, hidden));
        return this;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder multiply(String modifier, float multiplier, @Optional boolean hidden) {
        recipe.addEffect(new EffectMultiply(modifier, multiplier, hidden));
        return this;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder multiply(String modifier, float multiplier, float min, float max, @Optional boolean hidden) {
        recipe.addEffect(new EffectMultiply(modifier, multiplier, min, max, hidden));
        return this;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder gain(int output) {
        recipe.addEffect(new EffectLoss(recipe.getInputConsumed(), output));
        return this;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder max(String modifier, float amount, @Optional boolean hidden) {
        recipe.addEffect(new EffectMax(modifier, amount, hidden));
        return this;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder min(String modifier, float amount, @Optional boolean hidden) {
        recipe.addEffect(new EffectMin(modifier, amount, hidden));
        return this;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder info(String modifier) {
        recipe.addEffect(new EffectInfo(modifier));
        return this;
    }

    @ZenMethod
    @ReturnsSelf
    public StillModifierBuilder info(String modifier, String format) {
        recipe.addEffect(new EffectInfo(modifier, format));
        return this;
    }
}

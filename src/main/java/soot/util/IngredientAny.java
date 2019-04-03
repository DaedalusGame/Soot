package soot.util;

import teamroots.embers.util.IngredientSpecial;

public class IngredientAny extends IngredientSpecial {
    public IngredientAny() {
        super(stack -> true);
    }
}

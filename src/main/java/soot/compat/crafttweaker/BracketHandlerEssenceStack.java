package soot.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.IBracketHandler;
import soot.brewing.EssenceStack;
import soot.brewing.EssenceType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

import java.util.List;

@BracketHandler
@ZenRegister
public class BracketHandlerEssenceStack implements IBracketHandler {
    private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerEssenceStack.class, "getFromString", String.class);

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if(tokens == null || tokens.size() < 3 || !tokens.get(0).getValue().equalsIgnoreCase("brew_essence"))
            return null;
        String name = tokens.get(2).getValue();
        return position -> new ExpressionCallStatic(position,environment,method, new ExpressionString(position, name));
    }

    public static EssenceStackCT getFromString(String name) {
        return new EssenceStackCT(new EssenceStack(EssenceType.getType(name), 1));
    }

    @Override
    public String getRegexMatchingString() {
        return "brew_essence:.*";
    }

    @Override
    public Class<?> getReturnedClass() {
        return EssenceStackCT.class;
    }
}

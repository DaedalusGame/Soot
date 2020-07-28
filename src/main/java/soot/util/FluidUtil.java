package soot.util;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.brewing.CaskManager;
import soot.brewing.EssenceStack;
import soot.brewing.FluidModifier;

import java.util.*;

public class FluidUtil {
    public static final String BREW_MODIFIERS_TAG = "brew_modifiers";
    public static final HashMap<String,FluidModifier> MODIFIERS = new HashMap<>();
    public static final TreeSet<String> SORTED_MODIFIER_KEYS = new TreeSet<>();

    public static void registerModifier(FluidModifier fluidModifier) {
        MODIFIERS.put(fluidModifier.name,fluidModifier);
        SORTED_MODIFIER_KEYS.add(fluidModifier.name);
    }

    public static void setDefaultValue(Fluid fluid, String name, float value)
    {
        FluidModifier modifier = MODIFIERS.get(name);
        if(modifier != null)
        {
            modifier.setDefault(fluid,value);
        }
    }

    public static void setDefaultValues(Fluid fluid, Map<String,Float> valuemap)
    {
        for (Map.Entry<String, Float> entry : valuemap.entrySet()) {
            setDefaultValue(fluid,entry.getKey(),entry.getValue());
        }
    }

    public static NBTTagCompound createModifiers(FluidStack stack)
    {
        if(stack.tag == null)
            stack.tag = new NBTTagCompound();
        NBTTagCompound brew_modifiers = stack.tag.getCompoundTag(BREW_MODIFIERS_TAG);
        stack.tag.setTag(BREW_MODIFIERS_TAG,brew_modifiers);
        return brew_modifiers;
    }

    public static NBTTagCompound getModifiers(FluidStack stack)
    {
        return (stack == null || stack.tag == null) ? new NBTTagCompound() : stack.tag.getCompoundTag(BREW_MODIFIERS_TAG);
    }

    public static List<String> getModifierKeys(FluidStack stack) {
        List<String> rList = new ArrayList<>();
        NBTTagCompound compound = getModifiers(stack);
        for (String key : SORTED_MODIFIER_KEYS) {
            FluidModifier modifier = FluidUtil.MODIFIERS.get(key);
            if(modifier.has(compound, stack.getFluid())){
                rList.add(key);
            }
        }
        return rList;
    }

    public static List<PotionEffect> getPrimaryEffects(FluidStack stack) {
        List<PotionEffect> rList = new ArrayList<>();
        NBTTagCompound compound = getModifiers(stack);
        for (String key : SORTED_MODIFIER_KEYS) {
            FluidModifier modifier = FluidUtil.MODIFIERS.get(key);
            if(modifier.type == FluidModifier.EnumType.PRIMARY && modifier.has(compound, stack.getFluid())){
                modifier.providePotionEffects(rList, compound, stack.getFluid());
            }
        }
        return rList;
    }

    public static void garbageCollect(FluidStack stack)
    {
        if(stack.tag == null)
            return;
        NBTTagCompound brew_modifiers = stack.tag.getCompoundTag(BREW_MODIFIERS_TAG);
        if(!brew_modifiers.getKeySet().stream().anyMatch(key -> brew_modifiers.getFloat(key) != 0))
            stack.tag.removeTag(BREW_MODIFIERS_TAG);
        if(stack.tag.getSize() == 0)
            stack.tag = null;
    }

    public static Random getRandom(World world, FluidStack stack, int seedoffset)
    {
        NBTTagCompound brew_modifiers = stack.tag != null ? stack.tag.getCompoundTag(BREW_MODIFIERS_TAG) : new NBTTagCompound();
        long seed = world.getSeed() ^ brew_modifiers.hashCode() + seedoffset;
        return new Random(seed);
    }

    public static FluidModifier.EnumType getType(String name) {
        FluidModifier modifier = MODIFIERS.get(name);
        if(modifier != null)
            return modifier.type;
        else
            return FluidModifier.EnumType.TERTIARY;
    }

    public static FluidModifier.EffectType getEffectType(String name) {
        FluidModifier modifier = MODIFIERS.get(name);
        if(modifier != null)
            return modifier.effectType;
        else
            return FluidModifier.EffectType.NEUTRAL;
    }

    public static EssenceStack modifierToEssence(String name, float amount) {
        FluidModifier modifier = MODIFIERS.get(name);
        if(modifier != null)
            return modifier.toEssence(amount);
        else
            return EssenceStack.EMPTY;
    }

    public static float getDefault(String name) {
        FluidModifier modifier = MODIFIERS.get(name);
        if(modifier != null)
            return modifier.defaultValue;
        else
            return 0;
    }

    public static String getFormatType(String name) {
        FluidModifier modifier = MODIFIERS.get(name);
        if(modifier != null)
            return modifier.formatType;
        else
            return null;
    }

    public static float getModifier(FluidStack stack, String name)
    {
        return getModifier(getModifiers(stack),stack!=null ? stack.getFluid() : null,name);
    }

    public static float getModifier(NBTTagCompound compound, Fluid fluid, String name)
    {
        FluidModifier modifier = MODIFIERS.get(name);
        return modifier != null ? modifier.getOrDefault(compound, fluid) : 0;
    }
}

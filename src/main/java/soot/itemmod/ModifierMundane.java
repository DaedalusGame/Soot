package soot.itemmod;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import soot.Registry;
import soot.util.MiscUtil;
import teamroots.embers.api.itemmod.ItemModUtil;
import teamroots.embers.api.itemmod.ModifierBase;
import teamroots.embers.gui.GuiCodex;

import java.util.*;
import java.util.regex.Pattern;

public class ModifierMundane extends ModifierBase {
    static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    static final UUID ATTACK_BONUS_UUID = UUID.fromString("051b0eb9-ecbd-402e-83d6-e99455da641c");
    public static final String CODE_STAT = MiscUtil.generateFormatMatchCode(114);
    public static final String CODE_NOSTAT = MiscUtil.generateFormatMatchCode(115);
    public static final String GLOW_FORMAT = "!=!";

    public static WeakHashMap<EntityLivingBase,Double> ATTRIBUTE_CACHE = new WeakHashMap<>();

    public ModifierMundane() {
        super(EnumType.TOOL, "mundane", 0.0, false);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canApplyTo(ItemStack stack) {
        return super.canApplyTo(stack) && MiscUtil.getToolMaterial(stack.getItem()) != null;
    }

    @SubscribeEvent
    public void onTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote)
            return;
        ItemStack stack = entity.getHeldItemMainhand();
        IAttributeInstance instance = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        boolean isMundane = ItemModUtil.hasHeat(stack) && ItemModUtil.hasModifier(stack, Registry.MUNDANE);

        if(instance != null) { //This can happen.
            if (isMundane) {
                double mundaneBonus = getMundaneBonus(stack);
                AttributeModifier modifier = instance.getModifier(ATTACK_BONUS_UUID);
                if (modifier != null) {
                    double currentBonus = ATTRIBUTE_CACHE.getOrDefault(entity, modifier.getAmount());
                    if (currentBonus != mundaneBonus)
                        instance.removeModifier(modifier);
                } else {
                    instance.applyModifier(new AttributeModifier(ATTACK_BONUS_UUID, "mundane_bonus", mundaneBonus, 0));
                }
                ATTRIBUTE_CACHE.put(entity, mundaneBonus);
            } else {
                AttributeModifier modifier = instance.getModifier(ATTACK_BONUS_UUID);
                if (modifier != null)
                    instance.removeModifier(modifier);
                ATTRIBUTE_CACHE.remove(entity);
            }
        }
    }

    public double getMundaneBonus(ItemStack stack) {
        Item item = stack.getItem();
        Item.ToolMaterial material = MiscUtil.getToolMaterial(item);
        int level = ItemModUtil.getModifierLevel(stack,Registry.MUNDANE);
        double itemAttack = calculateAttributeTotal(item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()));
        double bonus = 3.0 + material.getAttackDamage() - itemAttack;
        return Math.min(level,Math.abs(bonus)) * Math.signum(bonus);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        EntityPlayer player = event.getEntityPlayer();
        if (player == null)
            return;
        boolean isExpanded = GuiScreen.isShiftKeyDown();

        if (isAttributeStrippable(stack)) {
            List<String> tooltip = event.getToolTip();

            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                Multimap<String, AttributeModifier> slotAttributes = stack.getAttributeModifiers(slot);
                AttributeModifier selectedModifier = getAttackDamageModifier(slotAttributes.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()));

                if (selectedModifier == null)
                    break;

                boolean stat = selectedModifier.getID().equals(ATTACK_DAMAGE_MODIFIER);
                String check = getAttribute(stack, player, SharedMonsterAttributes.ATTACK_DAMAGE.getName(), selectedModifier.getAmount(), stat);

                double mundaneBonus = getMundaneBonus(stack);

                for (int i = 1; i < tooltip.size(); i++)
                    if (tooltip.get(i).equals(check)) {
                        String modifiedAttribute = getModifiedAttribute(stack, player, SharedMonsterAttributes.ATTACK_DAMAGE.getName(), selectedModifier.getAmount(), mundaneBonus, stat, isExpanded);
                        String finalText = "";
                        int e = 0;
                        for(String string : modifiedAttribute.split(Pattern.quote(GLOW_FORMAT))) {
                            finalText += e % 2 == 0 ? string : MiscUtil.generateEmptyString(string);
                            e++;
                        }
                        tooltip.set(i, finalText);
                        break;
                    }

            }

        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTooltipRender(RenderTooltipEvent.PostText event) {
        ItemStack stack = event.getStack();
        EntityPlayer player = Minecraft.getMinecraft().player;
        boolean isExpanded = GuiScreen.isShiftKeyDown();

        List<Integer> formattedLines = new ArrayList<>();
        List<String> text = event.getLines();
        for (int i = 0; i < text.size(); i++) {
            if (text.get(i).endsWith(CODE_STAT) || text.get(i).endsWith(CODE_NOSTAT)) {
                formattedLines.add(i);
            }
        }

        if(formattedLines.size() > 0) {
            FontRenderer fontRenderer = event.getFontRenderer();

            double mundaneBonus = getMundaneBonus(stack);

            GlStateManager.disableDepth();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
            float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
            ListIterator<Integer> iterator = formattedLines.listIterator();
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if(!iterator.hasNext())
                    break;
                Multimap<String, AttributeModifier> slotAttributes = stack.getAttributeModifiers(slot);
                AttributeModifier selectedModifier = getAttackDamageModifier(slotAttributes.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()));
                int index = iterator.next();

                boolean stat = text.get(index).endsWith(CODE_STAT);

                String modifiedAttribute = getModifiedAttribute(stack, player, SharedMonsterAttributes.ATTACK_DAMAGE.getName(), selectedModifier.getAmount(), mundaneBonus, stat, isExpanded);
                int e = 0;
                int xOff = 0;
                for(String string : modifiedAttribute.split(GLOW_FORMAT)) {
                    if(e % 2 == 1)
                        GuiCodex.drawTextGlowingAura(fontRenderer, string, event.getX() + xOff, event.getY() + (fontRenderer.FONT_HEIGHT + 1) * (index) + 2);
                    xOff += fontRenderer.getStringWidth(string);
                    e++;
                }
            }
            GlStateManager.alphaFunc(func, ref);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
        }
    }

    public AttributeModifier getAttackDamageModifier(Collection<AttributeModifier> attributeModifiers) {
        AttributeModifier selectedModifier = null;
        for (AttributeModifier modifier : attributeModifiers) {
            if (modifier.getID().equals(ATTACK_DAMAGE_MODIFIER) || (modifier.getOperation() == 0 && selectedModifier == null))
                selectedModifier = modifier;
        }
        return selectedModifier;
    }

    public String getAttribute(ItemStack stack, EntityPlayer player, String attribute, double amount, boolean isStat) {
        boolean flag = false;

        if (isStat) {
            amount += player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
            amount += (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
            flag = true;
        }

        String check = null;
        String formattedName = I18n.format("attribute.name." + attribute);
        if (flag) {
            check = (" " + I18n.format("attribute.modifier.equals.0", ItemStack.DECIMALFORMAT.format(amount), formattedName));
        } else if (amount > 0.0D) {
            check = (TextFormatting.BLUE + " " + net.minecraft.client.resources.I18n.format("attribute.modifier.plus.0", ItemStack.DECIMALFORMAT.format(amount), formattedName));
        } else if (amount < 0.0D) {
            amount *= -1.0D;
            check = (TextFormatting.RED + " " + net.minecraft.client.resources.I18n.format("attribute.modifier.take.0", ItemStack.DECIMALFORMAT.format(amount), formattedName));
        }
        return check;
    }

    public String getModifiedAttribute(ItemStack stack, EntityPlayer player, String attribute, double amount, double bonus, boolean isStat, boolean isExpanded) {
        boolean flag = false;

        if (isStat) {
            amount += player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
            amount += (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
            flag = true;
        }

        if (!isExpanded)
            amount += bonus;

        String check = "";
        String formattedName = I18n.format("attribute.name." + attribute);
        String formattedAmount;

        if (!isExpanded) {
            formattedAmount = ItemStack.DECIMALFORMAT.format(Math.abs(amount));
        } else {
            formattedAmount = ItemStack.DECIMALFORMAT.format(Math.abs(amount)) + " " + GLOW_FORMAT + String.format("%+.2g", bonus) + GLOW_FORMAT;
        }

        if (flag) {
            check = (" " + I18n.format("attribute.modifier.equals.0", formattedAmount, formattedName));
        } else if (amount > 0.0D) {
            check = (TextFormatting.BLUE + " " + net.minecraft.client.resources.I18n.format("attribute.modifier.plus.0", formattedAmount, formattedName));
        } else if (amount < 0.0D) {
            check = (TextFormatting.RED + " " + net.minecraft.client.resources.I18n.format("attribute.modifier.take.0", formattedAmount, formattedName));
        }

        if (!isExpanded)
            check = GLOW_FORMAT + check + GLOW_FORMAT;
        return check + CODE_STAT;
    }

    private boolean isAttributeStrippable(ItemStack stack) {
        return (!stack.hasTagCompound() || (stack.getTagCompound().getInteger("HideFlags") & 2) == 0) && ItemModUtil.hasHeat(stack) && ItemModUtil.hasModifier(stack, Registry.MUNDANE);
    }

    private static double calculateAttributeTotal(Collection<AttributeModifier> modifiers) { //Endless Trash
        double attribute = 0;
        double multiplier = 0;
        double finalMultiplier = 1;

        for (AttributeModifier modifier : modifiers) {
            switch (modifier.getOperation()) {
                case (0):
                    attribute += modifier.getAmount();
                    break;
                case (1):
                    multiplier += modifier.getAmount();
                    break;
                case (2):
                    finalMultiplier *= modifier.getAmount() + 1;
                    break;
            }
        }

        return (attribute + attribute * multiplier) * finalMultiplier;
    }
}

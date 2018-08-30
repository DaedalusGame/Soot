package soot.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import teamroots.embers.entity.EntityAncientGolem;

import java.util.Random;

public class MundaneStoneHandler {
    static Random random = new Random();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityTick(LivingDropsEvent event)
    {
        EntityLivingBase target = event.getEntityLiving();
        if(target instanceof EntityAncientGolem) {
            if(random.nextInt(1000) <= event.getLootingLevel() * (event.isRecentlyHit() ? 2 : 1)) {
                EntityItem entityitem = new EntityItem(target.world, target.posX, target.posY, target.posZ, new ItemStack(Registry.MUNDANE_STONE));
                entityitem.setDefaultPickupDelay();
                event.getDrops().add(entityitem);
            }
        }
    }
}

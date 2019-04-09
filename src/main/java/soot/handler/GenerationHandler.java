package soot.handler;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Config;
import soot.world.SulfurGenerator;

import java.util.Random;

public class GenerationHandler {
    static SulfurGenerator sulfurGenerator = new SulfurGenerator();

    @SubscribeEvent
    public static void onGenerateOres(OreGenEvent.Post event) {
        Random random = event.getRand();
        World world = event.getWorld();
        if(Config.isSulfurEnabled(world.provider.getDimension())) {
            BlockPos chunkPos = event.getPos();
            int x = random.nextInt(16) + 8;
            int y = Config.SULFUR_MIN_Y + random.nextInt(Config.SULFUR_MAX_Y - Config.SULFUR_MIN_Y + 1);
            int z = random.nextInt(16) + 8;
            BlockPos blockpos2 = chunkPos.add(x, y, z);
            sulfurGenerator.generate(world, random, blockpos2);
        }
    }
}

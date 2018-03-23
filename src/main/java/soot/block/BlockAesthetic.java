package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public class BlockAesthetic extends Block {
    public static final PropertyEnum<BlockAesthetic.EnumType> TYPE = PropertyEnum.create("blocktype", BlockAesthetic.EnumType.class);

    public BlockAesthetic() {
        super(Material.ROCK);
    }

    public enum EnumType implements IStringSerializable {
        COOLBLOCK(0, "blah", MapColor.BROWN_STAINED_HARDENED_CLAY, Material.WOOD, SoundType.WOOD, 1.0f, 5.0f);

        public static final BlockAesthetic.EnumType[] VALUES = values();

        private final int meta;
        private final String name;
        private final MapColor color;
        private final Material material;
        private final SoundType soundType;
        private final float hardness;
        private final float resistance;
        private final int[] rotations;
        private final int[] mirrors;

        EnumType(int meta, String name, MapColor color, Material material, SoundType soundType, float hardness, float resistance)
        {
            this(meta, name, color, material, soundType, hardness, resistance, new int[]{meta,meta,meta,meta}, new int[]{meta,meta});
        }

        EnumType(int meta, String name, MapColor color, Material material, SoundType soundType, float hardness, float resistance, int[] rotations, int[] mirrors) {
            this.meta = meta;
            this.name = name;
            this.color = color;
            this.material = material;
            this.soundType = soundType;
            this.hardness = hardness;
            this.resistance = resistance;
            this.rotations = rotations;
            this.mirrors = mirrors;
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMeta() {
            return meta;
        }

        public MapColor getColor() {
            return color;
        }

        public Material getMaterial() {
            return material;
        }

        public SoundType getSoundType() {
            return soundType;
        }

        public float getHardness() {
            return hardness;
        }

        public float getResistance() {
            return resistance;
        }

        public int[] getRotations() {
            return rotations;
        }

        public int[] getMirrors() {
            return mirrors;
        }
    }
}

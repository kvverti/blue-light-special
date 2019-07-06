package io.github.kvverti.bluelightspecial.feature;

import com.mojang.datafixers.Dynamic;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class TwistleFeature extends Feature<DefaultFeatureConfig> {

    public TwistleFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> f) {
        super(f);
    }

    @Override
    public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> gen, Random rand, BlockPos pos, DefaultFeatureConfig cfg) {
        int y = world.getTop(Heightmap.Type.OCEAN_FLOOR, pos.getX(), pos.getZ());
        BlockPos.Mutable mpos = new BlockPos.Mutable(pos.getX(), y, pos.getZ());
        BlockState plant = BlueLightSpecial.TWISTLE_PLANT.getDefaultState();
        int height = rand.nextInt(7) + 4;
        int i;
        for(i = 0; i < height && world.getFluidState(mpos).getFluid() == Fluids.WATER; i++) {
            world.setBlockState(mpos, plant, 2);
            mpos = mpos.setOffset(Direction.UP);
        }
        if(i != 0) {
            mpos = mpos.setOffset(Direction.DOWN);
            world.setBlockState(mpos, BlueLightSpecial.TWISTLE.getDefaultState(), 2);
            return true;
        }
        return false;
    }
}

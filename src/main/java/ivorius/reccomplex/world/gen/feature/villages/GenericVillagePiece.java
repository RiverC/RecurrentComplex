/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.world.gen.feature.villages;

import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.world.gen.feature.structure.*;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.gentypes.GenerationInfo;
import ivorius.reccomplex.world.gen.feature.structure.generic.gentypes.VanillaGenerationInfo;
import ivorius.reccomplex.utils.NBTStorable;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

/**
 * Created by lukas on 18.01.15.
 */
public class GenericVillagePiece extends StructureVillagePieces.Village
{
    public String structureID;
    public String generationID;

    public boolean mirrorX;
    public boolean startedGeneration;
    public NBTBase instanceData;

    public GenericVillagePiece()
    {
    }

    public GenericVillagePiece(StructureVillagePieces.Start start, int generationDepth)
    {
        super(start, generationDepth);
    }

    public static AxisAlignedTransform2D getTransform(EnumFacing front, boolean mirrorX, EnumFacing toFront)
    {
        return AxisAlignedTransform2D.from(getRotations(front, mirrorX, toFront), mirrorX);
    }

    public static int getRotations(EnumFacing front, boolean mirrorX, EnumFacing toFront)
    {
        Integer rotations = Directions.getHorizontalClockwiseRotations(front, toFront, mirrorX);
        return rotations == null ? 0 : rotations;
    }

    @Nullable
    public static GenericVillagePiece create(String structureID, String generationID)
    {
        return VanillaGenerationClassFactory.instance().create(structureID, generationID);
    }

    @Nullable
    public static GenericVillagePiece create(String structureID, String generationID, StructureVillagePieces.Start start, int generationDepth)
    {
        return VanillaGenerationClassFactory.instance().create(structureID, generationID, start, generationDepth);
    }

    public static boolean canVillageGoDeeperC(StructureBoundingBox box)
    {
        return canVillageGoDeeper(box);
    }

    public void setIds(String structureID, String generationID)
    {
        this.structureID = structureID;
        this.generationID = generationID;
    }

    public void setOrientation(EnumFacing front, boolean mirrorX, StructureBoundingBox boundingBox)
    {
        setCoordBaseMode(front);
        this.mirrorX = mirrorX;
        this.boundingBox = boundingBox;
    }

    @Nonnull
    private Biome biome(WorldServer world) {return startPiece != null ? startPiece.biome : Environment.getBiome(world, boundingBox);}

    @Nonnull
    protected Environment environment(WorldServer world, GenerationInfo generationInfo)
    {
        return new Environment(world, biome(world), structureType, generationInfo);
    }

    public void prepare(Random random, WorldServer world)
    {
        StructureInfo<?> structureInfo = StructureRegistry.INSTANCE.get(structureID);
        if (structureInfo != null)
        {
            GenerationInfo generationInfo = structureInfo.generationInfo(generationID);

            if (generationInfo instanceof VanillaGenerationInfo)
            {
                VanillaGenerationInfo vanillaGenInfo = (VanillaGenerationInfo) generationInfo;
                AxisAlignedTransform2D transform = getTransform(vanillaGenInfo.front, mirrorX, getCoordBaseMode().getOpposite());

                instanceData = new StructureGenerator<>(structureInfo).random(random).environment(environment(world, generationInfo)).transform(transform).boundingBox(boundingBox)
                        .instanceData().map(NBTStorable::writeToNBT).orElse(null);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean addComponentParts(World world, Random random, StructureBoundingBox boundingBox)
    {
        StructureInfo structureInfo = StructureRegistry.INSTANCE.get(structureID);
        if (structureInfo != null)
        {
            GenerationInfo generationInfo = structureInfo.generationInfo(generationID);

            if (generationInfo instanceof VanillaGenerationInfo)
            {
                VanillaGenerationInfo vanillaGenInfo = (VanillaGenerationInfo) generationInfo;
                AxisAlignedTransform2D transform = getTransform(vanillaGenInfo.front, mirrorX, getCoordBaseMode().getOpposite());

                BlockPos structureShift = transform.apply(vanillaGenInfo.spawnShift, new int[]{1, 1, 1});

                if (this.averageGroundLvl < 0)
                {
                    this.averageGroundLvl = this.getAverageGroundLevel(world, boundingBox);

                    if (this.averageGroundLvl < 0)
                        return true;

                    // Structure shift y was included in bounding box, but must be re-added because it is overwritten
                    this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.minY + structureShift.getY(), 0);
                }

                if (world instanceof WorldServer)
                    generate((WorldServer) world, random, boundingBox, structureInfo, generationInfo, transform);

                return true;
            }
        }

        return false;
    }

    protected <T extends NBTStorable> void generate(WorldServer world, Random random, StructureBoundingBox generationBB, StructureInfo<T> structureInfo, GenerationInfo generationInfo, AxisAlignedTransform2D transform)
    {
        if (!startedGeneration)
            prepare(random, world);

        boolean firstTime = !startedGeneration;
        new StructureGenerator<>(structureInfo).environment(environment(world, generationInfo))
                .random(random).lowerCoord(StructureBoundingBoxes.min(boundingBox)).transform(transform).generationBB(StructureBoundingBoxes.wholeHeightBoundingBox(world, generationBB))
                .generationLayer(componentType).structureID(structureID).maturity(firstTime ? StructureSpawnContext.GenerateMaturity.FIRST : StructureSpawnContext.GenerateMaturity.COMPLEMENT)
                .instanceData(this.instanceData).generate();

        startedGeneration = true;
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tagCompound)
    {
        super.writeStructureToNBT(tagCompound);
        tagCompound.setString("RcSId", structureID);
        tagCompound.setString("RcGtId", generationID);
        tagCompound.setBoolean("RcMirror", mirrorX);
        tagCompound.setBoolean("RcStartGen", startedGeneration);
        if (instanceData != null)
            tagCompound.setTag("RcInstDat", instanceData);
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager manager)
    {
        super.readStructureFromNBT(tagCompound, manager);
        structureID = tagCompound.getString("RcSId");
        generationID = tagCompound.getString("RcGtId");
        mirrorX = tagCompound.getBoolean("RcMirror");
        startedGeneration = tagCompound.getBoolean("RcStartGen");
        instanceData = tagCompound.hasKey("RcInstDat") ? tagCompound.getTag("RcInstDat") : null;
    }
}

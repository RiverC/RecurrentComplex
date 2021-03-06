/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.world.gen.feature.structure.*;
import ivorius.reccomplex.utils.NBTStorable;
import ivorius.reccomplex.world.gen.feature.structure.context.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by lukas on 25.05.14.
 */
public abstract class Transformer<S extends NBTStorable>
{
    @Nonnull
    protected String id;

    public Transformer(@Nonnull String id)
    {
        this.id = id;
    }

    public static String randomID(Class<? extends Transformer> type)
    {
        Random random = new Random();
        return String.format("%s_%s", StructureRegistry.TRANSFORMERS.iDForType(type), Integer.toHexString(random.nextInt()));
    }

    public static String randomID(String type)
    {
        Random random = new Random();
        return String.format("%s_%s", type, Integer.toHexString(random.nextInt()));
    }

    @Nonnull
    public String id()
    {
        return id;
    }

    public void setID(@Nonnull String id)
    {
        this.id = id;
    }

    public abstract String getDisplayString();

    public abstract TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate);

    public abstract S prepareInstanceData(StructurePrepareContext context, IvWorldData worldData);

    public void configureInstanceData(S s, StructurePrepareContext context, IvWorldData worldData, RunTransformer transformer)
    {

    }

    public abstract S loadInstanceData(StructureLoadContext context, NBTBase nbt);

    public boolean mayGenerate(S instanceData, StructureSpawnContext context, IvWorldData worldData, RunTransformer transformer)
    {
        return true;
    }

    public boolean skipGeneration(S instanceData, StructureLiveContext context, BlockPos pos, IBlockState state, IvWorldData worldData, BlockPos sourcePos)
    {
        return false;
    }

    public void transform(S instanceData, Phase phase, StructureSpawnContext context, IvWorldData worldData, RunTransformer transformer)
    {

    }

    public enum Phase
    {
        BEFORE,
        AFTER
    }
}

/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.saved;

import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.maze.components.MazeComponent;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.rules.TableDataSourceMazeRuleConnect;
import ivorius.reccomplex.world.gen.script.WorldScriptMazeGenerator;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.*;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.LimitAABBStrategy;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.MazeRule;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.ReachabilityStrategy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by lukas on 21.03.16.
 */
public class MazeRuleConnect extends MazeRule
{
    public final List<SavedMazePath> start = new ArrayList<>();
    public final List<SavedMazePath> end = new ArrayList<>();

    public boolean preventConnection = false;

    protected static Set<MazePassage> buildPaths(List<SavedMazePath> start)
    {
        return start.stream().map(SavedMazePath::build).collect(Collectors.toSet());
    }

    @Override
    public String displayString()
    {
        return String.format("%s %s->%s %s", summarize(start), preventConnection ? TextFormatting.GOLD : TextFormatting.GREEN, TextFormatting.RESET, summarize(end));
    }

    private String summarize(List<SavedMazePath> list)
    {
        return list.size() == 0 ? "?" : String.format("%s%s", list.get(0).getSourceRoom().toString(), list.size() > 1 ? "..." : "");
    }

    @Override
    public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate, List<SavedMazePathConnection> expected, List<IntegerRange> bounds)
    {
        return new TableDataSourceMazeRuleConnect(this, delegate, navigator, bounds);
    }

    @Override
    public ReachabilityStrategy<MazeComponentStructure<Connector>, Connector> build(WorldScriptMazeGenerator script, Set<Connector> blockedConnections, ConnectorFactory connectorFactory, Collection<? extends MazeComponent<Connector>> components)
    {
        if (start.size() > 0 && end.size() > 0)
        {
            List<Collection<MazePassage>> points = Arrays.asList(buildPaths(start), buildPaths(end));
            Predicate<Connector> traverser = ReachabilityStrategy.connectorTraverser(blockedConnections);
            LimitAABBStrategy<MazeComponent<Object>, Object> confiner = new LimitAABBStrategy<>(script.mazeComponent.boundsSize());

            return preventConnection ? ReachabilityStrategy.preventConnection(points, traverser, confiner)
                    :  ReachabilityStrategy.connect(points, traverser, confiner, ReachabilityStrategy.compileAbilities(components, traverser));
        }
        else
            return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        start.clear();
        start.addAll(NBTCompoundObjects.readListFrom(compound, "start", SavedMazePath.class));

        end.clear();
        end.addAll(NBTCompoundObjects.readListFrom(compound, "end", SavedMazePath.class));

        preventConnection = compound.getBoolean("preventConnection");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        NBTCompoundObjects.writeListTo(compound, "start", start);
        NBTCompoundObjects.writeListTo(compound, "end", end);

        compound.setBoolean("preventConnection", preventConnection);
    }
}

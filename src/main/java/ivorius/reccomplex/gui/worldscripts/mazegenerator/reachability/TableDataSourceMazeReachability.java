/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.gui.worldscripts.mazegenerator.reachability;

import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.reccomplex.gui.table.*;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourcePreloaded;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeReachability;
import ivorius.ivtoolkit.tools.IvTranslations;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * Created by lukas on 16.03.16.
 */
public class TableDataSourceMazeReachability extends TableDataSourceSegmented
{
    protected SavedMazeReachability reachability;

    private TableDelegate tableDelegate;
    private TableNavigator tableNavigator;

    public TableDataSourceMazeReachability(SavedMazeReachability reachability, TableDelegate tableDelegate, TableNavigator tableNavigator, Set<SavedMazePath> expected, List<IntegerRange> bounds)
    {
        this.reachability = reachability;
        this.tableDelegate = tableDelegate;
        this.tableNavigator = tableNavigator;

        addManagedSegment(0, new TableDataSourcePreloaded(new TitledCell(
                new TableCellTitle("", IvTranslations.get("reccomplex.reachability.groups")))
                .withTitleTooltip(IvTranslations.formatLines("reccomplex.reachability.groups.tooltip"))));
        addManagedSegment(1, new TableDataSourceMazeReachabilityGroups(reachability.groups, expected, tableDelegate, tableNavigator));

        addManagedSegment(2, new TableDataSourcePreloaded(new TitledCell(
                new TableCellTitle("", IvTranslations.get("reccomplex.reachability.crossconnections")))
                .withTitleTooltip(IvTranslations.formatLines("reccomplex.reachability.crossconnections.tooltip"))));
        addManagedSegment(3, new TableDataSourcePathConnectionList(reachability.crossConnections, tableDelegate, tableNavigator, bounds));
    }

    public SavedMazeReachability getReachability()
    {
        return reachability;
    }

    public void setReachability(SavedMazeReachability reachability)
    {
        this.reachability = reachability;
    }

    public TableDelegate getTableDelegate()
    {
        return tableDelegate;
    }

    public void setTableDelegate(TableDelegate tableDelegate)
    {
        this.tableDelegate = tableDelegate;
    }

    public TableNavigator getTableNavigator()
    {
        return tableNavigator;
    }

    public void setTableNavigator(TableNavigator tableNavigator)
    {
        this.tableNavigator = tableNavigator;
    }

    @Nonnull
    @Override
    public String title()
    {
        return "Reachability";
    }
}

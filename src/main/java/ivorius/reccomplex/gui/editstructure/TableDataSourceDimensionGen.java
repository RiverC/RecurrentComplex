/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.gui.editstructure;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.*;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.DimensionGenerationInfo;

import javax.annotation.Nonnull;

/**
 * Created by lukas on 05.06.14.
 */
public class TableDataSourceDimensionGen extends TableDataSourceSegmented
{
    private DimensionGenerationInfo generationInfo;

    private TableDelegate tableDelegate;

    public TableDataSourceDimensionGen(DimensionGenerationInfo generationInfo, TableDelegate tableDelegate)
    {
        this.generationInfo = generationInfo;
        this.tableDelegate = tableDelegate;
        
        addManagedSegment(0, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.dimensions"), generationInfo.getDimensionMatcher(), null));
    }

    @Nonnull
    @Override
    public String title()
    {
        return "Dimension";
    }

    @Override
    public int numberOfSegments()
    {
        return 2;
    }

    @Override
    public int sizeOfSegment(int segment)
    {
        return segment == 1 ? 1 : super.sizeOfSegment(segment);
    }

    @Override
    public TableCell cellForIndexInSegment(GuiTable table, int index, int segment)
    {
        if (segment == 1)
        {
            return RCGuiTables.defaultWeightElement(val -> generationInfo.setGenerationWeight(TableCells.toDouble(val)), generationInfo.getGenerationWeight());
        }

        return super.cellForIndexInSegment(table, index, segment);
    }
}

/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.structuregen.gui.table;

/**
 * Created by lukas on 03.06.14.
 */
public interface TableElementProperty<P> extends TableElement
{
    P getPropertyValue();

    void setPropertyValue(P value);
}
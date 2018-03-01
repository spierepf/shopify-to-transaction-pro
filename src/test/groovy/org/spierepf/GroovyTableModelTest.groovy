package org.spierepf

import groovy.util.GroovyTestCase

/**
 * Unit test GroovyTableModel.
 */
public class GroovyTableModelTest
    extends GroovyTestCase
{
    public void testGetRowCount()
    {
        assertEquals(1, new GroovyTableModel([["A":""]]).getRowCount());
        assertEquals(2, new GroovyTableModel([["A":""], ["A":""]]).getRowCount());
    }

    public void testGetColumnCount()
    {
        assertEquals(1, new GroovyTableModel([["A":""]]).getColumnCount());
        assertEquals(2, new GroovyTableModel([["A":"", "B":""]]).getColumnCount());
        assertEquals(2, new GroovyTableModel([["A":""], ["B":""]]).getColumnCount());
    }

    public void testGetValueAt()
    {
        assertEquals("a", new GroovyTableModel([["A":"a"]]).getValueAt(0, 0));
        assertEquals("b", new GroovyTableModel([["A":"a", "B":"b"]]).getValueAt(0, 1));
        assertEquals("b", new GroovyTableModel([["A":"a"], ["B":"b"]]).getValueAt(1, 1));
    }

    public void testGetColumnName()
    {
        assertEquals("A", new GroovyTableModel([["A":""]]).getColumnName(0));
        assertEquals("B", new GroovyTableModel([["A":"", "B":""]]).getColumnName(1));
        assertEquals("B", new GroovyTableModel([["A":""], ["B":""]]).getColumnName(1));
    }
}

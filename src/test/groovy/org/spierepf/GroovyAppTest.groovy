package org.spierepf

import groovy.util.GroovyTestCase

import java.io.StringReader
import com.opencsv.CSVReader
import java.io.StringWriter
import com.opencsv.CSVWriter

/**
 * Unit test for simple App.
 */
public class GroovyAppTest 
    extends GroovyTestCase
{
    public void testExtract()
    {
        CSVReader reader = new CSVReader(new StringReader('Name,ID\n"Spierenburg, Peter-Frank",12345\n'))
        def records = App.extract(reader)
        assertEquals('Spierenburg, Peter-Frank', records[0]['Name'])
        assertEquals('12345', records[0]['ID'])
    }

    public void testLoad()
    {
        def records = [[Name: 'Spierenburg, Peter-Frank', ID: 12345]]

        def stringWriter = new StringWriter()
        def writer = new CSVWriter(stringWriter)
        App.load(writer, records)

        assertEquals('"Name","ID"\n"Spierenburg, Peter-Frank","12345"\n', stringWriter.getBuffer().toString())
    }

    public void testTransform()
    {
        def records = [[Price: "1000.00"]]

        App.transform(records, 'inRecord["Commission"] = String.format("%.2f", inRecord["Price"].toFloat() * 0.60)\noutRecords << inRecord')

        assertEquals("1000.00", records[0]["Price"])
        assertEquals("600.00", records[0]["Commission"])
    }
}

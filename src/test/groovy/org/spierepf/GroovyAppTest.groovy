package org.spierepf

import groovy.util.GroovyTestCase

import java.io.StringReader
import com.opencsv.CSVReader

/**
 * Unit test for simple App.
 */
public class GroovyAppTest 
    extends GroovyTestCase
{
    def extract(def reader)
    {
        def records = []

        def header = reader.readNext()

        def line = reader.readNext()
        while(line != null) {
            def record = [:]
            header.eachWithIndex { value, index -> record[value] = line[index] }
            records << record
            line = reader.readNext()
        }

        return records
    }

    public void testExtract()
    {
        CSVReader reader = new CSVReader(new StringReader('Name,ID\n"Spierenburg, Peter-Frank",12345\n'))
        def records = extract(reader)
        assertEquals('Spierenburg, Peter-Frank', records[0]['Name'])
        assertEquals('12345', records[0]['ID'])
    }
}

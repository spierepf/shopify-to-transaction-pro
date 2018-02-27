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

    def load(def writer, def records)
    {
        def csvWriter = new CSVWriter(writer)

        Set headerSet = []
        records.forEach { headerSet.addAll(it.keySet()) }
        String[] header = new String[headerSet.size()]
        headerSet.toArray(header)
        csvWriter.writeNext(header)

        String[] line = new String[headerSet.size()]
        records.forEach { record ->
            for(int i = 0; i < header.size(); ++i) {
                line[i] = record[header[i]]
            }
            csvWriter.writeNext(line)
        }
    }

    public void testExtract()
    {
        CSVReader reader = new CSVReader(new StringReader('Name,ID\n"Spierenburg, Peter-Frank",12345\n'))
        def records = extract(reader)
        assertEquals('Spierenburg, Peter-Frank', records[0]['Name'])
        assertEquals('12345', records[0]['ID'])
    }

    public void testLoad()
    {
        def records = [[Name: 'Spierenburg, Peter-Frank', ID: 12345]]

        def stringWriter = new StringWriter()
        def writer = load(stringWriter, records)

        assertEquals('"Name","ID"\n"Spierenburg, Peter-Frank","12345"\n', stringWriter.getBuffer().toString())
    }
}

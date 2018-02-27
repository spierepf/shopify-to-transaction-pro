package org.spierepf

import groovy.swing.*
import javax.swing.*
import java.awt.*

static def extract(def reader)
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

static def load(def writer, def records)
{
    Set headerSet = []
    records.forEach { headerSet.addAll(it.keySet()) }
    String[] header = new String[headerSet.size()]
    headerSet.toArray(header)
    writer.writeNext(header)

    String[] line = new String[headerSet.size()]
    records.forEach { record ->
        for(int i = 0; i < header.size(); ++i) {
            line[i] = record[header[i]]
        }
        writer.writeNext(line)
    }
}

static def transform(def records, def script)
{
    def binding = new Binding()
    def shell = new GroovyShell(binding)

    records.each { record ->
        binding.setProperty('record', record)
        shell.evaluate(script)
    }

    return records
}

new SwingBuilder().edt {
    lookAndFeel 'nimbus'  // Simple change in look and feel.
    frame(title: 'App', pack: true, show: true, locationRelativeTo: null, defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) {
        borderLayout(vgap: 5)
         
        panel(constraints: BorderLayout.CENTER, border: emptyBorder(10)) {
            tableLayout {
                tr {
                    td {
                        label 'Source File:'
                    }
                    td {
                        sourceFileNameTextField = textField id: 'sourceFileName', columns: 30
                    }
                    td {
                        button 'Choose...', actionPerformed: {
                            def initialPath = System.getProperty("user.dir")
                            JFileChooser fc = new JFileChooser(initialPath)
                            fc.setFileSelectionMode(JFileChooser.FILES_ONLY)
                            int result = fc.showOpenDialog( null )
                            switch ( result )
                            {
                            case JFileChooser.APPROVE_OPTION:
                                sourceFileNameTextField.text = fc.getSelectedFile().getAbsolutePath()
                                break
                            case JFileChooser.CANCEL_OPTION:
                            case JFileChooser.ERROR_OPTION:
                                break
                            }
                        }
                    }
                }
                tr {
                    td {
                        label 'Destination File:'
                    }
                    td {
                        destinationFileNameTextField = textField id: 'destinationFileName', columns: 30
                    }
                    td {
                        button 'Choose...', actionPerformed: {
                            def initialPath = System.getProperty("user.dir")
                            JFileChooser fc = new JFileChooser(initialPath)
                            fc.setFileSelectionMode(JFileChooser.FILES_ONLY)
                            int result = fc.showOpenDialog( null )
                            switch ( result )
                            {
                            case JFileChooser.APPROVE_OPTION:
                                destinationFileNameTextField.text = fc.getSelectedFile().getAbsolutePath()
                                break
                            case JFileChooser.CANCEL_OPTION:
                            case JFileChooser.ERROR_OPTION:
                                break
                            }
                        }
                    }
                }
            }
             
        }
         
        panel(constraints: BorderLayout.SOUTH) {
            button text: 'OK', actionPerformed: {
            }
        }
    } 
}

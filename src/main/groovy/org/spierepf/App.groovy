package org.spierepf

import groovy.swing.*
import javax.swing.*
import java.awt.*
import com.opencsv.*
import javax.swing.filechooser.*

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

static def transform(def inRecords, def script)
{
    def outRecords = [:]
    def binding = new Binding()
    def shell = new GroovyShell(binding)
    binding.setProperty('outRecords', outRecords)

    inRecords.each { inRecord ->
        binding.setProperty('inRecord', inRecord)
        shell.evaluate(script)
    }

    return outRecords
}

def updateOkButton() {
    okButton.enabled = "" != sourceFileNameTextField.text && "" != destinationFileNameTextField.text
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
                            fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"))
                            int result = fc.showOpenDialog( null )
                            switch ( result )
                            {
                            case JFileChooser.APPROVE_OPTION:
                                def sourceFile = fc.getSelectedFile()
                                sourceFileNameTextField.text = sourceFile.getAbsolutePath()

                                def suggestedDestinationFileName = new StringBuffer(sourceFile.getName())
                                suggestedDestinationFileName.insert(suggestedDestinationFileName.lastIndexOf('.'), '.' + "out")
                                def suggestedDestinationFile = new File(sourceFile.getParentFile(), suggestedDestinationFileName.toString())
                                if("" == destinationFileNameTextField.text)
                                {
                                    destinationFileNameTextField.text = suggestedDestinationFile.getAbsolutePath()
                                }
                                break
                            case JFileChooser.CANCEL_OPTION:
                            case JFileChooser.ERROR_OPTION:
                                break
                            }
                            updateOkButton()
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
                            fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"))
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
                            updateOkButton()
                        }
                    }
                }
            }
        }
         
        panel(constraints: BorderLayout.SOUTH) {
            okButton = button text: 'OK', enabled: false, actionPerformed: {
                def reader = new CSVReader(new FileReader(sourceFileNameTextField.text))
                def records = extract(reader)
                reader.close()

                transform(records, 'inRecord["Commission"] = String.format("%.2f", inRecord["Price"].toFloat() * 0.60)\noutRecords << inRecord')

                def writer = new CSVWriter(new FileWriter(destinationFileNameTextField.text))
                load(writer, records)
                writer.flush()
                writer.close()
                System.exit(0)
            }
        }
    } 
}

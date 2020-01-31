package merge;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class XML_Parser {

    /**
     * Append records in xml input file into an output file.
     *
     * @param input_path  Input file's path.
     * @param output_path Output file's path.
     */
    public static void XML_Combine(String input_path, String output_path, String[] header) {

        try {
            // Create buffered writer to write the output file. Always overwrite.
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(output_path), StandardOpenOption.APPEND);
            // Create printer for writing new records in output file. This time we don't provide header because it is
            // already in the file.
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            // Read xml file from input file.
            File fXmlFile = new File(input_path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();

            // Parse red xml file and normalize.
            Document doc = documentBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            // Get all elements in the xml file with tag name report and insert into a list of nodes.
            NodeList nList = doc.getElementsByTagName("report");

            // Process each report.
            for (int rep = 0; rep < nList.getLength(); rep++) {
                // Transform report into node.
                Node node = nList.item(rep);

                // Test that node is an element and transform into element.
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Create array to store records.
                    ArrayList<String> record = new ArrayList<>();

                    // If packet-serviced is 0, ignore.
                    if(Integer.parseInt(element.getElementsByTagName("packets-serviced").item(0).getTextContent()) != 0){
                        // For each column in the input file, add it to record. Using header to preserve order from first
                        // input file (csv).
                        for (String s : header) {
                            // Add value to record.
                            record.add(element.getElementsByTagName(s).item(0).getTextContent());
                        }
                        // Print record in output file and flush.
                        csvPrinter.printRecord(record);
                        csvPrinter.flush();
                    }
                }
            }
            // Close printer.
            csvPrinter.close();
        } catch (ParserConfigurationException | SAXException e) {
            System.err.println("Error parsing XML file.");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Error reading or writing JSON or CSV file.");
            System.exit(-1);
        }
    }
}
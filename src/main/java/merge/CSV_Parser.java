package merge;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class CSV_Parser {

    /**
     * Append records in csv input file into an output file.
     * @param input_path Input file's path.
     * @param output_path Output file's path.
     */
    public static String[] CSV_Combine(String input_path, String output_path){
        try {
            // Create buffered writer to write the output file. Always overwrite.
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(output_path));

            // Create reader to read the input file.
            Reader reader = Files.newBufferedReader(Paths.get(input_path));
            // Create parser for input file. First record is the header.
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            // Get headers from input file.
            String[] header = Arrays.copyOf(csvParser.getHeaderNames().toArray(), csvParser.getHeaderNames().toArray()
                    .length, String[].class);

            // Create printer for writing new records in output file using header. Provide header from input file.
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(header));

            // Get each record in the input file.
            for (CSVRecord csvRecord : csvParser) {
                // If packet-serviced is 0, ignore.
                if(Integer.parseInt(csvRecord.get("packets-serviced")) != 0) {
                    // Print record into file and flush printer.
                    csvPrinter.printRecord(csvRecord.toMap().values());
                    csvPrinter.flush();
                }
            }
            // Close printer.
            csvPrinter.close();

            // Return header with order of columns.
            return header;
        } catch (IOException e) {
            System.err.println("Error reading or writing CSV file.");
            System.exit(-1);
            return null;
        }
    }
}

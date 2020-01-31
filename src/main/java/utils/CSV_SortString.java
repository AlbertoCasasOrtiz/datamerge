package utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CSV_SortString {

    /**
     * Sort the document by a column in ascending or descending order.
     */
    public static void CSV_sort(String path, String column, String[] header, boolean asc){
        try {
            // Create reader to read the input file.
            Reader reader = Files.newBufferedReader(Paths.get(path));
            // Create iterable using reader, so we can iterate over records.
            Iterable<CSVRecord> csvRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            // Create list containing all records.
            List<CSVRecord> csvRecordsList = new ArrayList<>();
            csvRecords.iterator().forEachRemaining(csvRecordsList::add);

            // Create comparator to compare elements, this will allow to sort in ascending or descending order.
            Comparator<CSVRecord> comparator;
            if(asc){
                comparator = (op1, op2) -> -op2.get(column).compareTo(op1.get(column));
            } else {
                comparator = (op1, op2) -> op2.get(column).compareTo(op1.get(column));
            }

            // Sort list using comparator.
            csvRecordsList.sort(comparator);

            // Create buffered writer to write the output file. Always overwrite.
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
            // Create printer for writing new records in output file. Provide header from input file.
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(header));

            // Print all record into file and then flush and close printer.
            csvPrinter.printRecords(csvRecordsList);
            csvPrinter.flush();
            csvPrinter.close();

        } catch (IOException e) {
            System.err.println("Error reading or writing CSV file.");
            System.exit(-1);
        }
    }
}

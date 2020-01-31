package recordsperserviceguid;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CSV_RecordsPerServiceGuid {

    /**
     * Get records from a CSV file and return a TreeMap containing the number of records (value) per each
     * value in the requested column.
     * @param path Path of the CSV file.
     * @param column Column to quantify.
     * @return TreeMap containing the number of records (value) per each value in the requested column.
     */
    public static TreeMap<String, Integer> getRecords(String path, String column){
        try {
            // Create reader to read the input file.
            Reader reader = Files.newBufferedReader(Paths.get(path));
            // Create iterable using reader, so we can iterate over records.
            Iterable<CSVRecord> csvRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            // Create list containing all records.
            List<CSVRecord> csvRecordsList = new ArrayList<>();
            csvRecords.iterator().forEachRemaining(csvRecordsList::add);

            // Create treeMap to store all "service-guid" (non repeated) as key, and number of reports per each guids as value.
            TreeMap<String, Integer> serviceGuids = new TreeMap<>();

            // Process each report.
            for (CSVRecord strings : csvRecordsList) {
                // If the "service-guid" has been already inserted in the treeMap, update it value.
                if (serviceGuids.containsKey(strings.get(column))) {
                    int prevValue = serviceGuids.get(strings.get(column));
                    serviceGuids.put(strings.get(column), prevValue + 1);
                }
                // If the "service-guid" has never been in the treeMap, insert with value 1.
                else {
                    serviceGuids.put(strings.get(column), 1);
                }
            }

            // Return treeMap containing all reports per "service-guid".
            return serviceGuids;

        } catch (IOException e) {
            System.err.println("Error reading or writing JSON or CSV file.");
            System.exit(-1);
            return null;
        }
    }


    /**
     * Creates a CSV with the number of records per each value of a given column included in the treemap.
     * @param reportsPerServiceGuid TreeMap containing number of records per each value in the given column.
     * @param path Path of the output file.
     */
    public static void printReportsPerServiceGuid(TreeMap<String, Integer> reportsPerServiceGuid, String column, String path){
        try {
            // Create buffered writter to write the output file. Append mode.
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
            // Create printer for writing new records in output file. This time we don't provide header because it is
            // already in the file.
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(column, "number-of-reports"));

            for (String key: reportsPerServiceGuid.keySet()) {
                // Print record in output file and flush.
                csvPrinter.printRecord(key, reportsPerServiceGuid.get(key));
                csvPrinter.flush();
            }
            // Close printer.
            csvPrinter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

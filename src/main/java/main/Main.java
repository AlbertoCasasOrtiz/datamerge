package main;

import merge.CSV_Parser;
import merge.JSON_Parser;
import merge.XML_Parser;
import recordsperserviceguid.CSV_RecordsPerServiceGuid;
import utils.CSV_SortString;

import java.io.File;
import java.util.TreeMap;

public class Main {
    private static final String CSV_FILE_PATH = "./reports.csv";
    private static final String JSON_FILE_PATH = "./reports.json";
    private static final String XML_FILE_PATH = "./reports.xml";

    private static final String OUTPUT_PATH = "./output";
    private static final String CSV_OUTPUT_FILE_PATH = "./output/merged-and-ordered.csv";
    private static final String CSV_SERVICE_GUIDS_OUTPUT_FILE_PATH = "./output/service-guids.csv";

    public static void main(String[] args){
        boolean output_exists = false, output_created = false;
        // If output directory does not exist, create it.
        File output_directory = new File(OUTPUT_PATH);
        // If file does not exists, create it.
        if (!output_directory.exists()){
            output_created = output_directory.mkdir();
        } else {
            output_exists = true;
        }
        // If file has been created, or already existed, proceed.
        if(output_created || output_exists) {
            // Read CSV file, filter "packets-serviced" with value 0, and insert in output. Return header
            // so we have the order of the columns for next files.
            String[] header = CSV_Parser.CSV_Combine(CSV_FILE_PATH, CSV_OUTPUT_FILE_PATH);
            // Read JSON file, filter "packets-serviced" with value 0, and insert in output. Transform dates
            // from milliseconds to ADT Timezone.
            JSON_Parser.JSON_Combine(JSON_FILE_PATH, CSV_OUTPUT_FILE_PATH, header);
            // Read XML file, filter "packets-serviced" with value 9, and insert in output.
            XML_Parser.XML_Combine(XML_FILE_PATH, CSV_OUTPUT_FILE_PATH, header);
            // Order output file by "request-time" in ascending order.
            CSV_SortString.CSV_sort(CSV_OUTPUT_FILE_PATH, "request-time", header, true);
            // Get a map containing how many records are in the output file per each "service-guid".
            TreeMap<String, Integer> reportsPerServiceGuid = CSV_RecordsPerServiceGuid.getRecords(CSV_OUTPUT_FILE_PATH, "service-guid");
            // Print a csv file containing how many reports are per each service guid.
            CSV_RecordsPerServiceGuid.printReportsPerServiceGuid(reportsPerServiceGuid, "service-guid", CSV_SERVICE_GUIDS_OUTPUT_FILE_PATH);
        }
    }

}

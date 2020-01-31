package merge;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class JSON_Parser {

    /**
     * Append records in json input file into an output file.
     * @param input_path Input file's path.
     * @param output_path Output file's path.
     */
    public static void JSON_Combine(String input_path, String output_path, String[] header) {


        try {
            // Create buffered writter to write the output file. Append mode.
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(output_path), StandardOpenOption.APPEND);
            // Create printer for writing new records in output file. This time we don't provide header because it is
            // already in the file.
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            // Create reader to read the input file.
            Reader reader = Files.newBufferedReader(Paths.get(input_path));

            //JSON parser object to parse input file.
            JSONParser jsonParser = new JSONParser();
            //Read JSON input file.
            Object obj = jsonParser.parse(reader);

            // Get JSON Array containing all reports.
            JSONArray reports = (JSONArray) obj;

            // Process each report.
            for (Object rep : reports) {
                // Turn each report into a JSON Object.
                JSONObject report = (JSONObject) rep;

                // Create a list of string with report elements. This will be a record for the output file.
                ArrayList<String> record = new ArrayList<>();

                // If packet-serviced is 0, ignore.
                if (((Long) report.get("packets-serviced")).intValue() != 0) {
                    // For each column in the input file, add it to record. Using header to preserve order from first
                    // input file (csv).
                    for (String s : header) {
                        // If the column is request time, convert time in ms to time in ADT Timezone formatted.
                        if(s.compareTo("request-time") == 0) {
                            // Get time in ms as Long.
                            Long timeSTamp = (Long) report.get(s);
                            // Convert time in ms into time in ADT format.
                            String dateFormatted = JSON_Parser.convertMsIntoADT(timeSTamp);
                            // Add value to record.
                            record.add(dateFormatted);
                        } else {
                            // Add value to record.
                            record.add(report.get(s).toString());
                        }
                    }
                    // Print record in output file and flush.
                    csvPrinter.printRecord(record);
                    csvPrinter.flush();
                }
            }
            // Close printer.
            csvPrinter.close();
        } catch (IOException e) {
            System.err.println("Error reading or writing JSON or CSV file.");
            System.exit(-1);
        } catch (ParseException e){
            System.err.println("Error parsing JSON file.");
            System.exit(-1);
        }
    }

    /**
     * Converts a date in milliseconds into a date in ADT timezone formatted.
     * @param ms Date in ms.
     * @return String containing date in format "yyyy-MM-dd HH:mm:ss ADT"
     */
    private static String convertMsIntoADT(Long ms){
        // Create date with time in ms.
        Date date = new Date(ms);
        // Format date as yyyy-MM-dd HH:mm:ss . An space at the end to ad "ADT".
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        // Set "ADT" timezone using GMT-3:00.
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
        // Format date and add "ADT". Then add date to record.
        String dateFormatted = formatter.format(date);
        dateFormatted += "ADT";
        return dateFormatted;
    }
}

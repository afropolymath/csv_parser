package com.afropolymath.goeurocsvconverter;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author chidieberennadi
 */
public class GoEuroCSVConverter {
    public static final String DEFAULT_OUTPUT_FILE = "output.csv";
    
    public static void main(String[] args) {
        try {
            // Command line city parameter
            String city = args[0];
            
            System.out.printf("Getting suggestions for %s...\n", city);
            GoEuroCSVConverter conv = new GoEuroCSVConverter();
            ArrayList<String[]> suggestionsList = conv._getSuggestions(city);
            if(suggestionsList != null) {
                conv.writeCSVFile(suggestionsList);
            }
        } catch (UnirestException ex) {
            System.err.println("There was an error trying to access the API");
            Logger.getLogger(GoEuroCSVConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("The city parameter is missing");
            Logger.getLogger(GoEuroCSVConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Writes suggestions to CSV file.
     * The suggestionsList parameter should be a list of String arrays. Each
     * string array in the list represents a single line in the CSV file.
     *
     * @param   suggestionsList a string representing the City being searched for
     * @return                  a boolean indicating whether the operation was successful or not
     */
    public boolean writeCSVFile(ArrayList<String[]> suggestionsList) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DEFAULT_OUTPUT_FILE))) {
            Iterator<String[]> it = suggestionsList.iterator();
            while(it.hasNext()) {
                writer.writeNext(it.next());
            }
            System.out.printf("Done writing to CSV file %s \n", DEFAULT_OUTPUT_FILE);
        } catch (IOException ex) {
            System.err.println("There was an error trying to access the File system");
            Logger.getLogger(GoEuroCSVConverter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    /**
     * Returns an ArrayList with a list of suggestions for the specified city. 
     * The city argument should specify the city for which suggestions are
     * required.
     *
     * @param   city    a string representing the City being searched for
     * @return          an ArrayList of suggestions for the city specified
     * @throws          com.mashape.unirest.http.exceptions.UnirestException
     */
    public ArrayList<String[]> _getSuggestions(String city) throws UnirestException {
        String baseApiUrl = "http://api.goeuro.com/api/v2/position/suggest/en/{city}";
        String[] headers = {"_id", "name", "type", "latitude", "longitude"};
        
        // Arraylist within which to store the CSV lines
        ArrayList<String []> lines = new ArrayList();
        
        // Add in CSV the header
        lines.add(headers);
        
        // Run API Query
        HttpResponse<JsonNode> jsonResponse = Unirest.get(baseApiUrl)
                .routeParam("city", city)
                .asJson();
        // Parse the response body as JsonNode
        JsonNode body = jsonResponse.getBody();
        
        JSONArray suggestions = body.getArray();
        
        // Return if the array is empty
        if(suggestions.length() == 0) {
            System.out.println("Nothing was returned from the API");
            return null;
        }
        
        // Pasrse lines of the response
        for(int idx = 0; idx < suggestions.length(); idx++) {
            ArrayList<String> line = new ArrayList();
            JSONObject node = suggestions.getJSONObject(idx);
            line.add(String.valueOf(node.getInt("_id")));
            line.add(node.getString("name"));
            line.add(node.getString("type"));
            JSONObject location = node.getJSONObject("geo_position");
            line.add(String.valueOf(location.getDouble("latitude")));
            line.add(String.valueOf(location.getDouble("longitude")));
            lines.add(line.toArray(new String[line.size()]));
        }
        return lines;    
    }
}

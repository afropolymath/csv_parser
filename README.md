# API CSV Parser
Sample Code for a Java API-CSV Parser. The snippet consumes GoEuro's Location API and outputs the data in a CSV file called _output.csv_.

## Building the application
To build the application you can simply open up the project with Netbeans and run "Clean and Build Project" to build a single JAR file.

## Running the current build
The current build is located at `target/GoEuroCSVConverter-1.0-SNAPSHOT-jar-with-dependencies.jar`. You can run the program using the following command

```
java -jar GoEuroCSVConverter-1.0-SNAPSHOT-jar-with-dependencies.jar {city}
```

The {city} parameter specified can be any city within the scope of the Location API. The program with report errors on the command line if they're encountered.

## Using as a library
To use the codebase as a library, simply import the `GoEuroCSVConverter` into your project. To use the libary functionality, you need the following functions available in the `GoEuroCSVConverter` class:
- `public ArrayList<String[]> _getSuggestions(String city) throws UnirestException` - Call this function to get all the suggestions for a city as an arraylist of string arrays. Each element in the list is a single result row. The first element of the list is the header.
- `public boolean writeCSVFile(ArrayList<String[]> suggestionsList)` - Call this function to write the suggestion list returned from the previous function into a CSV file.

> When using the class in library mode, you can override the default output file by extending the class and overriding the value of `outputFile` in the subclass.

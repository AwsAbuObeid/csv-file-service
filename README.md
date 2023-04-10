# CSV File Service

CSV File Service is a Java library that provides functionality for reading and manipulating CSV files. It offers methods to retrieve, append, search, and delete records in CSV files. The library is implemented in Java and provides an interface, CSVFileService, which is implemented by the DefaultFileService class.

## Features

- Retrieve all records from a CSV file
- Append a single record or a list of records to a CSV file
- Search for records in a CSV file based on key-value pairs or a map of search criteria
- Delete rows from a CSV file based on search criteria

## Installation

To use CSV File Service in your Java project, you can include the following Maven dependency in your pom.xml file:

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>csv-file-service</artifactId>
  <version>1.0.0</version>
</dependency>
```
Alternatively, you can download the JAR file from the CSV File Service GitHub repository and add it to your project's classpath.

Usage
To use CSV File Service in your Java code, you need to create an instance of the CSVFileService interface. Here's an example:

```java
import com.erabia.service.CSVFileService;
import com.erabia.service.exception.CSVException;
import com.erabia.service.impl.DefaultFileService;

public class MyCsvFileProcessor {

  public static void main(String[] args) {
    CSVFileService csvFileService = DefaultFileService.getInstance();

    // Retrieve all records from a CSV file
      List<List<String>> records = csvFileService.getAll("path/to/myfile.csv", ",", true);
    // Append a record to a CSV file
      List<String> record = Arrays.asList("John", "Doe", "30");
      csvFileService.put("path/to/myfile.csv", ",", record);
    // Search for records in a CSV file
      List<List<String>> foundRecords = csvFileService.search("path/to/myfile.csv", ",", true, 0, "John");
    // Delete rows from a CSV file
      Map<Integer, List<String>> searchMap = new HashMap<>();
      searchMap.put(0, Arrays.asList("John", "Jane"));
      csvFileService.delete("path/to/myfile.csv", ",", searchMap);
  }
}
```
# API Documentation

## CSVFileService Interface

The CSVFileService interface provides the following methods:

### `List<List<String>> getAll(String filePath, String delimiter, boolean includeHead) throws CSVException`

Retrieves all records from the CSV file.

#### Parameters
- `filePath`: String - The file path of the CSV file to be read.
- `delimiter`: String - The delimiter used in the CSV file to separate fields.
- `includeHead`: boolean - Whether to include the header row in the returned list.

#### Throws
- `CSVException`: If any error occurs while reading the CSV file.

### `void put(String filePath, String delimiter, List<String> record) throws CSVException`

Appends a single record to the end of the CSV file.

#### Parameters
- `filePath`: String - The file path of the CSV file to be written.
- `delimiter`: String - The delimiter used in the CSV file to separate fields.
- `record`: List<String> - The record to be appended to the CSV file.

#### Throws
- `CSVException`: If any error occurs while writing the record to the CSV file.

### `void putAll(String filePath, String delimiter, List<List<String>> records) throws CSVException`

Appends a list of records to the end of the CSV file.

#### Parameters
- `filePath`: String - The file path of the CSV file to be written.
- `delimiter`: String - The delimiter used in the CSV file to separate fields.
- `records`: List<List<String>> - The list of records to be appended to the CSV file.

#### Throws
- `CSVException`: If any error occurs while writing the records to the CSV file.

### `List<List<String>> search(String filePath, String delimiter, boolean includeHead, int index, String key) throws CSVException`

Searches the CSV file for records that match a given key at a specified index.

#### Parameters
- `filePath`: String - The path to the CSV file to search.
- `delimiter`: String - The delimiter used in the CSV file.
- `includeHead`: boolean - Whether to include the header row in the returned records.
- `index`: int - The index of the column to search for the key in.
- `key`: String - The key to search for in the CSV file.

#### Returns
- `List<List<String>>`: A list of lists, each inner list representing a row in the CSV file that matches the search criteria.

#### Throws
- `CSVException`: If any error occurs while searching the CSV file.

### `List<List<String>> search(String filePath, String delimiter, boolean includeHead, int index, List<String> keys) throws CSVException`

Searches a CSV file for rows that match a set of search criteria.

#### Parameters
- `filePath`: String - The path of the CSV file to search.
- `delimiter`: String - The delimiter used in the CSV file.
- `includeHead`: boolean - Whether to include the header row in the returned records.
- `index`: int - The index of the column to search in.
- `keys`: List<String> - The list of values to search for in the specified column.

#### Returns
- `List<List<String>>`: A list of all rows in the file that match the search criteria.

#### Throws
- `CSVException`: If any error occurs while searching the CSV file.

### `List<List<String>> search(String filePath, String delimiter, boolean includeHead, Map<Integer,List<String>> searchMap) throws CSVException`

Searches a CSV file for rows that match a set of search criteria.

#### Parameters
- `filePath`: String - The path of the CSV file to search.
- `delimiter`: String - The delimiter used in the CSV file.
- `includeHead`: boolean - Whether to include the header row in the returned records.
- `searchMap`: Map<Integer,List<String>> - A map containing the search criteria, where the key is the index of the column to search in and the value is a list of values to search for in that column.

#### Returns
- `List<List<String>>`: A list of all rows in the file that match the search criteria.

#### Throws
- `CSVException`: If any error occurs while searching the CSV file.

### `void delete(String filePath, String delimiter, Map<Integer,List<String>> searchMap) throws CSVException`

Deletes rows from a CSV file that match a set of search criteria.

#### Parameters
- `filePath`: String - The path of the CSV file to delete rows from.
- `delimiter`: String - The delimiter used in the CSV file.
- `searchMap`: Map<Integer,List<String>> - A map containing the search criteria, where the key is the index of the column to search in and the value is a list of values to search for in that column.

#### Throws
- `CSVException`: If any error occurs while deleting rows from the CSV file.
package com.erabia.service;

import java.util.List;
import java.util.Map;

import com.erabia.service.exception.CSVException;

public interface CSVFileService {
	/**

	 Retrieves all records from the CSV file.
	 @param filePath the path to the CSV file to retrieve records from
	 @param delimiter the delimiter used in the CSV file
	 @param includeHead whether to include the header row in the returned records
	 @return a list of lists, each inner list representing a row in the CSV file
	 */
	List<List<String>> getAll(String filePath,String delimiter,boolean includeHead) throws CSVException;
	/**

	 Appends a single record to the end of the CSV file.
	 @param filePath the path to the CSV file to append the record to
	 @param delimiter the delimiter used in the CSV file
	 @param record the record to append to the CSV file
	 */
	void put(String filePath,String delimiter,List<String> record) throws CSVException;
	/**

	 Appends a list of records to the end of the CSV file.
	 @param filePath the path to the CSV file to append the records to
	 @param delimiter the delimiter used in the CSV file
	 @param records the records to append to the CSV file
	 */
	void putAll(String filePath,String delimiter,List<List<String>> records) throws CSVException;
	/**

	 Searches the CSV file for records that match a given key at a specified index.
	 @param filePath the path to the CSV file to search
	 @param delimiter the delimiter used in the CSV file
	 @param includeHead whether to include the header row in the returned records
	 @param index the index of the column to search for the key in
	 @param key the key to search for in the CSV file
	 @return a list of lists, each inner list representing a row in the CSV file that matches the search criteria
	 */
	List<List<String>> search(String filePath,String delimiter,boolean includeHead,int index,String key) throws CSVException;
	/**
	 * Searches a CSV file for rows that match a set of search criteria.
	 *
	 * @param filePath the path of the CSV file to search
	 * @param delimiter the delimiter used in the CSV file
	 * @param includeHead whether to include the first row of the file as a header or not
	 * @param index the index of the column to search in
	 * @param keys the list of values to search for in the specified column
	 * @return a list of all rows in the file that match the search criteria
	 */
	List<List<String>> search(String filePath,String delimiter,boolean includeHead,int index,List<String> keys) throws CSVException;
	/**
	 * Searches a CSV file for rows that match a set of search criteria.
	 *
	 * @param filePath the path of the CSV file to search
	 * @param delimiter the delimiter used in the CSV file
	 * @param includeHead whether to include the first row of the file as a header or not
	 * @param searchMap a map containing the search criteria, where the key is the index of the column to search in and the value is a list of values to search for in that column
	 * @return a list of all rows in the file that match the search criteria
	 */
	List<List<String>> search(String filePath,String delimiter,boolean includeHead,Map<Integer,List<String>> searchMap) throws CSVException;
	/**
	 * Deletes rows from a CSV file that match a set of search criteria.
	 *
	 * @param filePath the path of the CSV file to delete rows from
	 * @param delimiter the delimiter used in the CSV file
	 * @param searchMap a map containing the search criteria, where the key is the index of the column to search in and the value is a list of values to search for in that column
	 */
	void delete(String filePath,String delimiter,Map<Integer,List<String>> searchMap) throws CSVException;
	
}

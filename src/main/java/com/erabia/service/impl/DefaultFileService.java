package com.erabia.service.impl;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.erabia.service.CSVFileService;
import com.erabia.service.enums.CSVExceptionType;
import com.erabia.service.exception.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.erabia.service.enums.CSVExceptionType.*;


public class DefaultFileService implements CSVFileService {
    private static DefaultFileService instance;
    private DefaultFileService() {}

    public static DefaultFileService getInstance(){
        if(instance==null)
            instance=new DefaultFileService();
        return instance;
    }

    @Override
    public List<List<String>> getAll(String filePath, String delimiter, boolean includeHead) throws CSVException {
        return search(filePath,delimiter,includeHead,new HashMap<>());
    }

    @Override
    public void put(String filePath, String delimiter, List<String> record) throws CSVException{
        putAll(filePath, delimiter, Collections.singletonList(record));
    }

    @Override
    public void putAll(String filePath, String delimiter, List<List<String>> records) throws CSVException{
        if (filePath ==null )   throw new IllegalArgumentException("file path cannot be null");
        if ( records==null  )   return;
        validateDelimiter(delimiter);
        for(List<String> record : records) {
            int headSize;
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                headSize=parseCSV(br.readLine(),delimiter).size();
            }catch (FileNotFoundException e){
                throw new CSVException("the file path : "+filePath+" is invalid.",FILE_NOT_FOUND);
            }catch (IOException e){
                throw new CSVException("An error occurred while attempting to read from csv file: "+filePath ,IO_EXCEPTION);
            }
            StringBuilder sb = new StringBuilder();
            List<String> escapedRecord = new ArrayList<>();
            for (int i=0;i<headSize;i++) {
                String field="";
                if(i<record.size()) {
                    field=record.get(i);
                    if (field.contains(delimiter))
                        field =sb.append('"').append(field).append('"').toString();
                }
                sb.setLength(0);
                escapedRecord.add(field);
            }
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))){
                writer.write(System.getProperty("line.separator")+String.join(delimiter, escapedRecord));
            }
            catch (FileNotFoundException e){
                throw new CSVException("the file path : "+filePath+" is invalid.",FILE_NOT_FOUND);
            } catch (IOException e){
                throw new CSVException("An error occurred while attempting to read from csv file: "+filePath ,IO_EXCEPTION);
            }
        }
    }

    @Override
    public List<List<String>> search(String filePath, String delimiter, boolean includeHead, int index, String key) throws CSVException {
        return search(filePath, delimiter, includeHead, Collections.singletonMap(index, Collections.singletonList(key)));
    }

    @Override
    public List<List<String>> search(String filePath, String delimiter, boolean includeHead, int index, List<String> keys) throws CSVException {
        return search(filePath, delimiter, includeHead, Collections.singletonMap(index, keys));
    }

    @Override
    public List<List<String>> search(String filePath, String delimiter, boolean includeHead, Map<Integer, List<String>> searchMap) throws CSVException {
        if (filePath ==null )   throw new IllegalArgumentException("file path cannot be null");
        if ( searchMap==null )  searchMap= new HashMap<>();
        validateDelimiter(delimiter);
        List<List<String>> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            if (includeHead)    result.add(parseCSV(br.readLine(),delimiter));
            else                br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                List<String> rowValues = parseCSV(line,delimiter);
                if (matchRow(rowValues,searchMap))
                    result.add(rowValues);
            }
        } catch (FileNotFoundException e){
            throw new CSVException("the file path : "+filePath+" is invalid.",FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new CSVException("An error occurred while attempting to read from csv file: "+filePath ,IO_EXCEPTION);
        }
        return result;
    }

    private List<List<String>> threadFunc(List<String> lines,String delimiter,Map<Integer,List<String>> searchMap) throws CSVException {
        List<List<String>> res = new ArrayList<>();
        for(String line :lines) {
            List<String> rowValues = parseCSV(line, delimiter);
            if (matchRow(rowValues, searchMap))
                res.add(rowValues);
        }
        return res;
    }

    /**
     * a second search implementation that uses multiple threads to speed up search.
     */
    public List<List<String>> searchThreaded(String filePath, String delimiter, boolean includeHead, Map<Integer, List<String>> searchMap) throws CSVException {
        List<List<String>> threadOutput = Collections.synchronizedList(new ArrayList<>());
        int chunkSize=25;
        int numOfThreads=5;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            if (includeHead)    threadOutput.add(parseCSV(br.readLine(),delimiter));
            else                br.readLine();

            ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
            List<String> lines=new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                if (lines.size()==chunkSize) {
                    List<String> finalLines = lines;
                    executor.execute(() -> {
                        try {
                            threadOutput.addAll(threadFunc(finalLines,delimiter,searchMap));
                        } catch (CSVException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    lines=new ArrayList<>();
                }
            }
            if(!lines.isEmpty()) {
                List<String> finalLines1 = lines;
                executor.execute(() -> {
                    try {
                        threadOutput.addAll(threadFunc(finalLines1,delimiter,searchMap));
                    } catch (CSVException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            executor.shutdown();
            while (!executor.isTerminated()){}

        } catch (FileNotFoundException e){
            throw new CSVException("the file path : "+filePath+" is invalid.",FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new CSVException("An error occurred while attempting to read from csv file: "+filePath ,IO_EXCEPTION);
        }
        return threadOutput;
    }

    @Override
    public void delete(String filePath, String delimiter, Map<Integer, List<String>> searchMap) throws CSVException {
        if (filePath ==null )   throw new IllegalArgumentException("File Path cannot be null.");
        if ( searchMap==null )        searchMap= new HashMap<>();
        validateDelimiter(delimiter);
        File inputFile = new File(filePath);
        File tempFile = new File(filePath + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            writer.write(reader.readLine());         //write header
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> rowValues = parseCSV(line,delimiter);
                if (!matchRow(rowValues,searchMap))
                    writer.write(System.getProperty("line.separator")+line );
            }
        } catch (FileNotFoundException e){
            throw new CSVException("the file path : "+filePath+" is invalid.",FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new CSVException("An error occurred while attempting to read from csv file: "+filePath ,IO_EXCEPTION);
        }

        if (!inputFile.delete())
            throw new CSVException ("Could not delete file: " + inputFile.getName(),IO_EXCEPTION);
        if (!tempFile.renameTo(inputFile))
            throw new CSVException ("Could not rename file: " + tempFile.getName(),IO_EXCEPTION);
    }

    private List<String> parseCSV(String line,String delimiter){
        return Arrays.stream(line.split(delimiter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",-1))
                .map(s -> s.replace("\"", ""))
                .collect(Collectors.toList());
    }
    private boolean matchRow(List<String> rowValues,Map<Integer, List<String>> searchMap) throws CSVException {
        for (Map.Entry<Integer, List<String>> entry : searchMap.entrySet()) {
            int columnIndex = entry.getKey();
            if (columnIndex<0 || columnIndex>= rowValues.size())
                throw new CSVException("index : "+ columnIndex+ "is out of bounds for the CSV file.",INDEX_OUT_OF_BOUNDS);
            List<String> searchValues = entry.getValue();
            String cellValue = rowValues.get(columnIndex);
            if (!searchValues.contains(cellValue))
                return false;
        }
        return true;
    }
    private void validateDelimiter(String delimiter) throws CSVException{
        if(delimiter==null)         throw new IllegalArgumentException("Delimiter cannot be null.");
        if(delimiter.length()!=1)   throw new CSVException("Delimiter size must be 1.",INVALID_DELIMITER);
        if (delimiter.equals("\"") || delimiter.equals("\n") || delimiter.equals("\r") || delimiter.equals("\t"))
            throw new CSVException("Delimiter cannot be a special character (\", \\n, \\r, \\t).", CSVExceptionType.INVALID_DELIMITER);
    }

    //TODO add checks for when CSV file is badly formatted
}

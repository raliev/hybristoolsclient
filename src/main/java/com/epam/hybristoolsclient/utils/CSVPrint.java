package com.epam.hybristoolsclient.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rauf_Aliev on 8/17/2016.
 */
public class CSVPrint {

    static char DEFAULT_QUOTE = '\"';
    static char DEFAULT_SEPARATOR = '\t';
    public static void printAsCSV(String result, boolean fix, char delimiter) {
        List<String> lines = Arrays.asList(result.split("\n"));
        List<List<String>> csv = new ArrayList<>();
        for (String line : lines)
        {
            List<String> columns = new ArrayList<>();
            //columns.addAll(Arrays.asList(line.split(delimiter)));
            columns = parseLine(line, delimiter, DEFAULT_QUOTE);
            for (int i=0; i< columns.size(); i++)
            {
                columns.set(i, columns.get(i).trim());
            }
            csv.add(columns);
        }
        CSVPrint.writeCSV(csv, fix, delimiter);
    }
    public static void printAsCSV(String result, boolean fix) {
        printAsCSV(result, fix, '\t');
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                   /*
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }
                    */
                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }


    public static void writeCSV(List<List<String>> rows, boolean fix, char delimiter) {


        if (rows.size() == 0)
            throw new RuntimeException("No rows");

        // normalize data
        int longest = 0;
        for (List<String> row : rows)
            if (row.size() > longest)
                longest = row.size();

        for (List<String> row : rows)
            while (row.size() < longest)
                row.add("");

        if (longest == 0)
            throw new RuntimeException("No columns");

        // fix special characters
        for (int i = 0; i < rows.size(); i++)
            for (int j = 0; j < rows.get(i).size(); j++)
                rows.get(i).set(j, fix ? fixSpecial(rows.get(i).get(j), delimiter) : rows.get(i).get(j));

        // get the maximum size of one column
        int[] maxColumn = new int[rows.get(0).size()];

        for (int i = 0; i < rows.size(); i++)
            for (int j = 0; j < rows.get(i).size(); j++)
                if (maxColumn[j] < rows.get(i).get(j).length())
                    maxColumn[j] = rows.get(i).get(j).length();

        // create the format string
        String outFormat = "";
        for (int max : maxColumn)
            outFormat += "%-" + (max + 1) + "s"+delimiter+" ";
        outFormat = outFormat.substring(0, outFormat.length() - 2) + "\n";

        // print the data
        for (List<String> row : rows)
            System.out.printf(outFormat, row.toArray());

    }

    private static String fixSpecial(String s, char delimiter) {

        s = s.replaceAll("(\")", "$1$1");

        if (s.contains("\n") || s.contains(delimiter+"") || s.contains("\"") ||
                s.trim().length() < s.length()) {
            s = "\"" + s + "\"";
        }

        return s;
    }
}

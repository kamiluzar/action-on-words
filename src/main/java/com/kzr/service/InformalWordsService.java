package com.kzr.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kamil on 2017-05-05.
 */

@Service
public class InformalWordsService {
    static final String PATH_DICTIONARY = "http://dictionary.cambridge.org/dictionary/english/";

    public String isFormal(String text) throws MalformedURLException {
        BufferedReader in;
        ArrayList<String> sourceCodeLine = new ArrayList<>();
        String result = "";
        try {
            in = new BufferedReader(new InputStreamReader(
                    new URL(PATH_DICTIONARY + text).openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("usage")) {
                    sourceCodeLine.add(line);
                }
            }

            String sourceCodeLineString = "";
            for (String s : sourceCodeLine) {
                sourceCodeLineString += s + ", ";
            }

            String newLine;
            ArrayList<String> keywords = new ArrayList<>();
            Pattern pattern = Pattern.compile("class=\"usage\">(.*?)<");
            Matcher matcher = pattern.matcher(sourceCodeLineString);
            while (matcher.find()) {
                newLine = matcher.group(1);
                keywords.add(newLine);
            }

            String keywordsString = "";
            for(String s : keywords) {
                keywordsString += s + ", ";
            }

            String[] matches = new String[] {
                    "informal", "vulgar", "offensive", "slang", "old-fashioned"};

            String temp = "";

            for(String s : matches) {
                temp = temp + " " + s;

                if(keywordsString.contains(s)){
                    return "This is an incorrect word to put in a formal letter!";
                }
                else
                    result =  "This is a correct word to put in a formal letter!";
            }

            return result;
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        }
    }
}

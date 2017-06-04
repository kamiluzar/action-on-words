package com.kzr.service;

import org.springframework.stereotype.Service;

import java.io.*;
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
    static final String CORRECT_WORD_INFO = "This is a correct word to put in a formal letter!";
    static final String INCORRECT_WORD_INFO = "This is an incorrect word to put in a formal letter!";
    static final String CORRECT_WORD_WITH_EXCEPTION = "This is a correct word, but in some cases it is informal, so be careful!";
    static final String INCORRECT_WORD = "Incorrect word!";


    public String isFormal(String text) throws MalformedURLException {
        text = text.toLowerCase();
        String result = "";        try {
            if(existenceOfWord(text))
                return INCORRECT_WORD;
            String keywords = findInHTML(text, "class=\"usage\">(.*?)<");
            String[] matches = new String[]{
                    "vulgar", "offensive", "slang"};
            String temp = "";
            for (String s : matches) {
                temp += s + ", ";
                if (keywords.contains(s)) {
                    return INCORRECT_WORD_INFO;
                } else
                    result = CORRECT_WORD_INFO;
            }
            if (keywords.contains(temp) == false && (keywords.contains("informal") || keywords.contains("old-fashioned"))) {
                return CORRECT_WORD_WITH_EXCEPTION;
            }
            return result;
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        }
    }

    public String findInHTML(String word, String searchText) throws IOException {
        BufferedReader in;
        String newLine;
        ArrayList<String> words = new ArrayList<>();
        in = new BufferedReader(new InputStreamReader(
                new URL(PATH_DICTIONARY + word).openStream()));
        String line;
        while ((line = in.readLine()) != null) {
            Pattern pattern = Pattern.compile(searchText);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                newLine = matcher.group(1);
                words.add(newLine);
            }
        }
        String wordsString = "";
        for(String s : words) {
            wordsString += s + ", ";
        }
        return wordsString;
    }

    public boolean existenceOfWord(String text) throws IOException {
        return !findInHTML(text, "class=\"entry-body__el clrd(.*?)<div").contains("js-share-holder");
    }
}
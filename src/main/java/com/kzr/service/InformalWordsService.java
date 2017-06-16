package com.kzr.service;

import com.kzr.dao.InformalWordsDao;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kamil on 2017-05-05.
 */

/**
 * Class designed to check the formality of the word
 */
@Service
public class InformalWordsService {
    static final String PATH_DICTIONARY = "http://dictionary.cambridge.org/dictionary/english/";
    static final String CORRECT_WORD_INFO = "This is a correct word to put in a formal letter!";
    static final String INCORRECT_WORD_INFO = "This is an incorrect word to put in a formal letter!";
    static final String CORRECT_WORD_WITH_EXCEPTION = "This is a correct word, but in some cases it is informal, so be careful!";
    static final String INCORRECT_WORD = "Incorrect word!";
    static final String INTERNECT_CONNECTION = "No internet connection!";
    InformalWordsDao informalWordsDao = new InformalWordsDao();
    /**
     * The method indicates whether the word is formal
     * @param text Search word
     * @return Whether the word is formal
     */
    public String isFormal(String text) throws MalformedURLException {
        text = text.toLowerCase();
        String check = informalWordsDao.readDB(text);
        if(check != null){
            return informalWordsDao.readDB(text);}
        String result = "";
        try {
            if(existenceOfWord(text))
                return INCORRECT_WORD;
            String keywords = findInHTML(text, "class=\"usage\">(.*?)<");
            String[] matches = new String[]{
                    "vulgar", "offensive", "slang"};
            String temp = "";
            for (String s : matches) {
                temp += s + ", ";
                if (keywords.contains(s)) {
                    informalWordsDao.addToDB(text, INCORRECT_WORD_INFO);
                    return INCORRECT_WORD_INFO;
                } else
                    result = CORRECT_WORD_INFO;
            }
            if (keywords.contains(temp) == false && (keywords.contains("informal") || keywords.contains("old-fashioned"))) {
                informalWordsDao.addToDB(text, CORRECT_WORD_WITH_EXCEPTION);
                return CORRECT_WORD_WITH_EXCEPTION;
            }
            informalWordsDao.addToDB(text, result);
            return result;
        } catch (UnknownHostException e) {
            return INTERNECT_CONNECTION;
        }catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        }
    }

    /**
     *
     * @param word Search word
     * @param searchText Search part of the HTML file
     * @return String contains information about the formality of the word
     */
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

    /**
     * This method checks if the word exists in the dictionary
     * @param text Search word
     * @return Whether the word exists in the dictionary
     */
    public boolean existenceOfWord(String text) throws IOException {
        return !findInHTML(text, "class=\"entry-body__el clrd(.*?)<div").contains("js-share-holder");
    }
}
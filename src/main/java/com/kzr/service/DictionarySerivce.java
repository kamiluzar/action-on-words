package com.kzr.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Kamil on 2017-04-26.
 */

@Service
public class DictionarySerivce {


    static final String PATH = "http://words.bighugelabs.com/api/2/9a0d1e46117e2bdb3bf6e1a1568faa3e/";
    static final String XML = "/xml";
    static final String INCORRECT_WORD = "Incorrect word or lack of antonym in the database!";

    public  String findAntonyms(String word) throws IOException {
        word = word.toLowerCase();
        try {
            return checkFile(word);
        }
        catch (NullPointerException en) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                URL url = new URL(PATH + word + XML);
                URLConnection connection = url.openConnection();
                Document doc = parseXML(connection.getInputStream());
                NodeList xml = doc.getElementsByTagName("w");
                String xmlElementValue = null;
                ArrayList<String> antonymsCollection = new ArrayList<>();

                for (int i = 0; i < xml.getLength(); i++) {
                    Element element = (Element) xml.item(i);
                    if (isAntonym(element.getAttribute("r"))) {
                        xmlElementValue = element.getTextContent();
                        antonymsCollection.add(xmlElementValue);
                    }
                }

                String lastElement = antonymsCollection.get(antonymsCollection.size()-1);
                String antonymsCollectionString = "";

                for(String s : antonymsCollection) {
                    if(!s.equals(lastElement))
                        antonymsCollectionString += s + ", ";
                    else
                        antonymsCollectionString += s + ".";
                }

                saveToFile(word, antonymsCollectionString);

                return "Antonym/s for " + word + ": " + antonymsCollectionString;
            } catch (ArrayIndexOutOfBoundsException e) {
                return INCORRECT_WORD;
            } catch(IOException e){
                return INCORRECT_WORD;
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                return errors.toString();
            }
        }
    }

    private static boolean isAntonym(String text) {
        return text != null && text.equals("ant");
    }

    private static Document parseXML(InputStream stream) throws Exception {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;
        try {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
            doc = objDocumentBuilder.parse(stream);
        } catch (Exception ex) {
            throw ex;
        }
        return doc;
    }

    public String checkFile(String word) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("antonyms.txt"));
        boolean searchNext = true;
        String antonym;
        String line;
        do {
            line = reader.readLine();
            if(line!=null)
                if(line.split(" - ")[0].equals(word))
                    searchNext = false;
        }
        while (line!=null && searchNext);
        antonym = line.split(" - ")[1];
        reader.close();
        return "Antonym/s for " + word + ": " + antonym;
    }

    public void saveToFile(String word, String antonyms) throws IOException {
        PrintWriter save = new PrintWriter(new FileWriter("antonyms.txt", true));
        save.println(word + " - " + antonyms);
        save.close();
    }
}

package com.kzr.service;

import com.kzr.dao.DictionaryDao;
import org.h2.jdbc.JdbcSQLException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Kamil on 2017-04-26.
 */

/**
 * Class designed to search for antonyms
 */
@Service
public class DictionarySerivce {
    static final String PATH = "http://words.bighugelabs.com/api/2/9a0d1e46117e2bdb3bf6e1a1568faa3e/";
    static final String XML = "/xml";
    static final String INCORRECT_WORD = "Incorrect word or lack of antonym in the database!";
    static final String DATABASE_ERROR = "Database error";
    static final String INTERNECT_CONNECTION = "No internet connection!";

    DictionaryDao dictionaryDao = new DictionaryDao();

    /**
     * This method connects to a web page and uses the DOM to search for antonyms
     * @param word Search word
     * @return List of antonyms
     */
    public  String findAntonyms(String word) throws IOException {
            try {
                word = word.toLowerCase();
                String check = dictionaryDao.readDB(word);
                if(!check.contains("null"))
                    return dictionaryDao.readDB(word); //checkFile(word);
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
                 dictionaryDao.addToDB(word, antonymsCollectionString); //saveToFile(word, antonymsCollectionString);
                return "Antonym/s for " + word + ": " + antonymsCollectionString;
            } catch (UnknownHostException e) {
                return INTERNECT_CONNECTION;
            } catch (NullPointerException e) {
                return INCORRECT_WORD;
            } catch (ArrayIndexOutOfBoundsException e) {
                return INCORRECT_WORD;
            } catch(IOException e){
                return INCORRECT_WORD;
            } catch (JdbcSQLException e) {
                return DATABASE_ERROR;
            }  catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                return errors.toString();
            }
    }

    /**
     * Whether the attribute contain antonym
     * @param text Fragment of the xml attribute
     * @return Whether the attribute contain antonym
     */
    private static boolean isAntonym(String text) {
        return text != null && text.equals("ant");
    }

    /**
     * Parsing the XML file
     * @param stream Input stream
     * @return XML in Document format
     * @throws Exception
     */
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

    /**
     * The method checks if it has an antonym
     * @param word Search word
     * @return List of antonyms from the file
     */
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

    /**
     * Method writes to the file found antonyms
     * @param word Search word
     * @param antonyms List of antonyms
     */
    public void saveToFile(String word, String antonyms) throws IOException {
        PrintWriter save = new PrintWriter(new FileWriter("antonyms.txt", true));
        save.println(word + " - " + antonyms);
        save.close();
    }
}
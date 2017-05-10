package com.kzr.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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

    public  String findAntonyms(String word) {
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

            String antonymsCollectionString = "";

            for(String s : antonymsCollection) {
                antonymsCollectionString += s + ", ";
            }

            return antonymsCollectionString;

        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return errors.toString();
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
}

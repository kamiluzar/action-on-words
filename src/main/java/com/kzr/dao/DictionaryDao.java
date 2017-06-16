package com.kzr.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Kamil on 2017-06-15.
 */

/**
 * Class with database related to antonyms
 */
public class DictionaryDao {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/test";
    static final String USER = "";
    static final String PASS = "";
    Connection conn = null;
    Statement stmt = null;

    /**
     * This method adds antonyms to the database
     * @param word Search word
     * @param antonym Antonym of the inscribed word
     */
    public void addToDB(String word, String antonym) {
        int antonym_id = 0;
        try {
            Class.forName(JDBC_DRIVER) ;
            conn = DriverManager.getConnection(DB_URL,USER,PASS) ;
            stmt = conn.createStatement() ;
            String sql = "SELECT max(antonym_id) FROM antonyms";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next() ) {
                antonym_id = rs.getInt("max(antonym_id)");
            }
            antonym_id++;
            sql = "INSERT INTO antonyms (antonym_id, word, antonym)" + "VALUES ('"+antonym_id+"','"+word+"','"+antonym+"') ";
            stmt.executeUpdate(sql) ;
            stmt.close() ;
            conn.close() ;
        } catch(SQLException se) {
            se.printStackTrace() ;
        } catch(Exception e) {
            e.printStackTrace() ;
        } finally {
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            }
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se) {
                se.printStackTrace() ;
            }
        }
    }

    /**
     * This method reads data from the database
     * @param searchWord Written the word for which user is looking for antonym
     * @return List of antonym from database
     */
    public String readDB(String searchWord) {
        String word = null;
        String antonym = null;
        ArrayList<String> wordsList = new ArrayList<String>();
        String wordListString = "";
        try {
            Class.forName(JDBC_DRIVER) ;
            conn = DriverManager.getConnection(DB_URL,USER,PASS) ;
            stmt = conn.createStatement() ;
            String sql = "SELECT word FROM antonyms";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next() ) {
                word = rs.getString("word");
                wordsList.add(word);
            }
            for(String s : wordsList) {
                wordListString += s;
            }
            if (wordListString.contains(searchWord)) {
                sql = "SELECT antonym FROM antonyms WHERE `word`='"+searchWord+"'";
                rs = stmt.executeQuery(sql);
            }
            while(rs.next() ) {
                antonym = rs.getString("antonym");
            }
            stmt.close() ;
            conn.close() ;
            return "Antonym/s for " + searchWord + ": " + antonym;
        } catch(SQLException se) {
            StringWriter errors = new StringWriter();
            se.printStackTrace(new PrintWriter(errors));
            return errors.toString();

        } catch(Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        } finally {
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            }
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se) {
                se.printStackTrace() ;
            }
        }
    }
}

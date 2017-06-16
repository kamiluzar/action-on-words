package com.kzr.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Kamil on 2017-06-15.
 */

public class InformalWordsDao {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/test";
    static final String USER = "";
    static final String PASS = "";
    Connection conn = null;
    Statement stmt = null;

    public void addToDB(String word, String response) {
        int formal_id = 0;
        try {
            Class.forName(JDBC_DRIVER) ;
            conn = DriverManager.getConnection(DB_URL,USER,PASS) ;
            stmt = conn.createStatement() ;
            String sql = "SELECT max(formal_id) FROM formal_words";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next() ) {
                formal_id = rs.getInt("max(formal_id)");
            }
            formal_id++;
            sql = "INSERT INTO formal_words (formal_id, word, response)" + "VALUES ('"+formal_id+"','"+word+"','"+response+"') ";
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

    public String readDB(String searchWord) {
        String word;
        String response = null;
        ArrayList<String> wordsList = new ArrayList<String>();
        String wordListString = "";
        try {
            Class.forName(JDBC_DRIVER) ;
            conn = DriverManager.getConnection(DB_URL,USER,PASS) ;
            stmt = conn.createStatement() ;
            String sql = "SELECT word FROM formal_words";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next() ) {
                word = rs.getString("word");
                wordsList.add(word);
            }
            for(String s : wordsList) {
                wordListString += s;
            }
            if (wordListString.contains(searchWord)) {
                sql = "SELECT response FROM formal_words WHERE `word`='"+searchWord+"'";
                rs = stmt.executeQuery(sql);
            }
            while(rs.next() ) {
                response = rs.getString("response");
            }
            stmt.close() ;
            conn.close() ;
            return response;
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

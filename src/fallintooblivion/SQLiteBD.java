/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

/**
 *
 * @author jferr
 */

import java.sql.*;

public class SQLiteBD {
    
    private Connection c;
    private Statement stmt;
    
    //construtor vazio
    public SQLiteBD(){
        
    }
    
    //returna o statement da bd
    public Statement returnStmt(){
        c = null;
        stmt = null;
        try{
   
            Class.forName("org.sqlite.JDBC");
            c= DriverManager.getConnection("jdbc:sqlite:oblivion.db");
            stmt = c.createStatement();
            
        }catch(Exception e){
            System.out.println(e.getClass().getName() +": "+e.getMessage());
            System.exit(0);
        }
        return stmt;
    }
    
    public void closeBD() throws SQLException{
        stmt.close();
        c.close();
    }
    
    //cria a bd e tabela file
    public void createBD(){
        
        c = null;
        try{
   
            Class.forName("org.sqlite.JDBC");
            c= DriverManager.getConnection("jdbc:sqlite:oblivion.db");
            
        }catch(Exception e){
            System.out.println(e.getClass().getName() +": "+e.getMessage());
            System.exit(0);
        }
        
        System.out.println("Opended database successfuly");
        
        stmt = null;
        
        try{

            stmt = c.createStatement();
           
            String sql = "CREATE TABLE IF NOT EXISTS File "+
                  "(name VARCHAR(100) PRIMARY KEY NOT NULL, "+
                  "ext VARCHAR(20) NOT NULL, "+  
                  "salt INTEGER NOT NULL, "+
                  "iv CHAR(32) NOT NULL, "+
                  "hmac CHAR(32) NOT NULL);"  ;
            
            stmt.executeUpdate(sql); 
            
            stmt.close();
            c.close();
            
        }catch(Exception e){
            System.out.println(e.getClass().getName() +": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfuly");
        
    }

    Statement createStatement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

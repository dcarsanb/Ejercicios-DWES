package com.mycompany.gestionnominas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
 private static final String URL = "jdbc:mariadb://localhost:3306/gestionNominas";
 private static final String USER = "root";
 private static final String PASSWORD = "1234";
 
 public static Connection obtenerConexion(){
     Connection enlace = null;
     
     try{
         
         enlace = DriverManager.getConnection(URL, USER, PASSWORD);
     } catch(SQLException e){
         System.out.println("Error al conectar: "+e.getMessage());
     }
     return enlace;
 }
}

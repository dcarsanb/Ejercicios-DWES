package com.mycompany.gestionnominas;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CalculaNominas {
    private static void escribe(Empleado e, Nomina n){
        e.imprime();
        
        System.out.println("Su sueldo es: "+n.sueldo(e));
    }
    public static void main(String[] args) {
        
        Connection cn = Conexion.obtenerConexion();
        
        if(cn != null){
            System.out.println("Conexion establecida");
               
        String query = "SELECT id, nombre FROM usuarios";
        
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);){
            System.out.println("Lista de Usuarios:");
            System.out.println("DNI NOMBRE");
            System.out.println("---------");
            
            while(rs.next()){
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                System.out.println(dni+" "+nombre);
            }
        }catch(SQLException e){
            System.out.println("Error al consultar datos "+e.getMessage());
        }finally{
            try{
            cn.close();
            System.out.println("--------");
            System.out.println("Conexion cerrada de forma segura");
        }catch(SQLException ex){
                System.out.println("Error al cerrar conexion "+ex.getMessage());
                }
            }
        }else{
            System.out.println("No se pudo iniciar la consulta porque la conexion a la base de datos fallo");
        
        /**Lo metemos todo dentro del try catch para poder escribir el codigo y atrapar las exception que puedan salir de las cuales hemos capturado antes.*/
//        
//        try(BufferedReader br = new BufferedReader(new FileReader("empleados.txt"))){
//            Empleado emp1 = new Empleado("James Cosling", "32000032G", "M", 4, 7);
//            Empleado emp2 = new Empleado("Ada Lovelace", "32000031R", "F");
//
//            Nomina n = new Nomina();
//
//            escribe(emp1, n);
//            escribe(emp2, n);
//
//            emp2.incrAnyo();
//            emp1.setCategoria(9);
//
//            escribe(emp1, n);
//            escribe(emp2, n);
//        }catch (Exception e){
//            System.out.println(e);
//        }
    }
    }
    
}

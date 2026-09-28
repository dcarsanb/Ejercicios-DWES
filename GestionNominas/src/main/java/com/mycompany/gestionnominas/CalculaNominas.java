package com.mycompany.gestionnominas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class CalculaNominas {
    
    private static final Scanner teclado = new Scanner(System.in);
    
    public static void altaEmpleado(Empleado e) throws SQLException{
        Connection cn = null;
        Statement st = null;
        
        try{
            cn = DBUtils.getConnection();
            st = cn.createStatement();
            
            String sqlEmpleado =
                    "INSERT INTO empleados "+
                    "(dni, nombre, sexo, categoria, anyos) VALUES ("+
                    "'" +e.dni + "', "+
                    "'" +e.nombre + "', "+
                    "'" +e.sexo + "', "+
                    e.getCategoria() + ", "+
                    e.anyos + ")";
            
            st.executeUpdate(sqlEmpleado);
            
            Nomina n = new Nomina();
            double sueldo = n.sueldo(e);
            
            String sqlNomina =
                    "INSERT INTO nominas (dni, sueldo) VALUES (" +
                    "'" +e.dni + "', "+
                    sueldo +")";
            
            st.executeUpdate(sqlNomina);
            
            System.out.println("Empleado dado de alta correctamente");
            System.out.println("Sueldo: "+sueldo);
        }finally{
            if(st !=null){
                st.close();
            }
            if(cn != null){
                cn.close();
            }
        }
    }
    
    public static void altaEmpleado(String fichero) throws Exception{
        try(BufferedReader br = new BufferedReader(new FileReader(fichero))){
            String linea;
            
            while((linea = br.readLine()) != null){
            if(linea.isEmpty()){
            continue;
            }
            
            String[] datos = linea.split(";");
            
            String nombre = datos[0];
            String dni = datos[1];
            String sexo = datos[2];
            int categoria = Integer.parseInt(datos[3]);
            int anyos = Integer.parseInt(datos[4]);
            
            Empleado e = new Empleado(
                nombre,
                dni,
                sexo,
                categoria,
                anyos
            );
            
            altaEmpleado(e);
        }
            br.close();
        }
    }
    
    private static void mostrarEmpleados() throws SQLException {
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
            cn = DBUtils.getConnection();
            st = cn.createStatement();
            
            String sql =
                    "SELECT dni, nombre, sexo, categoria, anyos "+
                    "FROM empleados";
            
            rs = st.executeQuery(sql);
            
            System.out.println();
            System.out.println("LISTA DE EMPLEADOS");
            System.out.println("------------------");
            
            while(rs.next()){
                
                System.out.println("DNI: "+ rs.getString("dni")
                                    + " | Nombre: "+ rs.getString("nombre")
                                    + " | Sexo: "+ rs.getString("sexo")
                                    + " | Categoria: "+ rs.getInt("categoria")
                                    + " | Anyos: "+ rs.getInt("anyos")
                );
            }
        } finally{
            if(rs != null){
                rs.close();
            }
            if( st != null){
                st.close();
            }
            if(cn != null){
                cn.close();
            }
        }
    }
    
    private static void mostrarSalario(String dni) throws SQLException{
        
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;
        try{
            cn = DBUtils.getConnection();
            st = cn.createStatement();
            
            String sql =
                    "SELECT e.nombre, e.dni, n.sueldo " +
                    "FROM empleados e, nominas n " +
                    "WHERE e.dni = n.dni " +
                    "AND e.dni = '" + dni + "'";
            
            rs = st.executeQuery(sql);
            
            if(rs.next()){
                System.out.println();
                System.out.println("Empleado: "+ rs.getString("nombre"));
                System.out.println("DNI: "+ rs.getString("dni"));
                System.out.println("Sueldo: "+ rs.getDouble("sueldo"));
            }else{
                System.out.println("No existe ningun empleado con ese dni");
            }
        } finally{
            if(rs != null){
                rs.close();
            }
            if( st != null){
                st.close();
            }
            if(cn != null){
                cn.close();
            }
        }
    }
    
    private static void modificarEmpleado(String dni) throws SQLException{
        
        Connection cn = null;
        Statement st = null;
        
        try{
            
            cn = DBUtils.getConnection();
            st = cn.createStatement();
            
            System.out.println("Nuevo nombre: ");
            String nombre = teclado.nextLine();
            
            System.out.println("Nuevo sexo: ");
            String sexo = teclado.nextLine();
            
            System.out.println("Nueva categoria: ");
            int categoria = Integer.parseInt(teclado.nextLine());
            
            System.out.println("Nuevos anyos trabajados: ");
            int anyos = Integer.parseInt(teclado.nextLine());
            
            if(categoria < 1 || categoria > 10 || anyos <0){
            System.out.println("Datos no correctos");
            return;
        }
            
            String sql = "UPDATE empleados SET "+
                    "nombre = '"+nombre+"', "+
                    "sexo = '"+sexo+"', "+
                    "categoria = "+categoria+", "+
                    "anyos = " + anyos +
                    "WHERE dni = '" +dni+"'";
            
            int filas = st.executeUpdate(sql);
            
            if(filas > 0){
                Empleado e = new Empleado(
                        nombre,
                        dni,
                        sexo,
                        categoria,
                        anyos
                );
                
                Nomina n = new Nomina();
                double sueldo = n.sueldo(e);
                
                String sqlNomina =
                        "UPDATE nominas SET " +
                        "sueldo = " + sueldo +
                        " WHERE dni = '" + dni + "'";
                
                st.executeUpdate(sqlNomina);
                
                System.out.println("EMpleado modificado correctamente");
                
                System.out.println("Nuevo sueldo: "+sueldo);
            }else{
                System.out.println("No existe ningun empleado con ese dni");
            }
        } catch(DatosNoCorrectosException e){
            System.out.println("Datos no corretos");
        }finally{
            if(st != null){
                st.close();
            }
            if(cn != null){
                cn.close();
            }
        }
    }
    
    private static void recalcularSalario(String dni) throws SQLException {
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            cn = DBUtils.getConnection();
            st = cn.createStatement();

            String sql ="SELECT nombre, sexo, categoria, anyos " +
                    "FROM empleados " +
                    "WHERE dni = '" + dni + "'";

            rs = st.executeQuery(sql);

            if (!rs.next()) {
                System.out.println("No existe ningún empleado con ese DNI.");
                return;
            }
            try {
                Empleado e = new Empleado(
                        rs.getString("nombre"),
                        dni,
                        rs.getString("sexo"),
                        rs.getInt("categoria"),
                        rs.getInt("anyos"));

                Nomina n = new Nomina();
                double sueldo = n.sueldo(e);

                String sqlUpdate ="UPDATE nominas SET sueldo = " +
                        sueldo +" WHERE dni = '" + dni + "'";

                st.executeUpdate(sqlUpdate);

                System.out.println("Sueldo recalculado: " + sueldo);

            } catch (DatosNoCorrectosException e) {

                System.out.println("Datos no correctos.");
            }

        }finally{

            if(rs != null){
                rs.close();
            }
            if(st != null){
                st.close();
            }
            if(cn != null){
                cn.close();
            }
        }
    }
    
    private static void recalcularTodos() throws SQLException {
        Connection cn = null;
        Statement st = null;
        Statement stUpdate = null;
        ResultSet rs = null;

        try {
            cn = DBUtils.getConnection();
            st = cn.createStatement();
            stUpdate = cn.createStatement();

            String sql ="SELECT dni, nombre, sexo, categoria, anyos " +
                    "FROM empleados";

            rs = st.executeQuery(sql);

            while (rs.next()) {
                try {
                    Empleado e = new Empleado(
                            rs.getString("nombre"),
                            rs.getString("dni"),
                            rs.getString("sexo"),
                            rs.getInt("categoria"),
                            rs.getInt("anyos")
                    );

                    Nomina n = new Nomina();
                    double sueldo = n.sueldo(e);

                    String sqlUpdate ="UPDATE nominas SET sueldo = " +
                            sueldo +" WHERE dni = '" +
                            rs.getString("dni") + "'";
                    stUpdate.executeUpdate(sqlUpdate);

                } catch (DatosNoCorrectosException e) {
                    System.out.println("Datos no correctos para el empleado "+ rs.getString("dni"));
                }
            }

            System.out.println("Todos los salarios han sido recalculados.");

        }finally{
            if (rs != null){
                rs.close();
            }
            if(stUpdate != null){
                stUpdate.close();
            }
            if(st != null){
                st.close();
            }
            if(cn != null){
                cn.close();
            }
        }
    }
    
    private static void backup() {

        System.out.println("La copia de seguridad en ficheros se realizará "
                + "mediante los ficheros de texto.");

    }
    private static void menu() {

        int opcion = -1;
        while (opcion != 0) {
            System.out.println();
            System.out.println("==================================");
            System.out.println("       GESTION DE NOMINAS");
            System.out.println("==================================");
            System.out.println("1. Mostrar empleados");
            System.out.println("2. Mostrar salario por DNI");
            System.out.println("3. Modificar empleado");
            System.out.println("4. Recalcular salario de un empleado");
            System.out.println("5. Recalcular todos los salarios");
            System.out.println("6. Copia de seguridad");
            System.out.println("7. Dar de alta un empleado");
            System.out.println("8. Dar de alta empleados desde fichero");
            System.out.println("0. Salir");
            System.out.println("==================================");

            try {
                System.out.print("Seleccione una opcion: ");

                opcion=Integer.parseInt(teclado.nextLine());

                switch(opcion){
                    case 0:
                        System.out.println("Programa terminado.");
                        break;
                    case 1:
                        mostrarEmpleados();
                        break;
                    case 2:
                        System.out.print("DNI: ");
                        String dni = teclado.nextLine();
                        mostrarSalario(dni);
                        break;
                    case 3:
                        System.out.print("DNI del empleado: ");
                        String dniModificar =teclado.nextLine();
                        modificarEmpleado(dniModificar);
                        break;
                    case 4:
                        System.out.print("DNI del empleado: ");
                        String dniRecalcular =teclado.nextLine();
                        recalcularSalario(dniRecalcular);
                        break;
                    case 5:
                        recalcularTodos();
                        break;
                    case 6:
                        backup();
                        break;
                    case 7:
                        altaDesdeTeclado();
                        break;
                    case 8:
                        System.out.print("Nombre del fichero: ");
                        String fichero =teclado.nextLine();
                        try {
                            altaEmpleado(fichero);
                        } catch (Exception e){
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                }

            }catch(NumberFormatException e){
                System.out.println("Debe introducir un numero.");
            }catch(SQLException e){
                System.out.println("Error de base de datos: "+ e.getMessage());
            }
        }
    }
    
     private static void altaDesdeTeclado() {

        try {
            System.out.print("Nombre: ");
            String nombre = teclado.nextLine();

            System.out.print("DNI: ");
            String dni = teclado.nextLine();

            System.out.print("Sexo: ");
            String sexo = teclado.nextLine();

            System.out.print("Categoria: ");
            int categoria =Integer.parseInt(teclado.nextLine());

            System.out.print("Años trabajados: ");
            int anyos =Integer.parseInt(teclado.nextLine());

            Empleado e = new Empleado(
                    nombre,
                    dni,
                    sexo,
                    categoria,
                    anyos
            );
            altaEmpleado(e);
        }catch(DatosNoCorrectosException e){

            System.out.println("Datos no correctos.");
        }catch(NumberFormatException e){
            System.out.println("Debe introducir numeros correctamente.");

        }catch(SQLException e){
            System.out.println("Error de base de datos: "+ e.getMessage());
        }
    }

    public static void main(String[] args) {
        menu();
    }
}

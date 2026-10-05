package com.mycompany.gestionnominas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class CalculaNominas {

    private static final Scanner teclado = new Scanner(System.in);

    private static void altaEmpleado(Empleado e) throws SQLException {
        Connection cn = null;
        Statement st = null;

        try {
            cn = DBUtils.getConnection();
            st = cn.createStatement();

            String sqlEmpleado
                    = "INSERT INTO empleados "
                    + "(dni, nombre, sexo, categoria, anyos) VALUES ("
                    + "'" + e.dni + "', "
                    + "'" + e.nombre + "', "
                    + "'" + e.sexo + "', "
                    + e.getCategoria() + ", "
                    + e.anyos + ")";

            st.executeUpdate(sqlEmpleado);

            Nomina n = new Nomina();
            double sueldo = n.sueldo(e);

            String sqlNomina
                    = "INSERT INTO nominas (dni_empleado, sueldo) VALUES ("
                    + "'" + e.dni + "', "
                    + sueldo + ")";

            st.executeUpdate(sqlNomina);

            System.out.println("Empleado dado de alta correctamente");
            System.out.println("Sueldo: " + sueldo);
        } finally {
            if (st != null) {
                st.close();
            }
            if (cn != null) {
                cn.close();
            }
        }
    }


    private static void mostrarEmpleados() throws SQLException {
    String sql = "SELECT dni, nombre, sexo, categoria, anyos FROM empleados";

    System.out.println("\nLISTA DE EMPLEADOS");
    System.out.println("------------------");

    try (Connection cn = DBUtils.getConnection();
         Statement st = cn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {

        while (rs.next()) {
            System.out.println("DNI: " + rs.getString("dni")
                    + " | Nombre: " + rs.getString("nombre")
                    + " | Sexo: " + rs.getString("sexo")
                    + " | Categoria: " + rs.getInt("categoria")
                    + " | Años: " + rs.getInt("anyos")
            );
        }
    }
}


    private static void mostrarSalario(String dni) throws SQLException {
    String sql = "SELECT e.nombre, e.dni, n.sueldo "
               + "FROM empleados e "
               + "JOIN nominas n ON e.dni = n.dni_empleado "
               + "WHERE e.dni = ?";

    try (Connection cn = DBUtils.getConnection();
         PreparedStatement st = cn.prepareStatement(sql)) {
        
        st.setString(1, dni);
        
        try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                System.out.println();
                System.out.println("Empleado: " + rs.getString("nombre"));
                System.out.println("DNI: " + rs.getString("dni"));
                System.out.println("Sueldo: " + rs.getDouble("sueldo"));
            } else {
                System.out.println("No existe ningún empleado con ese DNI");
            }
        }
    }
}

    private static void modificarEmpleado(String dni) throws SQLException {
    String sqlEmpleado = "UPDATE empleados SET nombre = ?, sexo = ?, categoria = ?, anyos = ? WHERE dni = ?";
    String sqlNomina = "UPDATE nominas SET sueldo = ? WHERE dni_empleado = ?";

    try {
        System.out.println("Nuevo nombre: ");
        String nombre = teclado.nextLine();

        System.out.println("Nuevo sexo: ");
        String sexo = teclado.nextLine();

        System.out.println("Nueva categoría: ");
        int categoria = Integer.parseInt(teclado.nextLine());

        System.out.println("Nuevos años trabajados: ");
        int anyos = Integer.parseInt(teclado.nextLine());

        if (categoria < 1 || categoria > 10 || anyos < 0) {
            System.out.println("Datos no correctos");
            return;
        }

        try (Connection cn = DBUtils.getConnection();
             PreparedStatement stEmpleado = cn.prepareStatement(sqlEmpleado);
             PreparedStatement stNomina = cn.prepareStatement(sqlNomina)) {

            stEmpleado.setString(1, nombre);
            stEmpleado.setString(2, sexo);
            stEmpleado.setInt(3, categoria);
            stEmpleado.setInt(4, anyos);
            stEmpleado.setString(5, dni);

            int filas = stEmpleado.executeUpdate();

            if (filas > 0) {
                Empleado e = new Empleado(nombre, dni, sexo, categoria, anyos);
                Nomina n = new Nomina();
                double sueldo = n.sueldo(e);

                stNomina.setDouble(1, sueldo);
                stNomina.setString(2, dni);
                stNomina.executeUpdate();

                System.out.println("Empleado modificado correctamente");
                System.out.println("Nuevo sueldo: " + sueldo);
            } else {
                System.out.println("No existe ningún empleado con ese DNI");
            }
        }
    } catch (DatosNoCorrectosException e) {
        System.out.println("Datos no correctos");
    }
}


    private static void recalcularSalario(String dni) throws SQLException {
    String sqlSelect = "SELECT nombre, sexo, categoria, anyos FROM empleados WHERE dni = ?";
    String sqlUpdate = "UPDATE nominas SET sueldo = ? WHERE dni_empleado = ?";

    try (Connection cn = DBUtils.getConnection();
         PreparedStatement stSelect = cn.prepareStatement(sqlSelect);
         PreparedStatement stUpdate = cn.prepareStatement(sqlUpdate)) {

        stSelect.setString(1, dni);

        try (ResultSet rs = stSelect.executeQuery()) {
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
                        rs.getInt("anyos")
                );

                Nomina n = new Nomina();
                double sueldo = n.sueldo(e);

                stUpdate.setDouble(1, sueldo);
                stUpdate.setString(2, dni);
                stUpdate.executeUpdate();

                System.out.println("Sueldo recalculado: " + sueldo);

            } catch (DatosNoCorrectosException e) {
                System.out.println("Datos no correctos.");
            }
        }
    }
}


    private static void recalcularTodos() throws SQLException {
    String sqlSelect = "SELECT dni, nombre, sexo, categoria, anyos FROM empleados";
    String sqlUpdate = "UPDATE nominas SET sueldo = ? WHERE dni_empleado = ?";

    try (Connection cn = DBUtils.getConnection();
         PreparedStatement stSelect = cn.prepareStatement(sqlSelect);
         PreparedStatement stUpdate = cn.prepareStatement(sqlUpdate);
         ResultSet rs = stSelect.executeQuery()) {

        while (rs.next()) {
            String dniEmpleado = rs.getString("dni");
            
            try {
                Empleado e = new Empleado(
                        rs.getString("nombre"),
                        dniEmpleado,
                        rs.getString("sexo"),
                        rs.getInt("categoria"),
                        rs.getInt("anyos")
                );

                Nomina n = new Nomina();
                double sueldo = n.sueldo(e);

                stUpdate.setDouble(1, sueldo);
                stUpdate.setString(2, dniEmpleado);
                stUpdate.executeUpdate();

            } catch (DatosNoCorrectosException e) {
                System.out.println("Datos no correctos para el empleado con DNI: " + dniEmpleado);
            }
        }

        System.out.println("Todos los salarios han sido recalculados con éxito.");
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
            int categoria = Integer.parseInt(teclado.nextLine());

            System.out.print("Años trabajados: ");
            int anyos = Integer.parseInt(teclado.nextLine());

            Empleado e = new Empleado(
                    nombre,
                    dni,
                    sexo,
                    categoria,
                    anyos
            );
            altaEmpleado(e);
        } catch (DatosNoCorrectosException e) {
            System.out.println("Datos no correctos.");
        } catch (NumberFormatException e) {
            System.out.println("Debe introducir numeros correctamente.");

        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println();
            System.out.println("----------------------------------");
            System.out.println("GESTION DE NOMINAS");
            System.out.println("----------------------------------");
            System.out.println("0. Salir");
            System.out.println("1. Mostrar empleados");
            System.out.println("2. Mostrar salario por DNI");
            System.out.println("3. Modificar empleado");
            System.out.println("4. Recalcular salario de un empleado");
            System.out.println("5. Recalcular todos los salarios");
            System.out.println("6. Dar de alta un empleado");
            System.out.println("----------------------------------");

            try {
                System.out.print("Seleccione una opcion: ");

                opcion = Integer.parseInt(teclado.nextLine());

                switch (opcion) {
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
                        String dniModificar = teclado.nextLine();
                        modificarEmpleado(dniModificar);
                        break;
                    case 4:
                        System.out.print("DNI del empleado: ");
                        String dniRecalcular = teclado.nextLine();
                        recalcularSalario(dniRecalcular);
                        break;
                    case 5:
                        recalcularTodos();
                        break;
                    case 7:
                        altaDesdeTeclado();
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un numero.");
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        }
    }
}

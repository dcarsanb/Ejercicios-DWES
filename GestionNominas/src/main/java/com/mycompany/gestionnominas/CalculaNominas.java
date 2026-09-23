package com.mycompany.gestionnominas;

public class CalculaNominas {
    private static void escribe(Empleado e, Nomina n){
        e.imprime();
        
        System.out.println("Su sueldo es: "+n.sueldo(e));
    }
    public static void main(String[] args) {
        /**Lo metemos todo dentro del try catch para poder escribir el codigo y atrapar las exception que puedan salir de las cuales hemos capturado antes.*/
        try{
        Empleado emp1 = new Empleado("James Cosling", "32000032G", "M", 4, 7);
        Empleado emp2 = new Empleado("Ada Lovelace", "32000031R", "F");
        
        Nomina n = new Nomina();
        
        escribe(emp1, n);
        escribe(emp2, n);
        
        emp2.incrAnyo();
        emp1.setCategoria(9);
        
        escribe(emp1, n);
        escribe(emp2, n);
        }catch (Exception e){
            System.out.println(e);
        }
    }
    
}

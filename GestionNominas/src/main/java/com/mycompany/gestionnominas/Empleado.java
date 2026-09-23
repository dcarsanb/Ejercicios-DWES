package com.mycompany.gestionnominas;

public class Empleado extends Persona{
    
    private int categoria;
    public int anyos;

    public Empleado(String nombre, String dni, String sexo, int categoria, int anyos) throws DatosNoCorrectosException {
        super(nombre, dni, sexo);
        if(categoria < 1 || categoria > 10){ /**Aqui miramos si la categoria introducida esta fuera delas indicaciones
                                            Si esta fuera se lanza la exception, sino continua abajo. */
             throw new DatosNoCorrectosException("Datos no correctos");
        }else{
            this.categoria = categoria;
        }
        if(anyos <=0){/**Aqui hacemos igual que arriba, comprobamos los anyos, si es menor que cero lanza la exception.
                    Si es mayor que cero se salta la exception */
            throw new DatosNoCorrectosException("Datos no correctos");
        }else{
            this.anyos = anyos;
        }
    }

    public Empleado(String nombre, String dni, String sexo) throws DatosNoCorrectosException{
        super(nombre, dni, sexo);
        categoria =1;
        anyos=0;
        
        if(categoria < 1 || categoria > 10 || anyos <=0){ //En este caso hacemos igual que arriba, pero metiendo todas las variables en el mismo if
            throw new DatosNoCorrectosException("Datos no correctos");
        }
        
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }
    
    public void incrAnyo(){
        anyos++;
    }
    
    public void imprime(){
        System.out.println("Nombre: "+nombre+", DNI: "+dni+", Sexo: "+sexo+", Categoria: "+categoria+", Anyos:"+anyos);
    }
    
}

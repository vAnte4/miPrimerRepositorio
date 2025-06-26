package hash;

import linkedlist.ListaEnlazada;
/****************EJERCICIO 1 *********************/
public class HashEntero {
    // creo un arreglo de listas enlazadas que guardan numeros enteros
    private ListaEnlazada<Integer>[] table;
    // variable para guardar el tamaño de la tabla
    private int size;
    
    public HashEntero(int size) {
        this.size = size;
        // guardo el tamaño que me pasen

        this.table = new ListaEnlazada[size];
        // creo el arreglo con ese tamaño

        for (int i = 0; i < size; i++) {
            table[i] = new ListaEnlazada<>();
            // en cada posicion del arreglo creo una lista vacia
        }
    }

    private int hash(int value) {
        return value % size;
        // calculo el indice usando modulo (valor % tamaño)
    }

    public void insert(int value) {
        int index = hash(value);
        // obtengo el indice aplicando la funcion hash

        table[index].insertLast(value);
        // inserto el numero al final de la lista que esta en ese indice
    }

    public void printTable() {
        for (int i = 0; i < size; i++) {
            System.out.println("Índice " + i + ":");
            // muestro el numero del indice

            table[i].recorrer();
            // recorro e imprimo todos los numeros que estan en la lista de ese indice

            System.out.println();
            // dejo un espacio entre indices
        }
    }
}

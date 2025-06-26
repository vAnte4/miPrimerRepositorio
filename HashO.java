package hash;

import linkedlist.ListaEnlazada;
import linkedlist.Nodo;

/**
 * Clase que implementa una tabla hash con encadenamiento (listas enlazadas).
 */

 public class HashO {

    private ListaEnlazada<Register>[] table; 
    // creo un arreglo de listas enlazadas para guardar registros

    private int size; 
    // variable para guardar el tamaño de la tabla hash

    // constructor
    public HashO(int size) {
        this.size = size; 
        // guardo el tamaño recibido

        this.table = new ListaEnlazada[size]; 
        // creo el arreglo con ese tamaño

        for (int i = 0; i < size; i++) {
            table[i] = new ListaEnlazada<>(); 
            // en cada posicion del arreglo creo una lista vacia
        }
    }
    public int getSize() { return size; } //getter del tamaño de la lista
    //getter de bucket(cada posicion del arreglo) en idx
    public linkedlist.ListaEnlazada<Register> getBucket(int idx) { return table[idx]; }

    // funcion hash (clave % tamaño)
    private int hash(int key) {
        return key % size; 
        // calculo el indice usando modulo
    }

    // insertar un registro
    public void insert(Register reg) {
        int index = hash(reg.getKey()); //calcula el bucket donde debe ir el registro (index)
        // inserto el registro al final de la lista en ese indice
        table[index].insertLast(reg); 
    }

    // buscar un registro por clave
    public Register search(int key) {
        int index = hash(key); //calcula el bucket inicial (index)
        // obtengo el primer nodo de la lista en ese bucket
        Nodo<Register> current = table[index].getFirst(); 
        //hacemos un recorrido de la lista enlazda
        while (current != null) {
            if (current.getData().getKey() == key) { //si el Register del nodo tiene la misma clave
                return current.getData(); // devuelvo ese registro
            }
            // si no es, paso al siguiente nodo
            current = current.getNext(); 
        }
        return null; // si termine y no encontre, devuelvo null
    }

    // eliminar un registro por clave
    public void delete(int key) {
        int index = hash(key); //Calcula el bucket (index)
        //obtengo el primer nodo de la lista en ese bucket
        Nodo<Register> current = table[index].getFirst(); 
        //hacemos un recorrido de la lista enlazda
        while (current != null) {
            if (current.getData().getKey() == key) { //si el Register del nodo tiene la misma clave
                table[index].removeNodo(current.getData()); //eliminamos el nodo de esa lista enlazada del bucket
                return;
            }
            current = current.getNext(); // si no es, sigo recorriendo
        }
    }

    // imprimir la tabla hash
    public void printTable() {
        //recorre cada bucket de 0 a size−1  de la tablaaaa
        for (int i = 0; i < size; i++) {
            // muestro el numero de indice
            System.out.println("Índice " + i + ":"); 
            // recorro e imprimo los elementos (nodos) de la lista en ese indice (bucket)
            table[i].recorrer(); 
            System.out.println();  // dejo un espacio entre indices
        }
    }
}

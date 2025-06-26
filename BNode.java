package btree;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    // Contador estático para asignar IDs únicos a cada nodo
    // statico ya que va ser una sola copia compartida para todas las instancias ya que pertenece a la clase
    private static int counter = 0;
    private int idNode;               // Identificador del nodo
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;

    public BNode(int n) { //n (orden) el max de hijos
        this.idNode = ++counter; //al crear un nodo , incrementa 

        this.keys   = new ArrayList<>(n); //n num max de hijos
        this.childs = new ArrayList<>(n); //n xq se considera es eespcio adicional , xqen realidad es n-1
        this.count  = 0;
        for (int i = 0; i < n; i++) {
            this.keys .add(null);
            this.childs.add(null);
        }
    }

    public boolean nodeFull(int maxKeys) {
        return this.count == maxKeys; //si el num max de claves es igual al contador
    }

    public boolean nodeEmpty() {
        return this.count == 0; //count es 0
    }

    public boolean searchNode(E key, int[] pos) { //arreglo de tamaño 1 para devolver la posición donde esto o debria estar la clave
        int i = 0;
        //mientrasrecorra hasta la ultima clave valida
        //y que key sea mayor que la clave en la posición i
        while (i < count && key.compareTo(keys.get(i)) > 0) {
            i++; // si cumple seguimos buscndo
        }
        //si es que salimos del bucle significa que
        //i == count osea llegamos al final
        //o key ≤ keys.get(i) la clave en i ya no es menor
        pos[0] = i; //aqui hacemos el hack de java y guardamos la posicion 
        //devuelve true si: no nos pasamos del final y la clave en la posicion i es igual a la que buscamos
        return (i < count && key.compareTo(keys.get(i)) == 0);
    }

    public int getIdNode() {
        return idNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ID");
        sb.append(idNode).append(": [");
        for (int i = 0; i < count; i++) {
            sb.append(keys.get(i));
            if (i < count - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}


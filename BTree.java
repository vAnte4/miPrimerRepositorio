package btree;

import exceptions.ItemNotFound;
import exceptions.ExceptionIsEmpty;
import exceptions.ItemDuplicated;

public class BTree<E extends Comparable<E>> {
    public BNode<E> root;
    private int order;
    private boolean up; //par que en la insercion, si al subir recursivamente necesitamos propagar un split
    private BNode<E> nDes; //rama derecha creada cuando hacemos un split de nodo

    public BNode<E> getRoot() {
        return this.root;
    }

    public BTree(int orden) {
        this.order = orden;
        this.root = null;
        this.up = false;
        this.nDes = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public int size() {
        return this.order;
    }

    public void insert(E cl) throws ItemDuplicated {
        up = false; // nos indicara si hay split que debe subir una mediana
        E mediana; //la clave que sube cuando hacemos un split
        //push q es un metodo recursivo que baja hasta llegar a una hoja para insertar
        //puede insertar o divir nodos en el camino
        mediana = push(this.root, cl);
        //si up es true , hubo split en la antigua raiz
        //osea este es solo en caso no se pudo resolver y hay que crear una nueva raiz
        if (up) { 
            BNode<E> pnew = new BNode<E>(this.order); //creamos un nodo nuevo con su orden (este sera la raiz)
            pnew.count = 1; //deicmos que solo va tener una clave q va ser la mediana
            pnew.keys.set(0, mediana); //colocamos la mediana en la posicion 0 de pnew
            pnew.childs.set(0, this.root); //apunta al árbol viejo (subárbol izquierdo).
            pnew.childs.set(1, nDes); //childs[1] xq aounta a nDes, q es el subarbol derecho creado en el split
            this.root = pnew; //x utlimo asignamos pnew como la nuevz raiz del arbol
        }
    }

    private E push(BNode<E> current, E cl) throws ItemDuplicated {
        //arreglo de una posicion,para guarar la posicion  dentro del current donde se encontro o deberia estar la clave
        int pos[] = new int[1];
        E mediana;
        //si llegamos aqui es xq hemos llegado mas abajo de una hoja
        // osea aqui insertamos
        if (current == null) { 
            up = true; //para decirle al padre que traemos una clave para insertar
            nDes = null; //xq aun no hya subarbol derecho
            return cl; //la clave sube como mediana a insertar en el padre
        } else { //sino
            boolean fl = current.searchNode(cl, pos); //aqui verificamos si ya existe la clave en el nodo
            if (fl) { //si es true
                up = false; 
                throw new ItemDuplicated("Elemento duplicado: " + cl); //lanzamos excepcion
            }
            //hacemos recursion para al hijo que si es correcto
            mediana = push(current.childs.get(pos[0]), cl);

            if (up) { //al volver, si up es true significa que traigo una mediana por insertar
                //si el nodo esta lleno lo divido y obtengo la nueva mediana
                if (current.nodeFull(this.order - 1)) { //orden -1 xq ese es el num max de claves
                    mediana = dividedNode(current, mediana, pos[0]); 
                } else { //si hay espacio, inserto la mediana y el subarbol derecho
                    putNode(current, mediana, nDes, pos[0]);
                    up = false; //ya que ya acomodamos la clave, no hay mas split
                }
            }
            return mediana; //devuelvo la mediana para que el nivel superior lo maneje
        }
    }

    private void putNode(BNode<E> current, E cl, BNode<E> rd, int k) {
        int i; //indice para el bucle 
        //empieza desde la ultima clave hasta k (posicion q queremos insertar)
        for (i = current.count - 1; i >= k; i--) {
            //cda clave en i se copia a i+1 (desplazo hacia la derecha), para abrir espacio a k  (poicicion deseada)
            current.keys.set(i + 1, current.keys.get(i));
            //cada referencia de hijo en i+1 se mueve a i+2, osea desplzao hacia la derecha
            current.childs.set(i + 2, current.childs.get(i));
        }
        current.keys.set(k, cl); //inserto la nueva clave en la posicion k (poicicion deseada)
        current.childs.set(k + 1, rd);  //enlazo el subarbol derecho rd como hijo en la posición k+1
        //incrementamos el contador de las claves del nodo
        current.count++;
    }

    private E dividedNode(BNode<E> current, E cl, int k) {
        //guardo el subárbol derecho recibido antes del split
        BNode<E> rd = nDes;
        int i, posMdna; //declaramos estas variables

        // Calcular posicion de la mediana
        // Elige dónde cortar el nodo:
        // si la clave nueva va antes o en el medio, uso order/2; si va después, uso order/2 + 1
        posMdna = (k <= this.order / 2) ? this.order / 2 : this.order / 2 + 1;
        nDes = new BNode<E>(this.order); //nDes será el hermano derecho tras el split

        // Mover claves y hijos al nDes(nuevonodo), posteriores a la mediana
        for (i = posMdna; i < this.order - 1; i++) {
            //trasladamos la clave i desde current a la posición (i - posMdna) de nDes
            nDes.keys.set(i - posMdna, current.keys.get(i));
            //aqui vamos a mover el hijo a la derecha de es clave
            //current.childs.get(i+1) es el hijo de la derecha current.keys.get(i)
            // lo colocamos en nDes.childs en la posicion (i - posMdna + 1),
            // que conserva su relacion derecha respecto a la clave movida
            nDes.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }
        //actualizamos cuantas claves tiene cada nodo despues del split:

        //el nuevo nodo derecho nDes recibe todas las claves que quedaron tras la mediana,
        // que son desde posMdna hasta el final (order–1 claves en total)
        nDes.count = (this.order - 1) - posMdna;

        //el nodo original (lado izquierdo) conserva las claves desde 0 hasta posMdna–1,
        //x lo q ahora su conteo es justo posMdna
        current.count = posMdna;

        //insertar la nueva clave cl y su subarbol rd en el nodo que corresponda:

        //si k esta en la primera mitad la clave va al nodo izquierdo-current
        //si k esta en la segunda mitad ponemos el indice-k - posMdna
        //y la clave va al nodo derecho-nDes
        if (k <= this.order / 2) {
            putNode(current, cl, rd, k);
        } else {
            putNode(nDes, cl, rd, k - posMdna);
        }

        // Obtener la mediana que subira

        //aqui tomammos la ultima clave valida en current, q es la mediwana q debemos subir al padre
        E median = current.keys.get(current.count - 1);
        //despues, el hijo q estaa a la dercha de la clave en current (indice count) pasa a ser el 1er hijo de nDes
        nDes.childs.set(0, current.childs.get(current.count));
        //decrmnta para la extraccion de la medinaa
        current.count--;

        return median; //retonamos la mediana para q el nivel sperio la inserteeeee
    }

    public void remove(E cl) throws ItemNotFound {
        delete(root, cl);
        if (root != null && root.count == 0) {
            root = root.childs.get(0);
        }
    }

    public boolean delete(BNode<E> node, E key) throws ItemNotFound {
        //Si el nodo es null, la clave no existe
        if (node == null) {
            throw new ItemNotFound("Elemento no encontrado: " + key); //lanza excepcion
        }
        //arreglo de una posicion, para guardar la posicion
        int pos[] = new int[1];
        boolean found = node.searchNode(key, pos); //buscar la clave o la posicion de insercion en este nodo

        if (found) { //si esta en este nodo 
            if (node.childs.get(pos[0]) != null) { //y tiene hijo derecho, es interno
                //borramos la clave dejando el hueci en este mismo nodo
                removeKey(node, pos[0]);
                return true;
            //sino
            } else {
                //si es hoja, reemplazamos por el predecesor (max del arbol izq)
                E pred = getPredecessor(node, pos[0]);
                //reempzamos el pred y luego borramos esaclave en la hoja
                node.keys.set(pos[0], pred);
                //borro la clave predecesora en la hoja
                return delete(node.childs.get(pos[0]), pred);
            }
        //sino - found ==false
        } else {
            //la clave no esta en este nodo,
            if (node.childs.get(pos[0]) != null) {
                return false; //retornamos false
            } else { //si si existe un hijo bajamos recursivamente
                boolean isDeleted = delete(node.childs.get(pos[0]), key);
                //luego de borrarlo vemos si ese hijo quedo con pocas claves y hay que arreglar underflow
                if (node.childs.get(pos[0]).count < (order - 1) / 2) {
                    fix(node, pos[0]);
                }
                return isDeleted;
            }
        }
    }
    //para eliminar la clave en la posicion index de un nodo hoja o interno
    private void removeKey(BNode<E> node, int index) {
        //vamos a desplzar todas las claves a la derecha de index 
        for(int i = index; i < node.count - 1; i++) {
            node.keys.set(i, node.keys.get(i + 1)); //una posicion a al izq
        }
        //borramos el duplicado que queda al final
        node.keys.set(node.count - 1, null);
        node.count--; //actualizamos el conteo de las claves ya q eliminamos
    }
    //para tener el valor mayor del subarbol izq
    private E getPredecessor(BNode<E> node, int index) {
        //vamos al hijo izq en la posicion del index
        BNode<E> current = node.childs.get(index);
        //bucle para bajar en el subarbol izq , el valor mas a la derecha , el mayor
        while (current.childs.get(index + 1) != null) {
            current = current.childs.get(index + 1);
        }
        //cuando ya llegamos a l ultima clave ese es el predecesor
        return current.keys.get(current.count - 1);
    }

    //fusiona el hijo en 'index' con su hermano a la derecha (index+1)
    private void merge(BNode<E> parent, int index) {
        //aqui obtenemos referncias a los dos hijos
        BNode<E> left = parent.childs.get(index);
        BNode<E> right = parent.childs.get(index + 1);

        // Mover clave del padre al hijo izquierdo
        left.keys.set(left.count, parent.keys.get(index));
        left.count++;

        // Mover claves e hijos del hijo derecho al izquierdo
        for (int i = 0; i < right.count; i++) {
            left.keys.set(left.count + i, right.keys.get(i));
        }
        for (int i = 0; i <= right.count; i++) {
            left.childs.set(left.count + i, right.childs.get(i));
        }
        left.count += right.count;

        // Eliminar la clave del padre y el hijo derecho
        for (int i = index; i < parent.count - 1; i++) {
            parent.keys.set(i, parent.keys.get(i + 1));
            parent.childs.set(i + 1, parent.childs.get(i + 2));
        }

        parent.keys.set(parent.count - 1, null);
        parent.childs.set(parent.count, null);
        parent.count--;
    }

    private void borrowFromLeft(BNode<E> parent, int index ) {
        BNode<E> left = parent.childs.get(index - 1);
        BNode<E> current = parent.childs.get(index);

        // Desplazar claves e hijos del niño a la derecha
        for (int i = current.count; i >= 0; i--) {
            current.keys.set(i + 1, current.keys.get(i));
        }

        // Mover clave del padre al niño
        current.keys.set(0, parent.keys.get(index - 1));
        parent.keys.set(index - 1, left.keys.get(left.count - 1));
        left.keys.set(left.count - 1, null);

        // Mover hijo del hermano al niño
        if (left.childs.get(left.count) != null) {

            for (int i = current.count; i >= 0; i--) {
                current.childs.set(i + 1, current.childs.get(i));
            }
            current.childs.set(0, left.childs.get(left.count));
            left.childs.set(left.count, null);
        }

        current.count++;
        left.count--;
    }

    private void borrowFromRight(BNode<E> parent, int index) {
        BNode<E> right = parent.childs.get(index + 1);
        BNode<E> current = parent.childs.get(index);

        // Insertar clave del padre al final de current
        current.keys.set(current.count, parent.keys.get(index));

        // Reemplazar clave del padre con la primera del hermano derecho
        parent.keys.set(index, right.keys.get(0));

        // Desplazar las claves del hermano derecho una posición a la izquierda
        for (int i = 1; i < right.count; i++) {
            right.keys.set(i - 1, right.keys.get(i));
        }
        right.keys.set(right.count - 1, null);

        // Si tiene hijos, mover el primer hijo del hermano derecho al final de current
        if (right.childs.get(0) != null) {
            current.childs.set(current.count + 1, right.childs.get(0));
            for (int i = 1; i <= right.count; i++) {
                right.childs.set(i - 1, right.childs.get(i));
            }
            right.childs.set(right.count, null);
        }

        current.count++;
        right.count--;
    }

    private void fix(BNode<E> parent, int index) {
        if (index > 0 && parent.childs.get(index - 1).count > (order - 1) / 2) {
            borrowFromLeft(parent, index);
        }

        else if (index < parent.count && parent.childs.get(index + 1).count > (order - 1) / 2) {
            borrowFromRight(parent, index);
        }

        else {
            if (index > 0) {
                merge(parent, index - 1);
            } else {
                merge(parent, index);
            }
        }

    }

    @Override
    public String toString() {
        String s = "";
        if (isEmpty()) {
            s += "BTree is empty...";
        } else {
            s += "Id.Nodo | Claves Nodo     | Id.Padre | Id.Hijos\n";
            s += writeTree(this.root, null);
        }
        return s;
    }

    private String writeTree(BNode<E> current, BNode<E> parent) {
        if (current == null) return "";

        StringBuilder sb = new StringBuilder();

        // Id.Nodo
        sb.append(current.getIdNode()).append("       | ");

        // Claves Nodo
        for (int i = 0; i < current.count; i++) {
            sb.append(current.keys.get(i));
            if (i < current.count - 1) sb.append(", ");
        }

        // Id.Padre
        sb.append("     | ");
        sb.append((parent != null) ? parent.getIdNode() : "--");

        // Id.Hijos
        sb.append("     | [");
        boolean first = true;
        for (BNode<E> child : current.childs) {
            if (child != null) {
                if (!first) sb.append(", ");
                sb.append(child.getIdNode());
                first = false;
            }
        }
        sb.append("]\n");

        // Recorrer hijos válidos
        for (BNode<E> child : current.childs) {
            if (child != null) {
                sb.append(writeTree(child, current));
            }
        }

        return sb.toString();
    }

    public void printIndentedTree() {
        printIndentedTree(this.root, 0);
    }

    private void printIndentedTree(BNode<E> node, int level) {
        if (node == null) return;

        String indent = "    ".repeat(level);
        System.out.print(indent + "Nodo " + node.getIdNode() + " -> [");
        for (int i = 0; i < node.count; i++) {
            System.out.print(node.keys.get(i));
            if (i < node.count - 1) System.out.print(", ");
        }
        System.out.println("]");

        for (int i = 0; i <= node.count; i++) {
            if (node.childs.get(i) != null) {
                printIndentedTree(node.childs.get(i), level + 1);
            }
        }
    }

    public boolean search(E cl) throws ExceptionIsEmpty {
        if (isEmpty()) throw new ExceptionIsEmpty("El árbol está vacío.");
        return searchRecursive(this.root, cl);
    }

    private boolean searchRecursive(BNode<E> node, E cl) {
        if (node == null) return false;

        int[] pos = new int[1];
        boolean found = node.searchNode(cl, pos);

        if (found) {
            System.out.println(cl + " se encuentra en el nodo " + node.getIdNode() + " en la posición " + pos[0]);
            return true;
        } else {
            return searchRecursive(node.childs.get(pos[0]), cl);
        }
    }
  
}

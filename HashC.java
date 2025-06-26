package hash;

/**
 * implementación de una tabla hash usando hash cerrado (sondeo lineal).
 */
public class HashC {

    // clase interna para representar una celda de la tabla
    private static class Element {
        private Register register;   // registro que guardo aquí (o null)
        private boolean isAvailable; // true = libre para insertar

        public Element() {
            this.register    = null;  // arranco sin nada
            this.isAvailable = true;  // arranco libre
        }
    }

    private Element[] table; // arreglo de celdas de tipo Element 
    private int size;        // tamaño de la tabla (debe ser primo idealmente)

    /**
     * constructor: inicializa la tabla con 'size' celdas vacías.
     */
    public HashC(int size) {
        this.size  = size;
        this.table = new Element[size];
        for (int i = 0; i < size; i++) { //aqui recorremos las posiciones e inicializo con un nuevo Element vacio  disponible
            table[i] = new Element();
        }
    }

    /**
     * función hash: mapea la clave a un índice [0..size-1].
     */
    private int hash(int key) {
        return key % size;
    }

    /**
     * inserta un nuevo registro con sondeo lineal.
     * si la tabla está llena, simplemente no inserta.
     */
    public void insert(Register reg) {
        int start = hash(reg.getKey()); //vamoa a calcular el indice incial
        int idx   = start; //como una copia para hacer el recorrido
        //bucle do…while que garantiza al menos una iteracion
        do {
            Element e = table[idx]; //en el objeto e almacenamos la celda actual
            // si la celda está libre o borrada isAvailable=true
            if (e.register == null || e.isAvailable) {
                e.register    = reg;      // guardo el registro
                e.isAvailable = false;    // marco como ocupado
                return;
            }
            //para avanzar al siguiente índice en la tabla de forma ciclica(del final vuelves al principio) "wrap‐around"
            idx = (idx + 1) % size; 
        } while (idx != start);
        // termina si vuelvo al inicio, la tabla está llena y no inserto
    }

    /**
     * busca un registro por su clave.
     * devuelve el Register o null si no está.
     */
    public Register search(int key) {
        int start = hash(key); //vamos a calcular el indice incial
        int idx   = start; //como una copia para hacer el recorrido
        //bucle do…while que garantiza al menos una iteracion
        do {
            Element e = table[idx]; //e es la celda actua en table[idx]
            // si e.register != null : osea hay un registro
            // !e.isAvailable: que esa celda no sea un tombstone (no fue borrada)
            // e.register.getKey() == key: que la clave del registro coincida con la buscada
            if (e.register != null && !e.isAvailable && e.register.getKey() == key) {
                return e.register; // lo encontramos  :D y lo retorno
            }
            // si llego a una celda nunca usada, ya no existe
            if (e.register == null && e.isAvailable) {
                break; //terminamos el bucle
            }
            //si ninguna de las anteriores se cumplio entonces avanzamos al siguiente indice de forma ciclica
            idx = (idx + 1) % size; //% size asegurar que siempre quede entre 0 y size−1
        } while (idx != start); //repetimos hasta que idx vuelva a start, una vuelta completa sin exito
        return null; //no encontrado
    }

    /**
     * eliminación lógica: marca la celda como disponible de nuevo.
     */
    public void delete(int key) {
        int start = hash(key); //vamos a calcular el indice incial
        int idx   = start; //como una copia para hacer el recorrido
        //bucle do…while que garantiza al menos una iteracion
        do {
            Element e = table[idx]; //e es la celda actua en table[idx]
            //que la celda tenga un objeto y que no sea un tombstone (borrado) y q sea el registro que buscamos
            if (e.register != null && !e.isAvailable && e.register.getKey() == key) {
                e.register    = null; //ponemos null para eliminar el objeto
                e.isAvailable = true; //y que ahora esta vacio
                return;
            }
            //si encontramos una celda nunca usada
            if (e.register == null && e.isAvailable) {
                break; //la clave no existe mas adelante entonces rompemos el ciclo
            }
            //si ninguna de las anteriores se cumplio entonces avanzamos al siguiente indice de forma ciclica
            idx = (idx + 1) % size; //% size asegurar que siempre quede entre 0 y size−1
        } while (idx != start); //repetimos hasta que idx vuelva a start, una vuelta completa sin exito
    }

    /**
     * imprime el estado actual de la tabla.
     * para cada índice muestra: (key, name)  o '(vacío)' o '(borrado)'.
     */
    public void printTable() {
        //bucle que va de i = 0 hasta i = size - 1, cubriendo todas las posiciones de la tabla
        for (int i = 0; i < size; i++) {
            Element e = table[i]; //obtenemos la celda Element que esta en la posicion i del table
            //formateado entre corchetes (por ejemplo [ 0]: o [10]: ) 
            System.out.printf("[%2d]: ", i);
            // Caso 1: la celda está ocupada por un registro
            if (e.register != null && !e.isAvailable) {
                //imprime el toString() de ese objeto Register : (clave, nombre)
                System.out.println(e.register);
            //Caso 2: la celda nunca se ha usado
            } else if (e.register == null && e.isAvailable) {
                //imprime la palabra (vacio)
                System.out.println("(vacío)");
            //Caso 3: la celda ha sido ocupada y luego borrada (tombstone)
            } else { //e.register == null pero e.isAvailable == false
                //imprime (borrado)
                System.out.println("(borrado)");
            }
        }
    }
}

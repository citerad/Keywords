/**
 *
 */
package metodologie.progetto.sbe;
//import java.lang.Boolean;

/**
 * Questa classe permette di valutare String Boolean Expressions, in breve SBE.
 *
 * Le SBE possono essere definite induttivamente:
 *
 *   * una qualsiasi stringa non vuota che non contiene i caratteri '(', ')',
 * '&', '|' e che non inizia n� termina con il carattere spazio � una SBE
 * atomica; * se E e F sono SBE allora anche (E & F) e (E | F) sono SBE.
 *
 * Una volta che i valori booleani delle SBE atomiche che occorrono in una SBE
 * sono dati, il valore booleano dell'intera espressione � determinato.
 *
 * @author Domenico Citera
 */
public class StrBoolExpr {
    /* La stringa contenente la (presunta) SBE.*/

    private String esp;
    /* Il valutatore di SBE atomiche.*/
    private SBEAtom sbe;

    /**
     * Crea un oggetto relativo alla (presunta) SBE nella stringa expr e il
     * valutatore di SBE atomiche atom.
     *
     * @param expr una stringa contenente una (presunta) SBE.
     * @param atom un valutatore di SBE atomiche.
     */
    public StrBoolExpr(String expr, SBEAtom atom) {
        esp = expr;
        sbe = atom;
    }

    /**
     * Fa un controllo sintattico della espressione dell'oggetto: parentesi,
     * operatori, espressioni atomiche. Se il controllo ha successo ritorna 0,
     * altrimenti ritorna 1 + j, dove j � l'indice del primo carattere della
     * stringa che provoca un errore sintattico.
     *
     * @return 0 se il controllo ha successo e un valore positivo altrimenti.
     */
    public int check() {
        if (esp.startsWith(" ") || esp.endsWith(" ")) {
            return 1;
        }
        if (esp.contains("(") || esp.contains(")")) { //se contiene almeno una parentesi
            String espr = esp;	//creiamo una copia dell'espressione
            int open = 0;		//contiamo il numero di parentesi aperte e chiuse
            for (int i = 0; i < espr.length(); i++) {
                if (espr.charAt(i) == '(') {
                    open++;
                }
                if (espr.charAt(i) == ')') {
                    open--;
                }
            }
            if (open != 0) {
                return 1;		//se non combaciano l'espressione non � ben formata
            }
            int end = 0, start = espr.length();	//da end iniziamo a cercare 
            while (end != espr.length() - 1 && start != 0) {
                end = espr.indexOf(')', end);	//la prima parentesi chiusa e la mettiamo in end
                start = espr.lastIndexOf('(', end);	//e in start la prima parentesi aperta
                if (start == -1 || end == -1) {
                    return 1;	//se una di queste non � stata trovata l'ordine potrebbe essere invertito quindi non � ben formata
                }
                String temp = espr.substring(start, end + 1);	//altrimenti in temp il contenuto di una nidificazione
                String x = "";	//creiamo una stringa che sotituir� tale nidificazione nell'espressione in modo tale da 
                for (int i = 0; i < temp.length(); i++) {
                    x += "x";	//ricordarci che � stata valutata e da non perdere la dimensione effettiva dell'espressione
                }
                String[] ar = temp.split("\\s\\&\\s|\\s\\|\\s");	//divido la stringa in due parti mettendo gli operandi che contiene in un array
                if (ar.length == 2) { //se contiene due operandi
                    if (!ar[0].matches("^\\(\\!?[^!\\s&|()][^!&|()]*[^!\\s&|()]$|^\\(\\!?[^!\\s&|()]$")) {
                        return start + 1; //e il primo non � una sbe atomica ritorna l'indice del primo operando
                    }
                    if (!ar[1].matches("^\\!?[^!\\s&|()][^!&|()]*[^!\\s&|()]\\)$|^\\!?[^!\\s&|()]\\)$")) {
                        return start + ar[0].length() + 3;	//se il secondo � compatibile ritorna l'indice del secondo operando
                    }
                } else if (ar.length > 2) //altrimenti se contiene pi� di due operandi
                {
                    return (start + ar[0].length() + 3 + ar[1].length()); //l'espressione � mal formata, e ritorna l'indice del terzo operando
                } else {
                    return start + 1; //altrimenti avr� zero o un operando e l'espressione � mal formata.
                }
                espr = espr.substring(0, start) + x + espr.substring(end + 1, espr.length()); //,modifico la stringa mettendo x+ al posto della nidificazione analizzata 
                if ((start == 0 || end == espr.length() - 1) && !espr.matches("x+")) {
                    return 1;	//se uno degli indici arriva al termine dell'espressione e questa non � stata interamente analizzata allora � mal formata
                }
            }
        } else if (!esp.matches("^\\!?[^!\\s&|()][^!&|()]*[^!\\s&|()]$|^\\!?[^!\\s&|()]$")) {
            return 1; //se non contiene parentesi ma caratteri non consentiti ritorna 1
        }
        return 0;
    }

    /**
     * Ritorna il valore della SBE.
     *
     * @return il valore della SBE.
     * @throws IllegalStateException se l'SBE non � corretta.
     */
    public boolean eval() {
        int ris = check(); //controlliamo se l'espressione � ben formata
        if (ris != 0) {
            throw new IllegalStateException("	Espressione non valida al punto " + ris);
        }
        String temp = "", ex = esp;	//variabili di appoggio
        if (esp.contains("(")) { //se l'espresisone contiene parentesi
            String[] arr = esp.split("\\s\\|\\s|\\s\\&\\s");	//mettiamo in un arrai tutti gli operandi
            for (String a : arr) {
                a = a.replaceAll("\\(|\\)", "");	//togliamo le parentesi
                ex = ex.replaceAll("\\(" + a + "\\s\\&", "(" + sbe.eval(a) + " &");	//sostituiamo all'operando il suo valore
                ex = ex.replaceAll("\\(" + a + "\\s\\|", "(" + sbe.eval(a) + " |");	//modificando la stringa ausiliaria
                ex = ex.replaceAll("\\&\\s" + a + "\\)", "& " + sbe.eval(a) + ")");
                ex = ex.replaceAll("\\|\\s" + a + "\\)", "| " + sbe.eval(a) + ")");
            }
            int end = 0, start = ex.length();
            while (end != ex.length() - 1 && start != 0) {	//per ogni nidificazione
                end = ex.indexOf(')', end);
                start = ex.lastIndexOf('(', end);
                temp = ex.substring(start, end + 1);
                String val = "";	//variabile che contiene il valore di ogni nidificazione
                String[] ar = temp.split("\\s\\&\\s|\\s\\|\\s");	//mettiamo in un array gli operandi
                Boolean a = new Boolean(ar[0].substring(1)); //li trasformiamo in tipo booleano
                Boolean b = new Boolean(ar[1].substring(0, ar[1].length() - 1));
                if (temp.contains("|")) //se i due operandi sono sottoposti ad un or
                {
                    val = new Boolean(a | b).toString(); //metti il risultato in val trasformandolo in stringa
                } else if (temp.contains("&")) //se sono sottoposti ad un and
                {
                    val = new Boolean(a & b).toString();	//metti il risultato in val trasformandolo in stringa
                }
                ex = ex.substring(0, start) + val + ex.substring(end + 1, ex.length()); //modifica la stringa mettendo al posto della nidificazione il suo valore
                end = end - temp.length() + val.length();	//modifica gli indici in modo tale da avere sempre una dimensione giusta
            }
            Boolean b = new Boolean(ex);	//trasforma il risultato finale in boolean e restituiscilo
            return b;
        } else {
            return sbe.eval(esp);	//se � una sbe atomica restituisci il suo risultato
        }
    }
}

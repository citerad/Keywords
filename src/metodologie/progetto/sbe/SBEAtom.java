/**
 *
 */
package metodologie.progetto.sbe;

/**
 * Le ricerche di documenti effettuate mediante EBK deve basarsi sulla
 * implementazione della classe StrBoolExpr e dell'interfaccia SBEAtom. L'intera
 * implementazione della classe StrBoolExpr deve essere contenuta nel package
 * metodologie.progetto.sbe. Per potenziare le possibilit� di riuso ed
 * estensione del codice la valutazione di una EBK � stata divisa in due parti:
 * la parte che riguarda la valutazione delle EBK atomiche (interfaccia SBEAtom)
 * e la parte che riguarda la valutazione dell'espressone booleana, dopo che le
 * espressioni atomiche sono state valutate (classe StrBoolExpr). Inoltre, sia
 * l'interfaccia che la classe sono state pensate per la valutazione di
 * espressioni pi� generali delle EBK che sono state chiamate SBE (String
 * Boolean Expression). Le SBE sono definite nella documentazione della classe
 * StrBoolExpr. La connessione tra EBK e SBE � spiegata nei dettagli alla fine
 * di questa sezione.
 *
 * @author Domenico Citera
 */
public interface SBEAtom {

    /**
     * Ritorna true se la stringa atom contiene una SBE atomica ammissibile.
     * Altrimenti ritorna false.
     *
     * @param atom una stringa.
     * @return true se � una stringa atomica ammissibile
     *
     */
    boolean check(String atom);

    /**
     * Ritorna il valore boolean della SBE atomica atom. Se non � una SBE
     * atomica ammissibile lancia l'eccezione IllegalArgumetnException.
     *
     * @param atom una stringa
     * @return il valore della SBE atomica.
     * @throws IllegalArgumentException se non � una SBE atomica ammissibile.
     */
    boolean eval(String atom);

}

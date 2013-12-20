package metodologie.progetto.sbe;

import java.io.FileNotFoundException;
import metodologie.progetto.classif.*;
import metodologie.progetto.kh.*;

/**
 * Implementazione dell'interfaccia SBEAtom. Controlla e valuta le EBK atomiche.
 *
 * @author Domenico Citera
 */
public class EBKAtomic implements SBEAtom {
    /* Contiene la gerarchia di keywords.*/

    private KeywordHierarchy keyh;
    /* Contiene la classificazione.*/
    private Classification clas;
    /* il codice dell'elemento su cui verr� effettuata la valutazione.*/
    private String codEl;

    public EBKAtomic(KeywordHierarchy keyw, Classification classif, String codE) {
        keyh = keyw;
        clas = classif;
        codEl = codE;
    }

    /**
     * Ritorna true se la stringa atom contiene una EBK atomica ammissibile.
     * Altrimenti ritorna false.
     *
     * @param atom una stringa.
     * @return true se � una stringa atomica ammissibile
     */
    public boolean check(String atom) {
        try {
            if (atom.startsWith("!")) { 	//se l'atomo inizia con negato
                if (keyh.codeFromPath(atom.substring(1)) == null && //lo tolgo e controllo se � presente nella gerarchia
                        keyh.codeFromName(atom.substring(1)).length == 0) {
                    return false;
                }
            } else {
                if (keyh.codeFromPath(atom) == null && //altrimenti controllo se � presente nella gerarchia
                        keyh.codeFromName(atom).length == 0) {
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("	File non accessibile.");
            System.exit(1);
        }
        return true;
    }

    /**
     * Ritorna il valore boolean della SBE atomica atom. Se non � una EBK
     * atomica ammissibile lancia l'eccezione IllegalArgumetnException.
     *
     * @param atom una stringa
     * @return il valore della EBK atomica.
     * @throws IllegalArgumentException se non � una SBE atomica ammissibile.
     */
    public boolean eval(String atom) {
        try {		//se non � ammissibile lancio un eccezione
            if (!check(atom)) {
                throw new IllegalArgumentException("\n	SBA atomica '" + atom + "' non ammissibile");
            }
            String[] codatom = new String[1];	//conterra il o i codici dell'atomo con quel nome
            if (atom.startsWith("!")) {	//se inizia con la negazion
                if (atom.indexOf(KeywordHierarchy.PathSep) != -1) //mettiamo i codici nell'array
                {
                    codatom[0] = keyh.codeFromPath(atom.substring(1));
                } else {
                    codatom = keyh.codeFromName(atom.substring(1));
                }
                String[] S = clas.keywords(codEl);	//array che contiene i codici dei file associati
                if (S != null) {	//se ci sono file associati
                    for (int i = 0; i < S.length; i++) {	//controlla che almeno uno di questi
                        for (int j = 0; j < codatom.length; j++) {	//sia la stessa o una subk. 
                            if (codatom[j] == S[i]) {
                                return false;	// se � cos� ritorna false
                            }
                            if (keyh.isSub(S[i], codatom[j])) {
                                return false;
                            }
                        }
                    }
                    return true;	//altrimenti true
                }
            } else {	//se non � negata
                if (atom.indexOf(KeywordHierarchy.PathSep) != -1) //controlla se � una path o un nome
                {
                    codatom[0] = keyh.codeFromPath(atom);	//estrai i codici e mettili in codatom
                } else {
                    codatom = keyh.codeFromName(atom);
                }
                String[] S = clas.keywords(codEl);	//contine i codidi dei file associati
                if (S != null) {	//se esistono dei file associati
                    for (String b : S) {	//per oguno di questi controlla che
                        for (String a : codatom) {
                            if (a == b) {
                                return true; 	//sia la stessa k. o sia una superk. o una subk.
                            }
                            if (keyh.isSub(b, a)) {
                                return true;	//se � cos� ritorna true
                            }
                            if (keyh.isSuper(b, a)) {
                                return true;
                            }
                        }
                    }
                    return false;	//altrimenti ritorna false
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("	File non accessibile.");;
            System.exit(1);
        }
        return false;
    }
}

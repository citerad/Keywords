package metodologie.progetto.menu;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Iterator;
import static java.lang.System.out;

import metodologie.progetto.classif.Classification;
import metodologie.progetto.kh.KeywordHierarchy;
import metodologie.progetto.sbe.EBKAtomic;
import metodologie.progetto.sbe.StrBoolExpr;

/**
 * Gestisce tutte le operazioni possibili su una coppia di gerachia e
 * classificazione.
 *
 * @author Domenico Citera
 */
public class KeyOperations {
    /* Scanner input*/

    private Scanner in = new Scanner(System.in);
    /* Contiene il path assoluto*/
    private String PathAss = "";
    /* Contiene la gerarchia di keywords*/
    private KeywordHierarchy key = null;
    /* Contiene la classificazione*/
    private Classification classif = null;

    /**
     * Crea un men� contenente tutte le possibili scelte che l'utente pu�
     * effettuare all'apertura dell'applicazione.
     *
     * @throws FileNotFoundException se i file non sono accessibili
     */
    public void Menu() throws FileNotFoundException {	//menu principale
        out.println();
        out.println("		1. Imposta la directory corrente");	//creo un menu di scelta
        out.println("		2. Creazione di una nuova gerarchia di Keywords");
        out.println("		3. Apri una gerarchia di keywords");
        out.println("		4. Crea una classificazione (E' necessario aver creato una gerarchia di keywords)");
        out.println("		5. Gestisci gerarchia aperta");
        out.println("		6. Gestisci classificazione aperta");
        out.println("		7. Esci");
        int choice = in.nextInt();
        in.nextLine();		//necessario per permettere gli altri nextLine
        if (choice == 3) {
            choice = 2;	//L'operazione 2 e 3 sono uguali poich� se il file non esiste sar� creato
        }
        switch (choice) {
            case 1: {
                out.println("	Inserisci il pathname assoluto");
                PathAss = in.nextLine();
                Menu();
                break;
            }
            case 2: {
                out.println("	Indica un pathname relativo (Invio per non darne)");
                String rel = "";	//il path relativo vuoto
                rel = in.nextLine();	//catturo il path
                out.println("	Digita il nome della gerarchia");
                String name = in.nextLine(); //catturo il nome
                if (name.equals("")) {	//se il nome non contiene caratteri
                    out.println("	Nome non valido");	//non � valido e ritorniamo al menu
                    Menu();
                    break;
                }	//altrimenti
                try {
                    key = new KeywordHierarchy(PathAss + rel, name); //creo la gerarchia seguendo il path assoluto+il relativo
                } catch (IllegalArgumentException e) {
                    out.println("	File esistente e non compatibile");
                }
                openmenukey(); 	//apriamo il menu per gestire la gerarchia
                break;
            }
            case 4: {
                if (key == null) {	//se una gerarchia non � stata aperta/creata
                    out.println("	Non � stata aperta nessuna gerarchia");
                    Menu();	//torniamo al menu principale
                    break;
                }	//altrimenti
                out.println("	Indica un pathname relativo (Invio per non darne)");
                String rel = "";	//il path relativo vuoto
                rel = in.nextLine();	//catturo il path
                out.println("	Digita il nome della Classificazione");
                String name = in.nextLine(); //catturo il nome
                if (name.equals("")) {
                    out.println("	Nome non valido");
                    Menu();
                    break;
                }
                try {
                    classif = new Classification(PathAss + rel, name, key);
                } catch (IllegalArgumentException e) {
                    System.out.println("	La classificazione esiste, ma non � compatibile con un file "
                            + "di Classificazione o con la gerarchia aperta");
                }
                openmenuclass();
                break;
            }
            case 5: {
                openmenukey();	//menu di gestione della classificazione
                break;
            }
            case 6: {
                openmenuclass();	//menu di gestione delle keywords
                break;
            }
            case 7: {
                if (classif != null) {
                    classif.close();
                }
                if (key != null) {
                    key.close();
                }
                System.exit(0);	//usciamo senza segnalare errori
            }
            default: {
                out.println("	Scelta non consentita");
                Menu();
                break;
            }

        }
    }

    /**
     * Sottomenu del menu principale. Questo viene generato quando si vuole
     * lavorare su una gerarchia.
     *
     * @throws FileNotFoundException se i file non sono accessibili
     */
    private void openmenukey() throws FileNotFoundException {	//menu di gestione delle keywords
        if (key != null) {
            out.println();
            out.println("		1. Visualizza tutti i nomi delle sub-keywords dirette di una keyword"); //creo un sottomenu
            out.println("		2. Visualizza il nome della super-keyword diretta di una keyword");
            out.println("		3. Aggiungi una nuova keyword alla gerarchia");
            out.println("		4. Cambia nome ad una keyword");
            out.println("		5. Apri una classificazione esistente");
            out.println("		6. Menu principale");
            int choice = in.nextInt();
            in.nextLine(); //necessario per permettere gli altri nextLine
            switch (choice) {
                case 1: {
                    out.println("	Specifica il nome della keyword o indica un path-keyword"
                            + "\n	'ANYTHING' per visualizzare le radici");
                    String name = in.nextLine();
                    String code = null;
                    if (name.indexOf(KeywordHierarchy.PathSep) != -1) //se � un pathname
                    {
                        code = key.codeFromPath(name);	//estrai il codice
                    } else {	//altrimenti sar� un nome
                        String[] arraycode = key.codeFromName(name);	//estrai i codici
                        if (arraycode.length != 0) {	//se l'array contiene almeno un codice
                            int index = 0;	//salviamo l'indice 0(poich� se ce n'� soltanto uno sar� sicuramente alla posizione 0)
                            if (arraycode.length > 1) {	//altrimenti se contine pi� di un codice visualizzali e permetti la scelta
                                out.println("	Sono presenti piu' keyword con nome '" + name + "', scegliere un path-keyword");
                                for (int i = 0; i < arraycode.length; i++) {
                                    out.println("	" + (i + 1) + ". " + key.path(arraycode[i]));
                                }
                                do {	//chiedi la scelta
                                    index = in.nextInt() - 1;
                                    in.nextLine();	//necessario per i prossimi nextLine
                                    if (index < 0 || index >= arraycode.length) {
                                        out.println("	Scelta non valida, riprova");
                                    }
                                } while (index < 0 || index >= arraycode.length);	//finch� l'indice non � corretto
                            }
                            code = arraycode[index]; //metti i sub-codici nell'array
                        }
                    }
                    navigasub(code);
                    openmenukey();	//ritorna al menu di gestione k
                    break;
                }
                case 2: {
                    out.println("	Specifica il nome della keyword o indica un path-keyword");
                    String name = in.nextLine();
                    String code = null;
                    if (name.indexOf(KeywordHierarchy.PathSep) != -1) //se � un pathkeyword
                    {
                        code = key.codeFromPath(name);	//estrai il codice 
                    } else { //altrimenti sar� una stringa
                        String[] arraycode = key.codeFromName(name); //metti in un array tutti i codici con quel nome
                        if (arraycode.length > 0) {	//se l'array contiene almeno un codice
                            int index = 0;	//memorizziamo l'indice 0 (poich� se ce ne sar� uno soltanto sicuramente sar� nella posizione 0)
                            if (arraycode.length > 1) { //se invece ce ne sono pi� di uno li stampiamo a video e consentiamo la scelta
                                out.println("	Sono presenti piu' keyword con nome '" + name + "', scegliere un path-keyword");
                                for (int i = 0; i < arraycode.length; i++) {
                                    out.println("	" + (i + 1) + ". " + key.path(arraycode[i]));
                                }
                                do {	//chiedi la scelta
                                    index = in.nextInt() - 1;
                                    in.nextLine();	//necessario per i prossimi nextLine
                                    if (index < 0 || index >= arraycode.length) {
                                        out.println("	Scelta non valida, riprova");
                                    }
                                } while (index < 0 || index >= arraycode.length);	//finch� l'indice non � corretto
                            }
                            code = arraycode[index]; //mettiamo il supercodice in superkcode
                        }
                    }
                    navigasuper(code);
                    openmenukey();	//ritorna al menu di gestione k
                    break;
                }
                case 3: {
                    out.println("	Digita il nome della nuova keyword");
                    String newk = in.nextLine();
                    out.println("	Digita il nome di una keyword esistente che sar� la sua super-keyword\n"
                            + "	Digita 'ANYTHING' se la nuova keyword sar� una radice");
                    String sup = in.nextLine();
                    try {
                        if (aggiungi(newk, sup)) {
                            out.println("	Inserimento eseguito");	//prova ad aggiungere
                        } else {
                            out.println("	Inserimento fallito: keyword gi� esistente, nome non valido o super-keyword non trovata");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("	Non c'� spazio per nuove keywords!");
                    }
                    openmenukey();	//ritorna al menu di gestione k
                    break;
                }
                case 4: {
                    out.println("	Digita il nome della keyword a cui vuoi cambiare il nome");
                    String oldname = in.nextLine();
                    String[] arraycode = key.codeFromName(oldname);	//estraggo i codici delle k con quel nome
                    if (arraycode.length == 0) {
                        out.println("	Non ci sono keyword con questo nome");	//se la lunghezza � 0 la k non esiste
                    } else {	//altrimenti
                        int index = 0;	//ce ne sar� almeno una quindi salvo il primo indice
                        if (arraycode.length > 1) {	//se ce ne sono pi� di una 
                            out.println("	Sono presenti piu' keyword con nome '" + oldname + "', scegliere un path-keyword");
                            for (int i = 0; i < arraycode.length; i++) //le visualizzo
                            {
                                out.println("	" + (i + 1) + ". " + key.path(arraycode[i]));
                            }
                            index = in.nextInt() - 1;	//salvo l'indice dell k scelta
                            in.nextLine();	//necessario per i prossimi nextLine
                        }
                        out.println("	Indica il nuovo nome");
                        String newname = in.nextLine();		//controllo s e� possibile sostituire il nome
                        if (key.replaceName(arraycode[index], newname)) {
                            out.println("	Operazione eseguita");
                        } else {
                            out.println("	Non consentito");
                        }
                    }
                    openmenukey();	//ritorno al menu di gestione k.
                    break;
                }
                case 5: {
                    out.println("	Indica un pathname relativo (Invio per non darne)");
                    String rel = "";	//il path relativo vuoto
                    rel = in.nextLine();	//catturo il path
                    out.println("	Digita il nome della Classificazione");
                    String name = in.nextLine(); //catturo il nome
                    if (!name.equals("")) {
                        try {
                            classif = new Classification(PathAss + rel, name, key);
                        } catch (IllegalArgumentException e) {
                            System.out.println("	La classificazione esiste, ma non � compatibile con un file di Classificazione o con la gerarchia aperta");
                        }
                        openmenuclass();	//apro il meno di gestione classificazioni
                        break;
                    } else {
                        out.println("	Nome non valido");
                    }
                    openmenukey();	//altrimeni il menu di gestione k.
                    break;
                }
                case 6: {
                    Menu();
                    break;
                }
                default: {
                    out.println("	Scelta non consentita");
                    openmenukey();
                    break;
                }
            }
        } else {
            out.println("	Nessuna gerarchia aperta");
            Menu();
        }
    }

    /**
     * Sottomenu del menu principale e delle gerarchie. Questo viene generato
     * quando si vuole lavorare su una classificazione.
     *
     * @throws FileNotFoundException se i file non sono accessibili.
     */
    private void openmenuclass() throws FileNotFoundException {	//menu classificazioni
        if (classif != null) {	//se � stata aperta una classificazione
            out.println();
            out.println("		1. Visualizza la lista delle keywords associate ad un documento"); //creo un sottomenu
            out.println("		2. Mostra il pathname di tutti i documenti della classificazione");
            out.println("		3. Aggiungere un documento alla Classificazione");
            out.println("		4. Associa una keyword ad un documento o rimuovine una");
            out.println("		5. Ricerca documenti tramite EBK");
            out.println("		6. Menu principale");
            int choice = in.nextInt();
            in.nextLine(); //necessario per permettere gli altri nextLine
            switch (choice) {
                case 1: {
                    out.println("	Specifica un documento");
                    String name = in.nextLine();
                    String code = classif.code(name);	//estraggo il codice
                    if (code == null) {
                        out.println("	Il documento non esiste");	//se code vale null non ci sono documenti con quel nome
                    } else {
                        String[] array = null;
                        array = classif.keywords(code);	//altrimenti metto in un array i codici delle k associate
                        if (array.length == 0) {
                            out.println("	Il documento non ha keyword associate");	//se lunghezza zero non esistono
                        } else {	// altrimenti le stampo
                            out.println("	Le keyword associate al documento sono \n");
                            for (String a : array) {
                                out.println("		" + key.name(a));
                            }
                        }
                    }
                    openmenuclass();
                    break;
                }
                case 2: {
                    Iterator<String> ite = classif.iterator();	//creo un iteratore sui codici degli identificatori
                    int c = 1;	//conta il numero di identificatori
                    String S = "";	//contiene la risposta
                    while (ite.hasNext() && S.equals("")) {	//finch� la risposta � c e ci sono altri codici
                        int i = 0;
                        String[] code = new String[15];	//creiamo un array per contenere i codici nel caso la risposta sia S
                        while (ite.hasNext() && i < 15) {	//mentre ci sono altri codici ne stampiamo al max 15
                            code[i] = ite.next();
                            out.println("		" + (i + 1) + ". (" + c + ") " + classif.id(code[i]));	//i riferito alla scelta c per numerarli
                            i++;
                            c++;
                        }
                        if (ite.hasNext()) {		//se ci sono altri codici visualizziamo l'opzione per stamparne altri
                            out.println("\n	 Invio per visualizzare la prossima pagina");
                        }		//e in oqni caso le altre due opzioni
                        out.println("	'S' per selezionarne uno e vedere la lista delle keywords\n"
                                + "	'Q' per terminare la visualizzazione");
                        S = in.nextLine();		//gestiamo la risposta
                        if (S.equals("Q") || S.equals("q")) {
                            break;
                        }
                        if (S.equals("S") || S.equals("s")) {
                            out.println("\n	Seleziona una keyword");
                            int set = in.nextInt();
                            in.nextLine();
                            if (set > code.length || set < 1) {	//se la scelta non � tra quelle disponibili
                                out.println("	Scelta non corretta");
                                openmenuclass();
                                break;
                            }	//altrimenti visualizziamo il risultato
                            String[] array = classif.keywords(code[set - 1]);
                            if (array.length == 0) {
                                out.println("	Il documento non ha keyword associate");
                            } else {
                                out.println("	Le keyword associate al documento sono \n");
                                for (String a : array) {
                                    out.println("		" + key.name(a));
                                }
                            }
                        }
                    }
                    openmenuclass();
                    break;
                }
                case 3: {
                    out.println("	Digita il suo pathname");
                    String id = in.nextLine();
                    try {
                        if (classif.add(id) != null) {
                            out.println("	Eseguito correttamente");
                        } else {
                            out.println("	Non eseguito");
                        }
                    } catch (IllegalArgumentException e) {
                        out.println("	Non c'� spazio per nuovi identificatori");
                    }
                    openmenuclass();
                    break;
                }
                case 4: {
                    out.println("	Digita il nome del documento");
                    String id = in.nextLine();
                    String code = classif.code(id);
                    if (code == null) {
                        out.println("	Il documento non esiste");	//se code � nullo il documento non esiste
                    } else {
                        String[] array = null;
                        array = classif.keywords(code);	//altrimenti metto in un array i codici delle k associate
                        if (array.length == 0) {
                            out.println("	Il documento non ha keyword associate");	//se lunghezza zero non esistono k associate
                        } else {	// altrimenti le stampo
                            out.println("	Le keyword associate al documento sono \n");
                            for (int i = 0; i < array.length; i++) {
                                out.println("		" + (i + 1) + ". " + key.name(array[i]));
                            }
                            out.println("	1. Rimuovi una keyoword");	//se ne esiste almeno una stampo questa opzione
                        }	//in ogni caso stampo le altre opzioni
                        out.println("	2. Aggiungi una keyword\n"
                                + "	3. Indietro");
                        int C = in.nextInt();
                        in.nextLine();
                        if (C == 1 && array.length != 0) {
                            out.println("	Seleziona una keyword");
                            int K = in.nextInt();
                            in.nextLine();
                            if (K > 0 && K <= array.length && classif.removeKeyword(code, array[K - 1])) //controllo che la scelta sia corretta
                            {
                                out.println("	Operazione effettuata correttamente");	//e che sia stato possibile rimuoverla
                            } else {
                                out.println("	Scelta non corretta");
                            }
                        } else if (C == 2) {
                            out.println("	Digita la keyword da associare");
                            String ass = in.nextLine();
                            String[] arraycode = key.codeFromName(ass);	//estrai i codici
                            if (arraycode.length != 0) {	//se l'array contiene almeno un codice
                                int index = 0;	//salviamo l'indice 0(poich� se ce n'� soltanto uno sar� sicuramente alla posizione 0)
                                if (arraycode.length > 1) {	//altrimenti se contine pi� di un codice visualizzali e permetti la scelta
                                    out.println("	Sono presenti piu' keyword con nome '" + ass + "', scegliere un path-keyword");
                                    for (int i = 0; i < arraycode.length; i++) {
                                        out.println("	" + (i + 1) + ". " + key.path(arraycode[i]));
                                    }
                                    index = in.nextInt() - 1;
                                    in.nextLine();	//necessario per i prossimi nextLine	
                                }	//controllo che l'indice sia corretto e che sia stato possibile aggiungerla
                                if (index > -1 && index < arraycode.length && classif.addKeyword(code, arraycode[index])) {
                                    out.println("	Operazione eseguita correttamente");
                                } else {
                                    out.println("	Operazione respinta.");
                                }
                            } else {
                                out.println("	Non esistono keywords con nome '" + ass + "'");
                            }
                        }
                    }
                    openmenuclass();
                    break;
                }
                case 5: {
                    out.println("	Indica l'espresisone booleana (Deve essere ben formata)"); //es. ((A | B) & (C | D))
                    String exp = in.nextLine();
                    System.out.println("\n	Documenti selezionati:\n");
                    Iterator<String> ite = classif.iterator();	//creo un iteratore per scorrere gli elementi
                    //boolean t=false;	//flag per memorizzare se ci sono associazioni ai documenti
                    int c = 0;	//memorizzare se ci sono associazioni ai documenti
                    try {
                        while (ite.hasNext()) {
                            String cod = ite.next();	//memorizzo il prossimo codice
                            EBKAtomic atom = new EBKAtomic(key, classif, cod);
                            StrBoolExpr str = new StrBoolExpr(exp, atom);
                            if (str.eval()) {	//valuto l'espressione su quel codice
                                c++;
                                out.println("		" + classif.id(cod));	//e lo stampo
                            }
                        }
                        if (c == 0) {
                            out.println("\n	Non ci sono documenti associati");
                        } else {
                            out.println("\n	Tot.: " + c);
                        }
                    } catch (IllegalStateException e) {
                        out.println(e.getMessage());
                    }
                    openmenuclass();
                    break;
                }
                case 6: {
                    Menu();
                    break;
                }
                default: {
                    out.println("	Scelta non consentita");
                    openmenuclass();
                    break;
                }
            }
        } else {
            out.println("	Nessuna classificazione aperta");
            Menu();
        }
    }

    /**
     * Consente di navigare tra le sub-keywords. Dato un codice di una keywords
     * stamper� tutte le sub-keywords dirette di quella keyword.
     *
     * @param code il codice di una keywords
     * @throws FileNotFoundException se il file non � accessibile
     */
    private void navigasub(String code) throws FileNotFoundException {	// in questo modo passando il codice e non il nome, se ci sono keyword di livello
        //superiore con lo stesso nome evitiamo che provenendo dalla k di livello inferiore venga chiesto quale bisogna scegliere
        String[] arraysub = null;	//contiene tutti i codice delle sub-k.
        if (code != null) {
            arraysub = key.subK(code);
        }
        if (arraysub != null) {	//se l'array non � null
            if (arraysub.length == 0) {
                out.println("	La keyword '" + key.name(code) + "' non ha sub-keyword dirette"); //ma � di lunhgezza 0 allora la keyword non ha sub-k.
            } else { //se non � nullo ma ha almeno un elemento stampa il suo contenuto
                out.println("	Le sub-keyword dirette della keyword '" + key.name(code) + "' sono:");
                for (int i = 0; i < arraysub.length; i++) {
                    out.println("	" + (i + 1) + ". " + key.name(arraysub[i]));
                }
                out.println();	//stampa un menu che consente di scegliere cosa fare	
                out.println("		1. Visualizza le sub-keyword dirette");
                out.println("		2. Visualizza le super-keyword dirette");
                out.println("		3. Indietro");
                int choice = in.nextInt();
                in.nextLine();		//necessario per permettere gli altri nextLine
                int index = 0;
                if ((choice == 1 || choice == 2) && arraysub.length > 1) {
                    do {	//chiedi la scelta
                        out.println("	Scegli una keyword");
                        index = in.nextInt() - 1;
                        in.nextLine();	//necessario per i prossimi nextLine
                        if (index < 0 || index >= arraysub.length) {
                            out.println("	Scelta non valida, riprova");
                        }
                    } while (index < 0 || index >= arraysub.length);	//finch� l'indice non � corretto
                }
                if (choice == 1) {
                    navigasub(arraysub[index]);
                } else if (choice == 2) {
                    navigasuper(arraysub[index]);
                }
            }
        } else {
            out.println("	La keyword non esiste");
        }
    }

    /**
     * Consente di navigare tra le super-keywords. Dato un codice di una
     * keywords stamper� la sua super-keywords diretta di quella keyword.
     *
     * @param code il codice di una keywords
     * @throws FileNotFoundException se il file non � accessibile
     */
    private void navigasuper(String code) throws FileNotFoundException {
        String superkcode = null;	//contiene il supercodice della keyword code
        if (code != null) {
            superkcode = key.superK(code);
        }
        if (superkcode != null) { //se � stato trovato un supercodice
            if (key.name(superkcode) == null) {
                out.println("	La keyword '" + key.name(code) + "' � una radice");	//ma non vi sono nomi associati a esso vuol dire che � la keyword ANYTHING
            } else { //altrimenti se non � nullo stampa il supercodice e un menu che permette di scegliere cosa fare
                out.println("	La super-keyword diretta della keyword '" + key.name(code) + "' �\n\n		" + key.name(superkcode));
                out.println();
                out.println("		1. Visualizza le sub-keyword dirette");
                out.println("		2. Visualizza le super-keyword dirette");
                out.println("		3. Indietro");
                int choice = in.nextInt();
                in.nextLine();		//necessario per permettere gli altri nextLine
                if (choice == 1) {
                    navigasub(superkcode);
                } else if (choice == 2) {
                    navigasuper(superkcode);
                }
            }
        } else {
            out.println("	La keyword non esiste");
        }
    }

    /**
     * Permette di aggiungere una keywords alla gerarchia. Dato il nome della
     * nuova keyword e quello della sua super-keyword stamper� tutti i
     * path-keyword delle super-keywords, e permetter� la scelta.
     *
     * @param newk la nuova keyword.
     * @param sup la super-keyword della nuova keyword.
     * @return	true se non ci sono errori, false altrimenti.
     * @throws FileNotFoundException se il file non � accessibile
     */
    private boolean aggiungi(String newk, String sup) throws FileNotFoundException {
        if (!sup.equals("ANYTHING")) {
            String[] arraycode = key.codeFromName(sup);	//estrai i codici
            System.out.println(arraycode[0]);
            if (arraycode.length != 0) {	//se l'array contiene almeno un codice
                int index = 0;	//salviamo l'indice 0(poich� se ce n'� soltanto uno sar� sicuramente alla posizione 0)
                if (arraycode.length > 1) {	//altrimenti se contine pi� di un codice visualizzali e permetti la scelta
                    out.println("	Sono presenti piu' keyword con nome '" + sup + "', scegliere un path-keyword");
                    for (int i = 0; i < arraycode.length; i++) {
                        out.println("	" + (i + 1) + ". " + key.path(arraycode[i]));
                    }
                    do {	//chiedi la scelta
                        index = in.nextInt() - 1;
                        in.nextLine();	//necessario per i prossimi nextLine
                        if (index < 0 || index >= arraycode.length) {
                            out.println("	Scelta non valida, riprova");
                        }
                    } while (index < 0 || index >= arraycode.length);	//finch� l'indice non � corretto
                } // prova a inserire la nuva keyword
                if (key.add(key.codeFromPath(key.path(arraycode[index])), newk) != null) {
                    return true;
                }
            }
        } else if (key.add(sup, newk) != null) {
            return true;
        }
        return false;
    }
}

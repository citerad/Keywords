package metodologie.progetto.classif;

import metodologie.progetto.kh.*;
import java.io.*;
import java.util.*;

/**
 * Questa classe gestisce Classificazioni che sono matenute in files che
 * obbediscono a un opportuno formato. La classe opera in congiunzione con la
 * classe metodologie.progetto.kh.KeywordHierarchy. La natura degli elementi di
 * una Classificazione non � specificata (possono essere files, siti web,
 * oggetti fisici come libri o dvd, ecc.). Ogni elemento � identificato da una
 * stringa chiamata appunto l'identificatore dell'elemento (se gli elementi sono
 * files l'identificatore potrebbe essere il pathname, se sono siti web potrebbe
 * essere l'URL, se sono libri l'identificatore potrebbe essere la
 * collocazione).
 *
 * Siccome gli identificatori possono essere stringhe piuttosto lunghe e in
 * vista di possibili estensioni che permettano di gestire anche link tra
 * elementi, conviene assegnare ad ogni elemento un codice, ovviamente univoco.
 * Il codice � composto da CODELENGTH caratteri appartenenti all'insieme
 * {'a','b',...,'z','A','B',...'Z','0','1',...,'9'}.
 *
 * Una Classificazione � mantenuta in due files. Il file degli elementi con nome
 * name.ELM e il file delle keywords con nome name.KYW. La stringa name deve
 * essere comune ad entrambi i files e rappresenta il nome della
 * Classificazione. Entrambi i files sono di tipo testo e quindi sono gestiti
 * tramite accesso sequenziale. La struttura del file degli elementi � la
 * seguente. La prima linea contiene CLASSIFICATION:name:N, dove name � il nome
 * della Classificazione e N � il numero di nanosecondi ritornato dal metodo
 * System.nanoTime() nel momento in cui la Classificazione � creata. La seconda
 * linea contiene lastcode che � l'ultimo codice usato per gli elementi della
 * Classificazione. Poi c'� una linea per ogni elemento contenente code;ID, dove
 * code � il codice dell'elemento e ID � l'identificatore dell'elemento. La
 * struttura del file delle keywords � la seguente. La prima linea contiene
 * CLASSIFICATION_KEYWORDS:name:N, dove name � il nome della Classificazione N �
 * il numero di nanosecondi mantenuto nel file degli elementi. La seconda linea
 * contiene l'identificatore della gerarchia di keywords usata (cio� la stringa
 * ritornata dal metodo getID() della classe
 * metodologie.progetto.kh.KeywordHierarchy). Poi per ogni elemento che ha
 * almeno una keyword associata c'� una linea contenente code;K1;K2;...KN, dove
 * code � il codice dell'elemento e K1, K1,..., KN sono i codici delle keywords
 * associate all'elemento.
 *
 * @author Domenico Citera
 */
public class Classification implements Iterable<String> {
    /*La lunghezza dei codici delle keywords.
     * Valore di default 6.*/

    public static final int CodeLength = 6;
    /*Stream di output per scrivere nel file.*/
    private FileOutputStream OutKeyS;
    /*Flusso di stampa.*/
    private PrintStream keyStrm;
    /*Mantiene la locazione del file.*/
    private File Keyfile;
    /*Interpreta il contenuto del file. */
    private Scanner keyScan;
    /*Stream di output per scrivere nel file.*/
    private FileOutputStream OutElemS;
    /*Flusso di stampa.*/
    private PrintStream ElemStrm;
    /*Mantiene la locazione del file.*/
    private File Elemfile;
    /*Interpreta il contenuto del file. */
    private Scanner ElemScan;
    /*La gerarchia di Keywords.*/
    private KeywordHierarchy keyh;
    /* Usato per indicare la chiusura del file.
     Quando vale <code>true</code>, viene impedito l'accesso allo Scanner, che il metodo <code>rewind</code> riaprirebbe ogni qual volta chiamato.
     */
    private boolean closed = false;

    /**
     * Apre una Classificazione o ne crea una nuova. Il nome della
     * Classificazione � name e i files si trovano nella directory di pathname
     * dir. La Classificazione usa la gerarchia di keyword kh
     *
     * @param dir il pathname di una directory
     * @param name il nome di una Classificazione
     * @param kh una gerarchia di keyword.
     * @throws FileNotFoundException se uno dei files coinvolti non �
     * accessibile
     * @throws IllegalArgumentException se kh � null o uno dei files non �
     * compatibile con la struttura di un file di una Classificazione o la
     * gerarchia di keywords non � compatibile con la Classificazione (ad
     * esempio l'identificatore della gerarchia � differente da quello riportato
     * nel file delle keywords).
     */
    public Classification(String dir, String name, KeywordHierarchy kh) throws FileNotFoundException {
        Elemfile = new File(dir, name + ".ELM");
        Keyfile = new File(dir, name + ".KYW");
        if (Elemfile.exists() && Keyfile.exists()) {	//se i files esistono controlla se sono compatibile
            keyScan = new Scanner(Keyfile);		//apro lo scanner sui files preparandoli alla lettura
            ElemScan = new Scanner(Elemfile);
            keyScan.useDelimiter(":");			//delimita una parola nella riga.
            ElemScan.useDelimiter(":");
            if ((keyScan.hasNext() && ElemScan.hasNext())
                    && ElemScan.next().equals("CLASSIFICATION") && //controlliamo se i files sono validi
                    ElemScan.next().equals(name)
                    && keyScan.next().equals("CLASSIFICATION_KEYWORDS")
                    && keyScan.next().equals(name)
                    && (keyScan.hasNextLine() && ElemScan.hasNextLine())
                    && keyScan.nextLine().equals(ElemScan.nextLine())
                    && keyScan.hasNextLine()
                    && keyScan.nextLine().equals(kh.getID())) {
                OutElemS = new FileOutputStream(Elemfile, true);	//se lo sono li prepariamo alla scrittura
                ElemStrm = new PrintStream(OutElemS, true);
                OutKeyS = new FileOutputStream(Keyfile, true);
                keyStrm = new PrintStream(OutKeyS, true);
                keyh = kh;
                System.out.println("	Il file � stato aperto");
            } else {
                throw new IllegalArgumentException("File esistenti non compatibili o gerarchia non valida");
            }
        } else {		//altrimenti crea i files
            OutElemS = new FileOutputStream(Elemfile, true);
            ElemStrm = new PrintStream(OutElemS, true);
            OutKeyS = new FileOutputStream(Keyfile, true);
            keyStrm = new PrintStream(OutKeyS, true);
            long nanotime = System.nanoTime();
            ElemStrm.println("CLASSIFICATION:" + name + ":" + nanotime);
            for (int i = 0; i < CodeLength; i++) {
                ElemStrm.print("0");	//scrivo nella seconda linea il primo 'lastcode'
            }
            ElemStrm.println();
            keyStrm.println("CLASSIFICATION_KEYWORDS:" + name + ":" + nanotime);
            keyStrm.println(kh.getID());
            keyScan = new Scanner(Keyfile);		//apro lo scanner sui files preparandoli alla lettura
            ElemScan = new Scanner(Elemfile);
            keyh = kh;
            System.out.println("	Il file � stato creato");
        }
    }

    /**
     * Riporta lo Scanner alla terza riga dei files.
     *
     * @throws FileNotFoundException se il file non � accessibile.
     */
    private void rewind() throws FileNotFoundException {
        if (!closed) {		//se non � stato invocato il metodo 'close'
            keyScan.close();		//riapri lo scanner
            keyScan = new Scanner(Keyfile);
            keyScan.nextLine();		//e scendi di due righe.
            keyScan.nextLine();
            ElemScan.close();		//riapri lo scanner
            ElemScan = new Scanner(Elemfile);
            ElemScan.nextLine();		//e scendi di due righe.
            ElemScan.nextLine();
        }
    }

    /**
     * Ritorna il codice dell'elemento con identificatore id. Se non c'� nessun
     * elemento con quell'identificatore ritorna null.
     *
     * @param id l'identificatore di un elemento
     * @return il codice dell'elemento o null
     * @throws FileNotFoundException se uno dei files coinvolti non �
     * accessibile.
     */
    public String code(String id) throws FileNotFoundException {
        rewind();
        while (ElemScan.hasNextLine()) {
            String linea = ElemScan.nextLine();
            String elem = linea.substring(CodeLength + 1, linea.length()); //estraggo l'id
            if (elem.equals(id)) {
                return linea.substring(0, CodeLength); //estraggo il codice
            }
        }
        return null;
    }

    /**
     * Ritorna l'identificatore dell'elemento di codice code. Se non c'� nessun
     * elemento con quel codice ritorna null.
     *
     * @param code il codice di un elemento
     * @return l'identificatore dell'elemento o null
     * @throws FileNotFoundException se uno dei files coinvolti non �
     * accessibile
     */
    public String id(String code) throws FileNotFoundException {
        if (code.length() != CodeLength) {
            return null;
        }
        rewind();
        while (ElemScan.hasNextLine()) {
            String linea = ElemScan.nextLine();
            String cod = linea.substring(0, CodeLength); //estraggo il codice
            if (cod.equals(code)) {
                return linea.substring(CodeLength + 1, linea.length()); //estraggo l'id
            }
        }
        return null;
    }

    /**
     * Ritorna un iteratore sui codici degli elementi. L'oggetto di tipo
     * Iterator<String> ritornato permette di scorrere i codici di tutti gli
     * elementi della Classificazione (anche quelli che non hanno keywords
     * associate).
     *
     * @return un iteratore sui codici degli elementi.
     */
    public Iterator<String> iterator() {
        ArrayList<String> lista = new ArrayList<String>();	//creo un oggetto arraylist
        try {
            rewind();
            while (ElemScan.hasNextLine()) {	//scansiono il file
                String linea = ElemScan.nextLine();
                String cod = linea.substring(0, CodeLength); //estraggo il codice per ogni linea del file
                lista.add(cod);			//e lo inserisco nell'arraylist
            }
        } catch (FileNotFoundException e) {
            System.out.println("	File non accesibile");
        }
        return lista.iterator();		//ritorno l'iteratore sulla lista.
    }

    /**
     * Ritorna in un array i codici delle keywords associate all'elemento di
     * codice code. Se L'elemento non ha keywords associate ritorna un array di
     * lunghezza 0. Se l'elemento non esiste ritorna null.
     *
     * @param code il codice di un elemento
     * @return un array dei codici delle keywords associate all'elemento o null.
     * @throws FileNotFoundException se uno dei files coinvolti non �
     * accessibile
     */
    public String[] keywords(String code) throws FileNotFoundException {
        String[] arrayCode = new String[0];
        rewind();
        while (keyScan.hasNextLine()) {	//scansiono il file
            String linea = keyScan.nextLine();
            String cod = linea.substring(0, CodeLength); //estraggo il codice per ogni linea del file
            if (cod.equals(code)) {	// se sono uguali
                if (linea.length() > CodeLength) { //e contine elmeno una keyword associata
                    cod = linea.substring(CodeLength + 1, linea.length()); //tutti i codici delle keywords associate
                    arrayCode = cod.split(";");	// li separo mettendole nell'array
                    return arrayCode;	//restituisco l'array
                }
            }
        }
        Iterator<String> ite = iterator();
        while (ite.hasNext()) {
            if (code.equals(ite.next())) {
                return arrayCode;
            }
        }
        return null;	//altrimenti ritorno null
    }

    /**
     * Genera i codici per gli elementi.
     *
     * @param lastcode L'ultimo codice gi� usato.
     * @return un nuovo codice.
     * @throws IllegalArgumentException se non � possibile generare altri
     * codici.
     */
    private String generateCode(String lastcode) {
        if (lastcode.matches("z{" + CodeLength + "}")) //controlla se � possibile generare nuovi codici
        {
            throw new IllegalArgumentException("Codici Esauriti!");
        }
        //altrimenti inizia a generare un nuovo codice
        char[] code = lastcode.toCharArray();//conterr� il nuovo codice, ci inserisco il lastcode
        //contiene l'insieme dei possibili caratteri del codice
        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        boolean t = false;		//blocca il ciclo quando � uguale a true
        for (int i = code.length - 1; (i >= 0 && !t); i--) {	//se non � l'ultima posizione di chars
            for (int g = 0; (g < chars.length && !t); g++) {	//confronta l'i-esimo carattere di code
                if (code[i] == chars[g] && g != chars.length - 1) {	  //con il g-esimo carattere di chars
                    code[i] = chars[g + 1];		//se sono uguali mette nell'i-esima posizione di code 
                    t = true;							//il suo successivo e blocca il ciclo
                } else if (g == chars.length - 1) //altrimenti se � l'ultima posizione di chars
                {
                    code[i] = '0';		//metto nell'iesima posizione di code 0.
                }
            }
        }	//converte  array di char in string
        String newCode = new String(code);
        return newCode;
    }

    /**
     * Aggiunge alla Classificazione un nuovo elemento. L'identificatore del
     * nuovo elemento � id. Ritorna il codice assegnato all'elemento. Se c'� gi�
     * un elemento con quel identificatore, allora non aggiunge l'elemento e
     * ritorna null.
     *
     * @param id l'identificatore di un nuovo elemento
     * @return il codice del nuovo elemento o null
     * @throws FileNotFoundException se uno dei files coinvolti non �
     * accessibile
     */
    public String add(String id) throws FileNotFoundException {
        ElemScan.close();
        ElemScan = new Scanner(Elemfile);
        ElemScan.nextLine();		//porto il file degli elementi alla seconda riga
        String lastcode = ElemScan.nextLine();	//ed estraggo l'ultimo codice usato
        while (ElemScan.hasNextLine()) {		//per ogni linea estraggo l'identificatore
            String linea = ElemScan.nextLine();
            linea = linea.substring(CodeLength + 1, linea.length());
            if (linea.equals(id)) {
                return null;		//se sono uguali ritorno null	
            }
        }	//altrimenti
        String nextCode = generateCode(lastcode);	//generiamo un nuovo codice
        ElemScan.close();	//chiudo gli stram
        ElemStrm.close();
        RecreateFile(nextCode, 2, Elemfile);	//ricreo il file sostiutendo il lastcode
        ElemScan = new Scanner(Elemfile);	//riapro gli stream
        OutElemS = new FileOutputStream(Elemfile, true);
        ElemStrm = new PrintStream(OutElemS, true);
        ElemStrm.println(nextCode + ";" + id);	//inserisco negli elementi il nuovo identificatore
        return nextCode;	//ritorno il nuovo codice
    }

    /**
     * Ricrea il file quando viene sotituita una riga.
     *
     * @param replaceString la nuova stringa che sar� inserita
     * @param numberLine Il numero di linea dove sar� inserita la nuova stringa.
     * @throws FileNotFoundException se il file non � accessibile.
     */
    private void RecreateFile(String replaceString, int numberLine, File file) throws FileNotFoundException {
        File temp = new File("./TMP.temp"); 	//creo un file temporaneo
        Scanner fileScn = new Scanner(file);
        FileOutputStream fw = new FileOutputStream(temp);	//apro gli stream
        PrintStream bw = new PrintStream(fw);
        int count = 1;	//conto le linee per arrivare a quella da sostituire
        while ((fileScn.hasNextLine())) {
            if (count != numberLine) //se non � la linea da sostituire
            {
                bw.println(fileScn.nextLine());	//ricopio la linea sul file temporaneo
            } else {
                bw.println(replaceString);	//altrimenti scrivo la nuova linea sul file temporaneo
                fileScn.nextLine();
            }
            count++;
        }
        fileScn.close();
        bw.close();
        file.delete();		//dopo averlo chiuso cancello il vecchio file
        temp.renameTo(file);	//rinomino il nuovo file con il nome del vecchio file
    }

    /**
     * Associa ad un elemento una nuova keyword. Il codice dell'elemento � code
     * e il codice della keyword � kCode. Se l'operazione ha successo ritorna
     * true. Se la keyword era gi� stata associata all'elemento o uno dei codici
     * � errato ritorna false.
     *
     * @param code il codice di un elemento
     * @param kCode il codice di una keywords
     * @return true se l'operazione ha successo, false altrimenti
     * @throws FileNotFoundException se il file non � accessibile.
     */
    public boolean addKeyword(String code, String kCode) throws FileNotFoundException {
        if (keyh.name(kCode) == null) {
            return false;	//se il codice della keyword non esiste
        }
        rewind();
		//String [] codici=null;
        //Iterator<String> ite=iterator();
		/*boolean t=false;
         while (ite.hasNext()&&!t)
         if(code.equals(ite.next())) t=true;
         if(!t) return false;*/
        if (id(code) == null) {
            return false;
        }
        int count = 2;	//conto le rige
        while (keyScan.hasNextLine()) {		//se il codice dell'elemento viene trovato
            count++;
            String linea = keyScan.nextLine();
            String cod = linea.substring(0, CodeLength);
            if (code.equals(cod)) {
                String[] codici = keywords(code);	//metto in un array tutte le keywords associate a quel codice
                for (int i = 0; i < codici.length; i++) //se � gi� presente quella che voglio associare
                {
                    if (codici[i].equals(kCode)) {
                        return false; //ritorno falso
                    }
                }
                linea += ";" + kCode;		//altrimenti ricreo il file con la nuova aggiunta e ritorno true
                keyScan.close();
                keyStrm.close();
                RecreateFile(linea, count, Keyfile);
                keyScan = new Scanner(Keyfile);
                OutKeyS = new FileOutputStream(Keyfile, true);
                keyStrm = new PrintStream(OutKeyS, true);
                return true;
            }
        }
        keyStrm.println(code + ";" + kCode);
        return true;	//se il codice non � stato trovato ritorno false
    }

    /**
     * Rimuove la keyword di codice kCode dalle keywords associate all'elemento
     * di codice code. Se l'operazione ha successo ritorna true. Se la keyword
     * non � presente o uno dei codici � errato, ritorna false.
     *
     * @param code il codice di un elemento
     * @param kCode il codice di una keyword
     * @return true se l'operazione ha successo, false altrimenti
     * @throws FileNotFoundException FileNotFoundException se il file non �
     * accessibile.
     */
    public boolean removeKeyword(String code, String kCode) throws FileNotFoundException {
        String[] codici = keywords(code); //metto le keywords associate in un array
        if (codici == null || codici.length == 0) {
            return false; //se l'array � null, non esiste il codice o non ci sono keyword associate
        }
        String newline = "";	//uso una stringa per creare la nuova linea da sostituire al file
        boolean t = false;	//indica se � presente la keyword da eliminare
        for (int i = 0; i < codici.length; i++) {	//controlliamo se � presente e intanto ci costruiamo la nuova linea
            if (!codici[i].equals(kCode)) {
                newline += ";" + codici[i];
            } else {
                t = true;
            }
        }
        rewind();
        int count = 2;	//conto la linea da sostituire
        while (keyScan.hasNextLine() && t) {	//se la keyword da eliminare � stata trovata
            count++;
            String linea = keyScan.nextLine();
            String cod = linea.substring(0, CodeLength);	//trova il codice e 
            if (code.equals(cod)) {
                keyScan.close();
                keyStrm.close();	//ricrea il file con la nuova linea
                RecreateFile(cod + newline, count, Keyfile);
                keyScan = new Scanner(Keyfile);
                OutKeyS = new FileOutputStream(Keyfile, true);
                keyStrm = new PrintStream(OutKeyS, true);
                return true;
            }
        }
        return false;
    }

    /**
     * Chiude la Classificazione. Se sono state apportate delle modifiche sono
     * salvate nel file. Tutte le risorse sono rilasciate. Dopo l'invocazione di
     * questo metodo l'invocazione di un qualunque metodo della classe dovrebbe
     * provocare il lancio di una eccezione di tipo IllegalStateException.
     *
     * @throws IOException
     */
    public void close() {
        keyScan.close();
        keyStrm.close();
        ElemScan.close();
        ElemStrm.close();
        closed = true;		//necessaria per non permettere la riapertura dello scanner.
    }
}

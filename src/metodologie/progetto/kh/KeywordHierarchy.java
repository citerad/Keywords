package metodologie.progetto.kh;

import java.io.*;
import java.util.*;

/**
 * Questa classe gestisce gerarchie di keywords mantenute su files che
 * obbediscono ad uno specifico formato.
 *
 * I nomi delle keywords sono stringhe non vuote di qualsiasi lunghezza e
 * composte da caratteri appartenenti all'insieme
 * {'a','b',...,'z','A','B',...'Z','0','1',...,'9','-','_',' '} (cio�, caratteri
 * alfabetici minuscoli e maiuscoli, cifre, trattino, underscore e spazio). I
 * nomi delle keywords non sono sensibili alla capitalizzazione e non possono
 * iniziare n� terminare con un carattere spazio. Ad ogni keyword � assegnato un
 * codice univoco di CODELENGTH caratteri appartenenti all'insieme
 * {'a','b',...,'z','A','B',...'Z','0','1',...,'9'} (se CODELENGTH = 6 ci sono
 * pi� di 56 miliardi di codici possibili). La keyword speciale ANYTHING ha
 * sempre il codice 00...0.
 *
 * Una gerarchia di keywords � mantenuta in un file di testo (gestito quindi
 * tramite accesso sequenziale) che ha la seguente struttura. Il nome del file
 * deve essere name.KWH dove name � il nome della gerarchia. La prima linea del
 * file contiene l'identificatore della gerarchia di keywords che � la stringa
 * KEYWORD_HIERARCHY:name:N dove name � il nome della gerarchia e N � il numero
 * di nanosecondi ritornato dal metodo System.nanoTime() nel momento in cui il
 * file della gerarchia � creato. La seconda linea contiene lastcode che �
 * l'ultimo codice di keyword usato nella gerarchia. Dopo le prime due linee,
 * per ogni keyword (eccetto la keyword speciale ANYTHING) c'� una linea con il
 * seguente formato: code;K;supercode dove code � il codice della keyword, K �
 * il nome della keyword e supercode � il codice della super-keyword diretta.
 * Queste informazioni sono sufficienti per ricostruire l'intera gerarchia.
 *
 * @author Domenico Citera
 */
public class KeywordHierarchy {
    /*La lunghezza dei codici delle keywords.
     Valore di default 6.*/

    public static final int CodeLength = 6;
    /*Carattere usato come separatore nel path-keywords.
     Valore di default '>'.*/
    public static final char PathSep = '>';
    /*Stream di output per scrivere nel file.*/
    private FileOutputStream outkWordS;
    /*Flusso di stampa.*/
    private PrintStream kWordStrm;
    /*Mantiene la locazione del file.*/
    private File kWord;
    /*Interpreta il contenuto del file. */
    private Scanner kWordScan;
    /* Usato per indicare la chiusura del file.
     Quando vale <code>true</code>, viene impedito l'accesso allo Scanner, che il metodo <code>rewind</code> riaprirebbe ogni qual volta chiamato.
     */
    private boolean closed = false;

    /**
     * Apre una gerarchia di keywords o ne crea una nuova. Il nome della
     * gerarchia � <code>name</code> e il file si trova nella directory di
     * pathname <code>dir</code>.
     *
     * @param dir il pathname di una directory.
     * @param name il nome di una gerarchia di keywords.
     * @throws FileNotFoundException se il file non � accessibile
     * @throws IllegalArgumentException se il file non � compatibile con la
     * struttura di un file che contiene una gerarchia di keywords.
     */
    public KeywordHierarchy(String dir, String name) throws FileNotFoundException {
        kWord = new File(dir, name + ".KWH");
        if (kWord.exists()) {	//se il file esiste controlla se � compatibile
            kWordScan = new Scanner(kWord);
            kWordScan.useDelimiter(":");
            if (kWordScan.hasNext()
                    && (kWordScan.next().equals("KEYWORD_HIERARCHY")) && //controllola prima
                    (kWordScan.next().equals(name))) {		// e seconda parola
                outkWordS = new FileOutputStream(kWord, true);	//se � valido 
                kWordStrm = new PrintStream(outkWordS, true);		//lo prepariamo alla scrittura
                System.out.println("	Il file � stato aperto");
            } //altrimenti lanciamo un eccezione
            else {
                throw new IllegalArgumentException("File esistente non compatibile");
            }
        } else {		//se non esiste crea il file
            outkWordS = new FileOutputStream(kWord, true);
            kWordStrm = new PrintStream(outkWordS, true);
            kWordStrm.println("KEYWORD_HIERARCHY:" + name + ":" + System.nanoTime());
            kWordStrm.println(anythingCode());		//scrivo nella seconda linea il primo 'lastcode'
            kWordScan = new Scanner(kWord);		//apro lo scanner del file
            System.out.println("	Il file � stato creato");
        }
    }

    /**
     * Crea un codice di <code>CodeLength</code> 0. Questo ser� usato in altri
     * metodi come confronto.
     *
     * @return un codice di <code>CodeLength</code> 0.
     */
    private String anythingCode() {
        String a = "";
        for (int i = 0; i < CodeLength; i++) {
            a += "0";
        }
        return a;
    }

    /**
     * Riporta lo Scanner alla terza riga del file.
     *
     * @throws FileNotFoundException se il file non � accessibile.
     */
    private void rewind() throws FileNotFoundException {
        if (!closed) {	//se non � stato invocato il metodo 'close'
            kWordScan.close();	//riapri lo scanner
            kWordScan = new Scanner(kWord);
            kWordScan.nextLine();	//e scendi di due righe.
            kWordScan.nextLine();
        }
    }

    /**
     * Genera i codici per le keywords.
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
     * Ricrea il file quando viene sotituita una riga.
     *
     * @param replaceString La nuova stringa che sar� inserita.
     * @param numberLine Il numero di linea dove sar� inserita la nuova stringa.
     * @throws FileNotFoundException se il file non � accessibile.
     */
    private void RecreateFile(String replaceString, int numberLine) throws FileNotFoundException {
        File temp = new File("./", "KWH.temp"); 	//creo un file temporaneo
        kWordScan.close();
        kWordScan = new Scanner(kWord);
        FileOutputStream fw = new FileOutputStream(temp);	//apro gli stream del file temporaneo
        PrintStream bw = new PrintStream(fw);
        int count = 1;	//conto le linee per arrivare a quella da sostituire
        while ((kWordScan.hasNextLine())) {
            if (count != numberLine) //se non � la linea da sostituire
            {
                bw.println(kWordScan.nextLine());	//la linea sul file temporaneo
            } else {
                bw.println(replaceString);	//altrimenti scrivo la nuova linea
                kWordScan.nextLine();
            }
            count++;
        }
        kWordScan.close();
        kWordStrm.close();
        bw.close();
        kWord.delete();		//dopo averlo chiuso cancello il vecchio file
        temp.renameTo(kWord);	//rinomino il nuovo file con il nome del vecchio file
        kWordScan = new Scanner(kWord);	//lo riapro e preparo alla lettura scrittura
        outkWordS = new FileOutputStream(kWord, true);
        kWordStrm = new PrintStream(outkWordS, true);
    }

    /**
     * Ritorna l'identificatore di questa gerarchia di keywords. Ovvero una
     * stringa contenente <code>KEYWORD_HIERARCHY:NAME:N</code> dove
     * <code>name</code> � il nome della gerarchia e <code>N</code> � il numero
     * di nanosecondi ritornato dal metodo <code>System.out.nanoTime()</code>
     * nel momento in cui il file della gerarchia � stato creato.
     * L'identificatore � registrato nel file della gerarchia.
     *
     * @return l'identificatore di questa gerarchia di keywords.
     * @throws FileNotFoundException se il file non � accessibile
     */
    public String getID() throws FileNotFoundException {
        kWordScan.close();	//riporto lo scanner alla testa del file
        kWordScan = new Scanner(kWord);
        return kWordScan.nextLine();	//ritorno la prima linea
    }

    /**
     * Ritorna in un array i codici delle keywords che hanno nome
     * <code>kName</code>. Se non ci sono keywords con quel nome ritorna un
     * array di lunghezza 0.
     *
     * @param kName un nome di keyword.
     * @return l'array dei codici delle keyword con il nome dato.
     * @throws FileNotFoundException se il file non � accessibile
     */
    public String[] codeFromName(String kName) throws FileNotFoundException {
        String[] arrayCode = new String[0];
        if (kName.equals("ANYTHING")) {
            arrayCode = new String[1];
            arrayCode[0] = anythingCode();
        } else {
            rewind();
            while (kWordScan.hasNextLine()) {
                String linea = kWordScan.nextLine();	//estraggo la prima linea
                String name = linea.substring(CodeLength + 1, linea.length() - (CodeLength + 1));  //estraggo il nome
                if (kName.equals(name)) {	//se il nome estratto � uguale a kName
                    String[] temp = new String[arrayCode.length + 1];	//incremento l'arrai di una posizione
                    for (int i = 0; i < arrayCode.length; i++) {
                        temp[i] = arrayCode[i];
                    }
                    temp[arrayCode.length] = linea.substring(0, CodeLength);	//e ci inserisco il nuovo codice
                    arrayCode = temp;
                }
            }
        }
        return arrayCode;
    }

    /**
     * Ritorna il nome della keyword che ha codice <code>code</code>. Se non ci
     * sono keywords con quel codice ritorna null.
     *
     * @param code il codice di una keywords
     * @return il nome della keyword o null
     * @throws FileNotFoundException se il file non � accessibile
     */
    public String name(String code) throws FileNotFoundException {
        if (code.matches("0{" + CodeLength + "}")) {
            return "ANYTHING";
        }
        rewind();
        while (kWordScan.hasNextLine()) {
            String linea = kWordScan.nextLine();
            String codefile = linea.substring(0, CodeLength);
            if (codefile.equals(code)) {
                return linea.substring(CodeLength + 1, linea.length() - (CodeLength + 1));	//estraggo il nome
            }
        }
        return null;
    }

    /**
     * Ritorna il codice della super-keyword diretta della keyword di codice
     * code. Se non ci sono keywords con quel codice ritorna null.
     *
     * @param code il codice di una keyword
     * @return il codice della super-keyword diretta o null.
     * @throws FileNotFoundException se il file non � accessibile.
     */
    public String superK(String code) throws FileNotFoundException {
        rewind();
        while (kWordScan.hasNextLine()) {
            String linea = kWordScan.nextLine();
            String codefile = linea.substring(0, CodeLength);
            if (codefile.equals(code)) {
                return linea.substring(linea.length() - CodeLength, linea.length()); //estraggo il codice della superk.
            }
        }
        return null;
    }

    /**
     * Ritorna in un array i codici delle keywords che sono sub-keywords dirette
     * della keyword con codice code. Se non ci sono sub-keywords, ritorna un
     * array di lunghezza 0 e se non ci sono keywords con codice code, ritorna
     * null.
     *
     * @param code il codice di una keyword
     * @return l'array dei codici delle sub-keywords dirette o null
     * @throws FileNotFoundException se il file non � accessibile
     */
    public String[] subK(String code) throws FileNotFoundException {
        String[] arraySuperCode = new String[0];
        rewind();
        while (kWordScan.hasNextLine()) {
            String linea = kWordScan.nextLine();
            String supercode = linea.substring(linea.length() - CodeLength, linea.length());	//estraggo il supercodice
            if (supercode.equals(code)) {	//se il supercodice � uguale al code
                String[] temp = new String[arraySuperCode.length + 1];	//incremento di una posizione l'array
                for (int i = 0; i < arraySuperCode.length; i++) {
                    temp[i] = arraySuperCode[i];
                }
                temp[arraySuperCode.length] = linea.substring(0, CodeLength);	//e gli inserisco il codice estratto
                arraySuperCode = temp;
            }
        }
        return arraySuperCode;
    }

    /**
     * Controlla che il nome sia ammissibili.
     *
     * @param kName un nome
     * @return true o false.
     */
    private boolean checkName(String kName) {
        if (kName.startsWith(" ") || kName.endsWith(" ") || kName.equals("")) {
            return false;
        }
        for (int i = 0; i < kName.length(); i++) {	//controlla se ci sono
            int c = (int) kName.charAt(i);	//caratteri non consentiti nel nome
            if (!(c == 32 || c == 45 || c == 95 || (c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Aggiunge una nuova keyword alla gerarchia. Il nome della nuova keyword �
     * kName ed � aggiunta come sub-keyword diretta della keyword di codice
     * supercode. Ritorna il codice assegnato alla nuova keyword. Se
     * l'operazione non pu� essere effettuata (perch� supercode non esiste o
     * kName non � un nome ammissibile o esiste gi� una sub-keyword diretta
     * della keyword supercode con quello stesso nome), allora ritorna null.
     *
     * @param supercode il codice della super-keyword diretta
     * @param kName il nome della nuova keyword
     * @return il codice della nuova keyword o null
     * @throws FileNotFoundException se il file non � accessibile.
     */
    public String add(String supercode, String kName) throws FileNotFoundException {
        if (!checkName(kName)) {
            return null;		//controlla il nome e il codice
        }
        if (supercode.equals("ANYTHING")) {
            supercode = anythingCode();
        }
        kWordScan.close();	//porto lo scanner al secondo rigo
        kWordScan = new Scanner(kWord);
        kWordScan.nextLine(); //e estraggo l'ultimo codice usato
        String lastCode = kWordScan.nextLine();
        boolean t = false;	//indica se il supercode � stato trovato
        while (kWordScan.hasNextLine()) {	//per ogni linea
            String linea = kWordScan.nextLine();
            String code = linea.substring(0, CodeLength);	//estraggo il codice
            String superK = linea.substring(linea.length() - CodeLength, linea.length());	//il supercodice
            String name = linea.substring(CodeLength + 1, linea.length() - (CodeLength + 1));	//il nome
            if (superK.equals(supercode) && name.equals(kName)) {
                return null;	//se il supercodice e il nome sono
            }
            if (code.equals(supercode)) {
                t = true;	//uguali a quelli dati return null altrimenti 
            }
        }	//se il supercodice esiste
        if (t || supercode.matches("0{" + CodeLength + "}")) {	//o se � radice
            String nextCode = generateCode(lastCode);	//generiamo un nuovo codice
            RecreateFile(nextCode, 2);				//ricreo il file con il nuovo codice come lastcode
            kWordStrm.println(nextCode + ";" + kName + ";" + supercode);	//aggiungo la nuova keyword
            return nextCode;
        } else {
            return null;
        }
    }

    /**
     * Chiude la gerarchia di keywords. Se sono state apportate delle modifiche
     * sono salvate nel file. Tutte le risorse sono rilasciate. Dopo
     * l'invocazione di questo metodo l'invocazione di un qualunque metodo della
     * classe dovrebbe provocare il lancio di una eccezione di tipo
     * IllegalStateException.
     */
    public void close() {
        kWordScan.close();
        kWordStrm.close();
        closed = true;	//necessaria per non permettere la riapertura dello scanner.
    }

    /**
     * Sostituisce il nome della keyword di codice code con kName e ritorna
     * true. Se l'operazione fallisce (perch� o non esistono keyword con quel
     * codice, o il nuovo nome non � ammissibile o il nuovo nome coincide con
     * quello di un'altra keyword che � una sub-keyword diretta della stessa
     * super-keyword diretta della keyword di codice code), allora ritorna
     * false,
     *
     * @param code il codice della keyword
     * @param kName il nuovo nome della keyword
     * @return true o se fallisce false
     * @throws FileNotFoundException se il file non � accessibile.
     */
    public boolean replaceName(String code, String kName) throws FileNotFoundException {
        if (!checkName(kName)) {
            return false;	//controllo se il nome � ammissibile
        }
        rewind();
        int count = 2;	//inizio a contare la riga da sostituire
        int riga = -1;	//mantiene la riga da sostituire
        while (kWordScan.hasNextLine() && count != riga) {
            count++;
            String linea = kWordScan.nextLine();
            String codek = linea.substring(0, CodeLength);	//estraggo il codice
            if (codek.equals(code)) {	//se il codice � stato trovato
                String superK1 = linea.substring(linea.length() - CodeLength, linea.length());	//il supercodice
                riga = count;	//assegno a riga il numero della riga da sostituire
                String[] codename = codeFromName(kName);	//tutti i codici con nome kName
                for (String a : codename) //per ognuno controllo se hanno lo stesso superk
                {
                    if (superK(a).equals(superK1)) {
                        return false;	//altrientni
                    }
                }
                RecreateFile(code + ";" + kName + ";" + superK1, riga);	//ricreo il nuovo file con il nuovo nome.
                return true;
            }
        }
        return false;
    }

    /**
     * Ritorna il codice della keyword specificata dal path-keyword pathK. Se
     * non � presente ritorna null.
     *
     * @param pathK un path-keyword
     * @return il codice della keyword o null
     * @throws FileNotFoundException se il file non � accessibile.
     */
    public String codeFromPath(String pathK) throws FileNotFoundException {
        int i = pathK.lastIndexOf(Character.toString(PathSep));
        String stringa = pathK.substring(i + 1, pathK.length());	//l'ultima keyword del path
        Scanner fileScan2 = new Scanner(kWord);
        fileScan2.nextLine();
        fileScan2.nextLine();
        while (fileScan2.hasNextLine()) {
            String linea = fileScan2.nextLine();
            String namek = linea.substring(CodeLength + 1, linea.length() - (CodeLength + 1));
            if (namek.equals(stringa)) {	//se il nome � stato trovato
                String codek = linea.substring(0, CodeLength);
                if (path(codek).equals(pathK)) {	//se il path-keyword del codice
                    fileScan2.close();
                    return codek;	//coincide con il pathK ritorna il codice
                }
            }
        }
        fileScan2.close();
        return null;
    }

    /**
     * Ritorna il path-keyword della keyword che ha codice code. Se non ci sono
     * keyword con quel codice ritorna null.
     *
     * @param code il codice di una keyword
     * @return il path-keyword o null
     * @throws FileNotFoundException se il file non � accessibile.
     */
    public String path(String code) throws FileNotFoundException {
        rewind();
        boolean t = false;	//interrompe il ciclo quando � true
        while (t == false && kWordScan.hasNextLine()) {
            String linea = kWordScan.nextLine();
            String codek = linea.substring(0, CodeLength);
            if (codek.equals(code)) {		//se il codice � stato trovato
                t = true;				//e se il supercodice non � radice 
                String namek = linea.substring(CodeLength + 1, linea.length() - (CodeLength + 1));
                String superk = linea.substring(linea.length() - CodeLength, linea.length());
                if (!superk.matches("0{" + CodeLength + "}")) {
                    namek = PathSep + namek;	//restituisco il suo nome preceduto dal PathSep
                }
                return path(superk) + namek;	//altrimenti restituisco solo il nome
            }
        }	//se il codice non � stato trovato perch� � una radice non restituisco nulla
        if (!t && code.matches("0{" + CodeLength + "}")) {
            return "";
        } else {
            return null;								//altrimenti resitutisco null
        }
    }

    /**
     * Ritorna true se la keyword di codice subcode � uguale o � una sub-keyword
     * della keyword di codice code. Altrimenti ritorna false, anche quando uno
     * dei codici � errato.
     *
     * @param subcode il codice di una keyword
     * @param code il codice di una keyword
     * @return true o false
     * @throws FileNotFoundException se il file non � accessibile.
     */
    public boolean isSub(String subcode, String code) throws FileNotFoundException {
        String superk = "";
        if (subcode.equals(code)) {
            return true;	//se � la stessa ritorno vero
        }
        if (subcode.matches("0+")) {
            return false;	//nessuna � sub di tutte
        }
        rewind();
        boolean t = false;		//interrompe il ciclo quando vale true
        while (kWordScan.hasNextLine() && t == false) {
            String linea = kWordScan.nextLine();
            String codek = linea.substring(0, CodeLength);
            if (codek.equals(subcode)) {	//se subcode � stato trovato
                t = true;
                superk = linea.substring(linea.length() - CodeLength, linea.length());
                if (superk.equals(code)) {
                    return true;		//e se � uguale a code return true
                }
            }
        }
        return isSub(superk, code);
    }

    /**
     * Ritorna true se la keyword di codice supercode � uguale o � una
     * super-keyword della keyword di codice code. Altrimenti ritorna false,
     * anche quando uno dei codici � errato.
     *
     * @param supercode il codice di una keyword
     * @param code il codice di una keyword
     * @return true o false
     * @throws FileNotFoundException se il file non � accessibile
     */
    public boolean isSuper(String supercode, String code) throws FileNotFoundException {
        return isSub(code, supercode); //se supercode � una super-keyword di code allora code � una sub-keyword di supercode.
    }
}

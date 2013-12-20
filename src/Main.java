import java.io.FileNotFoundException;
import java.util.InputMismatchException;

import metodologie.progetto.menu.*;

/**
 * Il main.
 *
 * @author Domenico Citera
 *
 */
public class Main {

    public static void main(String[] args) {
        KeyOperations A = new KeyOperations();
        while (true) {
            try {
                A.Menu();
            } catch (FileNotFoundException e) {
                System.out.println("	File non trovato o Path non corretti\n"
                        + "	Ripetere il punto 1 e riprovare");
                continue;
            } catch (InputMismatchException e) {
                System.out.println("	Input non consentito");
            }
            break;
        }

    }
}

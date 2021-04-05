import com.opencsv.CSVReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class Vaccination {
    public static void main(String[] args) {
        if (args.length !=2){
            System.out.println("Proszę podać ścieżkę do pliku oraz numer miesiąca do analizy (np. 3 - marzec)");
            System.exit(1);
        }

        Path reportLocation = Paths.get(args[0]);
        int analyzedMonth = Integer.parseInt(args[1]);

        validateInput(reportLocation, analyzedMonth);
        runAnalysis(reportLocation, analyzedMonth);
    }

    private static void runAnalysis(Path reportLocation, int analyzedMonth) {
        try (CSVReader reader = new CSVReader(Files.newBufferedReader(reportLocation))) {

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static void validateInput(Path reportLocation, int analyzedMonth) {
        if (analyzedMonth<1 || analyzedMonth >12){
            System.out.println("Błędny miesiąc: " + analyzedMonth + ". Podaj liczbę 1-12");
            System.exit(2);
        }

        if (!Files.exists(reportLocation)) {
            System.out.println("Brak pliku " + reportLocation.toAbsolutePath());
            System.exit(2);
        }
    }

}

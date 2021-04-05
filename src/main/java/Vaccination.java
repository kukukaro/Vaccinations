import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Vaccination {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Proszę podać ścieżkę do pliku oraz numer miesiąca do analizy (np. 3 - marzec)");
            System.exit(1);
        }

        Path reportLocation = Paths.get(args[0]);
        int analyzedMonth = Integer.parseInt(args[1]);

        validateInput(reportLocation, analyzedMonth);
        runAnalysis(reportLocation, analyzedMonth);
    }

    private static void runAnalysis(Path reportLocation, int analyzedMonth) {
        try (Reader reader = Files.newBufferedReader(reportLocation)) {
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader());

            List<Event> events = new ArrayList<>();
            for (CSVRecord record : parser) {
                String nurseId = record.get("pm_ext");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n");
                String vaccinationDateString = record.get("pj_data_szczepienia");
                LocalDateTime vaccinationDate;
                if (vaccinationDateString == null || vaccinationDateString.isEmpty()) {
                    vaccinationDate = null;
                } else {
                    vaccinationDate = LocalDateTime.parse(vaccinationDateString, formatter);
                }

                LocalDateTime cancellationDate;
                String cancellationDateString = record.get("pj_data_anulowania");
                if (cancellationDateString == null || cancellationDateString.isEmpty()) {
                    cancellationDate = null;
                } else {
                    cancellationDate = LocalDateTime.parse(cancellationDateString, formatter);
                }

                String month = record.get("okres_rozl");
                String productCode = record.get("pj_kod_prod_rozl"); //, 99.03.0801, 99.03.0803 (m), Nieznany
                String productName = record.get("pj_nazwa_prod_rozl");

                Event event = new Event(nurseId, vaccinationDate, cancellationDate, month, productCode, productName);
                events.add(event);
            }

            System.out.println("Number of events: " + events.size());

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void validateInput(Path reportLocation, int analyzedMonth) {
        if (analyzedMonth < 1 || analyzedMonth > 12) {
            System.out.println("Błędny miesiąc: " + analyzedMonth + ". Podaj liczbę 1-12");
            System.exit(2);
        }

        if (!Files.exists(reportLocation)) {
            System.out.println("Brak pliku " + reportLocation.toAbsolutePath());
            System.exit(2);
        }
    }

}

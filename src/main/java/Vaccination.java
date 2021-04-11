import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Vaccination {
    private  static Logger logger = LoggerFactory.getLogger(Vaccination.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Proszę podać ścieżkę do pliku oraz numer miesiąca do analizy (np. 3 - marzec)");
            System.exit(1);
        }

        Path reportLocation = Paths.get(args[0]);
        int analyzedMonth = Integer.parseInt(args[1]);

        try {
            runInternal(reportLocation, analyzedMonth);
        } catch (IOException e) {
            logger.error("Blad wykonania programu", e);
            System.exit(4);
        }
    }

    protected static Report runInternal(Path reportLocation, int analyzedMonth) throws IOException {
        validateInput(reportLocation, analyzedMonth);
        return runAnalysis(reportLocation, analyzedMonth);

    }

    private static Report runAnalysis(Path reportLocation, int analyzedMonth) throws IOException {
        try (Reader reader = Files.newBufferedReader(reportLocation)) {
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader());

            List<Event> events = new ArrayList<>();
            for (CSVRecord record : parser) {
                String nurseId = record.get("pm_ext");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n");
                String vaccinationDateString = record.get("pj_data_szczepienia");
                LocalDateTime vaccinationDate;
                if (isEmpty(vaccinationDateString)) {
                    vaccinationDate = null;
                } else {
                    vaccinationDate = LocalDateTime.parse(vaccinationDateString, formatter);
                }

                LocalDateTime cancellationDate;
                String cancellationDateString = record.get("pj_data_anulowania");
                if (isEmpty(cancellationDateString)) {
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

            logger.info("Ilosc rekordow zaimportowana z pliku {}", events.size());
            events = filterByMonth(events, analyzedMonth);
             return analyze(events, analyzedMonth);
        }
    }

    private static List<Event> filterByMonth(List<Event> events, int analyzedMonth) {
        String eventMonth = "2021-" + (analyzedMonth < 10 ? "0" + analyzedMonth : analyzedMonth);
        Month expectedMonth = Month.of(analyzedMonth);
        return events.stream()
                .filter(event -> event.getMonth().equals(eventMonth))
                .map(event -> {
                    if(event.getVaccinationDate() != null) {
                        LocalDateTime vacDate = event.getVaccinationDate();
                        if(!vacDate.getMonth().equals(expectedMonth)) {
                            logger.warn("Miesiac szczepenia nie zgadza sie z okresem rozliczeniowym");
                        }
                    }
                    return event;
                })
                .collect(Collectors.toList());
    }

    private static Report analyze(List<Event> events, int month) {
        Map<String, List<Event>> eventsByNurseId = new HashMap<>();
        for (Event event : events) {
            String nurseId = event.getNurseId();
            if(isEmpty(nurseId) && event.getVaccinationDate() != null) {
                logger.error("Bledne dane - brak pielegniarki dla szczepenia");
            } else {
                if(isEmpty(nurseId)) {
                    continue;
                }
                if(!eventsByNurseId.containsKey(nurseId)) {
                    eventsByNurseId.put(nurseId, new ArrayList<>());
                }
                eventsByNurseId.get(nurseId).add(event);
            }
        }

        Report report = new Report();
        report.setMonth(month);

        for (String nurseId : eventsByNurseId.keySet()) {
            List<Event> nurseEvents = eventsByNurseId.get(nurseId);
            int vaccinations = nurseEvents.size();
            int mobileVacs = (int) nurseEvents.stream().filter(Event::isMobile).count();
            report.registerNextNurse(nurseId, vaccinations, mobileVacs);
        }
        report.print();
        return report;
    }

    private static boolean isEmpty(String nurseId) {
        return nurseId == null || nurseId.isEmpty();
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

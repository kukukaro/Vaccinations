import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

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

            //System.out.println("Number of events: " + events.size());
            events = filterByMonth(events, analyzedMonth);
            analyze(events);

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
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
                            System.err.println("Miesiac szczepenia nie zgadza sie z okresem rozliczeniowym");
                        }
                    }
                    return event;
                })
                .collect(Collectors.toList());
    }

    private static void analyze(List<Event> events) {
        Map<String, List<Event>> eventsByNurseId = new HashMap<>();
        for (Event event : events) {
            String nurseId = event.getNurseId();
            if(isEmpty(nurseId) && event.getVaccinationDate() != null) {
                System.err.println("Bledne dane - brak pielegniarki dla szczepenia");
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
        System.out.println("Szczepienia wg pielegniarki:");
        System.out.println("ID\tszczepien\tw tym wyjazdowych");
        int allVaccinations = 0;
        int allMobiles = 0;
        for (String nurseId : eventsByNurseId.keySet()) {
            List<Event> nurseEvents = eventsByNurseId.get(nurseId);
            int vaccinations = nurseEvents.size();
            allVaccinations+=vaccinations;
            long mobileVacs = nurseEvents.stream().filter(Event::isMobile).count();
            allMobiles += mobileVacs;
            System.out.println(nurseId+"\t"+vaccinations+"\t"+mobileVacs);
        }
        System.out.println("\nWszystkich szczepien: "+allVaccinations + " w tym wyjazdowych: "+allMobiles+"\n");
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

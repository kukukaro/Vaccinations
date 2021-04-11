package kukukaro.vaccinations;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class VaccinationTest {
    private  static Logger logger = LoggerFactory.getLogger(VaccinationTest.class);

    @Test
    public void shouldCountVaccinationFromCsvFile() throws URISyntaxException, IOException, InvalidInputException {
        logger.info("Test name: shouldCountVaccinationFromCsvFile");
        URI testFileLocation = VaccinationTest.class.getResource("test_data.csv").toURI();
        Path testFile = Paths.get(testFileLocation);
        int month = 3;

        Report report = Vaccination.runInternal(testFile, month);
        Report expectedReport = new Report();
        expectedReport.registerNextNurse("1P-Mus",4,0);
        expectedReport.registerNextNurse("1P-Kuk",3,1);
        expectedReport.setMonth(month);

        assertThat(report).isEqualTo(expectedReport);
    }

    @Test
    public void shouldNotifyInvalidMonth() {
        logger.info("Test name: shouldNotifyInvalidMonth");
        Path anyFile = Mockito.mock(Path.class);
        int month = 0;

        assertThatThrownBy(() -> Vaccination.runInternal(anyFile, month))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Błędny miesiąc: 0");

    }

    @Test
    public void shouldNotifyMissingFile() {
        logger.info("Test name: shouldNotifyMissingFile");
        Path missingFile = Paths.get("missingFile.csv");

        assertThatThrownBy(() -> Vaccination.runInternal(missingFile, 3))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Brak pliku")
                .hasMessageContaining("missingFile.csv");
    }


}

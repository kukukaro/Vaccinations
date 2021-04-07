import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class VaccinationTest {

    @Test
    public void shouldCountVaccinationFromCsvFile() throws URISyntaxException {
        URI testFileLocation = VaccinationTest.class.getResource("test_data.csv").toURI();
        Path testFile = Paths.get(testFileLocation);
        int month = 3;

        Report report = Vaccination.runInternal(testFile, month);

        assertThat(report.getMonth()).isEqualTo(month);
        assertThat(report.getAllVaccinations()).isEqualTo(7);
        assertThat(report.getAllMobileVaccination()).isEqualTo(1);
        report.hashCode();
    }
}

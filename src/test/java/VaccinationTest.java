import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class VaccinationTest {

    @Test
    public void shouldCountVaccinationFromCsvFile() throws URISyntaxException, IOException {
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
        
    }
}

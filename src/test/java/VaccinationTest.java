import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VaccinationTest {

    @Test
    public void shouldCountVaccinationFromCsvFile() throws URISyntaxException {
        URI testFileLocation = VaccinationTest.class.getResource("test_data.csv").toURI();
        Path testFile = Paths.get(testFileLocation);
        int month = 3;

        Vaccination.main(new String[]{testFile.toString(), "3"});

    }
}

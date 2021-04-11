package kukukaro.vaccinations;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class ReportTest {

    @Test
    public void registerNextNurse() {
        //given
        Report report = new Report();

        //when
        report.registerNextNurse("1P-Mus",4,0);
        report.registerNextNurse("1P-Kuk",3,1);

        //then
        List<Report.NurseReport> nursesFromRaport = report.getNurses();

        List<Report.NurseReport> expectedNurses = new ArrayList<>();
        expectedNurses.add(new Report.NurseReport("1P-Mus",4,0));
        expectedNurses.add(new Report.NurseReport("1P-Kuk",3,1));

        assertThat(nursesFromRaport).isEqualTo(expectedNurses);


    }

    @Test
    public void getAllVaccinations() {
    }

    @Test
    public void getAllMobileVaccination() {
    }
}
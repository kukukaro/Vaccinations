package kukukaro.vaccinations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Report {
    private int month;
    private int allVaccinations = 0;
    private int allMobileVaccination = 0 ;
    private List<NurseReport> nurses = new ArrayList<>();


    private static class NurseReport{
        private String id;
        private int vaccinations;
        private int mobileVaccinations;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NurseReport that = (NurseReport) o;
            return vaccinations == that.vaccinations &&
                    mobileVaccinations == that.mobileVaccinations &&
                    Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, vaccinations, mobileVaccinations);
        }
    }

    public void registerNextNurse(String id, int vaccinations, int mobileVaccination) {
        NurseReport nurseReport = new NurseReport();
        nurseReport.id = id;
        nurseReport.vaccinations = vaccinations;
        nurseReport.mobileVaccinations = mobileVaccination;
        nurses.add(nurseReport);
        allVaccinations += vaccinations;
        allMobileVaccination += mobileVaccination;
    }

    public void print() {
        System.out.println("Szczepienia wg pielegniarki dla miesiaca "+ month +":");
        System.out.println("ID\tszczepien\tw tym wyjazdowych");
        for (NurseReport nurseReport : nurses) {
            System.out.println(nurseReport.id+"\t"+nurseReport.vaccinations+"\t"+nurseReport.mobileVaccinations);
        }
        System.out.println("\nWszystkich szczepien: "+allVaccinations + " w tym wyjazdowych: "+allMobileVaccination+"\n");
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    public int getAllVaccinations() {
        return allVaccinations;
    }

    public int getAllMobileVaccination() {
        return allMobileVaccination;
    }

    public List<NurseReport> getNurses() {
        return nurses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return month == report.month &&
                allVaccinations == report.allVaccinations &&
                allMobileVaccination == report.allMobileVaccination &&
                Objects.equals(nurses, report.nurses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, allVaccinations, allMobileVaccination, nurses);
    }
}

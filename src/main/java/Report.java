import java.util.ArrayList;
import java.util.List;

public class Report {
    private int month;
    private int allVaccinations = 0;
    private int allMobileVaccination = 0 ;
    private List<NurseReport> nurses = new ArrayList<>();


    private static class NurseReport{
        private String id;
        private int vaccinations;
        private int mobileVaccinations;
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
}

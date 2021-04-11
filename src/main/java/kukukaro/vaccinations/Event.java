package kukukaro.vaccinations;

import java.time.LocalDateTime;

public class Event {
    private String nurseId; // pm_ext
    private LocalDateTime vaccinationDate; //pj_data_szczepienia
    private LocalDateTime cancellationDate; //pj_data_anulowania
    private String month; //okres_rozl
    private String productCode; //pj_kod_prod_rozl, 99.03.0801, 99.03.0803 (m), Nieznany
    private String productName; //pj_nazwa_prod_rozl

    public Event(String nurseId, LocalDateTime vaccinationDate, LocalDateTime cancellationDate, String month, String productCode, String productName) {
        this.nurseId = nurseId;
        this.vaccinationDate = vaccinationDate;
        this.cancellationDate = cancellationDate;
        this.month = month;
        this.productCode = productCode;
        this.productName = productName;
    }

    public boolean isMobile() {
        return productCode.equals("99.03.0803");
    }
    public String getNurseId() {
        return nurseId;
    }

    public LocalDateTime getVaccinationDate() {
        return vaccinationDate;
    }

    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }

    public String getMonth() {
        return month;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }
}

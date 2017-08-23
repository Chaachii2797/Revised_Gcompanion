package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 8/20/2017.
 */

public class ApplyUnitData {

    private String ApartmentID, TenantName, TenantEmail;

    public ApplyUnitData() {
    }

    public ApplyUnitData(String apartmentID, String tenantName, String tenantEmail) {
        ApartmentID = apartmentID;
        TenantName = tenantName;
        TenantEmail = tenantEmail;
    }

    public String getApartmentID() {
        return ApartmentID;
    }

    public void setApartmentID(String apartmentID) {
        ApartmentID = apartmentID;
    }

    public String getTenantName() {
        return TenantName;
    }

    public void setTenantName(String tenantName) {
        TenantName = tenantName;
    }

    public String getTenantEmail() {
        return TenantEmail;
    }

    public void setTenantEmail(String tenantEmail) {
        TenantEmail = tenantEmail;
    }
}


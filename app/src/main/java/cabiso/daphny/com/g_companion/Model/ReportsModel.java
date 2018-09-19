package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 9/15/2018.
 */

public class ReportsModel {

    public String reportedBy;
    public String complaint;
    public String customerID;
    public String reportDate;

    public ReportsModel(String reportedBy, String complaint, String customerID, String reportDate){
        this.reportedBy = reportedBy;
        this.complaint = complaint;
        this.customerID = customerID;
        this.reportDate = reportDate;

    }

    public ReportsModel(){
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}

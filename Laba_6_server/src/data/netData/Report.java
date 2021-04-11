package data.netData;

import java.io.Serializable;

public class Report implements Serializable {
    private ReportState reportState;
    private String reportBody;
    // TODO private static long serializationVersionID = 1L; <-- create this field in other classes

    public Report(ReportState reportState, String reportBody) {
        this.reportState = reportState;
        this.reportBody = reportBody;
    }

    public ReportState getReportState() {
        return reportState;
    }

    public String getReportBody() {
        return reportBody;
    }
}

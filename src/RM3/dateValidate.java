package RM3;
import java.util.Date;

public class dateValidate {

    Date startDate;
    Date endDate;

    public dateValidate(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isWithinRange(Date testDate) {


        return testDate.getTime() >= startDate.getTime() &&
                testDate.getTime() <= endDate.getTime();
    }

}
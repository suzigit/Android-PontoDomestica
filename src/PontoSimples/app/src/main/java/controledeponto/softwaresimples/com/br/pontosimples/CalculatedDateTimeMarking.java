package controledeponto.softwaresimples.com.br.pontosimples;

/**
 * Created by suzana on 2/9/2016.
 */
public class CalculatedDateTimeMarking extends DateTimeMarking {


    private long workWithoutInterval;
    private long workWithInterval;

    public CalculatedDateTimeMarking(String startDate, String startTime, String stopDate, String stopTime,
                                     String workWithoutInterval, String workWithInterval, int intervalValueInMinutes,
                                     String startLocation, String startLocationTime, String stopLocation, String stopLocationTime) {

        super (startDate + " " + startTime, stopDate + " " + stopTime, intervalValueInMinutes, startLocation, startLocationTime,stopLocation, stopLocationTime);
        this.workWithInterval = getWorkTimeFormattedStringInSeconds(workWithInterval);
        this.workWithoutInterval = getWorkTimeFormattedStringInSeconds(workWithoutInterval);
    }

    public long getWorkWithoutInterval() {
        return workWithoutInterval;
    }

    public long getWorkWithInterval() {
        return workWithInterval;
    }
}

package controledeponto.softwaresimples.com.br.pontosimples;

import android.location.Location;
import android.location.LocationProvider;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by suzana on 11/21/2015.
 */
public class DateTimeMarking implements Parcelable {

    private Date startDateTime;
    private Date stopDateTime;
    private final SimpleDateFormat dfTime = new SimpleDateFormat ("HH:mm:ss");
    private final SimpleDateFormat dfDate = new SimpleDateFormat ("dd/MM/yyyy");
    private final SimpleDateFormat dfDateTime = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss");

    private String startLocationLatLong = "Sem dados";
    private Date startLocationTime;
    private String stopLocationLatLong = "Sem dados";
    private Date stopLocationTime;

    public String getStartLocationLatLong() {
        return startLocationLatLong;
    }

    public String getStartLocationTime() {
        String result = "";
        if (this.startLocationTime!=null)  {
            result = this.dfDateTime.format(startLocationTime);
        }
        return result;
   }

    public String getStopLocationLatLong() {
        return stopLocationLatLong;
    }

    public String getStopLocationTime() {
        String result = "";
        if (this.stopLocationTime!=null)  {
            result = this.dfDateTime.format(stopLocationTime);
        }
        return result;
    }

    private int intervalValueInMinutes;

    public DateTimeMarking() {
    }


    protected DateTimeMarking(String startDateTime, String stopDateTime, int intervalValueInMinutes,
                              String startLocation, String startLocationTime, String stopLocation, String stopLocationTime) {
        try {
            this.startDateTime = this.dfDateTime.parse(startDateTime);
            this.stopDateTime = this.dfDateTime.parse(stopDateTime);
            this.intervalValueInMinutes = intervalValueInMinutes;
            this.startLocationLatLong = startLocation;
            this.startLocationTime = this.dfDateTime.parse(startLocationTime);
            this.stopLocationLatLong = stopLocation;
            this.stopLocationTime = this.dfDateTime.parse(stopLocationTime);


        } catch (Exception e) {
            System.out.println("Erro no parser ao construir DateTimeMarking" + e.getMessage());
        }
    }


    public int getIntervalValueInMinutes() {
        return this.intervalValueInMinutes;
    }

    public void setStart() {
        this.startDateTime = new Date();
        this.stopDateTime = null;
    }

    public void setIntervalValueInMinutes(int intervalValueInMinutes) {
        this.intervalValueInMinutes = intervalValueInMinutes;
    }


    public void setStartLocation(Location l) {
        this.startLocationTime = this.startDateTime;
        if (l!=null) {
            this.startLocationLatLong = "'" + l.getLatitude() + " " + l.getLongitude();
            this.startLocationTime  = new Date (l.getTime());
        }
        else {
            this.startLocationLatLong = "Sem dados";
        }
    }

    public void setStopLocation(Location l) {
        this.stopLocationTime = this.stopDateTime;
        if (l!=null) {
            this.stopLocationLatLong = "'" + l.getLatitude() + " " + l.getLongitude();
            this.stopLocationTime  = new Date (l.getTime());
        }
        else {
            this.stopLocationLatLong = "Sem dados";
        }

    }


    public void setStop() {
        this.stopDateTime = new Date();
    }


    public String getStartDateTime() {
        String result = "";
        if (this.startDateTime!=null)  {
            result = this.dfDateTime.format(startDateTime);
        }
        return result;
    }

    public int getStartMonth() {
        return this.startDateTime.getMonth();
    }

    public String getStartDateOnly() {
        String result = "";
        if (this.startDateTime!=null)  {
            result = this.dfDate.format(startDateTime);
        }
        return result;

    }

    public String getStartTimeOnly() {
        String result = "";
        if (this.startDateTime!=null)  {
            result = this.dfTime.format(startDateTime);
        }
        return result;
    }

    public String getStopDateTime() {
        String result = "";
        if (this.stopDateTime!=null)  {
            result = this.dfDateTime.format(stopDateTime);
        }
        return result;
    }

    public String getStopDateOnly() {
        String result = "";
        if (this.stopDateTime!=null)  {
            result = this.dfDate.format(stopDateTime);
        }
        return result;
    }

    public String getStopTimeOnly() {
        String result = "";
        if (this.stopDateTime!=null)  {
            result = this.dfTime.format(stopDateTime);
        }
        return result;
    }

    public String getWorkTimeWithoutInterval() {

        String textoValorTotal = "Nao calculado";

        //Soh calcula total se for no mesmo dia
        if (this.getStartDateOnly().equals(this.getStopDateOnly())) {

            long lStartDateTime = this.startDateTime.getTime();
            long lStopDateTime = this.stopDateTime.getTime();

            long totalSegundos = (lStopDateTime-lStartDateTime)/1000;

            textoValorTotal = getWorkTimeSecondsInFormattedString(totalSegundos);
        }
        return textoValorTotal;
    }


    public String getWorkTimeWithInterval() {

        String textoValorTotal = "Data fim diferente de data inicial";

        //Soh calcula total se for no mesmo dia
        if (this.getStartDateOnly().equals(this.getStopDateOnly())) {

            long lStartDateTime = this.startDateTime.getTime();
            long lStopDateTime = this.stopDateTime.getTime();

            long totalSegundos = (lStopDateTime-lStartDateTime)/1000;

            long totalSegundosDescontandoIntervalo = totalSegundos - getIntervalValueInSeconds();
            if (totalSegundosDescontandoIntervalo < 0) {
                textoValorTotal = "Intervalo > Tempo de trabalho";
            }
            else {
                textoValorTotal = getWorkTimeSecondsInFormattedString(totalSegundosDescontandoIntervalo);
            }
        }
        return textoValorTotal;
    }


    private int getIntervalValueInSeconds() {
        return this.intervalValueInMinutes*60;
    }


    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.getStartDateTime(), this.getStopDateTime(), this.intervalValueInMinutes+"",
            this.getStopLocationLatLong(), this.getStartLocationTime(), this.getStopLocationLatLong(), this.getStopLocationTime()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DateTimeMarking createFromParcel(Parcel in) {
            String[] data = new String[7];
            in.readStringArray(data);
            String startDateTime = data[0];
            String stopDateTime = data[1];
            int intervalValueInMinutes = Integer.parseInt(data[2]);
            String startLocation = data[3];
            String startLocationTime = data[4];
            String stopLocation = data[5];
            String stopLocationTime = data[6];
            return new DateTimeMarking(startDateTime,stopDateTime,intervalValueInMinutes,
                    startLocation,startLocationTime,stopLocation,stopLocationTime);
        }

        public DateTimeMarking[] newArray(int size) {
            throw new UnsupportedOperationException("Nao pode criar array de DateTimeMarking");
        }
    };

    /* Retorna uma string com HH:mm:ss que representa a quantidade de segundos recebida como parametro.
     *
     */
    public static String getWorkTimeSecondsInFormattedString(long seconds) {
        int totalMinutos = (int) (seconds/60);
        int totalHoras = totalMinutos / 60;
        int restoMinutos = totalMinutos % 60;
        int restoSegundos = (int) (seconds % 60);

        return (totalHoras + ":" + restoMinutos + ":" + restoSegundos);

    }

    public static long getWorkTimeFormattedStringInSeconds(String s) {
        int indexOfFirstDelimiter = s.indexOf(":");
        long total = 0;
        if (indexOfFirstDelimiter>0) {
            int indexOfSecondDelimiter = s.indexOf(":", indexOfFirstDelimiter + 1);

            long hours = Integer.parseInt(s.substring(0,indexOfFirstDelimiter));
            long minutes = Integer.parseInt(s.substring(indexOfFirstDelimiter+1,indexOfSecondDelimiter));
            long seconds = Integer.parseInt(s.substring(indexOfSecondDelimiter+1));

            total = hours*3600 + minutes*60 + seconds;
        }

        return total;
    }

}

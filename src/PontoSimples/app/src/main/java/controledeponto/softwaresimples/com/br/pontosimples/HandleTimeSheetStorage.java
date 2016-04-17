package controledeponto.softwaresimples.com.br.pontosimples;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by suzana on 11/21/2015.
 */
public class HandleTimeSheetStorage {


    private Context c;
    private File timeSheetFile;
    private char delimiterInsideLine = ',';
    private char delimiterBetweenLines = '\n';


    public HandleTimeSheetStorage(Context c) {
        String fileName = "timesheet18.csv";
        this.timeSheetFile = new File(c.getFilesDir().getAbsolutePath(),fileName);
        this.c = c;
    }

    public void writeData(DateTimeMarking dateTimeMarking)  {

        try {
            boolean printHeader = false;
            if (!this.timeSheetFile.exists()) {
                printHeader = true;
            }

            FileOutputStream fos = c.openFileOutput(this.timeSheetFile.getName(), c.MODE_APPEND);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

            if (printHeader) {
                String header = "Data Inicial" + delimiterInsideLine + "Hora Inicial"  + delimiterInsideLine
                        + "Data Final" + delimiterInsideLine + "Hora Final"  + delimiterInsideLine
                        + "Total de horas trabalhadas (sem descontar intervalo)" + delimiterInsideLine
                        + "Total de horas trabalhadas (descontanto intervalo)" + delimiterInsideLine
                        + "Tempo de intervalo considerado" +  delimiterInsideLine
                        + "Localizacao inicial" +  delimiterInsideLine
                        + "Hora da marcacao da localizacao inicial" +  delimiterInsideLine
                        + "Localizacao final" +  delimiterInsideLine
                        + "Hora da marcacao da localizacao final" +  delimiterBetweenLines;
                writer.print(header);

            }

            String s = dateTimeMarking.getStartDateOnly() + delimiterInsideLine + dateTimeMarking.getStartTimeOnly() + delimiterInsideLine
                    + dateTimeMarking.getStopDateOnly() + delimiterInsideLine + dateTimeMarking.getStopTimeOnly() + delimiterInsideLine
                    + dateTimeMarking.getWorkTimeWithoutInterval() + delimiterInsideLine + dateTimeMarking.getWorkTimeWithInterval() + delimiterInsideLine
                    + dateTimeMarking.getIntervalValueInMinutes() + delimiterInsideLine + dateTimeMarking.getStartLocationLatLong()
                    + delimiterInsideLine + dateTimeMarking.getStartLocationTime() + delimiterInsideLine
                    + dateTimeMarking.getStopLocationLatLong() + delimiterInsideLine + dateTimeMarking.getStopLocationTime()
                    + delimiterBetweenLines;

            writer.print(s);
            writer.close();
        }
        catch (Exception e) {
            Toast.makeText(c.getApplicationContext(), "Nao conseguiu abrir arquivo", Toast.LENGTH_LONG);
        }
    }



    /* Recupera as marcacoes de tempo.
     * Se encontra uma mudanca de mes ns datas marcadas, imprime um totalizador de horas trabalhadas desconsiderando
     * aquelas marcacoes que nao houve contagem de tempo.
     */
    public String getData(int valorTotalIntervalo)  {

        StringBuffer data = new StringBuffer();
        long numberOfWorkSecondsInMonthWithInterval = 0;
        long numberOfWorkSecondsInMonthWithoutInterval = 0;

        try {
            FileInputStream stream = c.openFileInput(this.timeSheetFile.getName());
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(reader);
            int lastMonth = -1;
            String line = br.readLine();

            if(line!=null) {
                //se existe algo no arquivo, a primeira linha eh o header
                data.append(line + "\n");
                line = br.readLine();
            }

            //Itera nas demais linhas do arquivo.
            while(line != null) {
                CalculatedDateTimeMarking dateTimeMarking = this.createDateTimeMarking(line);

                if ((dateTimeMarking.getStartMonth()!=lastMonth) && (lastMonth!=-1))  {
                    //exibe tempo total e zera contagem.
                    String workSecondsInMonth = getStringTotalMonthlyWithInterval(numberOfWorkSecondsInMonthWithInterval);
                    data.append(workSecondsInMonth + "\n");
                    workSecondsInMonth = getStringTotalMonthlyWithoutInterval(numberOfWorkSecondsInMonthWithoutInterval);
                    data.append(workSecondsInMonth + "\n");

                    numberOfWorkSecondsInMonthWithInterval = 0;
                    numberOfWorkSecondsInMonthWithoutInterval = 0;
                }

                //acumula tempo total. Se intervalo de datas diferentes, o intervalo serah 0.
                numberOfWorkSecondsInMonthWithInterval += dateTimeMarking.getWorkWithInterval();
                numberOfWorkSecondsInMonthWithoutInterval += dateTimeMarking.getWorkWithoutInterval();

                data.append(line + "\n");

                lastMonth = dateTimeMarking.getStartMonth();

                line = br.readLine();
            }

            //entrou no laco pelo menos uma vez
            if (lastMonth!=-1) {
                //exibe tempo total
                String workSecondsInMonth = getStringTotalMonthlyWithInterval(numberOfWorkSecondsInMonthWithInterval);
                data.append(workSecondsInMonth + "\n");
                workSecondsInMonth = getStringTotalMonthlyWithoutInterval(numberOfWorkSecondsInMonthWithoutInterval);
                data.append(workSecondsInMonth + "\n");

            }

            br.close();
        }
        catch (Exception e) {
            Toast.makeText(c.getApplicationContext(), "Nao conseguiu ler arquivo", Toast.LENGTH_LONG);
        }
        return data.toString();
    }

    private String getStringTotalMonthlyWithInterval(long numberOfWorkSecondsInMonth) {
        return "**** Tempo mensal total(descontando intervalos)" + this.delimiterInsideLine +
                                DateTimeMarking.getWorkTimeSecondsInFormattedString(numberOfWorkSecondsInMonth);
    }

    private String getStringTotalMonthlyWithoutInterval(long numberOfWorkSecondsInMonth) {
        return "**** Tempo mensal total(sem descontar intervalos)" + this.delimiterInsideLine +
                DateTimeMarking.getWorkTimeSecondsInFormattedString(numberOfWorkSecondsInMonth);
    }


    private CalculatedDateTimeMarking createDateTimeMarking (String line) {
        int indexOfStartDate = 0;
        int indexOfStartTime = line.indexOf(this.delimiterInsideLine, indexOfStartDate) + 1;
        int indexOfStopDate = line.indexOf(this.delimiterInsideLine, indexOfStartTime) + 1;
        int indexOfStopTime = line.indexOf(this.delimiterInsideLine, indexOfStopDate) + 1;
        int indexOfTotalSemIntervalo = line.indexOf(this.delimiterInsideLine,indexOfStopTime) + 1;
        int indexOfTotalComIntervalo = line.indexOf(this.delimiterInsideLine,indexOfTotalSemIntervalo) + 1;
        int indexOfIntervalo = line.indexOf(this.delimiterInsideLine,indexOfTotalComIntervalo) + 1;
        int indexOfStartLocation = line.indexOf(this.delimiterInsideLine,indexOfIntervalo) + 1;
        int indexOfStartLocationTime = line.indexOf(this.delimiterInsideLine,indexOfStartLocation) + 1;
        int indexOfStopLocation = line.indexOf(this.delimiterInsideLine,indexOfStartLocationTime) + 1;
        int indexofStopLocationTime = line.indexOf(this.delimiterInsideLine,indexOfStopLocation) + 1;


        String startDate = line.substring(0,indexOfStartTime-1);
        String startTime = line.substring(indexOfStartTime,indexOfStopDate-1);
        String stopDate = line.substring(indexOfStopDate, indexOfStopTime-1);
        String stopTime = line.substring(indexOfStopTime, indexOfTotalSemIntervalo-1);
        String totalSemInterval = line.substring(indexOfTotalSemIntervalo, indexOfTotalComIntervalo - 1);
        String totalComInterval = line.substring(indexOfTotalComIntervalo, indexOfIntervalo - 1);
        String interval = line.substring(indexOfIntervalo,indexOfStartLocation-1);
        String startLocation = line.substring(indexOfStartLocation,indexOfStartLocationTime-1);
        String startLocationTime = line.substring(indexOfStartLocationTime,indexOfStopLocation-1);
        String stopLocation = line.substring(indexOfStopLocation, indexofStopLocationTime-1);
        String stopLocationTime = line.substring(indexofStopLocationTime);


        return new CalculatedDateTimeMarking(startDate,startTime,stopDate,stopTime,totalSemInterval,
                totalComInterval,Integer.parseInt(interval),startLocation, startLocationTime,stopLocation, stopLocationTime);

    }

}

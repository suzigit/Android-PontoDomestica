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
 * Created by suzana on 11/29/2015.
 */
public class HandleSettingsStorage {

    public static int DEFAULT_INTERVAL_IN_MINUTES = 0;
    public static int DEFAULT_LOCALIZATION_TIMOUT = 3;


    private File settingsFile;
    private Context c;
    private static HandleSettingsStorage instance;
    private String email;
    private int interval;
    private int localizationTimeout;

    public static HandleSettingsStorage getInstance(Context c) {
        if (instance==null) {
            instance = new HandleSettingsStorage (c);
        }
        return instance;
    }


    private HandleSettingsStorage (Context c) {
        String fileName = "settings8.csv";
        this.c = c;
        this.settingsFile = new File(c.getFilesDir().getAbsolutePath(),fileName);
        if (!this.settingsFile.exists()) {
            writeData("", this.DEFAULT_INTERVAL_IN_MINUTES, this.DEFAULT_LOCALIZATION_TIMOUT);
        }
        readData();
    }


    private void readData() {
        String data = null;
        try {
            if (this.settingsFile.exists()) {
                FileInputStream stream = c.openFileInput(this.settingsFile.getName());
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader br = new BufferedReader(reader);

                //email
                data = br.readLine().toString();
                this.email = data;
                if ("".equals(email)) {
                    email = null;
                }

                //intervalo
                data = br.readLine().toString();
                this.interval = Integer.parseInt(data);

                //localization_timout
                data = br.readLine().toString();
                this.localizationTimeout = Integer.parseInt(data);
            }
        }
        catch (Exception e) {
            Toast.makeText(c.getApplicationContext(), "Nao conseguiu ler arquivo", Toast.LENGTH_LONG);
        }
    }

    public String getEmail() {
        return email;
    }

    public int getValorTotalIntervalo() {
        return interval;
    }

    public int getLocalizationTimeout () {
        return this.localizationTimeout;
    }

    private void update(String email, int interval, int localizationTimeout) {
        this.email = email;
        this.interval = interval;
        this.localizationTimeout = localizationTimeout;
    }

    private boolean isThereRecordingSettings() {
        return this.settingsFile.exists();
    }


    public void writeData(String email, int valorTotalIntervalo, int localizationTimeout)  {

        try {
            FileOutputStream fos = c.openFileOutput(this.settingsFile.getName(), c.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));
            writer.print(email + "\n" + valorTotalIntervalo + "\n" + localizationTimeout);
            writer.close();
            this.update(email, valorTotalIntervalo, localizationTimeout);

        }
        catch (Exception e) {
            Toast.makeText(c.getApplicationContext(), "Nao conseguiu escrever arquivo", Toast.LENGTH_LONG);
        }
    }


}

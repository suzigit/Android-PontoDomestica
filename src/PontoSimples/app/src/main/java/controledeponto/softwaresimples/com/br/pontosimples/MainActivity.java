package controledeponto.softwaresimples.com.br.pontosimples;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    private HandleTimeSheetStorage handleTimesheetStorage;
    private HandleSettingsStorage handleSettingsStorage;
    private HandleLocation handleLocation;

    private Button buttonStart;
    private Button buttonStop;
    private TextView startTextDateTime;
    private TextView stopTextDateTime;

    private DateTimeMarking dateTimeMarking;
    private boolean isRunning;

    private LocationListener mLocationListenerStart = new LocationListener() {

        // Called back when location changes

        public void onLocationChanged(Location l) {
            dateTimeMarking.setStartLocation(l);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // NA
        }
        public void onProviderEnabled(String provider) {
            // NA
        }
        public void onProviderDisabled(String provider) {
        }
    }; //fecha Listener

    private LocationListener mLocationListenerStop = new LocationListener() {

        // Called back when location changes

        public void onLocationChanged(Location l) {
            dateTimeMarking.setStopLocation(l);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // NA
        }
        public void onProviderEnabled(String provider) {
            // NA
        }
        public void onProviderDisabled(String provider) {

        }
    }; //fecha Listener




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        startTextDateTime = (TextView) findViewById(R.id.startTextDateTime);
        stopTextDateTime = (TextView) findViewById(R.id.stopTextDateTime);

        this.handleTimesheetStorage = new HandleTimeSheetStorage(getApplicationContext());
        this.handleSettingsStorage = HandleSettingsStorage.getInstance(getApplicationContext());
        this.handleLocation = HandleLocation.getInstance(getApplicationContext());

        // Reset instance state on reconfiguration
        if (savedInstanceState == null) { //primeira vez
            this.dateTimeMarking = new DateTimeMarking();
            this.isRunning = false;
        } else {
            this.dateTimeMarking = savedInstanceState.getParcelable("DATE_TIME_MARKING");
            this.isRunning = savedInstanceState.getBoolean("IS_RUNNING");
        }

        setUIItems();


        buttonStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isRunning == false) {
                    isRunning = true;
                    dateTimeMarking.setStart();
                    handleLocation.getLocation(mLocationListenerStart);
                    dateTimeMarking.setStopLocation(null);
                    setUIItems();
                }
            }
        });


        buttonStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isRunning == true) {
                    isRunning = false;
                    dateTimeMarking.setStop();
                    dateTimeMarking.setIntervalValueInMinutes(handleSettingsStorage.getValorTotalIntervalo());

                    if (!handleLocation.getLocation(mLocationListenerStop)) {
                        //adiciona marcacao ao arquivo
                        handleTimesheetStorage.writeData(dateTimeMarking);
                        setUIItems();
                    } else {


                        Executors.newScheduledThreadPool(1).schedule(new Runnable() {

                            @Override
                            public void run() {

                                //adiciona marcacao ao arquivo
                                handleTimesheetStorage.writeData(dateTimeMarking);


                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setUIItems();
                                    }
                                });

                            }
                        }, handleSettingsStorage.getLocalizationTimeout(), TimeUnit.SECONDS);
                    }
                }
            }
        });
    }

    private void setUIItems() {
        if (this.isRunning==true) {
            buttonStart.setBackgroundColor(Color.GRAY);
            buttonStop.setBackgroundColor(Color.RED);

        } else {
            buttonStart.setBackgroundColor(Color.GREEN);
            buttonStop.setBackgroundColor(Color.GRAY);
        }
        startTextDateTime.setText(getString(R.string.time_start_text) + " " + dateTimeMarking.getStartDateTime());
        stopTextDateTime.setText(getString(R.string.time_stop_text) + " " + dateTimeMarking.getStopDateTime());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getSettings();
            return true;
        } else if (id == R.id.send_email) {
            if (this.handleSettingsStorage.getEmail()!=null) {
                int valorTotalIntervalo = this.handleSettingsStorage.getValorTotalIntervalo();
                String email = this.handleSettingsStorage.getEmail();
                String data = this.handleTimesheetStorage.getData(valorTotalIntervalo);
                this.execute(email, data);
            }
            else {
                getSettings();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSettings () {
        Intent i = new Intent (Intent.ACTION_PICK);

        // Create Intent object for picking data from Contacts database
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (null != this.dateTimeMarking) {
            savedInstanceState.putParcelable("DATE_TIME_MARKING",this.dateTimeMarking);
        }
        savedInstanceState.putBoolean("IS_RUNNING", this.isRunning);

        super.onSaveInstanceState(savedInstanceState);
    }

    /** Nao pode enviar os dados em anexo porque o email nao tem acesso aos dados do aplicativo.
     * E tambem nao pode salvar em storage externo para nao ter risco de o arquivo ser alterado por outro programa.
     */
    public void execute (String emailTo, String data) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        String[] recipients = new String[1];
        recipients[0] = emailTo;
        i.putExtra(Intent.EXTRA_EMAIL, recipients);
        i.putExtra(Intent.EXTRA_SUBJECT, "Dados de Ponto Solicitados");


        i.putExtra(Intent.EXTRA_TEXT, data);

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }




}

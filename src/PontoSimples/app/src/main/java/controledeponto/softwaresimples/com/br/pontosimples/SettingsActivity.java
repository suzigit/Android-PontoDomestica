package controledeponto.softwaresimples.com.br.pontosimples;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class SettingsActivity extends Activity {


    private Button buttonOk;
    private EditText emailText;
    private RadioGroup valorIntervaloRG;

    private RadioButton valorSemIntervalo;
    private RadioButton valorIntervalo30;
    private RadioButton valorIntervalo60;
    private HandleSettingsStorage handleSettingsStorage;

    private EditText etTimout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.handleSettingsStorage = HandleSettingsStorage.getInstance(getApplicationContext());

        buttonOk = (Button) findViewById(R.id.buttonOk);
        emailText = (EditText) findViewById(R.id.email_address);
        valorIntervaloRG = (RadioGroup) findViewById(R.id.intervalo);
        valorSemIntervalo = (RadioButton) findViewById(R.id.rbIntervaloSemIntervalo);
        valorIntervalo30 = (RadioButton) findViewById(R.id.rbIntervalo30);
        valorIntervalo60 = (RadioButton) findViewById(R.id.rbIntervalo60);

        etTimout = (EditText) findViewById(R.id.localization_timeout);
        etTimout.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "60")});
        etTimout.setText(handleSettingsStorage.getLocalizationTimeout() + "");

        if (this.handleSettingsStorage.getEmail()!=null){
            this.emailText.setText(this.handleSettingsStorage.getEmail());
        }
        int valorTotalIntervalo = this.handleSettingsStorage.getValorTotalIntervalo();
        this.valorIntervaloRG.check(this.getCheckedRadioButton(valorTotalIntervalo));


        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterClicked();
            }
        });
    }

    private final int getCheckedRadioButton (int valorTotalIntervalo) {
        int idCheckedRadioButton = this.valorSemIntervalo.getId();
        if (valorTotalIntervalo == 30) {
            idCheckedRadioButton = this.valorIntervalo30.getId();
        }
        else if (valorTotalIntervalo == 60) {
            idCheckedRadioButton = this.valorIntervalo60.getId();
        }
        return idCheckedRadioButton;
    }

    private final int getValorTotalIntervalo (int checkedRadioButtonId) {
        int valorTotalIntervalo = 0;
        if (this.valorIntervalo30.getId() == checkedRadioButtonId) {
            valorTotalIntervalo = 30;
        }
        else if (this.valorIntervalo60.getId() == checkedRadioButtonId) {
            valorTotalIntervalo = 60;
        }
        return valorTotalIntervalo;
    }


    private void enterClicked()  {
        int interval = getValorTotalIntervalo(valorIntervaloRG.getCheckedRadioButtonId());
        String email = emailText.getText().toString();
        int localizationTimeout = Integer.parseInt(etTimout.getText().toString());
        handleSettingsStorage.writeData(email, interval, localizationTimeout);

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }


}

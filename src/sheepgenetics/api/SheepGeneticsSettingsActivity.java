package sheepgenetics.api;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SheepGeneticsSettingsActivity extends Activity implements OnClickListener {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
            	
                setContentView(R.layout.settings);
                Button okButton = (Button)findViewById(R.id.settingsOKButton);
                Button cancelButton = (Button)findViewById(R.id.settingsCancelButton);
                okButton.setOnClickListener(this);
                cancelButton.setOnClickListener(this);

            	
            	
            	
	}
	@Override
	public void onStart() {
		super.onStart();
        EditText userTxt = (EditText)findViewById(R.id.userName);
    	EditText passTxt = (EditText)findViewById(R.id.passWord);
    	EditText servTxt = (EditText)findViewById(R.id.serverName);
    	userTxt.setText(SheepGeneticsDemoActivity.Username);
    	passTxt.setText(SheepGeneticsDemoActivity.Password);
    	servTxt.setText(SheepGeneticsDemoActivity.Server);
    	
		
	}
	protected void cancelPressed() {
		// exit this screen and go back to main activity discarding text

	}
	protected void okPressed() {
		// exit this screen and save the settings
    	EditText userTxt = (EditText)findViewById(R.id.userName);
    	EditText passTxt = (EditText)findViewById(R.id.passWord);
    	EditText servTxt = (EditText)findViewById(R.id.serverName);

        SheepGeneticsDemoActivity.Username = (String) userTxt.getText().toString();
        SheepGeneticsDemoActivity.Password = (String) passTxt.getText().toString();
        SheepGeneticsDemoActivity.Server = (String) servTxt.getText().toString();
        
	}
	public void onClick(View arg0) {
		// if ok was pressed, save the settings
		if (arg0.equals((Button)findViewById(R.id.settingsOKButton)))
		{
			
			okPressed();
		}
        this.finish();
	}

	
	
}

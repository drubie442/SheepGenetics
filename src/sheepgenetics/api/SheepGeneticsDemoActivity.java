package sheepgenetics.api;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
public class SheepGeneticsDemoActivity extends Activity implements OnClickListener, OnLongClickListener {
	
	public static final String PREFS_NAME = "MyPrefsFile";

	public static String Username;
	public static String Password;
	public static String Server;
	enum msgTypes { ERROR, ANALYSIS, ANIMAL, ANIMALS, FLOCK, BREEDER };
	
	public int msgTypesAsInt(msgTypes aMsgType)
	{
		switch (aMsgType)
		{
		case ERROR:
			return 0;
		case ANALYSIS:
			return 1;
		case ANIMAL:
			return 2;
		case ANIMALS:
			return 3;
		case FLOCK:
			return 4;
		case BREEDER:
			return 5;
			
		}
		return -1; // not found
	}
	public msgTypes msgTypeFromInt(int aMsgInt)
	{
		switch (aMsgInt)
		{
		case 0:
			return msgTypes.ERROR;
		case 1:
			return msgTypes.ANALYSIS;
		case 2:
			return msgTypes.ANIMAL;
		case 3:
			return msgTypes.ANIMALS;
		case 4:
			return msgTypes.FLOCK;
		case 5:
			return msgTypes.BREEDER;
		}
		return msgTypes.ERROR;
	}
	SheepGeneticsNetService sgns;
	String lastError;
	public Handler hRefresh;  // handler in main activity thread for updating the gui
	public JSONObject jObj;  // temporary storage for objects coming up from the network requests
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // restore user preferences
     // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Server = settings.getString("Server", "guest");
        Username = settings.getString("Username", "guest");
        Password = settings.getString("Password", "sgsearchqa.sheepgenetics.org.au");
        
        // create net service
    	sgns = new SheepGeneticsNetService();
        // create the handler in this thread for handling gui updates
        hRefresh = new Handler(){
        	@Override
        	public void handleMessage(Message msg) {

        	            /*Refresh UI*/
        	            updateGUIState(msgTypeFromInt(msg.what));
        	}
        	};
        	
        setContentView(R.layout.main);
        Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }
    
    // implement the OnLongClickListener callback
    public boolean onLongClick(View v)
    {
    	TextView srcText = (TextView) v;
    	String newSearchAnimal = srcText.getText().toString().substring(0, 16);
    	EditText animalIdTxt = (EditText)findViewById(R.id.editText1);
    	//animalIdTxt.setText(newSearchAnimal);
    	sgns.AnimalRequest(this,Server, 5,newSearchAnimal,Username,Password);
    	return true;
    	
    }
    // Implement the OnClickListener callback
    public void onClick(View v) {
      // do something when the button is clicked
    	
    	// first change the button

    	
    	EditText animalIdTxt = (EditText)findViewById(R.id.editText1);
    	sgns.AnimalRequest(this,Server, 5,(String) animalIdTxt.getText().toString().substring(0, 16),Username,Password);
    	
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // pass in a mapped enum that indicates what kind of update has been received
    public void updateGUIState(msgTypes aMsgType)
    {
    	Drawable dr = getResources().getDrawable( R.drawable.cell_shape );
    	Drawable dri = getResources().getDrawable( R.drawable.hilight_cell_shape );
    	LinearLayout animalDetails = (LinearLayout) findViewById(R.id.animalDetails);
    	TableLayout breedingValueDetails = (TableLayout) findViewById(R.id.breedingValueDetails);
    	//tbl.setStretchAllColumns(true);
    	// clear all the rows out of the table
    	animalDetails.removeAllViews();
    	breedingValueDetails.removeAllViews();
    	//LinearLayout tbRow = new LinearLayout(this);
    	TextView tvid;
    	
    	 switch(aMsgType)
         {
         case ERROR: //error
        	tvid = new TextView(this);
        	tvid.append(lastError);

        	animalDetails.addView(tvid);
         	break;
         case ANIMAL:

         	try
         	{
         		//TableLayout animalTable = new TableLayout(this);
		    	// use two tables to display pedigree
		    	// make the id's clickable
	    		TableLayout idTable = new TableLayout(this);
	    		TableLayout sireTable = new TableLayout(this);
	    		//TableLayout damTable = new TableLayout(this);
	    		
	    		  ImageView malePic = new ImageView(this);
	    		  ImageView femalePic = new ImageView(this);
	    		  malePic.setImageResource(R.drawable.male);
	    		  femalePic.setImageResource(R.drawable.female);
	    		  malePic.setAdjustViewBounds(true);
	    		  femalePic.setAdjustViewBounds(true);
	    		  malePic.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
	    	    		  LayoutParams.WRAP_CONTENT));
	    		  	    		  femalePic.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
	    		  LayoutParams.WRAP_CONTENT));
	    		  
	    		TextView idText = new TextView(this);
	    		TextView flockText = new TextView(this);
	    		TextView sireidText = new TextView(this);
	    		TextView sireflockText = new TextView(this);
	    		TextView damidText = new TextView(this);
	    		
		    	
		    	
		    	String animalId = jObj.getString("animalid");
		    	String flock = jObj.getString("studname");
		    	String sire = jObj.getString("sire");
		    	String sirestud = jObj.getString("sirestudname");
		    	String dam = jObj.getString("dam");
		    	String sex = jObj.getString("sex");
		    	if (sex.startsWith("1"))
		    	{
		    		animalDetails.addView(malePic);
		    	}
		    	else
		    	{
		    		animalDetails.addView(femalePic);
		    	}
		    	idText.append(animalId);
		    	idText.setClickable(true);
		    	idText.setOnLongClickListener(this);
		    	flockText.append(flock);
		    	sireidText.append(sire);
		    	sireidText.setClickable(true);
		    	sireidText.setOnLongClickListener(this);
		    	sireflockText.append(sirestud);
		    	if (dam.startsWith(" "))
		    	{
		    		damidText.append("---------------");
		    	}
		    	else
		    	{
		    		damidText.append(dam);
		    		damidText.setClickable(true);
		    	}
		    	//damidText.textStyle(italic);
		    	idTable.addView(idText);
		    	idTable.addView(flockText);
		    	animalDetails.addView(idTable);
		    	
		    	sireTable.addView(sireidText);
		    	sireTable.addView(sireflockText);
		    	sireTable.addView(damidText);
		    	animalDetails.addView(sireTable);


		    	
		    	// now loop over breeding values, three per line
		
		    	TableRow bvRow = new TableRow(this);
		    	TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		    	
		    	lp.span = 1;
		    	for (int i=0; i < jObj.getJSONArray("BreedingValues").length(); i++)
		    	{
		    		lp.column = i % 3;
		    		TableLayout bvTable = new TableLayout(this);
		    		if (lp.column == 0)
		    		{
		    			// anything divisible by 3 means new row
		    			breedingValueDetails.addView(bvRow);
		    			bvRow = new TableRow(this);
		    		}
		    		TableRow thisBVRow = new TableRow(this);
		    		TableRow thisBVAccRow = new TableRow(this);
		    		TableRow thisBVValRow = new TableRow(this);
		    		
		    		TextView thisBVText = new TextView(this);
		    		TextView thisBVAccText = new TextView(this);
		    		TextView thisBVValText = new TextView(this);
		    		JSONObject bvObj = jObj.getJSONArray("BreedingValues").getJSONObject(i);
		    		thisBVText.append(bvObj.getString("name"));
		    		thisBVValText.append(((Double) bvObj.getDouble("value")).toString());
		    		thisBVAccText.append(((Integer) bvObj.getInt("accuracy")).toString());
		    		thisBVAccText.append("%");
		    		thisBVRow.addView(thisBVText);
		    		thisBVValRow.addView(thisBVValText);
		    		thisBVAccRow.addView(thisBVAccText);
		    		bvTable.addView(thisBVRow);
		    		bvTable.addView(thisBVValRow);
		    		bvTable.addView(thisBVAccRow);
		    		if (bvObj.getInt("percentile") == 1)
		    		{
		    			bvTable.setBackgroundDrawable(dri);
		    		}
		    		else
		    		{
		    			bvTable.setBackgroundDrawable(dr);
		    		}
		    		bvTable.setPadding(1, 1, 1, 1);
		    		bvRow.setPadding(1,1,1,1);
		    		//bvRow.setBackgroundDrawable(dr);
		    		bvRow.addView(bvTable,lp);
		    		
		    	}
		    	breedingValueDetails.addView(bvRow);
		    	breedingValueDetails.setColumnStretchable(0, true);
		    	breedingValueDetails.setColumnStretchable(1, true);
		    	breedingValueDetails.setColumnStretchable(2, true);
         	}
        	catch(JSONException jse)
        	{
            	tvid = new TextView(this);
            	tvid.append(jse.getMessage());

            	animalDetails.addView(tvid);
        	}
    	
        }
     }
    
    public void onError(String msg)
    {
    	lastError = msg;
    	hRefresh.sendEmptyMessage(msgTypesAsInt(msgTypes.ERROR));
    }
    public void onReceive(JSONObject ajobj)
    {
       	jObj = ajobj;
       	hRefresh.sendEmptyMessage(msgTypesAsInt(msgTypes.ANIMAL));
    }
    public void showSettings()
    {
    	Intent intent = new Intent(this, SheepGeneticsSettingsActivity.class);
    	startActivity(intent);
    }
    /*
    @Override
    public void onPause()
    {
    	super.onPause();
    	// save the user pass and server
		SharedPreferences settings = getSharedPreferences(SheepGeneticsDemoActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
	    editor.putString("Username", Username);
	    editor.putString("Server", Server);
        editor.putString("Password", Password);
        editor.commit();
    }
    */

}
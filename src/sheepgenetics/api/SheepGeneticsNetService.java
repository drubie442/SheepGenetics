package sheepgenetics.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SheepGeneticsNetService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String AnimalRequest(final SheepGeneticsDemoActivity act,
								final String server,
			                    final int analysisId,
			                    final String animalId,
			                    final String username,
			                    final String password)
	{
	
		new Thread(new Runnable() {
		    //Thread to stop network calls on the UI thread
		    public void run() {
		    	String msg;
		try
		{
		DefaultHttpClient hc = new DefaultHttpClient();
		hc.getCredentialsProvider().setCredentials( new AuthScope(server, AuthScope.ANY_PORT), new UsernamePasswordCredentials(username, password));
		HttpGet get = new HttpGet("http://" + server + "/api/query/animal/" + analysisId + "/sgid/" + animalId + ".json");

		HttpResponse rp = hc.execute(get);
		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(rp.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			}
			try {
			JSONTokener tokener = new JSONTokener(builder.toString());
			JSONObject finalResult = new JSONObject(tokener);
			act.onReceive(finalResult);
			}
			catch(JSONException jse)
			{
				msg = jse.getMessage();
				act.onError(builder.toString());
			}
			}
		else
		{
			act.onError(rp.getStatusLine().getReasonPhrase());
		}
		}catch(IOException e){
				msg = e.getMessage();
				act.onError(msg);
		}
		    }
		}).start();
		
		return "Please Wait...";
	}

}

package badner.contact.search;

import java.util.ArrayList;

import badner.contact.search.R;


import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.speech.RecognizerIntent;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	
	private TextView resultText;
	private TextView numberText;

	private SearchView searchView;
	
	private ImageView call;
	private ImageView sms;
	private String number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		resultText = (TextView)findViewById(R.id.searchViewResult);
		numberText = (TextView)findViewById(R.id.textView1);
		searchView =(SearchView)findViewById(R.id.searchView);
		searchView.setIconified(false);
		
		
		setupSearchView();
		
		
		call =(ImageView)findViewById(R.id.imageViewcall);
		call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				   String phoneCallUri ="tel:"+number;
				   Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
				   phoneCallIntent.setData(Uri.parse(phoneCallUri));
				   startActivity(phoneCallIntent);
				
				
			}
		});
		
		sms =(ImageView)findViewById(R.id.imageViewsms);
		sms.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				
				String uri= "smsto:"+number;
	            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
	            //intent.putExtra("sms_body", "outCipherText");
	            intent.putExtra("compose_mode", true);
	            startActivity(intent);
	            finish();
				
				
			
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

			//If Voice recognition is successful then it returns RESULT_OK
			if(resultCode == RESULT_OK) {

				
				
				ArrayList<String> textMatchList = data
				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                
				
				
				//אם יש מילת חיפוש
				if (!textMatchList.isEmpty()) {
					String tmpstr;
					tmpstr=textMatchList.get(0);
					searchView.setTag(tmpstr);
					//setupSearchView();
					//searchView.setSearchableInfo(tmpstr);
					

				}
			//Result code for various error.	
			}else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
				showToastMessage("Audio Error");
			}else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
				showToastMessage("Client Error");
			}else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
				showToastMessage("Network Error");
			}else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
				showToastMessage("No Match");
			}else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
				showToastMessage("Server Error");
			}
		super.onActivityResult(requestCode, resultCode, data);
	}
	void showToastMessage(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	
	//לחיפוש בספר טלפונים
	private void setupSearchView() {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) findViewById(R.id.searchView);
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (ContactsContract.Intents.SEARCH_SUGGESTION_CLICKED.equals(intent.getAction())) {
			//handles suggestion clicked query
			String displayName = getDisplayNameForContact(intent);
			resultText.setText(displayName);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			//resultText.setText("should search for query: '" + query + "'...");
			
			//String displayName = getDisplayNameForContact(intent);
			resultText.setText(query);
			 searchPhoneNumber(query);
			
			
			//למספר
			
		}
	}

	private String getDisplayNameForContact(Intent intent) {
		
		//לשם שמחפשים
		Cursor phoneCursor = getContentResolver().query(intent.getData(), null, null, null, null);
		phoneCursor.moveToFirst();
		int idDisplayName = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		String name = phoneCursor.getString(idDisplayName);
		
		
		       phoneCursor.close();//לשם 
		       searchPhoneNumber(name);
		return name;
	}
	
	private  void  searchPhoneNumber(String name){
      
		//למספר לפי השם שמצאנו
		//או ששלחנו
		ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
            "DISPLAY_NAME = '" + name + "'", null, null);
        if (cursor.moveToFirst()) {
            String contactId =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //
            //  Get all phone numbers.
            //
            Cursor phones = cr.query(Phone.CONTENT_URI, null,
                Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
                switch (type) {
                    case Phone.TYPE_HOME:
                        // do something with the Home number here...
                        break;
                    case Phone.TYPE_MOBILE:
                        // do something with the Mobile number here...
                    	numberText.setText(number);
                    	break;
                    case Phone.TYPE_WORK:
                        // do something with the Work number here...
                        break;
                    default: 
                    	numberText.setText("no number this name");
                    break;
                     
                    }
            }
           
            phones.close();
		
		
	} else{numberText.setText("no number this name");}
        cursor.close();//למספר
 
	}
}


	


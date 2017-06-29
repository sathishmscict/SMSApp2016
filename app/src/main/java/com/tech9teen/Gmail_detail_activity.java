package com.tech9teen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tech9teen.R;

import java.util.HashMap;

/**
 * @author manish
 * 
 */
public class Gmail_detail_activity extends AppCompatActivity {
	ImageView imageProfile;
	TextView textViewName, textViewEmail, textViewGender, textViewBirthday;
	String textName, textEmail, textGender, textBirthday, userImageUrl;
	private Button buttonsignin;
	private SessionManager sessionmanager;
	private Context context=this;
	private HashMap<String, String> userDetails;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gmail_detail);

		textViewName=(EditText)findViewById(R.id.edtusername);
		textViewEmail=(EditText)findViewById(R.id.edtemail);
		buttonsignin=(Button)findViewById(R.id.btnsignup);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		/**
		 * get user email using intent
		 */
		sessionmanager = new SessionManager(context);

		userDetails = new HashMap<String, String>();

		userDetails = sessionmanager.getUserDetails();

		textViewName.setText(userDetails.get(SessionManager.KEY_USERNAME));
		textViewEmail.setText(userDetails.get(SessionManager.KEY_EMAIL));



		setTitle("Sign Up");
		buttonsignin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//sessionmanager.checkloginornot();
				Intent in= new Intent(Gmail_detail_activity.this,MenuActivity.class);
				startActivity(in);
				finish();
			}
		});
	}
}
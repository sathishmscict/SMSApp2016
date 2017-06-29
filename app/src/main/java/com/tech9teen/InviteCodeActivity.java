package com.tech9teen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.app.MyApplication;
import com.tech9teen.helper.AllKeys;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class InviteCodeActivity extends AppCompatActivity {

	private EditText txtreferalcode;
	private TextView lblsubmit;
	private TextView txtnext;
	private SessionManager session;
	private HashMap<String, String> userDetails;
	private String referalcode = "";
	private String responseresult="";
	private Context context=this;
	private Toolbar toolbar;
	private CoordinatorLayout coordinatorLayout;
	private String TAG = InviteCodeActivity.class.getSimpleName();
	private SpotsDialog spotDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invitecode);


		spotDialog = new SpotsDialog(context);
		spotDialog.setCancelable(false);


		coordinatorLayout = (CoordinatorLayout) findViewById(R.id
				.coordinatorLayout);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		try {

			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} catch (Exception e) {
			Log.d("Error Actionbar", "" + e.getMessage());

		}

		//setTitle("Invite Code");

		session = new SessionManager(getApplicationContext());

		userDetails = new HashMap<String, String>();

		userDetails = session.getUserDetails();

		txtreferalcode = (EditText) findViewById(R.id.editText);
		lblsubmit = (TextView) findViewById(R.id.submit);
		txtnext = (TextView) findViewById(R.id.txtnext);

		final SpannableString content = new SpannableString("SKIP");
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		txtnext.setText(content);

		txtnext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						DashBoardActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

			}
		});

		lblsubmit.setOnClickListener(new View.OnClickListener() {
			

			@Override
			public void onClick(View v) {

				if (txtreferalcode.getText().toString().equals("")) {
					Animation shake = AnimationUtils.loadAnimation(InviteCodeActivity.this, R.anim.shake);
    				findViewById(R.id.editText).startAnimation(shake);
					/*Toast.makeText(getApplicationContext(),
							"Please Enter Referal Code", Toast.LENGTH_SHORT)
							.show();*/

					Snackbar.make(coordinatorLayout,"Please Enter Referal Code" , Snackbar.LENGTH_SHORT).show();

				} else if (txtreferalcode.getText().toString().length() != 8) {
					Animation shake = AnimationUtils.loadAnimation(InviteCodeActivity.this, R.anim.shake);
    				findViewById(R.id.editText).startAnimation(shake);
					/*Toast.makeText(getApplicationContext(),
							"Invalid Referal code,try again", Toast.LENGTH_LONG)
							.show();*/
					Snackbar.make(coordinatorLayout,"Invalid Referal code,try again",Snackbar.LENGTH_SHORT).show();
				} else {
					referalcode = txtreferalcode.getText().toString();


					if(NetConnectivity.isOnline(context))
					{
						//new InviteCodeVerification().execute();


						SendInviteCodeDetailsToServer();
					}
					else
					{
						checkInternet();
					}



				}

				// Toast.makeText(getApplicationContext() ,
				// "Please wait..",Toast.LENGTH_LONG).show();

			}
		});

	}

	private void SendInviteCodeDetailsToServer() {

		showDialog();

		String url_sendInviteDetails = AllKeys.TAG_WEBSITE_HAPPY+ "/GetCheckRefcode?action=checkrefcode&userid="+ userDetails.get(SessionManager.KEY_USERID) +"&refcode="+ txtreferalcode.getText().toString() +"";
		Log.d("URL Invite Code",""+url_sendInviteDetails);


		StringRequest str_invitecode = new StringRequest(Request.Method.GET, url_sendInviteDetails, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				Log.d(TAG , "Response GetCheckRefcode : "+response.toString());


				//Referral Code does not match.
				if(response.toString().equals("1"))
				{



					Snackbar.make(coordinatorLayout , "Your invite has been success",Snackbar.LENGTH_LONG).show();
					Toast.makeText(context, "Your invite has been success", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(context,DashBoardActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);

					finish();
					overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

				}
				else
				{

					Animation shake = AnimationUtils.loadAnimation(InviteCodeActivity.this, R.anim.shake);
					findViewById(R.id.editText).startAnimation(shake);
					Snackbar.make(coordinatorLayout, "" + response.toString(), Snackbar.LENGTH_SHORT).show();


				}


				hideDialog();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.d(TAG , "Error in Invite Code  :"+error.getMessage());

				hideDialog();
			}
		});

		MyApplication.getInstance().addToRequestQueue(str_invitecode);


	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_referalcode, menu);
		return true;
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*private class InviteCodeVerification extends AsyncTask<Void, Void, Void> {
		private ProgressDialog pDialog;
		private String vresval;

		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(InviteCodeActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// /Write here statements

			try {



				//String UserRechargeDetails = AllKeys.TAG_REFERAL_CHECK+ "?action=checkrefcode";
				String UserInviteDetails = Allkeys.TAG_WEBSITE+ "GetCheckRefcode?action=checkrefcode&custid="+ userDetails.get(SessionManager.KEY_USERID) +"&refcode="+ referalcode +"&userrefcode="+ userDetails.get(SessionManager.KEY_REFERAL_CODE) +"";
				Log.d("URL Invite Code",""+UserInviteDetails);

				ServiceHandler sh = new ServiceHandler();
				responseresult = sh.makeServiceCall(UserInviteDetails , ServiceHandler.GET);
				Log.d("Response Invite Friends", responseresult);
				
				
				
				
				
				
				// res = ans.toString();

			} catch (Exception e) {
			
				e.printStackTrace();
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();


			try
			{
				//Toast.makeText(getApplicationContext(), "result : "+responseresult, Toast.LENGTH_SHORT).show();
			
				//Referral Code does not match.
				if(responseresult.toString().equals("1"))
				{
		*//*			AlertDialog.Builder alert = new AlertDialog.Builder(context);
					alert.setIcon(R.drawable.ic_launcher);
					alert.setCancelable(false);
					alert.setTitle("YourCare");
					alert.setMessage(
							"Your invite has been success");

					alert.setNeutralButton("OK", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(context,MenuActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent);

								finish();
							overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
						}
					});

					alert.show();
		*//*

					Snackbar.make(coordinatorLayout , "Your invite has been success",Snackbar.LENGTH_LONG).show();

					Intent intent = new Intent(context,MenuActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);

					finish();
					overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

				}
				else
				{
					//Intent ii = new Intent(getApplicationContext(),
						//	InviteCodeActivity.class);
					//startActivity(ii);
					//finish();
					
					Animation shake = AnimationUtils.loadAnimation(InviteCodeActivity.this, R.anim.shake);
    				findViewById(R.id.editText).startAnimation(shake);

					//Toast.makeText(getApplicationContext(), "" + responseresult, Toast.LENGTH_SHORT).show();

					Snackbar.make(coordinatorLayout, "" + responseresult, Snackbar.LENGTH_SHORT).show();



					
				}
				
				
			}
			catch(Exception e)
			{
				System.out.print("Error : "+e.getMessage());
			}
			
			


			// Write statement after background process execution
		}
	}
*/
	public void checkInternet() {
		Snackbar snackbar = Snackbar
				.make(coordinatorLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_LONG)
				.setAction("RETRY", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (NetConnectivity.isOnline(context)) {
							Intent intent = new Intent(context, InviteCodeActivity.class);
							startActivity(intent);
							finish();
							overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

						} else {

							Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_SHORT);
							snackbar1.show();

						}


					}
				});

		// Changing message text color
		snackbar.setActionTextColor(Color.RED);

		// Changing action button text color
		View sbView = snackbar.getView();
		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

		textView.setTextColor(Color.YELLOW);

		snackbar.show();

	}



	public void showDialog() {


		if (!spotDialog.isShowing()) {
			spotDialog.show();
		}
	}

	public void hideDialog() {
		if (spotDialog.isShowing()) {

			spotDialog.cancel();
			spotDialog.dismiss();

		}

	}




}

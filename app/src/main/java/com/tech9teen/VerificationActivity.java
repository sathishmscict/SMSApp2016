package com.tech9teen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
/*
import dbhandler;
import com.cityplusmultiplex.helper.AlertDialogManager2;*/
import com.tech9teen.R;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.helper.ServiceHandler;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class VerificationActivity extends AppCompatActivity {

	private ProgressDialog pDialog;

	private String title = "VERIFICATION PROCESS";
	private TextView txtcode;
	private TextView txtverify;
	private TextView txt;
	private TextView txtresend;
	private TextView txterror;
	private Context context = this;
	private SessionManager sessionmanager;
	private HashMap<String, String> userDetails;
	private int counter;
	private Timer timer;

	/*dbhandler dh;*/
	SQLiteDatabase sd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		/*dh = new dbhandler(getApplicationContext());
		sd = dh.getReadableDatabase();
		sd = dh.getWritableDatabase();*/
		txtcode = (TextView) findViewById(R.id.txtcode);
		txtverify = (TextView) findViewById(R.id.txtverify);
		txt = (TextView) findViewById(R.id.textView2);
		txtresend = (TextView) findViewById(R.id.txtresend);
		txterror = (TextView) findViewById(R.id.txterror);

		txtcode.setClickable(false);
		txtcode.setDuplicateParentStateEnabled(false);
		// txtcode.setEnabled(false);

		sessionmanager = new SessionManager(context);

		userDetails = new HashMap<String, String>();

		userDetails = sessionmanager.getUserDetails();

		/*counter = Integer.parseInt(userDetails
				.get(SessionManager.KEY_VERIFICATION_COUNTER));*/

		if (NetConnectivity.isOnline(context)) {
			timer = new Timer();
			TimerTask hourlyTask = new TimerTask() {
				@Override
				public void run() {
					// your code here...

					userDetails = sessionmanager.getUserDetails();

					/*try {
						userDetails.get(SessionManager.KEY_CODE);

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
					if (userDetails.get(SessionManager.KEY_RECEIVECODE)
							.length() == 4)
					{
						if (userDetails.get(SessionManager.KEY_RECEIVECODE)
								.equals(userDetails
										.get(SessionManager.KEY_CODE))) {


							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										txtcode.setText("");
										txtcode.setText(""
												+ userDetails
												.get(SessionManager.KEY_RECEIVECODE));
										sessionmanager.CheckSMSVerificationNoActivity("","1");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
							Intent in= new Intent(VerificationActivity.this,Gmail_detail_activity.class);
							startActivity(in);
							finish();

							/*
							 * if(timer != null) { timer.cancel(); timer = null;
							 * }
							 */

						}
					}

				}
			};

			// schedule the task to run starting now and then every hour...
			timer.schedule(hourlyTask, 0l, 1000 * 5); // 1000*10*60 every 10 minutes

		}

		txtresend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// String msgurl =
				// AllKeys.TAG_WEBSITE+"?action=mobverify&userid="+
				// userDetails.get(SessionManager.KEY_UID) +"&code="+ Vcode +"";

				txterror.setVisibility(View.GONE);

				if (counter >= 3) {
					String dd = ", please mail us on info@"+ getString(R.string.app_name) +" from your registered mobile no";
					/*AlertDialogManager2.showAlertDialog(context,
							getString(R.string.app_name), "Maximum Request Exceeded....",
							false);*/
					Toast.makeText(context ,dd,Toast.LENGTH_SHORT ).show();
				} else {

					new sendSmsToUser().execute();

					 }
			}
		});

		if (NetConnectivity.isOnline(context)) {
			new sendSmsToUser().execute();
		} else {
			Toast.makeText(context, "Please enable internet",
					Toast.LENGTH_SHORT).show();
		}

		txt.setText("A verification code is being sent to your mobile number "
				+ userDetails.get(SessionManager.KEY_USER_MOBILE)
				+ ". To verify your mobile number, please enter the code once it  arrives.");

		txtverify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String currentcode = txtcode.getText().toString();
				// CheckVerification(currentcode);

				if (userDetails.get(SessionManager.KEY_CODE)
						.equals(currentcode)) {

					sessionmanager.CheckSMSVerificationNoActivity("",
							"1");
					// serviceP.asmx/SetStudentVerificationStatusUpdate?type=varemp&empid=string&mobileno=string&status=string&clientid=string&branch=strin
					Intent in= new Intent(VerificationActivity.this,Gmail_detail_activity.class);
					startActivity(in);
					finish();

				} else {
					Toast.makeText(context, "Invalid code", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.verification, menu); return true; }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Toast.makeText(getApplicationContext(), "Please Complete Verification",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getApplicationContext(),
				VerificationActivity.class);

		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

	}

	private class sendSmsToUser extends AsyncTask<Void, Void, Void> {

		String Error = "";
		private String jsonStr;

		String ans = "";
		String url;
		ServiceHandler sh;
		String sendsms;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(VerificationActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@SuppressLint("LongLogTag")
		@Override
		protected Void doInBackground(Void... arg0) {
			// /Write here statements

			sh = new ServiceHandler();

			if (userDetails.get(SessionManager.KEY_CODE).equals("0")) {
				Random r = new Random();
				int code = r.nextInt(9999 - 1000) + 1000;
				Log.d("Verification Code : ", "" + code);
				/*sendsms = "radiant.dnsitexperts.com/JSON_Data.aspx?type=otp&mobile="
						+ userDetails.get(SessionManager.KEY_USER_MOBILE)
						+ "&code=".
						+ code + "";*/
				sendsms = AllKeys.WEBSITE+"GetMobileVerification?action=action&mobileno="+ userDetails.get(SessionManager.KEY_USER_MOBILE) +"&code="+ code +"";

				Log.d("URL GetJSONForOneTimeVerification : ",""+sendsms);
				sessionmanager.createUserSendSmsUrl("" + code, sendsms);

			} else {
				userDetails = sessionmanager.getUserDetails();
				sendsms = userDetails.get(SessionManager.KEY_SMSURL);
			}

			Log.d("sendsms res : ", "" + sendsms);
			String resp = sh.makeServiceCall(sendsms, ServiceHandler.GET);
			Log.d("sendsms res : ", "" + resp);

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.cancel();

			// Write statement after background process execution
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		pDialog.dismiss();

	}

}

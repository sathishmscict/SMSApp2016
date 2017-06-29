package com.tech9teen;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
/*import com.tech9teen.adapter.ContactsAdapterRecyclerView;*/
import com.tech9teen.R;
import com.tech9teen.adapter.GroupMasterAdapterRecyclerView;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.ContactsData;
import com.tech9teen.pojo.GroupMaster;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class GroupMasterActivity extends AppCompatActivity {

    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private CoordinatorLayout coordinateLayout;
    private dbhandler db;
    private SQLiteDatabase sd;
    private Button btnSaveData;
    private TextInputLayout input_group;

    private TextView txt_groupname;
    private String TAG = GroupMasterActivity.class.getSimpleName();
    private LinearLayout lladddata, llshowdata;
    private FloatingActionButton fab;
    private Menu menu;
    private MenuItem menu_sync, menu_show;
    private RecyclerView mRecyclerViewContacts;
    private ContactsAdapterRecyclerView contacts_adapter;
    private CheckBox chkSelectall;
    private RecyclerView mRecyclerViewGroups;
    private ArrayList<GroupMaster> listGroups = new ArrayList<GroupMaster>();
    private int RECORD_ID = 0;
    private GroupMasterAdapterRecyclerView adapter_group;
    private ArrayList<HashMap<String, String>> all_contacts = new ArrayList<HashMap<String, String>>();
    private ArrayList<ContactsData> listContacts = new ArrayList<ContactsData>();
    private ArrayList<Integer> selected_contacts = new ArrayList<Integer>();
    private TextView txtseleted;
    private InputMethodManager imm;
    private SpotsDialog spotDialog;
    private List<ContactsData> conactsList;

    ArrayList<Integer> selectedContacts = new ArrayList<Integer>();
    private EditText txtSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_master);


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);


        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();

        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        setTitle("Manage Group");


        btnSaveData = (Button) findViewById(R.id.btnSaveData);
        input_group = (TextInputLayout) findViewById(R.id.input_layout_edtgroup);

        txt_groupname = (TextView) findViewById(R.id.edtgroup);


        lladddata = (LinearLayout) findViewById(R.id.lladddata);
        llshowdata = (LinearLayout) findViewById(R.id.llshowdata);


        chkSelectall = (CheckBox) findViewById(R.id.chkSelectall);
        txtseleted = (TextView) findViewById(R.id.txtseleted);

        txtSearch  =(EditText)findViewById(R.id.txtSearch);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {


                Log.d(TAG ,  " Search String  : "+s.toString());
                if(!s.toString().equals("") && !s.equals(null))
                {

                    try {
                        contacts_adapter.filter(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }






            }
        });



        chkSelectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                for (int i = 0; i < listContacts.size(); i++) {

                    if (isChecked == true) {

                        listContacts.get(i).setSeleted(true);
                    } else {
                        listContacts.get(i).setSeleted(false);
                    }

                }

                contacts_adapter.notifyDataSetChanged();


            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                GetAllContactsFromDevice();

                txt_groupname.setText("");
                btnSaveData.setText("save data");


                lladddata.setVisibility(View.VISIBLE);
                llshowdata.setVisibility(View.GONE);


                fab.setVisibility(View.GONE);

                menu_show.setVisible(true);
                menu_sync.setVisible(false);


            }
        });


        lladddata.setVisibility(View.GONE);
        llshowdata.setVisibility(View.VISIBLE);


        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean errorFlag = false;
                if (txt_groupname.getText().toString().equals("")) {

                    errorFlag = true;
                    input_group.setErrorEnabled(true);
                    input_group.setError("Please enter Group name");

                } else {

                    input_group.setErrorEnabled(false);

                }

                if (errorFlag == false) {


                    if(NetConnectivity.isOnline(context))
                    {

                        if (btnSaveData.getText().toString().toLowerCase().equals("save data")) {

                            Log.d(TAG, "Insert Data called");
                            conactsList = ((ContactsAdapterRecyclerView) contacts_adapter)
                                    .getContactsList();

                            selectedContacts.clear();

                            /**
                             * Check contact has been selected or not , if contact selected then procced for next action
                             */

                            for (int i = 0; i < conactsList.size(); i++) {

                                if (conactsList.get(i).isSeleted() == true) {


                                    Log.d(TAG, "Selected : " + conactsList.get(i).getName() + "  " + conactsList.get(i).getMobilenumber());
                                    selectedContacts.add(0);

                                }
                            }

                            Log.d(TAG, "Selected  contacts : " + selectedContacts.size());

                            //Check Contacts seleted or not
                            if (selectedContacts.size() > 0)
                            {

                                /**
                                 * Get Max id for Group Master
                                 */
                                String str_maxid = "select MAX(" + dbhandler.GROUPMASTER_ID + ") from " + dbhandler.TABLE_GROUPMASTER + "";
                                Log.d(TAG, "Max Group Query : " + str_maxid);
                                Cursor cur_maxid = sd.rawQuery(str_maxid, null);
                                cur_maxid.moveToFirst();
                                int max_groupid = cur_maxid.getInt(0);
                                Log.d(TAG, "Max Group ID Before: " + max_groupid);
                                ++max_groupid;
                                Log.d(TAG, "Max Group ID After: " + max_groupid);
                                RECORD_ID = max_groupid;


                                SendCurrentGroupDetailsToServer();












                            } else {
                                Snackbar.make(coordinateLayout, "Please select Contacts", Snackbar.LENGTH_SHORT).show();
                            }


                        }//close save data if condition
                        else {

                            Log.d(TAG, "Update  Data called");
                            List<ContactsData> conactsList = ((ContactsAdapterRecyclerView) contacts_adapter)
                                    .getContactsList();
                            Log.d(TAG , "Get Data From SaveData Size : "+conactsList.size());
                            ArrayList<Integer> selectedContacts = new ArrayList<Integer>();

                            /**
                             * Check contact has been selected or not , if contact selected then procced for next action
                             */

                            for (int i = 0; i < conactsList.size(); i++) {

                                if (conactsList.get(i).isSeleted() == true) {


                                    Log.d(TAG, "Selected : " + conactsList.get(i).getName() + "  " + conactsList.get(i).getMobilenumber());
                                    selectedContacts.add(0);

                                }
                            }

                            Log.d(TAG, "Selected  contacts : " + selectedContacts.size());

                            //Check Contacts seleted or not
                            if (selectedContacts.size() > 0) {

                                /**
                                 * Get Max id for Group Master
                                 */
                            /*String str_maxid = "select * from " + dbhandler.TABLE_GROUPMASTER + "";
                            Cursor cur_maxid = sd.rawQuery(str_maxid, null);
                            int max_groupid = cur_maxid.getCount();
                            ++max_groupid;*/
                                ContentValues cv_grp = new ContentValues();

                                //cv_grp.put(dbhandler.GROUPMASTER_ID, max_groupid);
                                cv_grp.put(dbhandler.GROUPMASTER_NAME, txt_groupname.getText().toString());
                                Log.d(TAG, "GroupMaster Data : " + cv_grp.toString());
                                //sd.insert(dbhandler.TABLE_GROUPMASTER, null, cv_grp);

                                sd.update(dbhandler.TABLE_GROUPMASTER, cv_grp, dbhandler.GROUPMASTER_ID + "=" + RECORD_ID + "", null);


                                /**
                                 * First delete all records from existing table after inserting new records
                                 */
                                sd.delete(dbhandler.TABLE_GROUPMASTERDET, dbhandler.GROUPMASTER_DET_GROUPMASTER_ID + "=" + RECORD_ID + "", null);
                                Log.d(TAG, "Delete GrpDet : " + "delete from " + dbhandler.TABLE_GROUPMASTERDET + " where " + dbhandler.GROUPMASTER_DET_GROUPMASTER_ID + "=" + RECORD_ID + "");
                                sd.execSQL("delete from " + dbhandler.TABLE_GROUPMASTERDET + " where " + dbhandler.GROUPMASTER_DET_GROUPMASTER_ID + "=" + RECORD_ID + "");

                                /**
                                 * Get Max id from GroupContactsDetails
                                 */
                            /*Cursor cur_max_category_by_goal = sd.rawQuery("SELECT MAX(" + dbhandler.GOAL_ID + ") FROM " + dbhandler.TABLE_GOAL, null);
                            cur_max_category_by_goal.moveToFirst();
                            int max_goalid = cur_max_category_by_goal.getInt(0);
                            max_goalid = ++max_goalid;
                            Log.d("Max Id By Goal : ", "" + max_goalid);*/

                                String query_maxcontactid = "select MAX(" + dbhandler.GROUPMASTER_DET_ID + ") from " + dbhandler.TABLE_GROUPMASTERDET + "";
                                Cursor cur_maxcontactid = sd.rawQuery(query_maxcontactid, null);
                                cur_maxcontactid.moveToFirst();
                                int max_contactid = cur_maxcontactid.getInt(0);
                                Log.d("Max Id Of Group : ", "" + max_contactid);


                                selectedContacts.clear();
                                for (int i = 0; i < conactsList.size(); i++) {

                                    if (conactsList.get(i).isSeleted() == true) {

                                        ++max_contactid;

                                        Log.d(TAG, "Selected : " + conactsList.get(i).getName() + "  " + conactsList.get(i).getMobilenumber());
                                        selectedContacts.add(0);

                                        ContentValues cv = new ContentValues();
                                        cv.put(dbhandler.GROUPMASTER_DET_ID, max_contactid);
                                        cv.put(dbhandler.GROUPMASTER_DET_CONTACTID, conactsList.get(i).getContacactid());
                                        cv.put(dbhandler.GROUPMASTER_DET_CONTACTNAME, conactsList.get(i).getName());
                                        cv.put(dbhandler.GROUPMASTER_DET_GROUPMASTER_ID, RECORD_ID);
                                        cv.put(dbhandler.GROUPMASTER_DET_MOBILENUMBER, conactsList.get(i).getMobilenumber());

                                        Log.d(TAG, "GroupMasterDet Data :" + cv.toString());

                                        sd.insert(dbhandler.TABLE_GROUPMASTERDET, null, cv);
                                        cv.clear();


                                    }

                                }
                                String query_update = "update " + dbhandler.TABLE_GROUPMASTER + " set " + dbhandler.GROUPMASTER_CONTACTS + "=" + selectedContacts.size() + " where " + dbhandler.GROUPMASTER_ID + "=" + RECORD_ID + "";
                                Log.d(TAG, "Update Query : " + query_update);
                                sd.execSQL(query_update);
                                cv_grp.clear();


                                db.backupDB(context);


                                FillDataOnRecyclerView();
                                GetAllContactsFromDevice();
                                txt_groupname.setText("");
                                txtseleted.setText("0 selected");


                                lladddata.setVisibility(View.GONE);
                                llshowdata.setVisibility(View.VISIBLE);
                                fab.setVisibility(View.VISIBLE);

                                btnSaveData.setText("save data");
                                selected_contacts.clear();
                                selectedContacts.clear();


                                Snackbar.make(coordinateLayout, "Group has been updated", Snackbar.LENGTH_SHORT).show();


                            } else {
                                Snackbar.make(coordinateLayout, "Please select Contacts", Snackbar.LENGTH_SHORT).show();
                            }//Updata else condition closed

                        }


                    }//Close check net connectivity if condition
                    else
                    {
                        checkInternet();
                    }


                }


            }
        });


        /**
         Fill data on contact recycler view
         *
         */

        mRecyclerViewContacts = (RecyclerView) findViewById(R.id.recycler_view_contacts);

        LinearLayoutManager mlayoutManager = new LinearLayoutManager(context);
        mRecyclerViewContacts.setLayoutManager(mlayoutManager);
        mRecyclerViewContacts.setHasFixedSize(true);
        mRecyclerViewContacts.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
        mRecyclerViewContacts.setItemAnimator(new DefaultItemAnimator());


        //Fetch contacts from device
        GetAllContactsFromDevice();

        /**
         * Complete filling contact data into contact recyclerview
         */

        mRecyclerViewGroups = (RecyclerView) findViewById(R.id.recycler_view_group);

        LinearLayoutManager mlayoutManager2 = new LinearLayoutManager(context);
        mRecyclerViewGroups.setLayoutManager(mlayoutManager2);
        mRecyclerViewGroups.setHasFixedSize(true);
        mRecyclerViewGroups.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
        mRecyclerViewGroups.setItemAnimator(new DefaultItemAnimator());

        /*adapter_group = new GroupMasterAdapterRecyclerView(context, listGroups);
        mRecyclerViewGroups.setAdapter(adapter_group);*/
        FillDataOnRecyclerView();


        /**
         * RecyclerView Swiping Gesture
         */
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerViewGroups,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    /*listMessageTemplate.remove(position);
                                    adapter.notifyItemRemoved(position);*/

                                    //                             Toast.makeText(context, "Left Detected", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Left Detected");

                                    /**
                                     * Get Current Template Details edit on Controls
                                     */
                                    GroupMaster MT = listGroups.get(position);
                                    Log.d(TAG, "Edited Template Data : " + MT.toString());

                                    RECORD_ID = MT.getGroupid();

                                    txt_groupname.setText(MT.getGroupname());
                                    //txt_templatedescr.setText(MT.getTemplateDescr());


                                    String query_select_seleteddata = "Select * from " + dbhandler.TABLE_GROUPMASTERDET + " where " + dbhandler.GROUPMASTER_DET_GROUPMASTER_ID + "=" + listGroups.get(position).getGroupid() + "";
                                    Cursor cur_selecteddata = sd.rawQuery(query_select_seleteddata, null);
                                    Log.d(TAG, "Selected Contacts : " + cur_selecteddata.getCount());

                                    listContacts.clear();
                                    all_contacts.clear();
                                    selected_contacts.clear();
                                    if (cur_selecteddata.getCount() > 0) {
                                        listContacts.clear();

                                        while (cur_selecteddata.moveToNext()) {


                                            ContactsData CD = new ContactsData(cur_selecteddata.getString(cur_selecteddata.getColumnIndex(dbhandler.GROUPMASTER_DET_MOBILENUMBER)), cur_selecteddata.getString(cur_selecteddata.getColumnIndex(dbhandler.GROUPMASTER_DET_CONTACTNAME)), cur_selecteddata.getInt(cur_selecteddata.getColumnIndex(dbhandler.GROUPMASTER_DET_CONTACTID)), false);

                                            //       listContacts.add(CD);

                                            HashMap<String, String> con_data = new HashMap<String, String>();
                                            con_data.put("MOBILENUMBER", CD.getMobilenumber());
                                            con_data.put("NAME", CD.getName());
                                            con_data.put("CONTACTID", String.valueOf(CD.getContacactid()));
                                            con_data.put("SELECTED", "false");

                                            all_contacts.add(con_data);
                                            selected_contacts.add(CD.getContacactid());


                                        }

                                    }


                                    txtseleted.setText(all_contacts.size() + " selected");

                                    String query_selectall = "Select * from " + dbhandler.TABLE_CONTACTMASTER + "";
                                    Cursor cur_allcontacts = sd.rawQuery(query_selectall, null);
                                    if (cur_allcontacts.getCount() > 0) {
                                        //listContacts.clear();

                                        while (cur_allcontacts.moveToNext()) {


                                            ContactsData CD = new ContactsData(cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_MOBILE_NO)), cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_NAME)), cur_allcontacts.getInt(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_ID)), false);


                                            HashMap<String, String> con_data = new HashMap<String, String>();
                                            con_data.put("MOBILENUMBER", CD.getMobilenumber());
                                            con_data.put("NAME", CD.getName());
                                            con_data.put("CONTACTID", String.valueOf(CD.getContacactid()));
                                            con_data.put("SELECTED", "false");

                                            boolean res = all_contacts.contains(con_data);
                                            con_data.clear();

                                            //int res= listContacts.indexOf(CD);
                                            //boolean isExist = listContacts.contains(CD);
                                            if (res) {
                                                CD = new ContactsData(cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_MOBILE_NO)), cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_NAME)), cur_allcontacts.getInt(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_ID)), true);
//                                                Log.d(TAG, "Contains Data");
                                                listContacts.add(CD);
                                            } else {
                                                //                                              Log.d(TAG, "Not Contains Data");
                                                listContacts.add(CD);

                                            }


                                        }

                                    }

                                    Log.d(TAG, "Total Contacts : " + listContacts.size());


                                    lladddata.setVisibility(View.VISIBLE);
                                    llshowdata.setVisibility(View.GONE);
                                    menu_show.setVisible(true);
                                    menu_sync.setVisible(false);
                                    btnSaveData.setText("update data");
                                    fab.setVisibility(View.GONE);
                                    txtSearch.setText("");


                                    /*String check_record = "Select * from "+ dbhandler.TABLE_TEMPALTE_MASTER +" where "+ dbhandler.TEMPLATE_ID +"="+ MT.getTemplateId() +"";
                                    Cursor cur_check_record = sd.rawQuery(check_record , null);
                                    if(cur_check_record.getCount() > 0 )
                                    {
                                        while (cur_check_record.moveToNext())
                                        {
                                            txt_templatetitle.setText(MT.getTemplateTitle());
                                            txt_templatedescr.setText(MT.getTemplateDescr());

                                        }

                                    }*/


                                }
                                contacts_adapter = new ContactsAdapterRecyclerView(context, listContacts);
                                mRecyclerViewContacts.setAdapter(contacts_adapter);

                                //adapter_group.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(final RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {

                                    Toast.makeText(context, "Right Detected", Toast.LENGTH_SHORT).show();


                                    //MessageTemplate MT = listMessageTemplate.get(position).getTemplateId();


                                    if(NetConnectivity.isOnline(context))
                                    {
                                        showDialog();



                                        String url_deletegroup = AllKeys.TAG_WEBSITE_HAPPY+"/DeleteGroupMaster?action=deletegroupmaster&userid="+ userDetails.get(SessionManager.KEY_USERID) +"&groupid="+ listGroups.get(position).getGroupid() +"";
                                        Log.d(TAG , "URL Delte Group : "+url_deletegroup);
                                        StringRequest str_deleterequest =new StringRequest(Request.Method.GET, url_deletegroup, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                Log.d(TAG , "Delete Group Response : "+response.toString());


                                                if(response.toString().equals("1"))
                                                {

                                                    sd.delete(dbhandler.TABLE_GROUPMASTER, dbhandler.GROUPMASTER_ID + " = " + listGroups.get(position).getGroupid(), null);
                                                    sd.delete(dbhandler.TABLE_GROUPMASTERDET, dbhandler.GROUPMASTER_DET_GROUPMASTER_ID + "=" + listGroups.get(position).getGroupid() + "", null);

                                                    listGroups.remove(position);
                                                    adapter_group.notifyItemRemoved(position);

                                                }
                                                else
                                                {
                                                    Snackbar.make(coordinateLayout,"Sorry, group has not deleted .Try again...",Snackbar.LENGTH_SHORT).show();
                                                }


                                                hideDialog();
                                                Snackbar.make(coordinateLayout,"Record has been deleted",Snackbar.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Log.d(TAG , "Error in deleting GroupMaster : "+error.getMessage());

                                                hideDialog();
                                                Snackbar.make(coordinateLayout,"Sorry, group has not deleted .Try again...",Snackbar.LENGTH_SHORT).show();

                                            }
                                        });
                                        MyApplication.getInstance().addToRequestQueue(str_deleterequest);




                                    }
                                    else
                                    {
                                        checkInternet();
                                    }


                                }
                                adapter_group.notifyDataSetChanged();

                                db.backupDB(context);

                            }
                        });


        mRecyclerViewGroups.addOnItemTouchListener(swipeTouchListener);


    }

    private void SendCurrentGroupDetailsToServer() {


        showDialog();
        String url_sendgroupdata= AllKeys.TAG_WEBSITE_HAPPY+"/InsertGroupMaster?action=insertgroupmaster&userid="+ userDetails.get(SessionManager.KEY_USERID) +"&groupid="+ RECORD_ID +"&groupname="+ dbhandler.convertEncodedString(txt_groupname.getText().toString()) +"&noofcontact="+ selected_contacts.size() +"";
        Log.d(TAG , "Insert GroupData : "+url_sendgroupdata);
        StringRequest str_sendgroupdata = new StringRequest(Request.Method.GET, url_sendgroupdata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                Log.d(TAG , "Response insertGroupData : "+response.toString());

                if(response.equals("1"))
                {

                    ContentValues cv_grp = new ContentValues();

                    cv_grp.put(dbhandler.GROUPMASTER_ID, RECORD_ID);
                    cv_grp.put(dbhandler.GROUPMASTER_NAME, txt_groupname.getText().toString());
                    Log.d(TAG, "GroupMaster Data : " + cv_grp.toString());
                    sd.insert(dbhandler.TABLE_GROUPMASTER, null, cv_grp);


                    /**
                     * Get Max id from GroupContactsDetails
                     */
                    String query_maxcontactid = "select MAX(" + dbhandler.GROUPMASTER_DET_ID + ") from " + dbhandler.TABLE_GROUPMASTERDET + "";
                    Cursor cur_maxcontactid = sd.rawQuery(query_maxcontactid, null);
                    cur_maxcontactid.moveToFirst();
                    int max_contactid = cur_maxcontactid.getInt(0);
                    Log.d("Max Id Of Group : ", "" + max_contactid);


                    selectedContacts.clear();
                    for (int i = 0; i < conactsList.size(); i++) {

                        if (conactsList.get(i).isSeleted() == true) {

                            ++max_contactid;

                            Log.d(TAG, "Selected : " + conactsList.get(i).getName() + "  " + conactsList.get(i).getMobilenumber());
                            selectedContacts.add(conactsList.get(i).getContacactid());

                            ContentValues cv = new ContentValues();
                            cv.put(dbhandler.GROUPMASTER_DET_ID, max_contactid);
                            cv.put(dbhandler.GROUPMASTER_DET_CONTACTID, conactsList.get(i).getContacactid());
                            cv.put(dbhandler.GROUPMASTER_DET_CONTACTNAME, conactsList.get(i).getName());
                            cv.put(dbhandler.GROUPMASTER_DET_GROUPMASTER_ID, RECORD_ID);
                            cv.put(dbhandler.GROUPMASTER_DET_MOBILENUMBER, conactsList.get(i).getMobilenumber());

                            Log.d(TAG, "GroupMasterDet Data :" + cv.toString());

                            sd.insert(dbhandler.TABLE_GROUPMASTERDET, null, cv);
                            cv.clear();


                        }

                    }
                    String query_update = "update " + dbhandler.TABLE_GROUPMASTER + " set " + dbhandler.GROUPMASTER_CONTACTS + "=" + selectedContacts.size() + " where " + dbhandler.GROUPMASTER_ID + "=" + RECORD_ID + "";
                    Log.d(TAG, "Update Query : " + query_update);
                    sd.execSQL(query_update);
                    cv_grp.clear();


                    db.backupDB(context);

                    FillDataOnRecyclerView();
                    GetAllContactsFromDevice();
                    //txtseleted.setText("0 selected");
                    //txt_groupname.setText("");
                    //selected_contacts.clear();
                    //selectedContacts.clear();

                    Snackbar.make(coordinateLayout, "Group has been created", Snackbar.LENGTH_SHORT).show();



                    txtSearch.setText("");
                    txtseleted.setText("0 selected");
                txt_groupname.setText("");
                selected_contacts.clear();
                SendCurrentGroupDataDetailsToServer();

                }//Close if condition insert success



                hideDialog();



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d(TAG , "GroupData Response Error :"+error.getMessage());


                hideDialog();
                Snackbar.make(coordinateLayout,"Group hasbeen not created try again",Snackbar.LENGTH_LONG).show();

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.d("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                }

                if (error instanceof TimeoutError) {
                    Log.d("Volley", "TimeoutError");
                } else if (error instanceof NoConnectionError) {
                    Log.d("Volley", "NoConnectionError");
                } else if (error instanceof AuthFailureError) {
                    Log.d("Volley", "AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.d("Volley", "ServerError");
                } else if (error instanceof NetworkError) {
                    Log.d("Volley", "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.d("Volley", "ParseError");
                }



            }
        });
        MyApplication.getInstance().addToRequestQueue(str_sendgroupdata);

    }

    private void SendCurrentGroupDataDetailsToServer() {

        showDialog();

        final String url_groupdata = AllKeys.TAG_WEBSITE_HAPPY+"/InsertGroupMasterDetail";
        StringRequest str_groupdataDetails= new StringRequest(Request.Method.POST, url_groupdata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG , "GroupDataDetails Response : "+response.toLowerCase());

                hideDialog();





            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d(TAG , "Error in GroupDataDetails : "+error.getMessage());
                hideDialog();
                if(NetConnectivity.isOnline(context))
                {

                SendCurrentGroupDataDetailsToServer();
                }
                else
                {
                    checkInternet();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "insertgroupdetail");
                params.put("userid", userDetails.get(SessionManager.KEY_USERID));

                String query_selectcontact = "select * from "+ dbhandler.TABLE_GROUPMASTERDET +" where "+ dbhandler.GROUPMASTER_DET_GROUPMASTER_ID +"="+ RECORD_ID +"";
                Log.d(TAG , "Query select contacts : "+query_selectcontact);
                Cursor cur= sd.rawQuery(query_selectcontact , null);


                String json = "";


                Log.d(TAG, "Total " + cur.getCount() + " Records From GroupDataDetails");


                if(cur.getCount() > 0)
                {
                    while (cur.moveToNext())
                    {

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("GroupDetailId",cur.getString(cur.getColumnIndex(dbhandler.GROUPMASTER_DET_ID)));
                            jsonObject.accumulate("GroupId", cur.getString(cur.getColumnIndex(dbhandler.GROUPMASTER_DET_GROUPMASTER_ID)));
                            jsonObject.accumulate("ContactId",cur.getString(cur.getColumnIndex(dbhandler.GROUPMASTER_DET_CONTACTID)));


                            jsonObject.accumulate("ContactName", cur.getString(cur.getColumnIndex(dbhandler.GROUPMASTER_DET_CONTACTNAME)));
                            jsonObject.accumulate("MobileNo", cur.getString(cur.getColumnIndex(dbhandler.GROUPMASTER_DET_MOBILENUMBER)));

                            json = json + jsonObject.toString() + ",";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    json = json.substring(0, json.length() - 1);
                    json = "[" + json + "]";
                    Log.d("CalllogMst Data : ", json);
                    Log.d("Json Data : ", url_groupdata + "?action=insertgroupdetail&userid="+ userDetails.get(SessionManager.KEY_USERID) +"&Data=" + json);
                    params.put("Data", json);


                }

                return params;

            }
        };
        MyApplication.getInstance().addToRequestQueue(str_groupdataDetails);

    }


    /**
     * Get Contact using ASynchronus Task
     */


    public  class  GetAllContactDetailsFromDevice extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
            Log.d(TAG , "onPreExecute Called");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG , "doInBackground Called");


            String query_selectall = "Select * from " + dbhandler.TABLE_CONTACTMASTER + "";
            Cursor cur_allcontacts = sd.rawQuery(query_selectall, null);
            if (cur_allcontacts.getCount() > 0) {
                listContacts.clear();


                all_contacts.clear();
                while (cur_allcontacts.moveToNext()) {


                    ContactsData CD = new ContactsData(cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_MOBILE_NO)), cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_NAME)), cur_allcontacts.getInt(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_ID)), false);

                    listContacts.add(CD);

                    HashMap<String, String> con_data = new HashMap<String, String>();
                    con_data.put("MOBILENUMBER", CD.getMobilenumber());
                    con_data.put("NAME", CD.getName());
                    con_data.put("CONTACTID", String.valueOf(CD.getContacactid()));
                    con_data.put("SELECTED", "false");

                    all_contacts.add(con_data);
                  //  con_data.clear();



                }

            }

            Log.d(TAG, "Total Contacts M : " + listContacts.size());




            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d(TAG , "onPostExecute Called");
            contacts_adapter = new ContactsAdapterRecyclerView(context, listContacts);
            mRecyclerViewContacts.setAdapter(contacts_adapter);
            hideDialog();

        }
    }
    /**
     * Complete Filling Data using Asynchronous task
     */

    /**
     * Get all contacts from device
     */
    private void GetAllContactsFromDevice() {

       /* showDialog();
        String query_selectall = "Select * from " + dbhandler.TABLE_CONTACTMASTER + "";
        Cursor cur_allcontacts = sd.rawQuery(query_selectall, null);
        if (cur_allcontacts.getCount() > 0) {
            listContacts.clear();


            all_contacts.clear();
            while (cur_allcontacts.moveToNext()) {


                ContactsData CD = new ContactsData(cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_MOBILE_NO)), cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_NAME)), cur_allcontacts.getInt(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_ID)), false);

                listContacts.add(CD);

                HashMap<String, String> con_data = new HashMap<String, String>();
                con_data.put("MOBILENUMBER", CD.getMobilenumber());
                con_data.put("NAME", CD.getName());
                con_data.put("CONTACTID", String.valueOf(CD.getContacactid()));
                con_data.put("SELECTED", "false");

                all_contacts.add(con_data);
                con_data.clear();



            }

        }

        Log.d(TAG, "Total Contacts M : " + listContacts.size());
        contacts_adapter = new ContactsAdapterRecyclerView(context, listContacts);
        mRecyclerViewContacts.setAdapter(contacts_adapter);

        hideDialog();*/

        /*showDialog();*/

        new GetAllContactDetailsFromDevice().execute();
    }
    //Oncreate completed

    /**
     * Fill data on recycler view
     */
    private void FillDataOnRecyclerView() {

        String query_selectall_templates = "Select * from " + dbhandler.TABLE_GROUPMASTER + "";
        Log.d(TAG, "Query Select All Templates : " + query_selectall_templates);
        Cursor cur_selectalldata = sd.rawQuery(query_selectall_templates, null);
        Log.d(TAG, "Total Records :" + cur_selectalldata.getCount() + " " + dbhandler.TABLE_TEMPALTE_MASTER);
        if (cur_selectalldata.getCount() > 0) {
            listGroups.clear();

            while (cur_selectalldata.moveToNext()) {


                GroupMaster MT = new GroupMaster(cur_selectalldata.getString(cur_selectalldata.getColumnIndex(dbhandler.GROUPMASTER_NAME)), cur_selectalldata.getInt(cur_selectalldata.getColumnIndex(dbhandler.GROUPMASTER_ID)), cur_selectalldata.getInt(cur_selectalldata.getColumnIndex(dbhandler.GROUPMASTER_CONTACTS)));


                listGroups.add(MT);


            }


            adapter_group = new GroupMasterAdapterRecyclerView(context, listGroups);
            mRecyclerViewGroups.setAdapter(adapter_group);
            adapter_group.notifyDataSetChanged();


        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {

            this.menu = menu;
            getMenuInflater().inflate(R.menu.common_sync, menu);
            // sync_data = (MenuItem) menu.findItem(R.id.action_sync);


            menu_sync = (MenuItem) menu.findItem(R.id.common_sync);
            menu_show = (MenuItem) menu.findItem(R.id.common_show);

            menu_sync.setVisible(true);
            menu_show.setVisible(false);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(getApplicationContext(),
                    DashBoardActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

        } else if (item.getItemId() == R.id.common_show) {

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            fab.setVisibility(View.VISIBLE);
            lladddata.setVisibility(View.GONE);
            llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(true);
            menu_show.setVisible(false);

        } else if (item.getItemId() == R.id.common_sync) {


            //fab.setVisibility(View.VISIBLE);
            lladddata.setVisibility(View.GONE);
            llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(true);
            menu_show.setVisible(false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent intent = new Intent(getApplicationContext(),
                DashBoardActivity.class);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }


    /**
     * ContactsAdaterRecyclerview
     */


    /**
     * Created by Satish Gadde on 14-09-2016.
     */
    public class ContactsAdapterRecyclerView extends RecyclerView.Adapter<ContactsAdapterRecyclerView.MyViewHolder>
    {


        private final Context context;
        private final ArrayList<ContactsData> listContacts;
        private final LayoutInflater inflater;
        private ArrayList<ContactsData> arraylist = new ArrayList<ContactsData>();

        public ContactsAdapterRecyclerView(Context context, ArrayList<ContactsData> contacts) {

            inflater = LayoutInflater.from(context);
            this.context = context;
            this.listContacts = contacts;
            this.arraylist = new ArrayList<ContactsData>();
            this.arraylist.addAll(contacts);


        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private final CheckBox chkContactName;
            private final TextView txtMobile;


            public MyViewHolder(View itemView) {
                super(itemView);


                chkContactName = (CheckBox) itemView.findViewById(R.id.chkContactName);
                txtMobile = (TextView) itemView.findViewById(R.id.txtMobile);

            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.row_single_contact_cardview, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            final int pos = position;


            final ContactsData CD = listContacts.get(position);

            holder.chkContactName.setText(CD.getName());
            holder.txtMobile.setText(CD.getMobilenumber());


            holder.chkContactName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    try {
                        CheckBox cb = (CheckBox) buttonView;






                        HashMap<String, String> con_data = new HashMap<String, String>();
                        con_data.put("MOBILENUMBER", CD.getMobilenumber());
                        con_data.put("NAME", CD.getName());
                        con_data.put("CONTACTID", String.valueOf(CD.getContacactid()));
                        con_data.put("SELECTED", String.valueOf(CD.isSeleted()));




                                                int d = all_contacts.indexOf(con_data);

                        Log.d(TAG , "Index From listContacts  :"+all_contacts.indexOf(con_data));


                        listContacts.get(pos).setSeleted(isChecked);
                        arraylist.get(all_contacts.indexOf(con_data)).setSeleted(isChecked);








                        //contact.setSeleted(isChecked);



                        //updatePostion();

                        if (isChecked) {
                            Log.d(TAG, "Selected ");

                            if (!selected_contacts.contains(CD.getContacactid())) {
                                selected_contacts.add(CD.getContacactid());
                                Log.d(TAG, "Added in selected_contacts");

                            } else {
                                Log.d(TAG, "Already Contains selected_contacts");
                            }

                            txtseleted.setText(selected_contacts.size() + " seleted");

                        } else {

                            if (selected_contacts.contains(CD.getContacactid())) {

                                int index = selected_contacts.indexOf(CD.getContacactid());

                                selected_contacts.remove(index);
                                txtseleted.setText(selected_contacts.size() + " seleted");
                                Log.d(TAG, "Remove From Selected Contacts");
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Checkbox Errorr : " + e.getMessage());
                    }


                }
            });

            holder.chkContactName.setChecked(CD.isSeleted());


        }

        @Override
        public int getItemCount() {
            return listContacts.size();
        }

        // method to access in activity after updating selection
        public List<ContactsData> getContactsList() {

            Log.d(TAG, " ListContacts Size Before: "+listContacts.size());
            Log.d(TAG , "ArrayList Size : "+arraylist.size());

            //this.listContacts = new ArrayList<ContactsData>();
            listContacts.clear();
            this.listContacts.addAll(arraylist);

            Log.d(TAG, " ListContacts Size After: "+listContacts.size());



            return listContacts;
        }


        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            listContacts.clear();
            if (charText.length() == 0) {
                listContacts.addAll(arraylist);
            } else {
                for (ContactsData pr : arraylist) {
                    if (pr.getName().toLowerCase(Locale.getDefault()).contains(charText)) {

                        listContacts.add(pr);
                    }
                }
            }
            notifyDataSetChanged();
        }




    }

    /**
     * Complete ContactsAdapterRecyclerView
     */


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


    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinateLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, GroupMasterActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                        } else {

                            Snackbar snackbar1 = Snackbar.make(coordinateLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_SHORT);
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




}

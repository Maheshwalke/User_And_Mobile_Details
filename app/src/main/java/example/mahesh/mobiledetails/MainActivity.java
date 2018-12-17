package example.mahesh.mobiledetails;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity
{
    private static final int PERMISSION_REQUEST_CODE = 1111;
    TextView helloo,name, email, mob_name, mob_model, mob_os, mob_imei, sim_name,sim_number;
    String Name,Email,MobileName,MobileModel,MobileOS,IMEI,PhoneNumber,sim_operator_Name;
    LinearLayout parentLL;
    CheckBox send_SMS;
    String sms="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLL=(LinearLayout)findViewById(R.id.parent_ll);
        helloo = (TextView) findViewById(R.id.welcome);
        name = (TextView) findViewById(R.id.person_name);
        email = (TextView) findViewById(R.id.email_id);
        mob_name = (TextView) findViewById(R.id.mobile_device);
        mob_model = (TextView) findViewById(R.id.mobile_model);
        mob_os = (TextView) findViewById(R.id.mobile_os);
        mob_imei = (TextView) findViewById(R.id.imei_no);
        sim_name = (TextView) findViewById(R.id.sim_name);
        sim_number = (TextView) findViewById(R.id.sim_no);
        send_SMS = (CheckBox) findViewById(R.id.send_sms);

        getpermissions();
        TelephonyManager telephonyManager = null;
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String User_EmailId = null;
        String fullName = null;
        try {
            User_EmailId = getEmailID(getApplicationContext());
            System.out.println("Accounts=="+User_EmailId);
            fullName = User_EmailId.substring(0,User_EmailId.lastIndexOf("@"));
            System.out.println("Account name=="+fullName);

            Cursor c = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            c.moveToFirst();
            Name=c.getString(c.getColumnIndex("display_name"));
            System.out.println("Name==="+Name);
            c.close();

        MobileModel=android.os.Build.MODEL;
        MobileName=Build.MANUFACTURER;
        MobileOS=Build.VERSION.RELEASE;
        IMEI = telephonyManager.getDeviceId();
        System.out.println("mob name:"+MobileName+"model :"+MobileModel+" Mob os"+MobileOS);
        sim_operator_Name= telephonyManager.getSimOperatorName();
        PhoneNumber  = telephonyManager.getLine1Number();
        System.out.println("Mobile Details: IMEI:"+IMEI + " sim opeartor:"+sim_operator_Name +" phone no:"+PhoneNumber);

            if (checkPermission()) {
                helloo.setText("Hello "+Name+" !");
                name.setText(Name);
                email.setText(User_EmailId);
                mob_name.setText(MobileName);
                mob_model.setText(MobileModel);
                mob_os.setText(MobileOS);
                mob_imei.setText(IMEI);
                sim_name.setText(sim_operator_Name);
                sim_number.setText(PhoneNumber);


                sms="Hello "+Name+"\nYour Mobile device :"+MobileName +" "+MobileModel+", Android OS Version : "+MobileOS +", IMEI Number :"+IMEI;
                System.out.println("SMS==="+sms);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        send_SMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sendSMS();
                }else {

                }
            }
        });

    }

    private String getEmailID(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

    public void sendSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(PhoneNumber, null, sms, null, null);
    }

    //Requesting device permissions and Handling
    private void getpermissions() {
        if (!checkPermission()) {
            requestPermission();
        } else {
        }
    }

    //To check mobile device permissions
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), GET_ACCOUNTS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE,GET_ACCOUNTS,READ_CONTACTS,SEND_SMS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean phonestate = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean account = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean internet = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean sms = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (phonestate && account && internet && sms){
                        Toast.makeText(this, "Permission Granted, Now you can access all the features of app.", Toast.LENGTH_SHORT).show();
                    }else {
                        name.setText("--");
                        email.setText("--");
                        mob_name.setText("--");
                        mob_model.setText("--");
                        mob_os.setText("--");
                        mob_imei.setText("--");
                        sim_name.setText("--");
                        sim_number.setText("--");
                        Toast.makeText(this, "Permission Denied,Please restart the app", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
                                showMessageOKCancel("You need to allow all permissions to use features of app",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{READ_PHONE_STATE,GET_ACCOUNTS,READ_CONTACTS,SEND_SMS},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    }});return; } } } }
                break; } }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNeutralButton("Cancel", null)
                .setCancelable(false)
                // .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}

package sg.com.hal.qrlable4partidentification;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "HALQRLabelPrint";
    private EditText txtDN;
    private EditText txtDNLine;
    private TextView lblEmpty ;
    private TextView lblMessage;
    private EditText txtSlNo;
    private TextView lblMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            HeaderFragment hf = HeaderFragment.newInstance("Home");
            ft.replace(R.id.fragmentContainerView3, hf);
            ft.commit();
        }catch (Exception ee){
            Log.v(TAG, "Fragment error "+ee.getLocalizedMessage());
        }


        txtDNLine = findViewById(R.id.txtDNLine);
        txtSlNo = findViewById(R.id.txtSlNo);
        lblEmpty = findViewById(R.id.lblEmpty);
        lblMessage = findViewById(R.id.lblMessage);
        lblEmpty.setText("");
        lblMessage.setText("");
        txtDN = findViewById(R.id.txtDN);
        lblMsg = findViewById(R.id.lblMsg);


    }

    public void doScanDN(View view){
        try {
            // we need to create the object
            // of IntentIntegrator class
            // which is the class of QR library
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setPrompt("Scan a barcode or QR Code");
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setBarcodeImageEnabled(true);
            intentIntegrator.initiateScan();
        }catch (Exception ee){
            Log.v(TAG, "Error "+ee.getLocalizedMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //==========scanned barcode value ==========
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                String cnt = intentResult.getContents();
                String fmt = intentResult.getFormatName();
                Log.i("QR", "Contents : " + cnt + ", fmt = " + fmt);

                try {
                    if (txtDN == null) {
                        txtDN = findViewById(R.id.txtDN);
                        //messageFormat = findViewById(R.id.textFormat);
                    }

                    txtDN.setText(cnt);

                    //messageFormat.setText(fmt);
                } catch (Exception ee) {
                    Log.i("QR", "Error : " + ee.getLocalizedMessage());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }//

    private DevlMaterialDao getTestDao(){
        DevlMaterialDao dao = new DevlMaterialDao();
        dao.setBatchNumber("4456610");
        dao.setWeightUOM("LB");
        dao.setMaterialNum("102182884");
        dao.setMaterialDesc("PKR,HF1,9653,TBG SET,PCH REL,.NX1");
        dao.setGrossWeight("1235.36");
        dao.setDelvNumber("821623466");
        dao.setDelvItemNum("10");
        dao.setPlant("2088");
        dao.setPartRev("F");
        dao.setSlNum(txtSlNo.getText().toString());
        return dao;
    }

    private boolean doCheckServerConnection(){
        try {
            //check DB COnnection getLionServerConnectionWithMSSQLAUth
            Connection con = DBUtils.getLionServerConnectionWithMSSQLAUth();
            if (con != null) {
                DBUtils.doClose(con, null, null);
                lblMsg.setText("DN Connection success");
                return true;
            }else{
                lblMsg.setText("DN Connection failed");
                return false;
            }
        }catch (Exception ee){
            Log.v(TAG, "DB Connection Error "+ee.getLocalizedMessage());
            lblMsg.setText("DN Connection failed");
            return false;
        }
    }

    public void doRetrieveTest(View view){
       DBUtils.doPrintNetworkPrinter(null);
    }

    public void doRetrieve(View view){
        /*ArrayList al = DBUtils.getAllCostCenter("2088");
        if(al.size()>0){
            for(int x=0;x<al.size();x++){
                String ss = (String)al.get(x);
                Log.v(TAG, "Cost centre "+ss);
            }
        }*/

        if(txtDN.getText().toString().length()<1){
            Toast.makeText(getBaseContext(), "DN can not be null", Toast.LENGTH_SHORT).show();
            return;
        }

        if(txtDNLine.getText().toString().length()<1){
            Toast.makeText(getBaseContext(), "DN Line can not be null", Toast.LENGTH_SHORT).show();
            return;
        }else{
            int inn = Integer.parseInt(txtDNLine.getText().toString());
            if(inn>99){
                Toast.makeText(getBaseContext(), "DN Line should be less than 100", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        if(txtSlNo.getText().toString().length()<1){
            //this ust be a number
            Toast.makeText(getBaseContext(), "Sl Num can not be null", Toast.LENGTH_SHORT).show();
            return;
        }else{
            int inn = Integer.parseInt(txtSlNo.getText().toString());
            if(inn>99){
                Toast.makeText(getBaseContext(), "Sl Num should be less than 100", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //
        DevlMaterialDao dao = null;
        try {

            lblMessage.setText("Trying to connect DB, Please wait ");
            lblMessage.setTextColor(Color.RED);


            //production
          if(  doCheckServerConnection()){
              dao = DBUtils.getDelvMtrlInfo(txtDN.getText().toString(), txtDNLine.getText().toString());
          }else{
              Toast.makeText(getBaseContext(), "DB Server connection failed ", Toast.LENGTH_SHORT).show();
              return;
          }
            //Test
             //dao = getTestDao();

            if(dao!=null) {
                //set batch number hereiself
                String btch = dao.getBatchNumber();
                dao.setBatchNumber(btch + "-" + getSlNo(txtSlNo.getText().toString()));

                lblMessage.setText("Success ");
                lblMessage.setTextColor(Color.GREEN);
            }else{
                Toast.makeText(getBaseContext(), "Requested material for for the DN "+txtDN.getText().toString() +" not found", Toast.LENGTH_SHORT).show();
            }



        }catch(Exception ee){
            Log.v(TAG, "error "+ee.getLocalizedMessage());
            lblMessage.setTextColor(Color.RED);
            lblMessage.setText("Error "+ee.getLocalizedMessage());
        }

        if(dao!=null) {

            try {
                Intent intent = new Intent(MainActivity.this, PrintQRActivity.class);
                intent.putExtra("DevlMaterialDao", dao);
                startActivity(intent);
            } catch (Exception ee) {
                Log.v(TAG, "error " + ee.getLocalizedMessage());
            }
        }
    }

    private void doToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private String getSlNo(String slno){
        int num = Integer.parseInt(slno);
        String ret = "";
        if(num<9){
            ret = "0"+ num ;
        }else{
            ret = ""+ num ;
        }

        return  ret;
    }

}
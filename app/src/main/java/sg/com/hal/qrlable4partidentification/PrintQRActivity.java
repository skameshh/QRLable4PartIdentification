package sg.com.hal.qrlable4partidentification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tscdll.TSCActivity;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintQRActivity extends AppCompatActivity {

    TSCActivity TscDll = new TSCActivity();
    private static String TAG = "HALQRLabelPrint";
    private TextView lblPartNum;
    private TextView lblPartDet;
    private TextView lblBatch;
    private TextView lblPlant;
    private TextView lblWeight;
    private TextView lblDN;
    private TextView lblDNLine;
    private TextView lblPartRev;
    private DevlMaterialDao dao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_qractivity);

        lblPartNum = findViewById(R.id.lblPartNum);
        lblPartDet = findViewById(R.id.lblPartDet);
        lblBatch = findViewById(R.id.lblBatch);
        lblPlant = findViewById(R.id.lblPlant);
        lblWeight = findViewById(R.id.lblWeight);
        lblDN = findViewById(R.id.lblDN);
        lblDNLine = findViewById(R.id.lblDNLine);
        lblPartRev = findViewById(R.id.lblPartRev);

        try {
            //Intent intent = getIntent();
             dao = (DevlMaterialDao) getIntent().getSerializableExtra("DevlMaterialDao");
            if (dao != null) {
                lblPartNum.setText(dao.getMaterialNum());
                lblPartDet.setText(dao.getMaterialDesc());
                lblBatch.setText(dao.getBatchNumber());//+"-"+getSlNo(dao.getSlNum()
                lblPlant.setText(dao.getPlant());
                lblWeight.setText(dao.getGrossWeight() +" "+dao.getWeightUOM());
                lblDN.setText(dao.getDelvNumber());
                lblDNLine.setText(dao.getDelvItemNum());
                lblPartRev.setText(dao.getPartRev());
                // gen Preview
                doGenerateQR();

            }
        }catch (Exception ee){
            Log.v(TAG, "Error "+ee.getLocalizedMessage());
        }
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

    private String doGetQR() {
        String batch = lblBatch.getText().toString().trim();
        String part = lblPartNum.getText().toString().trim();
        String partRev = lblPartRev.getText().toString();
        String dn = lblDN.getText().toString();
        String dnLine = lblDNLine.getText().toString();
        String weight = dao.getGrossWeight();
        String uom = dao.getWeightUOM();
        String slno = dao.getSlNum();
      //  String dn = lbl
        return batch+"-"+","+part+","+partRev+","+dn+","+dnLine+","+weight+","+uom;
    }


    public void doGenerateQR(){
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            String final_str = doGetQR();

            if(final_str!=null){
                Bitmap bitmap = barcodeEncoder.encodeBitmap(final_str, BarcodeFormat.QR_CODE, 500, 500);
                ImageView imageViewQrCode = (ImageView) findViewById(R.id.imageViewQRCode);
                imageViewQrCode.setImageBitmap(bitmap);
            }

        }catch(Exception e) {
            Log.e("QR", "Error"+e.getLocalizedMessage());
        }
    }

    //private String printer_ip = "00:80:A3:8B:CB:32";
    private String printer_ip = "00:80:A3:E8:C8:EA";

    public void doHome(View view){
        try {
            Intent intent = new Intent(PrintQRActivity.this, MainActivity.class);
            intent.putExtra("DevlMaterialDao", dao);
            startActivity(intent);
        } catch (Exception ee) {
            Log.v(TAG, "error " + ee.getLocalizedMessage());
        }
    }

    public void doPrintQR(View view){
        Log.i("QR", "Print QR : start now");
        //String final_str = doGetQR();

        /*String desc1 =  dao.getMaterialDesc().substring(0,20);
        String desc2 = "";
        if( dao.getMaterialDesc().length()>20) {
            desc2 = dao.getMaterialDesc().substring(20);
        }*/


       /* dao.getMaterialNum();
        dao.getPartRev();
        dao.getBatchNumber();
        dao.getDelvNumber();
        dao.getDelvItemNum();*/

        if(dao!=null) {
            try {
                MaterialObject mo = new MaterialObject();

                mo.setMaterial(dao.getMaterialNum());
                mo.setDNL(dao.getDelvItemNum());
                mo.setDN(dao.getDelvNumber());
                mo.setPartRev(dao.getPartRev());
                String desc = dao.getMaterialDesc();
                String desc2 = "desc2";
                String desc1 = "desc1";
                if (desc.length() > 0) {
                    desc1 = desc.substring(0, 20);
                }

                if (desc.length() > 20) {
                    desc2 = desc.substring(20);
                    if(desc2.length()>20){
                        desc2 = desc2.substring(0,20);
                    }
                }

                mo.setMaterialDesc1(desc1);
                mo.setMaterialDesc2(desc2);
                mo.setWeights(lblWeight.getText()+"");
                mo.setSearialNo(dao.getBatchNumber());

                RetErrorObj errorObj = DBUtils.doPrintNetworkPrinter(mo);
                if (errorObj.errorCode.equals("0")) {
                    //print success
                    Log.i("QR", "Pritn success : ");
                    Toast.makeText(this, "Print Success ", Toast.LENGTH_LONG).show();
                } else {
                    //print failed
                    Log.i("QR", "Pritn Failed : ");
                    Toast.makeText(this, "Print Failed ", Toast.LENGTH_LONG).show();
                }
            }catch (Exception ee){
                Toast.makeText(this,"Error  "+ee.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

        }else{
            //show error message
            Toast.makeText(this,"Scanned PO Not found ", Toast.LENGTH_LONG).show();
        }
    }

    public void doPrintQRTSC_OLD(View view){
        Log.i("QR", "Print QR : ");

        try {
            // TscDll.openport("00:0C:BF:32:6E:9B");
            //String ss =  TscDll.openport("00:80:A3:E8:C8:EA");
            Log.i("QR", "Trying to connect Bluetooth  pritner : "+printer_ip);

            String ss =  TscDll.openport(printer_ip);

            Log.i("QR", "Bluetooth open pritner : "+ss);


            if(!TscDll.IsConnected || ss.equals("-1")){
                Toast.makeText(this,"Printer not connected ", Toast.LENGTH_LONG).show();
                return;
            }
            //TscDll.downloadpcx("UL.PCX");
            //TscDll.downloadbmp("Triangle.bmp");
            TscDll.downloadttf("ARIAL.TTF");
            //TscDll.setup(55, 42, 3, 4, 0, 0, 0);
            TscDll.setup(75, 42, 3, 4, 0, 0, 0);
            TscDll.clearbuffer();
            TscDll.clearbuffer();

            TscDll.sendcommand("SET TEAR ON\n");
         /*   TscDll.sendcommand("SET COUNTER @11 1\n");
            TscDll.sendcommand("SET COUNTER @1 1\n");
            TscDll.sendcommand("SET COUNTER @2 1\n");
            TscDll.sendcommand("SET COUNTER @3 1\n");
            TscDll.sendcommand("SET COUNTER @4 1\n");
            TscDll.sendcommand("SET COUNTER @5 1\n");
            TscDll.sendcommand("SET COUNTER @15 1\n");*/

            String final_str = doGetQR();
            Log.i("QR", "final_str : "+final_str);
            String title = "HALLIBURTON";
            TscDll.sendcommand("@11 = \""+title+"\"\n");

            if(final_str!=null) {
                //TscDll.clearbuffer();

                String desc1 =  dao.getMaterialDesc().substring(0,20);
                String desc2 = "";
                if( dao.getMaterialDesc().length()>20) {
                    desc2 = dao.getMaterialDesc().substring(20);
                }

               // TscDll.sendcommand("@1 = \""+dao.getMaterialNum()+"\"\n");
               // TscDll.sendcommand("@2 = \""+desc1+"\"\n");
               // TscDll.sendcommand("@11 = \""+title+"\"\n");

                String title2 = "HALLIBURTON";
                TscDll.sendcommand("TEXT 150,50,\"4\",0,1,1,  \""+title2+"\"\n");//title
                //TscDll.sendcommand("TEXT 100,50,\"4\",0,1,1,  @11\n");//title
                TscDll.sendcommand("TEXT 250,110,\"4\",0,1,1,\""+dao.getMaterialNum()+"\"\n"); //Material Num
                TscDll.sendcommand("TEXT 250,150,\"2\",0,1,1,\"Rev: "+dao.getPartRev()+"\"\n");; //REv
                TscDll.sendcommand("TEXT 250,180,\"2\",0,1,1,\""+desc1+"\"\n");; //Desc-1
                TscDll.sendcommand("TEXT 250,210,\"2\",0,1,1,\""+desc2+"\"\n"); //Desc-2
                TscDll.sendcommand("TEXT 250,245,\"2\",0,1,1,\"S.No:"+dao.getBatchNumber()+"\"\n");
                //TscDll.sendcommand("TEXT 200,250,\"2\",0,1,1,\"  Wt:"+dao.getGrossWeight()+" "+dao.getWeightUOM()+"\"\n");

                TscDll.qrcode(90, 110, "H", "4", "A", "0", "M2", "S7", final_str); //OK

               // getCurretDate();

                //TscDll.sendcommand("@15 = \" DN:"+dao.getDelvNumber()+"| LINE "+dao.getDelvItemNum()+"\"\n");
               TscDll.sendcommand("TEXT 90,280,\"3\",0,1,1, \" DN:"+dao.getDelvNumber()+"| LINE "+dao.getDelvItemNum()+"\"\n");


                //print qr code working ok
                //TscDll.qrcode(50, 160, "H", "7", "A", "0", "M2", "S7", final_str);

                //printing
                //TscDll.sendcommand("@1 = \"Done\"\n");
            }else{
                return;
            }

            //200dpi , 1point=1/8mm, 300dpi 1point=1/12mm
            // TscDll.printerfont(10, 150, "1", 0, 1, 1, "Logistics");
            String status = TscDll.printerstatus();
            // text1.setText(status);

            Log.i(TAG, "Pritner status " + status);
            TscDll.printlabel(1, 1);

            TscDll.closeport(5000);

            TscDll.clearbuffer();
        }catch (Exception ee){
            Log.v(TAG, "Error "+ee.getLocalizedMessage());
        }
    }

    private static SimpleDateFormat simple_date = new SimpleDateFormat("yyyy-MM-dd");
    public static String getCurretDate(){
        return  simple_date.format(new Date());
    }

}
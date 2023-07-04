package sg.com.hal.qrlable4partidentification;

import android.os.StrictMode;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;


public class DBUtils {
   private static String TAG = "HALQRLabel4PartIdent";


   private static void doInit(){
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
   }

   public static Connection getLionServerConnectionWithMSSQLAUth(){
      doInit();
      Connection conn = null;
      conn = commonU(conn);
      return conn;
   }

   //private static String SERVER_APMFGDBS001 = "AZSEAPDBW098.corp.halliburton.com";//10.116.20.5
   private static String SERVER_APMFGDBS001 = "10.116.20.5";//10.116.20.5
   private static String SERVER_CARDEV001 = "AZUSCDDBW208.corp.halliburton.co";
   private static String SERVER_HCTNADBS001 = "AZUSCPDBW097.corp.halliburton.com";
   private static String SERVER_AP1Reporting = "ap1appl043.corp.halliburton.com";
   private static String SERVER_SCGREPORTING = "AZUSCPDBW323.corp.halliburton.com";

   static String dev_db_url = "jdbc:jtds:sqlserver://"+SERVER_CARDEV001+":1433/AssetMgmt";
   static String scg_db_url = "jdbc:jtds:sqlserver://"+SERVER_SCGREPORTING+":1433/CAR01_CPS_DATA";
   static String pritner_db_db_url = "jdbc:jtds:sqlserver://"+SERVER_APMFGDBS001+":1433/MPM";
   //"jdbc:jtds:sqlserver://AZSEAPDBW098.corp.halliburton.com:1433/MPM";
   //IP APMFGDBS001=10.116.20.5 (AZSEAPDBW098.corp.halliburton.com)
   //static String scg_db_url = "jdbc:jtds:sqlserver://10.123.36.26:1433/CAR01_CPS_DATA";

  //
  // doInit();
   private static Connection commonU(Connection conn){
      try {
         Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

         String dbURL = scg_db_url;
         conn = DriverManager.getConnection(dbURL, "2088_REPORTS", "L4c^K0i~K1f#");

        // String dbURL = dev_db_url;
        // conn = DriverManager.getConnection(dbURL, "ASSET_MGMT_USER", "ABCD7890&*()");

         //conn = DriverManager.getConnection(dbURL, "sa", "ABCD7890&*()");
         Log.v(TAG,"SCGREPORTING Connection success \n");
      }catch(Exception e){
         e.printStackTrace();
         Log.v(TAG,"SQL Connection Failed, msg="+e.getMessage()+"\n\n");
      }
      return conn;
   }

   private static Connection getConnectionForPrinterServer(){
      Connection conn = null;
      try {
         doInit();

         Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

         String dbURL = pritner_db_db_url;
         conn = DriverManager.getConnection(dbURL, "iusr_server", "iusr");
         Log.v(TAG,"getConnectionForPrinterServer Connection success  dbURL="+dbURL);
      }catch(Exception e){
         e.printStackTrace();
         Log.v(TAG,"getConnectionForPrinterServer SQL Connection Failed, msg="+e.getMessage()+"\n\n");
      }

      //Log.v(TAG,"getConnectionForPrinterServer return connection \n");
      return conn;


   }

   //wworking ok
//  EXEC MPM.[HSP_PrintLogisticsPartLabels]     @Plant_CD,  @Empl_Nbr,@Prntr_Oid,@LabelSize,@UpdateJson;
   public static RetErrorObj doPrintNetworkPrinter(MaterialObject dao ){
      Connection conn = null;
      CallableStatement cstmt = null;
      Log.v(TAG, "doPrintNetworkPrinter() ");
      String error_code = "";
      String error_msg = "";
      RetErrorObj errorObj = new RetErrorObj();
      try{
         conn = getConnectionForPrinterServer();

         Log.v(TAG, "doPrintNetworkPrinter()");
         Log.v(TAG, "doPrintNetworkPrinter()  connection seems ok  , Client info = " +conn.getMetaData().toString());
         cstmt = conn.prepareCall("{call MPM.HSP_PrintLogisticsPartLabels(?,?,?,?,?,?,?)}");
         cstmt.setString("Plant_CD", "2080");
         cstmt.setString("Empl_Nbr", "00659379");
         cstmt.setInt("Prntr_Oid", 906);
         cstmt.setString("LabelSize", "12");
        // cstmt.setString("UpdateJson", "'[{\"QrMaterial\":\"100006401\",\"Material\":\"100006401\",\"MaterialDesc1\":\"PKR.HF 1.9653 TBG\",\"MaterialDesc2\":\"PCL REL.. NX1\",\"SearialNo\":\"4443432-05\",\"Weights\":\"0.6 LB\"}]'");


         Gson gson = new Gson();
         //MaterialObject mo = new MaterialObject();
         ArrayList<MaterialObject> al9 = new ArrayList();
         al9.add(dao);
         String jsonstring = gson.toJson(al9);

         cstmt.setString("UpdateJson",jsonstring);
         Log.v(TAG, "doPrintNetworkPrinter() jsonstring = " +jsonstring);

         //output paramater
         cstmt.registerOutParameter("ErrorCode", Types.VARCHAR);
         cstmt.registerOutParameter("ErrorMessage", Types.VARCHAR);

         //execute
         boolean results = cstmt.execute();

         error_code = cstmt.getString("ErrorCode");
         error_msg = cstmt.getString("ErrorMessage");
         Log.v(TAG, "doPrintNetworkPrinter() error_code = " +error_code +", error_msg="+error_msg);

         errorObj.setErrorCode(error_code);
         errorObj.setErrorMsg(error_msg);

         //int rowsAffected  = cstmt.executeUpdate();

         Log.v(TAG, "doPrintNetworkPrinter() results = " +results);

      }catch (SQLException ee) {
         ee.printStackTrace();
         Log.v(TAG, "SQLException doPrintNetworkPrinter() ee " + ee.getLocalizedMessage());
      }catch (Exception ee){
         ee.printStackTrace();
         Log.v(TAG, "Exception doPrintNetworkPrinter() ee " + ee.getLocalizedMessage());
      }finally {
         if (cstmt != null) {
            try {
               cstmt.close();
            }catch (SQLException ee){

            }
         }
      }
      return errorObj;
   }




public static DevlMaterialDao getDelvMtrlInfo(String delvNum, String delvItemNum){
   DevlMaterialDao dao = null;
   String det = null;
   ArrayList al = new ArrayList();
   ResultSet rs = null;
   PreparedStatement pstmt = null;
   Connection conn = null;
   try {
      doInit();
      //
      String SQL_GET_MATRL_INFO_old = "SELECT [DLVRY_NBR],[DLVRY_ITM],[MTRL_NBR],[SHRT_TXT] as [Mat_Desc],[BTCH_NBR],[PLNT] \n" +
              " ,b.[Gross_Weight],b.[Weight_UOM] FROM [DUN02_HESDATAMART01].[dbo].[DLVRY_ITM] A with(nolock) \n" +
              " JOIN [CAR01_CPS_DATA].[dbo].[MATERIAL_MASTER_CPS_HEADER] B WITH(NOLOCK) ON B.Material = A.MTRL_NBR \n" +
              " where DLVRY_NBR like '%"+delvNum+"' and DLVRY_ITM like '%"+delvItemNum+"'";

      //SELECT [DLVRY_NBR],[DLVRY_ITM],a.[MTRL_NBR],[SHRT_TXT] as [Mat_Desc],[BTCH_NBR],[PLNT],b.GRS_WT,b.WT_UOM_CD
      //FROM  [DLVRY_ITM] A with(nolock) JOIN MTRL b WITH(NOLOCK) ON b.MTRL_NBR = A.MTRL_NBR
      //where DLVRY_NBR like '%821623466' and DLVRY_ITM like '%10'

      //821623466  10
      String SQL_GET_MATRL_INFO = "SELECT [DLVRY_NBR],[DLVRY_ITM],a.[MTRL_NBR],[SHRT_TXT] as [Mat_Desc],[BTCH_NBR],[PLNT] \n" +
              "     ,b.GRS_WT,b.WT_UOM_CD FROM [DUN02_HESDATAMART01].[dbo].[DLVRY_ITM] A with(nolock) \n" +
              "     JOIN [DUN02_HESDATAMART01].[dbo].[MTRL] B WITH(NOLOCK) ON B.MTRL_NBR = A.MTRL_NBR \n" +
              "     where DLVRY_NBR like '%"+delvNum+"' and DLVRY_ITM like '%"+delvItemNum+"'";


      Log.v(TAG, "getDelvMtrlInfo() Connection OK, SQL = "+SQL_GET_MATRL_INFO);

      conn = getLionServerConnectionWithMSSQLAUth();

      pstmt = conn.prepareStatement(SQL_GET_MATRL_INFO);
      //stmt.setString(1, wono);

      rs = pstmt.executeQuery();
      int count = 0;
      while (rs.next()) {
         dao = new DevlMaterialDao();

         try {
            String DLVRY_NBR = rs.getString("DLVRY_NBR");
            String DLVRY_ITM = rs.getString("DLVRY_ITM");
            String MTRL_NBR = rs.getString("MTRL_NBR");

            String Mat_Desc = rs.getString("Mat_Desc");
            String PLNT = rs.getString("PLNT");
            String BTCH_NBR = rs.getString("BTCH_NBR");
            String Gross_Weight = rs.getString("GRS_WT");
            String Weight_UOM = rs.getString("WT_UOM_CD");

            //MTRL_NBR = MTRL_NBR.substring(MTRL_NBR.indexOf("1"));
            MTRL_NBR = StringUtils.stripStart(MTRL_NBR,"0");
            BTCH_NBR = StringUtils.stripStart(BTCH_NBR,"0");
            DLVRY_ITM = StringUtils.stripStart(DLVRY_ITM,"0");


            dao.setDelvItemNum(DLVRY_ITM);
            dao.setDelvNumber(DLVRY_NBR);
            dao.setGrossWeight(Gross_Weight);
            dao.setMaterialDesc(Mat_Desc);
            dao.setPlant(PLNT);
            dao.setMaterialNum(MTRL_NBR);
            dao.setWeightUOM(Weight_UOM);
            dao.setBatchNumber(BTCH_NBR);
            dao.setPartRev("F");

            Log.v(TAG, "MTRL_NBR = " + MTRL_NBR +", Mat_Desc="+Mat_Desc +", BTCH_NBR="+BTCH_NBR) ;

            // dao = "PLNT_CD:"+PLNT_CD+",COST_CNTR_NM:"+COST_CNTR_NM+",COST_CNTR_CD:"+COST_CNTR_CD;
         }catch(Exception ee){
            Log.v(TAG, "Error connection "+ee.getLocalizedMessage());
            ee.printStackTrace();
         }
      }

      rs.close();
      pstmt.close();
      conn.close();

   } catch (Exception e) {
      Log.v(TAG, "Error connection "+e.getLocalizedMessage());
      e.printStackTrace();
   } finally {

      doClose(conn, pstmt, rs);
   }



   return dao;
}

   public static ArrayList getAllCostCenter(String plant){
      String det = null;
      ArrayList al = new ArrayList();
      ResultSet rs = null;
      PreparedStatement pstmt = null;
      Connection conn = null;
      try {
         doInit();
         //
         String SQL_GET_ALL_COST_CENTRE = "Select * from t_cc_cntr where PLANT='"+plant+"'";
         conn = getLionServerConnectionWithMSSQLAUth();
         Log.v(TAG, "getAllCostCenter() Connection OK");
         pstmt = conn.prepareStatement(SQL_GET_ALL_COST_CENTRE);
         //stmt.setString(1, wono);

         rs = pstmt.executeQuery();
         int count = 0;
         while (rs.next()) {
            String PLNT_CD = rs.getString("PLANT");
            String COST_CNTR_NM = rs.getString("CC_CNTR_NM");
            String COST_CNTR_CD = rs.getString("CC_CNTR");
            Log.v(TAG, "COST_CNTR_CD = " + COST_CNTR_CD );

           String dao = "PLNT_CD:"+PLNT_CD+",COST_CNTR_NM:"+COST_CNTR_NM+",COST_CNTR_CD:"+COST_CNTR_CD;
            al.add(dao);
         }

         rs.close();
         pstmt.close();
         conn.close();

      } catch (Exception e) {
         Log.v(TAG, "Error connection "+e.getLocalizedMessage());
         e.printStackTrace();
      } finally {

         doClose(conn, pstmt, rs);
      }

      return al;

   }

   public static void doClose(Connection conn, PreparedStatement pstmt, ResultSet rs ){
      try {
         if (conn != null) {
            conn.close();
            conn = null;
         }
      }catch (Exception ee){
         ee.printStackTrace();
      }

      try{
         if(pstmt!=null){
            pstmt.close();
            pstmt = null;
         }
      }catch (Exception ee){
         ee.printStackTrace();
      }

      try{
         if(rs!=null){
            rs.close();
            rs = null;
         }

      }catch (Exception ee){
         ee.printStackTrace();
      }
   }


}

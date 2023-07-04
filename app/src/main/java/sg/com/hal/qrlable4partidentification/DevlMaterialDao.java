package sg.com.hal.qrlable4partidentification;

import java.io.Serializable;

public class DevlMaterialDao implements Serializable {
   private String delvNumber;
   private String delvItemNum;
   private String batchNumber;
   private String materialNum;
   private String partRev;
   private String materialDesc;
   private String plant;
   private String grossWeight;
   private String weightUOM;
   private String slNum;


   public String getDelvNumber() {
      return delvNumber;
   }

   public void setDelvNumber(String delvNumber) {
      this.delvNumber = delvNumber;
   }

   public String getDelvItemNum() {
      return delvItemNum;
   }

   public void setDelvItemNum(String delvItemNum) {
      this.delvItemNum = delvItemNum;
   }

   public String getMaterialNum() {
      return materialNum;
   }

   public void setMaterialNum(String materialNum) {
      this.materialNum = materialNum;
   }

   public String getMaterialDesc() {
      return materialDesc;
   }

   public void setMaterialDesc(String materialDesc) {
      this.materialDesc = materialDesc;
   }

   public String getPlant() {
      return plant;
   }

   public void setPlant(String plant) {
      this.plant = plant;
   }

   public String getGrossWeight() {
      return grossWeight;
   }

   public void setGrossWeight(String grossWeight) {
      this.grossWeight = grossWeight;
   }

   public String getWeightUOM() {
      return weightUOM;
   }

   public void setWeightUOM(String weightUOM) {
      this.weightUOM = weightUOM;
   }

   public String getBatchNumber() {
      return batchNumber;
   }

   public void setBatchNumber(String batchNumber) {
      this.batchNumber = batchNumber;
   }

   public String getPartRev() {
      return partRev;
   }

   public void setPartRev(String partRev) {
      this.partRev = partRev;
   }

   public String getSlNum() {
      return slNum;
   }

   public void setSlNum(String slNum) {
      this.slNum = slNum;
   }
}

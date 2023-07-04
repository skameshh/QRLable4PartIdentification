package sg.com.hal.qrlable4partidentification;

public class MaterialObject {
    private String PartRev = "F";
    private String Material = "100006401";
    private  String MaterialDesc1 = "PKR.HF 1.9653 TBG";
    private  String MaterialDesc2 = "PCL REL.. NX1";
    private  String SearialNo = "4443432-05";
    private  String Weights = "0.6 LB";
    private String DN = "821623466";
    private String DNL = "10";
    private String QRRev = "A";


    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public String getMaterialDesc1() {
        return MaterialDesc1;
    }

    public void setMaterialDesc1(String materialDesc1) {
        MaterialDesc1 = materialDesc1;
    }

    public String getMaterialDesc2() {
        return MaterialDesc2;
    }

    public void setMaterialDesc2(String materialDesc2) {
        MaterialDesc2 = materialDesc2;
    }

    public String getSearialNo() {
        return SearialNo;
    }

    public void setSearialNo(String searialNo) {
        SearialNo = searialNo;
    }

    public String getWeights() {
        return Weights;
    }

    public void setWeights(String weights) {
        Weights = weights;
    }

    public String getDN() {
        return DN;
    }

    public void setDN(String DN) {
        this.DN = DN;
    }

    public String getDNL() {
        return DNL;
    }

    public void setDNL(String DNL) {
        this.DNL = DNL;
    }

    public String getPartRev() {
        return PartRev;
    }

    public void setPartRev(String partRev) {
        PartRev = partRev;
    }

    public String getQRRev() {
        return QRRev;
    }

    public void setQRRev(String QRRev) {
        this.QRRev = QRRev;
    }

    @Override
    public String toString() {
        return "MaterialObject{" +
                "PartRev='" + PartRev + '\'' +
                ", Material='" + Material + '\'' +
                ", MaterialDesc1='" + MaterialDesc1 + '\'' +
                ", MaterialDesc2='" + MaterialDesc2 + '\'' +
                ", SearialNo='" + SearialNo + '\'' +
                ", Weights='" + Weights + '\'' +
                ", DN='" + DN + '\'' +
                ", DNL='" + DNL + '\'' +
                ", QRRev='" + QRRev + '\'' +
                '}';
    }
}

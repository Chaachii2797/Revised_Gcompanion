package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 10/19/2017.
 */

public class CommunityItem {


    String val;
//    public List<String> itemMaterials;
//    public List<String> itemProcedures;

    public  CommunityItem(){}


    public CommunityItem(String val) {
        this.val = val;
//        this.itemMaterials = itemMaterials;
//        this.itemProcedures = itemProcedures;
    }




    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

//    public List<String> getItemMaterials() {
//        return itemMaterials;
//    }
//
//    public void setItemMaterials(List<String> itemMaterials) {
//        this.itemMaterials = itemMaterials;
//    }
//
//    public List<String> getItemProcedures() {
//        return itemProcedures;
//    }
//
//    public void setItemProcedures(List<String> itemProcedures) {
//        this.itemProcedures = itemProcedures;
//    }
}

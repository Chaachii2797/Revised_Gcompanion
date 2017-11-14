package cabiso.daphny.com.g_companion.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 10/19/2017.
 */

public class CommunityItem extends ArrayList<CommunityItem> {


    private CharSequence val;
//    public List<String> itemMaterials;
//    public List<String> itemProcedures;
    public CommunityItem(){

    }

    public CommunityItem(CharSequence val) {
        this.val = val;
//        this.itemMaterials = itemMaterials;
//        this.itemProcedures = itemProcedures;
    }

    public CharSequence getVal() {
        return this.val;
    }

    public void setVal(CharSequence val) {
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

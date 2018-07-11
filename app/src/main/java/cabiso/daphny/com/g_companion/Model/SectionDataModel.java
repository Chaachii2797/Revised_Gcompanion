package cabiso.daphny.com.g_companion.Model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 6/27/2018.
 */

public class SectionDataModel {



    private String headerTitle;
    private ArrayList<DIYnames> allItemsInSection;


    public SectionDataModel() {

    }
    public SectionDataModel(String headerTitle, ArrayList<DIYnames> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<DIYnames> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<DIYnames> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }


}

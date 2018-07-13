package cabiso.daphny.com.g_companion.Model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 6/27/2018.
 */

public class SectionDataModel {



    private String headerTitle;
    private ArrayList<String> allItemsInSection;
    private ArrayList<User_Profile> allProfileInSection;
    private ArrayList<DIYnames> allPicturesInSection;


    public SectionDataModel() {

    }
    public SectionDataModel(String headerTitle, ArrayList<String> allItemsInSection, ArrayList<User_Profile> allProfileInSection,
                            ArrayList<DIYnames> allPicturesInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
        this.allProfileInSection = allProfileInSection;
        this.allPicturesInSection = allPicturesInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<String> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<String> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

    public ArrayList<User_Profile> getAllProfileInSection() {
        return allProfileInSection;
    }

    public void setAllProfileInSection(ArrayList<User_Profile> allProfileInSection) {
        this.allProfileInSection = allProfileInSection;
    }

    public ArrayList<DIYnames> getAllPicturesInSection() {
        return allPicturesInSection;
    }

    public void setAllPicturesInSection(ArrayList<DIYnames> allPicturesInSection) {
        this.allPicturesInSection = allPicturesInSection;
    }
}

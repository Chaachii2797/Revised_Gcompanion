package cabiso.daphny.com.g_companion.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

import cabiso.daphny.com.g_companion.ImageRecognitionForMaterials;

/**
 * Created by cicctuser on 4/2/2018.
 */

public class DBMaterial implements Serializable {
    private String name;
    private int quantity;
    private String unit;
    Bitmap mat_image;

    public DBMaterial(){
        this.quantity = 0;
        this.unit = "pieces";
    }

    public String getName() {
        return name;
    }

    public DBMaterial setName(String name) {
        this.name = name;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public DBMaterial setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public DBMaterial setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Bitmap getMat_image() {
        Bitmap bm = ImageRecognitionForMaterials.constan.photoMap;

        return bm;
    }

    public void setMat_image(Bitmap mat_image) {
        this.mat_image = mat_image;
    }
}

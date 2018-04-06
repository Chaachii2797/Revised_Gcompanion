package cabiso.daphny.com.g_companion.Model;

import java.io.Serializable;

/**
 * Created by cicctuser on 4/2/2018.
 */

public class DBMaterial implements Serializable {
    private String name;
    private int quantity;
    private String unit;

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
}

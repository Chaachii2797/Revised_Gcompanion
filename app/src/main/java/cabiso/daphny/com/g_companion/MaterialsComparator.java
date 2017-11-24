package cabiso.daphny.com.g_companion;

import android.util.Log;

import java.util.Comparator;

import cabiso.daphny.com.g_companion.Model.DIYnames;

/**
 * Created by cicctuser on 11/23/2017.
 */

public class MaterialsComparator implements Comparator<DIYnames> {
    @Override
    public int compare(DIYnames o1, DIYnames o2) {

        if(o1==null){
            if(o2==null){
                Log.d("ComComThis both", "null");
            }else{
                Log.d("ComComThis o2", "null");
            }
        }else if(o2==null){
            if(o1==null){
                Log.d("ComComThis both", "null");
            }else{
                Log.d("ComComThis o1", "null");
            }
        }

        int flag = 0;
        if(o1.getMaterialMatches() > o2.getMaterialMatches()){
            Log.d("01material"+o1.getMaterialMatches(), "02"+o2.getMaterialMatches());
            flag = -1;
        }else if (o1.getMaterialMatches() < o2.getMaterialMatches()){
            Log.d("01material"+o1.getMaterialMatches(), "02"+o2.getMaterialMatches());
            flag = 1;
        }else{
            Log.d("01material"+o1.getMaterialMatches(), "02"+o2.getMaterialMatches());
            flag =0;
        }
        return flag;
    }
}

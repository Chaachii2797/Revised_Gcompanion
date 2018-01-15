package cabiso.daphny.com.g_companion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Lenovo on 10/19/2017.
 */

class ViewPagerAdapter extends FragmentStatePagerAdapter{

    private static int TAB_COUNT = 1;
    private static String PAGE_TITLE = "DIY Items";

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
//            case 0:
//                return SellingFragment.newInstance();
//            case 1:
//                return CommunityFragment.newInstance();
            default:
                return CommunityFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
              //  return TITLE;

//            case 1:
                return PAGE_TITLE;
        }
        return super.getPageTitle(position);
    }
}

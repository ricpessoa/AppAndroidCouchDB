package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;


import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.ui.MainActivity;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.FragmentListNotificationOfMonitoring;


public class FragmentMyDashboard extends Fragment {
    private static String TAG = FragmentMyDashboard.class.getName();
    public static String passVariableMacAddress = "passVariableMacAddress";

    private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    static public int currentPagerPosition = 0;
    private HashMap<String, String> hasMapDevices;
    private TextView textViewNoDevices;

    public FragmentMyDashboard() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_dashboard, container, false);
        mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup();

        textViewNoDevices = (TextView) rootView.findViewById(R.id.textViewNoDevices);

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);
        constructTabHost();
        return rootView;
    }

    private void constructTabHost() {
        hasMapDevices = Device.getHashMapOfDevices();
        if (hasMapDevices.size() == 0) {
            textViewNoDevices.setVisibility(View.VISIBLE);
        }

        for (Map.Entry<String, String> e : hasMapDevices.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();

            Bundle b = new Bundle();
            b.putString(FragmentListNotificationOfMonitoring.passIDOfDevice, key);
            mTabsAdapter.addTab(mTabHost.newTabSpec(key).setIndicator(value), FragmentListNotificationOfMonitoring.class, b);
        }


        Bundle bundle = this.getArguments();
        String macAddressReceived = "";
        if (bundle != null) {
            macAddressReceived = bundle.getString(passVariableMacAddress, "");
            currentPagerPosition = getPositionOfTabBarOnMacAddress(macAddressReceived);
        }
        if (hasMapDevices != null && hasMapDevices.size() >= currentPagerPosition) {
            mTabsAdapter.mTabHost.setCurrentTab(currentPagerPosition);
        } else {
            mTabsAdapter.mTabHost.setCurrentTab(0); // go to the first
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(0);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     * <p/>
     * FragmentStatePagerAdapter save the state of fragment on Pager =)
     */
    public static class TabsAdapter extends FragmentStatePagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);

            currentPagerPosition = position;

        }

        public void onPageScrollStateChanged(int state) {
        }
    }


    private int getPositionOfTabBarOnMacAddress(String mac_address) {
        int position = 0;
        for (int i = 0; i < mTabsAdapter.mTabs.size(); i++) {
            if (mac_address.equals(mTabsAdapter.mTabs.get(i).tag.toString())) {
                position = i;
                Log.d(TAG, "found the the position of tab " + mac_address + " " + position);
                //mTabsAdapter.mTabs.get(i).notifyAll();
                //mTabsAdapter.mTabHost.invalidate();
                //mTabsAdapter.mTabHost.setCurrentTab(i);
                //mTabsAdapter.mTabHost.getCurrentTabView().invalidate();
                // mTabsAdapter.mTabHost.getTabContentView().invalidate();
            }

        }
        return position;
    }

}
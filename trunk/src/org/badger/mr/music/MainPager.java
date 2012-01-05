package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.mult.daap.Contents;
import org.mult.daap.MediaPlayback;
import org.mult.daap.background.GetSongsForPlaylist;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;



public class MainPager extends FragmentActivity implements Observer {
    TabHost mTabHost;
    ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    private ProgressDialog pd = null;
	private TextView libSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i("MainPager","Creating Main Tab");
        
    	setContentView(R.layout.main_tab_pager);
    	super.onCreate(savedInstanceState);
        
        libSource = (TextView) findViewById(R.id.lib_source);
		libSource.setOnClickListener(libSourceListener);
		
		if (Contents.daapHost == null)
			libSource.setText("Local Library (Touch to change)");
		else
			libSource.setText("Remote Library (Touch to change)");
		if (Contents.songList.size() == 0)
		{
			Log.i("TabMain","Playlist is empty. Loading the local list");
			
			buildLocalPlaylist();
		}
		
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("artists").setIndicator("Artists"),
                ArtistsFragment.ArtistListFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("albums").setIndicator("Albums"),
        		AlbumsFragment.AlbumListFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("songs").setIndicator("Songs"),
                SongsFragment.SongListFragment.class, null);
        
        if (getIntent().hasExtra("tab"))
        	mTabHost.setCurrentTab(getIntent().getExtras().getInt("tab",0));
        
        if (savedInstanceState != null) {
        	mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }
    
    private View.OnClickListener libSourceListener = new View.OnClickListener() {
		public void onClick(View v) {
			final Intent intent = new Intent(MainPager.this, MediaSources.class);
            startActivity(intent);
		}
	};
    
	
	
	private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GetSongsForPlaylist.FINISHED) { // Finished
                if (pd != null) {
                    pd.dismiss();
                }
                Log.i("TabMain","Finished Loading Music, continuing to the music browser");
                Contents.getSongsForPlaylist = null;
                
                //mTabHost.setCurrentTab(0);
                final Intent intent = new Intent(MainPager.this, MainPager.class);
                startActivity(intent);
                MainPager.this.finish();
            }
            else if (msg.what == GetSongsForPlaylist.EMPTY) {
                if (pd != null) {
                    pd.dismiss();
                }
                Contents.getSongsForPlaylist = null;
                Toast tst = Toast.makeText(MainPager.this,
                        getString(R.string.empty_playlist), Toast.LENGTH_LONG);
                tst.setGravity(Gravity.CENTER, tst.getXOffset() / 2,
                        tst.getYOffset() / 2);
                tst.show();
            }
        }
    };
	
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		if (((Integer) data).compareTo(GetSongsForPlaylist.START) == 0) {
            pd = ProgressDialog.show(this,
                    getString(R.string.fetching_music_title),
                    getString(R.string.fetching_music_detail), true, false);
 		}
	    else if (((Integer) data).compareTo(GetSongsForPlaylist.FINISHED) == 0) {
	        uiHandler.sendEmptyMessage(GetSongsForPlaylist.FINISHED);
	    }
	    else if (((Integer) data).compareTo(GetSongsForPlaylist.EMPTY) == 0) {
	        uiHandler.sendEmptyMessage(GetSongsForPlaylist.EMPTY);
	    }
	}
	
	private void buildLocalPlaylist() {
		Log.i("TabMain","Building the local playlist");
    	NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Contents.clearLists();
        MediaPlayback.clearState();
        GetSongsForPlaylist gsfp = new GetSongsForPlaylist();
        Contents.getSongsForPlaylist = gsfp;
        gsfp.addObserver(this);
        gsfp.activityContext = this.getBaseContext();
        Thread thread = new Thread(gsfp);
        thread.start();
        update(gsfp, GetSongsForPlaylist.START);
    
    }
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
    
	    public static class TabsAdapter extends FragmentPagerAdapter
	    implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
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
			    Log.i("MainPager","Loading Tab at index " +  position);
			    Log.i("MainPager","Name: " +  info.clss.getName());
			    Log.i("MainPager","Args: " +  info.args);
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
			}
			
			public void onPageScrollStateChanged(int state) {
			}

			
			}

}

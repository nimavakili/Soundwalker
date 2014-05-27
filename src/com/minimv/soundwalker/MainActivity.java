package com.minimv.soundwalker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static MapSectionFragment mapFragment;
    private Bundle previousInstance;

    private Context mContext;
	private static String TAG = "GPSActiviy";
	private GPSService gpsService;
	private ServiceConnection gpsServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			gpsService = ((GPSService.LocationServiceBinder) service).getService();
			Log.v(TAG, "GPS service is tracking: " + gpsService.isTracking());
			if (gpsService.isTracking()) {
				tb.setChecked(true);
			}
			handler.post(GPSDialog);
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			gpsService = null;
		}
	};
	float scale;
	int width;
	public static Button bb; //, gb, ub, sb, stb;
	//public static ToggleButton tb, rb, pb;
	public static ToggleButton tb;
	//public static TextView tt, rt, gt, searching;
	public static TextView tt, searching, accuracy, lattitude, longitude, active, all;
	public static RelativeLayout about; //, parent, mapPins, help;
	private Runnable GPSDialog;
	private Handler handler;
	public static AlertDialog GPSAlert;
	private Intent gpsIntent;
	private BroadcastReceiver receiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getActionBar();
        //actionBar.setHomeButtonEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

		scale = getResources().getDisplayMetrics().widthPixels/320.0f;
		width = getResources().getDisplayMetrics().widthPixels;
		gpsIntent = new Intent(getApplicationContext(), GPSService.class);
		mContext = this;
		handler = new Handler();
		GPSDialog = new Runnable() {
			@Override
			public void run() {
				if (gpsService != null) {
					if (gpsService.GPSDisabled()) {
						//searching.setVisibility(View.INVISIBLE);
						searching.setText(getStr(R.string.gps_disabled));
		            	if (GPSAlert != null) {
		            		if (GPSAlert.isShowing()) {
		            			return;
		            		}
		            	}
						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			            builder.setMessage(getStr(R.string.gps_disabled_prompt));
			            builder.setCancelable(true);
			            builder.setPositiveButton(getStr(R.string.enable_gps), new DialogInterface.OnClickListener() {
			                 public void onClick(DialogInterface dialog, int id) {
			                      Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			                      gpsOptionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                      startActivity(gpsOptionsIntent);
			                  }
			             });
			             builder.setNegativeButton(getStr(R.string.do_nothing), new DialogInterface.OnClickListener() {
			                  public void onClick(DialogInterface dialog, int id) {
			                	  dialog.dismiss();
			                  }
			             });
			             GPSAlert = builder.create();
			             GPSAlert.show();
		            }
					else {
						if (!gpsService.gotLock())
							//searching.setVisibility(View.VISIBLE);
							searching.setText(getStr(R.string.searching));
					}
				}
			}
		};
	    receiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String messageLock = intent.getStringExtra(GPSService.messageLock);
	            double messageAcc = intent.getDoubleExtra(GPSService.messageAcc, -1000f);
	            double messageLat = intent.getDoubleExtra(GPSService.messageLat, -1000f);
	            double messageLon = intent.getDoubleExtra(GPSService.messageLon, -1000f);
	            int messageAct = intent.getIntExtra(GPSService.messageAct, -1000);
	            int messageAll = intent.getIntExtra(GPSService.messageAll, -1000);
	            if (messageLock != null) {
		            //Log.v("Receive", messageLock);
		            if (messageLock.equals("No")) {
		            	//searching.setVisibility(View.VISIBLE);
		            	if (gpsService.GPSDisabled()) {
		            		searching.setText(getStr(R.string.gps_disabled));
		            	}
		            	else {
		            		searching.setText(getStr(R.string.searching));
		            	}
		            	accuracy.setText("---");
		            	lattitude.setText("---");
		            	longitude.setText("---");
		            	active.setText("0");
		            	//all.setText("---");
		            }
		            else if (messageLock.equals("Yes")) {
		            	//searching.setVisibility(View.INVISIBLE);
		            	searching.setText(getStr(R.string.lock));
		            }
	            }
	            if (messageAcc != -1000f) {
	            	String acc = String.valueOf(messageAcc);
		            Log.v("Receive", acc);
	            	accuracy.setText(acc);
	            }
	            if (messageLat != -1000f) {
	            	String lat = String.valueOf(messageLat);
		            Log.v("Receive", lat);
		            if (lat.length() > 10)
		            	lat = lat.substring(0, 11);
	            	lattitude.setText(lat);
	            }
	            if (messageLon != -1000f) {
	            	String lon = String.valueOf(messageLon);
		            Log.v("Receive", lon);
		            if (lon.length() > 10)
		            	lon = lon.substring(0, 11);
	            	longitude.setText(lon);
	            }
	            if (messageAct != -1000) {
	            	String act = String.valueOf(messageAct);
		            Log.v("Receive", act);
	            	active.setText(act);
	        		if (mapFragment != null) {
	        			if (mapFragment.debug) {
	        				mapFragment.updateNodes();
	        			}
	        		}
	            }
	            if (messageAll != -1000) {
	            	String allS = String.valueOf(messageAll);
		            Log.v("Receive", allS);
	            	all.setText(allS);
	            }
	        }
	    };
	    
	    previousInstance = savedInstanceState;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		startService(gpsIntent);
		bindService(gpsIntent, gpsServiceConnection, Context.BIND_AUTO_CREATE);
		registerReceiver(receiver, new IntentFilter(GPSService.messageAction));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!isChangingConfigurations()) {
			unbindService(gpsServiceConnection);
		}
    	unregisterReceiver(receiver);
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		try {
			savedInstanceState.putBoolean("debug", mapFragment.debug);
		}
		catch (NullPointerException e) {
			//e.printStackTrace();
		}
	    super.onSaveInstanceState(savedInstanceState);
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		if (previousInstance != null)
			menu.getItem(0).setChecked(previousInstance.getBoolean("debug"));
	    return true;
	}

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    //@Override
    //public void onDestroy() {
    //	super.onDestroy();
    //}

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new MainSectionFragment();
                case 1:
                	mapFragment = new MapSectionFragment();
                	return mapFragment;
                case 2:
                	return new AboutFragment();
                default:
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            switch (i) {
            case 0:
                return "GPS Status";
            case 1:
            	return "Map";
            case 2:
            	return "About";
            default:
            	return "Section " + (i + 1);
            }
        }
    }

    public static class MainSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    		//gb = (Button) findViewById(R.id.goButton);
    		bb = (Button) rootView.findViewById(R.id.backButton);
    		//sb = (Button) findViewById(R.id.saveButton);
    		//stb = (Button) findViewById(R.id.stopButton);
    		//pb = (ToggleButton) findViewById(R.id.playButton);
    		tb = (ToggleButton) rootView.findViewById(R.id.trackingButton);
    		//rb = (ToggleButton) findViewById(R.id.recordingButton);
    		//tt = (TextView) findViewById(R.id.trackingText);
    		//rt = (TextView) findViewById(R.id.recordingText);
    		//gt = (TextView) findViewById(R.id.goText);
    		//center = (ImageView) findViewById(R.id.center);
    		//mapOver = (ImageView) findViewById(R.id.mapOver);
    		searching = (TextView) rootView.findViewById(R.id.searching);
    		accuracy = (TextView) rootView.findViewById(R.id.accuracy);
    		lattitude = (TextView) rootView.findViewById(R.id.lattitude);
    		longitude = (TextView) rootView.findViewById(R.id.longitude);
    		active = (TextView) rootView.findViewById(R.id.active);
    		active.setText("0");
    		all = (TextView) rootView.findViewById(R.id.all);
    		about = (RelativeLayout) rootView.findViewById(R.id.aboutFrame);
    		//help = (RelativeLayout) findViewById(R.id.helpFrame);
    		//gb.setEnabled(false);
    		//tb.setVisibility(View.INVISIBLE);
    		//rb.setVisibility(View.INVISIBLE);
    		//center.setVisibility(View.INVISIBLE);
    		//mapOver.setVisibility(View.INVISIBLE);
    		//searching.setVisibility(View.INVISIBLE);
            return rootView;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public static class AboutFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about, container, false);
            return rootView;
        }
    }

    private void showAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_about, null);
		builder.setView(view);
		builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
        });
        builder.setCancelable(true);
        /*String version = getStr(R.string.version);
        try {
			version = mContext.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}*/
        //builder.setTitle("About - v" + version);
        //builder.setCustomTitle(customTitleView);
        AlertDialog aboutDialog = builder.create();
        aboutDialog.show();
    }

    public static class MapSectionFragment extends Fragment {

    	private GoogleMap mMap;
    	private GroundOverlay mapOverlay;
    	public boolean debug = false;
    	private LatLngBounds arnoldArboretum;
    	public Circle[] circleO;
    	public Circle[] circleI;
    	public Marker[] marker;

    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		setRetainInstance(true);
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            setUpMap();
            return rootView;
        }

        private void setUpMap() {
            if (mMap == null) {
            	SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
            	mapFragment.setRetainInstance(true);
                mMap = mapFragment.getMap();
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                    //mMap.getMyLocation();
                    LatLng arnoldArboretumSW = new LatLng(42.296335, -71.121215);
                    LatLng arnoldArboretumNE = new LatLng(42.300715, -71.113227);
                    arnoldArboretum = new LatLngBounds(arnoldArboretumSW, arnoldArboretumNE);
                    GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                    	.image(BitmapDescriptorFactory.fromResource(R.drawable.arnold_arboretum))
                    	.positionFromBounds(arnoldArboretum);
                    mapOverlay = mMap.addGroundOverlay(newarkMap);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arnoldArboretum.getCenter(), 18));
                    mMap.getUiSettings().setCompassEnabled(true);
                    toggleDebug(debug);
                }
            }
        }
        
        public void toggleDebug(boolean dbg) {
        	debug = dbg;
            if (debug) {
            	mapOverlay.setTransparency(0.5f);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setAllGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                addNodes();
            }
            else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arnoldArboretum.getCenter(), 18));
            	mapOverlay.setTransparency(0.15f);
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                mMap.getUiSettings().setAllGesturesEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
        
        public void addNodes() {
        	if (GPSService.node != null) {
        		NodeManager node[] = GPSService.node;
        		circleO = new Circle[node.length];
        		circleI = new Circle[node.length];
        		marker = new Marker[node.length];
        		for (int i = 0; i < node.length; i++) {
        			LatLng latlon = new LatLng(node[i].getLat(), node[i].getLon());
        			circleO[i] = mMap.addCircle((new CircleOptions())
        					.center(latlon)
        					.radius(node[i].getRadO())
        					.strokeWidth(5)
        					.strokeColor(Color.argb(127, 0, 0, 0))
        					.zIndex(2)
        			);
        			if (node[i].getRadI() > 0) {
        				circleI[i] = mMap.addCircle((new CircleOptions())
	        					.center(latlon)
	        					.radius(node[i].getRadI())
	        					.strokeWidth(2)
	        					.strokeColor(Color.argb(63, 0, 0, 0))
	        					.zIndex(1)
	        			);
        			}
        			marker[i] = mMap.addMarker((new MarkerOptions())
        					.position(latlon)
        					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
        					.anchor(0.5f, 0.5f)
        			);
        		}
        		updateNodes();
        	}
        }
        public void updateNodes() {
        	int color;
			int colorI = getResources().getColor(android.R.color.holo_blue_light);
			int colorA = getResources().getColor(android.R.color.holo_red_light);
        	NodeManager[] node = GPSService.node;
    		for (int i = 0; i < node.length; i++) {
            	LatLng latlon = new LatLng(node[i].getLat(), node[i].getLon());
    			if (node[i].isPlaying()) {
    				color = colorA;
    			}
    			else {
    				color = colorI;
    			}
        		mapFragment.circleO[i].setCenter(latlon);
        		mapFragment.circleO[i].setRadius(node[i].getRadO());
            	if (mapFragment.circleI[i] != null) {
        			mapFragment.circleO[i].setFillColor(Color.argb(63, Color.red(color), Color.green(color), Color.blue(color)));
            		mapFragment.circleI[i].setCenter(latlon);
            		mapFragment.circleI[i].setRadius(node[i].getRadI());
            		mapFragment.circleI[i].setFillColor(Color.argb(63, Color.red(color), Color.green(color), Color.blue(color)));
            	}
    			else {
        			mapFragment.circleO[i].setFillColor(Color.argb(127, Color.red(color), Color.green(color), Color.blue(color)));
    			}
				marker[i].setPosition(latlon);
        	}
        }
    }

    public void onTrackingToggled(View view) {
		boolean on = tb.isChecked();

		if (on) {
			final Handler handler = new Handler();
			handler.post(GPSDialog);
			gpsService.startTracking();
		}
		else {
			gpsService.stopTracking();
		}
	}

	public void onAbout(MenuItem item) {
		showAboutDialog();
	}

	public void onReset(MenuItem item) {
		NodeManager.reset();
    	NodeManager[] node = GPSService.node;
		for (int i = 0; i < node.length; i++) {
			if (node[i].isPlaying())
				node[i].stopReset();
		}
	}

	public void onDebug(MenuItem item) {
		mapFragment.debug = !mapFragment.debug;
		mapFragment.toggleDebug(mapFragment.debug);
		item.setChecked(mapFragment.debug);
	}

    private String getStr(int id) {
		return getResources().getString(id);
	}
}
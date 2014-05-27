package com.minimv.soundwalker;

import com.minimv.soundwalker.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GPSActivity extends Activity {

	private Context mContext;
	//private MP3Recorder recorder = new MP3Recorder(44100, 64);
	
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
			//trackFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Grimpant", gpsService.lastSession() + ".csv");
			/*if (gb.getVisibility() == View.INVISIBLE) {
				getUnsent(true);
				handler.post(GPSDialog);
				if (!isUploading && !recordingMode)
					handler.post(unsentDialog);
			}*/
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
	//public static VideoView video;
	//public static ImageView center, mapOver, pngAnim1, blank;
	//public static LinearLayout mapAll;
	public static RelativeLayout about; //, parent, mapPins, help;
	//public static LinearLayout[] mapH = new LinearLayout[20];
	//public static ImageView[][] map = new ImageView[20][20];
	//public MediaPlayer mPlayer;
	//public static boolean recordingMode = false;
	//public static boolean listeningMode = false;
	//private int curFrameI;
	//private long curFrameT1, curFrameT2;
	//private Timer timer;
	//private boolean cancelAnim = false;
	//private boolean firstFocus = true;
	//private Drawable frame;
	//private Runnable setFrame, animCompletion, GPSDialog, unsentDialog, saveDialog, okDialog, deleteDialog;
	private Runnable GPSDialog;
	//private boolean frameLoaded = false;
	//loadNextFrame load;
	//setNextFrame set;
	//public ArrayList<File> unsentFiles = new ArrayList<File>();
	//public ArrayList<String> allAudioPaths = new ArrayList<String>();
	//public int unsentTracks = 0;
	//public int unsentAudios = 0;
	private Handler handler;
	//public NotificationManager mNotificationManager;
	//private NotificationCompat.Builder mBuilder;
	//public static AlertDialog GPSAlert, unsentAlert, saveAlert, okAlert, deleteAlert;
	public static AlertDialog GPSAlert;
	//private boolean saveRecording = false;
	//private File trackFile;
	private Intent gpsIntent;
	//private boolean isUploading = false;
	private BroadcastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//BugSenseHandler.initAndStartSession(this, "c662635a");
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gps);
		/*if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getApplicationContext(), getStr(R.string.no_sd), Toast.LENGTH_LONG).show();
			finish();
			return;
		}*/
		/*File grimpant = new File(Environment.getExternalStorageDirectory().getPath() + "/Grimpant");
		if (!grimpant.exists()) {
			grimpant.mkdir();
		}*/

		scale = getResources().getDisplayMetrics().widthPixels/320.0f;
		width = getResources().getDisplayMetrics().widthPixels;
		//gb = (Button) findViewById(R.id.goButton);
		bb = (Button) findViewById(R.id.backButton);
		//sb = (Button) findViewById(R.id.saveButton);
		//stb = (Button) findViewById(R.id.stopButton);
		//pb = (ToggleButton) findViewById(R.id.playButton);
		tb = (ToggleButton) findViewById(R.id.trackingButton);
		//rb = (ToggleButton) findViewById(R.id.recordingButton);
		//tt = (TextView) findViewById(R.id.trackingText);
		//rt = (TextView) findViewById(R.id.recordingText);
		//gt = (TextView) findViewById(R.id.goText);
		//center = (ImageView) findViewById(R.id.center);
		//mapOver = (ImageView) findViewById(R.id.mapOver);
		searching = (TextView) findViewById(R.id.searching);
		accuracy = (TextView) findViewById(R.id.accuracy);
		lattitude = (TextView) findViewById(R.id.lattitude);
		longitude = (TextView) findViewById(R.id.longitude);
		active = (TextView) findViewById(R.id.active);
		all = (TextView) findViewById(R.id.all);
		//parent = (RelativeLayout) findViewById(R.id.parent);
		//mapPins = (RelativeLayout) findViewById(R.id.mapPins);
		about = (RelativeLayout) findViewById(R.id.aboutFrame);
		//help = (RelativeLayout) findViewById(R.id.helpFrame);
		//pngAnim1 = (ImageView) findViewById(R.id.pngAnim);
		//gb.setEnabled(false);
		//tb.setVisibility(View.INVISIBLE);
		//rb.setVisibility(View.INVISIBLE);
		//center.setVisibility(View.INVISIBLE);
		//mapOver.setVisibility(View.INVISIBLE);
		//searching.setVisibility(View.INVISIBLE);
		//blank = (ImageView) findViewById(R.id.blank);
		//mapAll = (LinearLayout) findViewById(R.id.map);
		/*float widthDP = 409.6f;
		float heightDP = 409.6f;
		for (int j = 0; j < 20; j++) {
			mapH[j] = new LinearLayout(this);
			mapAll.addView(mapH[j]);
			for (int i = 0; i < 20; i++) {
				map[i][j] = new ImageView(this);
				map[i][j].setScaleType(ImageView.ScaleType.FIT_XY);
				mapH[j].addView(map[i][j], (int)(widthDP*scale), (int)(heightDP*scale));
			}
		}*/

		gpsIntent = new Intent(getApplicationContext(), GPSService.class);
		mContext = this;
		/*video = (VideoView) findViewById(R.id.videoView);
		Uri uri = Uri.parse("android.resource://com.minimv.phonelab.locationvisualization/raw/vine_grow_up");
		video.setVideoURI(uri);
		video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp)
            {                  
            	mp.setLooping(true);
            }
        });
		video.start();*/
		handler = new Handler();
		/*setFrame = new Runnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - curFrameT2 <= 50) {
					pngAnim1.setImageDrawable(frame);
				}
			}
		};
		animCompletion = new Runnable() {
			@Override
			public void run() {
				stopAnim();
				rb.setChecked(false);
			}
		};*/
		GPSDialog = new Runnable() {
			@Override
			public void run() {
				if (gpsService != null) {
					if (gpsService.GPSDisabled()) {
						searching.setVisibility(View.INVISIBLE);
		            	if (GPSAlert != null) {
		            		if (GPSAlert.isShowing()) {
		            			return;
		            		}
		            	}
						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			            builder.setMessage(getStr(R.string.gps_disabled));
			            builder.setCancelable(false);
			            builder.setPositiveButton(getStr(R.string.enable_gps), new DialogInterface.OnClickListener() {
			                 public void onClick(DialogInterface dialog, int id) {
			                      Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			                      gpsOptionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                      startActivity(gpsOptionsIntent);
			                  }
			             });
			             builder.setNegativeButton(getStr(R.string.do_nothing), new DialogInterface.OnClickListener() {
			                  public void onClick(DialogInterface dialog, int id) {
			                	  dialog.cancel();
			                  }
			             });
			             GPSAlert = builder.create();
			             GPSAlert.show();
		            }
					else {
						if (!gpsService.gotLock())
							searching.setVisibility(View.VISIBLE);
					}
				}
			}
		};
		/*unsentDialog = new Runnable() {
			@Override
			public void run() {
				if ((tb.isChecked() && unsentTracks > 1) || (tb.isChecked() && unsentAudios > 0) || (!tb.isChecked() && unsentFiles.size() > 0)) {
	            	if (unsentAlert != null) {
	            		if (unsentAlert.isShowing()) {
	            			return;
	            		}
	            	}
	            	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	            	if (unsentTracks > 0 && unsentAudios > 0)
	            		builder.setMessage(String.format(getStr(R.string.unsent_both), unsentTracks, unsentAudios));
	            	else if (unsentTracks > 0)
	            		builder.setMessage(String.format(getStr(R.string.unsent_track), unsentTracks));
	            	else if (unsentAudios > 0)
	            		builder.setMessage(String.format(getStr(R.string.unsent_audio), unsentAudios));
		            builder.setCancelable(false);
		            builder.setPositiveButton(getStr(R.string.yes), new DialogInterface.OnClickListener() {
		            	public void onClick(DialogInterface dialog, int id) {
		            		UploadUnsent uu = new UploadUnsent();
		            		uu.execute();
		            	}
		            });
		            builder.setNegativeButton(getStr(R.string.no), new DialogInterface.OnClickListener() {
		            	public void onClick(DialogInterface dialog, int id) {
		            		dialog.cancel();
		                }
		            });
		            unsentAlert = builder.create();
		            unsentAlert.show();
	            }
			}
		};
		saveDialog = new Runnable() {
			@Override
			public void run() {
            	if (saveAlert != null) {
            		if (saveAlert.isShowing()) {
            			return;
            		}
            	}
            	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            	if (saveRecording)
            		builder.setMessage(getStr(R.string.upload_recording));
            	else
            		builder.setMessage(getStr(R.string.upload_track));
	            builder.setCancelable(false);
	            builder.setPositiveButton(getStr(R.string.yes), new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int id) {
	            		if (saveRecording) {
	            			onSave(null);
	            		}
	            		else {
	            			UploadFile ut = new UploadFile(trackFile);
	        				ut.execute();
	            		}
	            	}
	            });
	            builder.setNegativeButton(getStr(R.string.no), new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int id) {
	            		if (saveRecording)
	            			onBack(null);
	            		else {
	            			trackFile.delete();
	            		}
	            		dialog.cancel();
	                }
	            });
	            saveAlert = builder.create();
	            saveAlert.show();
			}
		};
		okDialog = new Runnable() {
			@Override
			public void run() {
            	if (okAlert != null) {
            		if (okAlert.isShowing()) {
            			return;
            		}
            	}
            	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	            builder.setCancelable(false);
            	if (gpsService.GPSDisabled()) {
		            builder.setMessage(getStr(R.string.need_lock) + " " + getStr(R.string.gps_disabled));
		            builder.setPositiveButton(getStr(R.string.enable_gps), new DialogInterface.OnClickListener() {
		                 public void onClick(DialogInterface dialog, int id) {
		                      Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                      gpsOptionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		                      startActivity(gpsOptionsIntent);
		                  }
		             });
		             builder.setNegativeButton(getStr(R.string.do_nothing), new DialogInterface.OnClickListener() {
		                  public void onClick(DialogInterface dialog, int id) {
		                	  dialog.cancel();
		                  }
		             });
            	}
            	else {
            		builder.setMessage(getStr(R.string.need_lock) + " " + getStr(R.string.wait_lock));
    	            builder.setNeutralButton(getStr(R.string.ok), new DialogInterface.OnClickListener() {
    	            	public void onClick(DialogInterface dialog, int id) {
    	            		dialog.cancel();
    	                }
    	            });
            	}
	            saveAlert = builder.create();
	            saveAlert.show();
			}
		};
		deleteDialog = new Runnable() {
			@Override
			public void run() {
            	if (deleteAlert != null) {
            		if (deleteAlert.isShowing()) {
            			return;
            		}
            	}
            	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            	if (unsentTracks > 0 || unsentAudios > 0)
            		builder.setMessage(getStr(R.string.unsent_notif) + " " + getStr(R.string.delete_notif));
            	else
            		builder.setMessage(getStr(R.string.delete_notif));
	            builder.setCancelable(false);
	            builder.setPositiveButton(getStr(R.string.yes), new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int id) {
	            		File grimpant = new File(Environment.getExternalStorageDirectory().getPath() + "/Grimpant");
	            		File[] files = grimpant.listFiles();
	            		for (int i = 0; i < files.length; i++) {
	            			files[i].delete();
	            		}
	            	}
	            });
	            builder.setNegativeButton(getStr(R.string.no), new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int id) {
	            		dialog.cancel();
	            	}
	            });
	            deleteAlert = builder.create();
	            deleteAlert.show();
			}
		};*/

		//Intent intent = new Intent(this, GPSActivity.class);
	    //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, Notification.FLAG_AUTO_CANCEL);

	  /*  mBuilder = new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_notif)
		        .setContentTitle(getStr(R.string.unsent_notif))
		        .setContentText(getStr(R.string.detail_notif))
	    		.setContentIntent(pIntent)
	    		.setAutoCancel(true)
	    		.setTicker(getStr(R.string.unsent_notif));

	    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/
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
		            	searching.setVisibility(View.VISIBLE);
		            	accuracy.setText("---");
		            	lattitude.setText("---");
		            	longitude.setText("---");
		            	active.setText("---");
		            	//all.setText("---");
		            }
		            else if (messageLock.equals("Yes")) {
		            	searching.setVisibility(View.INVISIBLE);
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
	            	lattitude.setText(lat);
	            }
	            if (messageLon != -1000f) {
	            	String lon = String.valueOf(messageLon);
		            Log.v("Receive", lon);
	            	longitude.setText(lon);
	            }
	            if (messageAct != -1000) {
	            	String act = String.valueOf(messageAct);
		            Log.v("Receive", act);
	            	active.setText(act);
	            }
	            if (messageAll != -1000) {
	            	String allS = String.valueOf(messageAll);
		            Log.v("Receive", allS);
	            	all.setText(allS);
	            }
	        }
	    };
		registerReceiver(receiver, new IntentFilter(GPSService.messageAction));
	}
	
	/*@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (firstFocus) {
			LayoutParams params1 = pngAnim1.getLayoutParams();
			params1.height = (int)(290*scale);
			params1.width = (int)(290*scale);
			pngAnim1.setLayoutParams(params1);
			LayoutParams params3 = video.getLayoutParams();
			params3.height = width;
			params3.width = width;
			video.setLayoutParams(params3);
			blank.setVisibility(View.INVISIBLE);
			blank.setBackgroundColor(Color.argb(127, 255, 255, 255));
			gb.setEnabled(true);
			firstFocus = false;
			if (tb.isChecked()) {
				onGo(null);
			}
		}
		Runtime.getRuntime().gc();
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_gps, menu);
/*	    getLayoutInflater().setFactory( new Factory() {
	        @Override
	        public View onCreateView ( String name, Context context, AttributeSet attrs ) {
	            if ( name.equalsIgnoreCase( "TextView" ) ) {
	                try {
	                    LayoutInflater li = LayoutInflater.from(context);
	                    final View view = li.createView( name, null, attrs );
	                    handler.post( new Runnable() {
	                        public void run () {
	                        	if (((TextView) view).getText().equals(getStr(R.string.menu_delete))) {
		                        	((TextView) view).setTextSize(16);
	                        	}
	                        }
	                    });
	                    return view;
	                }
	                catch ( InflateException e ) {
	                    e.printStackTrace();
	                }
	                catch ( ClassNotFoundException e ) {
	                    e.printStackTrace();
	                }
	            }
	            return null;
	        }
	    });
*/	    
	    return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		/*if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getApplicationContext(), getStr(R.string.no_sd), Toast.LENGTH_LONG).show();
			moveTaskToBack(true);
			return;
		}*/
		startService(gpsIntent);
		bindService(gpsIntent, gpsServiceConnection, Context.BIND_AUTO_CREATE);
		//startActivity(new Intent(this, MapActivity.class));
	}

	@Override
	protected void onPause() {
		super.onPause();
		/*if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}*/
		/*if (gb.getVisibility() == View.VISIBLE) {
			onGo(null);
		}*/
		/*else {
			getUnsent(false);
		}*/
		unbindService(gpsServiceConnection);
	}

	/*public void onGo(View view) {
		getUnsent(true);
		if (view != null) {
			if (!isUploading && !recordingMode)
				handler.post(unsentDialog);
			handler.postDelayed(GPSDialog, 100);
		}
		
		gb.setVisibility(View.INVISIBLE);
		gt.setVisibility(View.INVISIBLE);
		tb.setVisibility(View.VISIBLE);
		rb.setVisibility(View.VISIBLE);
		mapOver.setVisibility(View.VISIBLE);
		video.pause();
		video.setVisibility(View.INVISIBLE);
		video.suspend();
		parent.removeView(video);
		Runtime.getRuntime().gc();
	}*/
	
	public void onTrackingToggled(View view) {
		boolean on = tb.isChecked();

		if (on) {
			final Handler handler = new Handler();
			handler.post(GPSDialog);
			gpsService.startTracking();
			//trackFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Grimpant", gpsService.lastSession() + ".csv");
		}
		else {
			gpsService.stopTracking();
			/*if (trackFile.length() < 1) {
				trackFile.delete();
			}
			else {
				saveRecording = false;
				handler.post(saveDialog);
			}*/
		}
	}

	/*public void onRecordingToggled(View view) {
		boolean start = rb.isChecked();
		if (start) {
			if (!gpsService.gotLock()) {
				handler.post(okDialog);
				rb.setChecked(false);
				return;
			}
			String path = gpsService.lastLocation();
			String name = path.split("/")[path.split("/").length - 1];
			double lat = Double.parseDouble(name.split(",")[0]);
			double lon = Double.parseDouble(name.split(",")[1]);
			if (lat < GPSService.topLat - GPSService.heightLat || lat > GPSService.topLat || lon < GPSService.leftLon || lon > GPSService.leftLon + GPSService.widthLon) {
				Toast.makeText(getApplicationContext(), getStr(R.string.outide_map), Toast.LENGTH_LONG).show();
				rb.setChecked(false);
				return;
			}
			if (pb.getVisibility() == View.VISIBLE) {
				onBack(null);
			}
			sb.setVisibility(View.INVISIBLE);
			tb.setEnabled(false);
			blank.setVisibility(View.VISIBLE);
			playAnim();
			recorder.start(path);
			recordingMode = true;
		}
		else {
			stopAnim();
		}
	}*/

	/*public void onListen(View view) {
		boolean play = pb.isChecked();
		if (play) {
			onPlay(view);
		}
		else {
			if (listeningMode) {
				seedStopAnim();
				return;
			}
			onStop(view);
		}
	}*/
	
	/*public void onPlay(View view) {
		stb.setEnabled(true);
		rb.setEnabled(false);
		bb.setEnabled(false);
		sb.setEnabled(false);
		playAnim();
		try {
			mPlayer.start();
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
	}*/

	/*public void onStop(View view) {
		pb.setEnabled(true);
		stb.setEnabled(false);
		bb.setEnabled(true);
		sb.setEnabled(true);

		try {
			mPlayer.pause();
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
		stopAnim();
		saveRecording = true;
		handler.post(saveDialog);
	}*/
	
	/*public void onSave(View view) {
		try {
			mPlayer.release();
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
		pb.setVisibility(View.INVISIBLE);
		stb.setVisibility(View.INVISIBLE);
		sb.setVisibility(View.INVISIBLE);
		tb.setEnabled(true);
		rb.setEnabled(true);
		pngAnim1.setVisibility(View.INVISIBLE);
		blank.setVisibility(View.INVISIBLE);
		if (gpsService.gotLock())
			center.setVisibility(View.VISIBLE);
		addSeed(recorder.lastMP3());
		File audioFile = new File(recorder.lastMP3());
		Log.v("Upload", "Audio file exists: " + audioFile.exists());
		UploadFile ua = new UploadFile(audioFile);
		ua.execute();
		recordingMode = false;
	}*/

	/*public void onBack(View view) {
		mPlayer.release();
		pb.setVisibility(View.INVISIBLE);
		stb.setVisibility(View.INVISIBLE);
		sb.setVisibility(View.INVISIBLE);
		tb.setEnabled(true);
		rb.setEnabled(true);
		pngAnim1.setVisibility(View.INVISIBLE);
		blank.setVisibility(View.INVISIBLE);
		try {
			File audioFile = new File(recorder.lastMP3());
			audioFile.delete();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (gpsService.gotLock())
			center.setVisibility(View.VISIBLE);
		recordingMode = false;
	}*/
	
	public void onAbout(MenuItem item) {
		//onReturn(null);
		about.setVisibility(View.VISIBLE);
		bb.setVisibility(View.VISIBLE);
	}

	/*public void onHelp(MenuItem item) {
		onReturn(null);
		help.setVisibility(View.VISIBLE);
		bb.setVisibility(View.VISIBLE);
	}*/

	public void onReturn(View view) {
		about.setVisibility(View.INVISIBLE);
		//help.setVisibility(View.INVISIBLE);
		bb.setVisibility(View.INVISIBLE);
	}
	
	/*public void onDelete(MenuItem item) {
		handler.post(deleteDialog);
	}*/

	public void onReset(MenuItem item) {
		NodeManager.reset();
		//handler.post(deleteDialog);
	}

	/*private class loadNextFrame extends TimerTask {
		public void run() {
			if (curFrameI <= 900 && !cancelAnim) {
				if (System.currentTimeMillis() - curFrameT1 <= 250) {
	        	    curFrameT1 = System.currentTimeMillis();
		    		String image = "";
		    		if (curFrameI < 10)
		    			image = "encircle_0000" + curFrameI;
		    		else if (curFrameI < 100)
		    			image = "encircle_000" + curFrameI;
		    		else
		    			image = "encircle_00" + curFrameI;
		            int resID = getResources().getIdentifier(image, "drawable", getPackageName());
		            frame = getResources().getDrawable(resID);
		            frameLoaded = true;
				}
				else {
					curFrameT1 = System.currentTimeMillis();
				}
			}
		}
	}

	private class setNextFrame extends TimerTask {
		public void run() {
			if (!cancelAnim) {
				if (curFrameI <= 900) {
					curFrameT2 = System.currentTimeMillis();
					if (frameLoaded) {
						runOnUiThread(setFrame);
						frameLoaded = false;
					}
		            
					curFrameI += 3;
				}
				else {
					runOnUiThread(animCompletion);
					cancelAnim = true;
				}
			}
		}
	}*/

	/*private void playAnim() {
		pngAnim1.setImageResource(R.drawable.encircle_00000);
		curFrameI = 3;
		cancelAnim = false;
		load = new loadNextFrame();
		set = new setNextFrame();
        timer = new Timer();
	    curFrameT1 = System.currentTimeMillis() - 200;
        timer.schedule(load, 0, 200);        
        timer.schedule(set, 0, 200);        
        pngAnim1.setVisibility(View.VISIBLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void stopAnim() {
		sb.setVisibility(View.VISIBLE);
		rb.setEnabled(false);
		pb.setVisibility(View.VISIBLE);
		stb.setEnabled(false);
		recorder.stop();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		cancelAnim = true;
		timer.cancel();
		Uri uri = Uri.parse("file://" + recorder.lastMP3());
		try {
			mPlayer = MediaPlayer.create(this, uri);
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            public void onCompletion(MediaPlayer mp) {
	        		pb.setEnabled(true);
	        		stb.setEnabled(false);
	        		bb.setEnabled(true);
	        		sb.setEnabled(true);
	            	stopAnim();
	        		saveRecording = true;
	        		handler.post(saveDialog);
	            }
	        });
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
	}*/

	/*public class UploadFile extends AsyncTask<Void, Void, Void> {
		private File fileToUpload;
		private boolean error = false;
		private String type = getStr(R.string.track);
		
		public UploadFile(File toUpload) {
			super();
			fileToUpload = toUpload;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.v("Upload", "Uploading file...");
			if (fileToUpload.exists()) {
				isUploading = true;
				if (fileToUpload.getName().endsWith(".csv"))
					type = getStr(R.string.track);
				else
					type = getStr(R.string.audio);
				mBuilder.setContentTitle(getStr(R.string.upload_notif));
				mBuilder.setContentText(getStr(R.string.uploading) + " " + type + "...");
				mBuilder.setTicker(getStr(R.string.uploading) + " " + type + "...");
				mNotificationManager.notify(1, mBuilder.build());
				FTPClient ftpClient = new FTPClient();
				try {
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String devid = tm.getDeviceId();
					ftpClient.connect(InetAddress.getByName("ftp.minim-v.com"));
					ftpClient.login("grimpant", "miNimVcom1!");

					if(!ftpClient.changeWorkingDirectory(devid)){
						ftpClient.makeDirectory(devid);
						ftpClient.changeWorkingDirectory(devid);
					}

					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					BufferedInputStream buffIn = null;
					buffIn = new BufferedInputStream(new FileInputStream(fileToUpload));
					ftpClient.enterLocalPassiveMode();
					if (ftpClient.storeFile(fileToUpload.getName(), buffIn)) {
						File rename = new File(fileToUpload.getParent(), "." + fileToUpload.getName());
						fileToUpload.renameTo(rename);
						Log.v("Upload", "Upload file successful");
					}
					else {
						Log.v("Upload", "storeFile error");
						BugSenseHandler.addCrashExtraData("Upload " + type, "storeFile error");
						error = true;
					}
					buffIn.close();
					
					ftpClient.logout();
					ftpClient.disconnect();
				}
				catch (Exception e) {
					e.printStackTrace();
					BugSenseHandler.sendException(e);
					error = true;
				}
			}
			else {
				Log.v("Upload", "File doesn't exist");
				BugSenseHandler.addCrashExtraData("Upload " + type, "File doesn't exist");
				error = true;
			}
			return null;
		}
		protected void onPostExecute(Void result) {
			String msg;
			if (!error) {
				msg = getStr(R.string.uploading) + " " + type + " " + getStr(R.string.uploading_finished) + ".";
				mNotificationManager.cancel(1);
			}
			else {
				msg = getStr(R.string.uploading_error) + " " + type + " !";
				mBuilder.setContentText(msg);
				mBuilder.setTicker(msg);
				mNotificationManager.notify(1, mBuilder.build());
			}
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			isUploading = false; 
			getUnsent(true);
		}
	}*/

	/*public class UploadUnsent extends AsyncTask<Void, Void, Void> {
		private boolean error = false;
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.v("Upload", "Uploading unsent files...");
			if (unsentFiles.size() > 0) {
				isUploading = true;
				mBuilder.setContentTitle(getStr(R.string.upload_notif));
				mBuilder.setContentText(getStr(R.string.uploading) + " " + getStr(R.string.uploading_unsent) + "...");
				mBuilder.setTicker(getStr(R.string.uploading) + " " + getStr(R.string.uploading_unsent) + "...");
				mNotificationManager.notify(1, mBuilder.build());
				FTPClient ftpClient = new FTPClient();
				try {
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String devid = tm.getDeviceId();
					ftpClient.connect(InetAddress.getByName("ftp.minim-v.com"));
					ftpClient.login("grimpant", "miNimVcom1!");
					if(!ftpClient.changeWorkingDirectory(devid)) {
						ftpClient.makeDirectory(devid);
						ftpClient.changeWorkingDirectory(devid);
					}
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					ftpClient.enterLocalPassiveMode();
					for (int i = 0; i < unsentFiles.size(); i++) {
						File fileToUpload = (File) unsentFiles.get(i);
						if (fileToUpload.exists()) {
							BufferedInputStream buffIn = null;
							buffIn = new BufferedInputStream(new FileInputStream(fileToUpload));
														
							if (ftpClient.storeFile(fileToUpload.getName(), buffIn)) {
								File rename = new File(fileToUpload.getParent(), "." + fileToUpload.getName());
								fileToUpload.renameTo(rename);
								Log.v("Upload", "Upload File Successful");
							}
							else {
								Log.v("Upload", "storeFile error");
								BugSenseHandler.addCrashExtraData("Upload unsent", "storeFile error");
								error = true;
							}
							
							buffIn.close();
						}
						else {
							Log.v("Upload", "File doesn't exist");
							BugSenseHandler.addCrashExtraData("Upload unsent", "File doesn't exist");
							error = true;
						}
					}
					ftpClient.logout();
					ftpClient.disconnect();
				}
				catch (Exception e) {
					e.printStackTrace();
					BugSenseHandler.sendException(e);
					error = true;
				}
			}
			return null;
		}
		protected void onPostExecute(Void result) {
			String msg;
			if (!error) {
				msg = getStr(R.string.uploading) + " " + getStr(R.string.uploading_unsent) + " " + getStr(R.string.uploading_finished) + ".";
				mNotificationManager.cancel(1);
			}
			else {
				msg = getStr(R.string.uploading_error) + " " + getStr(R.string.uploading_unsent) + " !";
				mBuilder.setContentText(msg);
				mBuilder.setTicker(msg);
				mNotificationManager.notify(1, mBuilder.build());
			}
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			isUploading = false; 
			getUnsent(true);
		}
	}*/

	/*private void getUnsent(boolean onResume) {
		if (!isUploading && !recordingMode) {
			File grimpant = new File(Environment.getExternalStorageDirectory().getPath() + "/Grimpant");
			File[] files = grimpant.listFiles();
			unsentFiles.clear();
			allAudioPaths.clear();
			unsentTracks = 0;
			unsentAudios = 0;
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					if (files[i].getName().endsWith(".mp3") && !files[i].isHidden()) {
						String path = files[i].getAbsolutePath();
						allAudioPaths.add(path);
					}
					if (!files[i].isHidden()) {
						Log.v("Test", "this file is not hidden: " + files[i].getName());
						if (!(tb.isChecked() && trackFile.equals(files[i])))
							unsentFiles.add(files[i]);
						if (files[i].getName().endsWith(".mp3"))
							unsentAudios++;
						else {
							if (!(tb.isChecked() && trackFile.equals(files[i])))
								unsentTracks++;
						}
					}
				}
			}
			if (unsentFiles.size() > 0) {
				mBuilder.setContentTitle(getStr(R.string.unsent_notif));
		        mBuilder.setContentText(getStr(R.string.detail_notif));
	    		mBuilder.setTicker(getStr(R.string.unsent_notif));
				mNotificationManager.notify(1, mBuilder.build());
			}
			Log.v("Upload", "Tracks: " + unsentTracks);
			Log.v("Upload", "Audios: " + unsentAudios);
			if (onResume) {
				DownloadAudioPaths getServerAudioPaths = new DownloadAudioPaths();
				getServerAudioPaths.execute();
			}
		}
	}*/
	
	/*public class DownloadAudioPaths extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
			    URL url = new URL("http://minim-v.com/paths.php?file_dir=grimpant");
			    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			    String path;
			    while ((path = in.readLine()) != null) {
			    	allAudioPaths.add(path);
			    }
			    in.close();
			}
			catch (MalformedURLException e) {
			}
			catch (IOException e) {
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			addAllSeeds();
		}
	}*/
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		/*try {
			mNotificationManager.cancel(1);
			//BugSenseHandler.closeSession(this);
			//video.suspend();
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}*/
	}
	
	/*private View addSeed(String path) {
		try {
			int widthDP = ((int)(409.6*scale))*20;
			int heightDP = ((int)(409.6*scale))*20;
			float seedWidth = 32.0f;
			float seedHeight = 32.0f;
			String name = path.split("/")[path.split("/").length - 1];
			double lat = Double.parseDouble(name.split(",")[0]);
			double lon = Double.parseDouble(name.split(",")[1]);
			if (lat < GPSService.topLat - GPSService.heightLat || lat > GPSService.topLat || lon < GPSService.leftLon || lon > GPSService.leftLon + GPSService.widthLon) {
				return null;
			}
			double x = -(widthDP*(GPSService.leftLon - lon)/GPSService.widthLon + seedWidth*scale/2.0f);
			double y = -(heightDP*(lat - GPSService.topLat)/GPSService.heightLat + seedHeight*scale/2.0f);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)(seedWidth*scale), (int)(seedHeight*scale));
	    	Log.v("Test", "addSeed: " + x + ", " + y);
			params.leftMargin = (int) x;
			params.topMargin = (int) y;
			ImageView pin = new ImageView(this);
			pin.setImageResource(R.drawable.seed);
			pin.setScaleType(ImageView.ScaleType.FIT_CENTER);
			pin.setContentDescription(path);
			pin.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
			        onSeedListen(v);
			    }
			});
			mapPins.addView(pin, params);
			Log.v("Test", "addSeed: " + path + " seed added");
			return pin;
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	/*private void addAllSeeds() {
		Log.v("Test", "addAllSeeds: childCount=" + mapPins.getChildCount());
		ArrayList<String> children = new ArrayList<String>();
		String path;
		for (int i = 0; i < mapPins.getChildCount(); i++) {
			path = mapPins.getChildAt(i).getContentDescription().toString();
			Log.v("Test", "addAllSeeds: i=" + i + ", childPath=" + path);
			if (path.startsWith("/storage")) {
				File file = new File(path);
				if (!file.exists()) {
					mapPins.removeViewAt(i);
					Log.v("Test", "addAllSeeds: deleted a seed");
				}
				else
					children.add(path);
			}
			else
			children.add(path);
		}
		for (int i = 0; i < allAudioPaths.size(); i++) {
			path = allAudioPaths.get(i);
			if (!children.contains(path)) {
				addSeed(path);
			}
		}
	}*/

	/*public void onSeedListen(View view) {
		if (recordingMode || listeningMode)
			return;
		if (isNetworkAvailable()) {
			listeningMode = true;
			findViewById(R.id.spinner).setVisibility(View.VISIBLE);
			String path = view.getContentDescription().toString();
		    Log.v("Test", path);

			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		    String devid = path.split("/")[path.split("/").length - 2];
		    Log.v("Test", devid);
		    if (devid.equals(tm.getDeviceId())) {
			   	String name = path.split("/")[path.split("/").length - 1];
			   	String localpath = Environment.getExternalStorageDirectory().getPath() + "/Grimpant/." + name;
			    Log.v("Test", localpath);
			   	File localfile = new File(localpath);
			   	if (localfile.exists()) {
			   		path = localpath;
			   	}
		    }
			
			if (path.startsWith("/storage")) {
				File file = new File(path);
				if (!file.exists()) {
					file =  new File(path.replace("Grimpant/", "Grimpant/."));
				}
				if (file.exists()) {
				    Log.v("Test", "playing from storage");
					path = "file://" + path;
				}
				else
					return;
			}
			Uri uri = Uri.parse(path);
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(this, uri);
				mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
		            public void onPrepared(MediaPlayer mp) {
		            	findViewById(R.id.spinner).setVisibility(View.INVISIBLE);
		            	mPlayer.start();
		        		playAnim();
		            }
		        });
				mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		            public void onCompletion(MediaPlayer mp) {
		            	seedStopAnim();
		            }
		        });
				mPlayer.prepareAsync();
				pb.setVisibility(View.VISIBLE);
				pb.setChecked(true);
				rb.setEnabled(false);
				blank.setVisibility(View.VISIBLE);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Toast.makeText(getApplicationContext(), getStr(R.string.no_internet), Toast.LENGTH_SHORT).show();
		}
	}*/
	
	/*private void seedStopAnim() {
		try{
			mPlayer.stop();
			mPlayer.release();
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		}
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		cancelAnim = true;
		timer.cancel();
        pngAnim1.setVisibility(View.INVISIBLE);
    	findViewById(R.id.spinner).setVisibility(View.INVISIBLE);
		pb.setVisibility(View.INVISIBLE);
		pb.setChecked(false);
		rb.setEnabled(true);
		blank.setVisibility(View.INVISIBLE);
		listeningMode = false;
	}*/
	
	private String getStr(int id) {
		return getResources().getString(id);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (bb.getVisibility() == View.VISIBLE) {
		    	onReturn(null);
	        	Log.v("Test", "onBackDown");
		    	return true;
        	}
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/*private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}*/
}

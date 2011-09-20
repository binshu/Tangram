package com.bs.studio.android.tangram.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;

import com.bs.studio.android.math.Point;
import com.bs.studio.android.tangram.R;
import com.bs.studio.android.tangram.views.PlayTanView;

public class TangramPlayActivity extends Activity {

	private PlayTanView mainView = null;
	private TangramManager tangramManager = null;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.tangame);
		
		int tangramId = this.getIntent().getIntExtra("TangramId", 0);
		
		mainView = (PlayTanView) findViewById(R.id.playTanView1);
		Display display = this.getWindowManager().getDefaultDisplay();
		mainView.initialize(new Point(display.getWidth(), display.getHeight()));
		
		tangramManager = TangramManager.createTamgramManager(this);
		
		mainView.setTangram(tangramManager.getTangramById(tangramId));
	}
	
		
}
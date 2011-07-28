package com.swp.workshop.fbint;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

public class SWPFacebookWorkshopActivity extends Activity implements OnClickListener{
    
	Facebook facebook = new Facebook("119397311477456");
	
	Button btLogin;
	Button btPostWall;
	Button btPhotoUpload;
	Button btLogout;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btLogin = (Button) findViewById(R.id.btLogin);
        btPostWall = (Button) findViewById(R.id.btPostWall);
        btPhotoUpload = (Button) findViewById(R.id.btPhotoUpload);
        btLogout = (Button) findViewById(R.id.btLogout);
        
        btLogin.setOnClickListener(this);
        btPostWall.setOnClickListener(this);
        btPhotoUpload.setOnClickListener(this);
        btLogout.setOnClickListener(this);
        
        boolean isSessionValid = SessionStore.restore(facebook, this);
        setUILogined(isSessionValid);
    }
    
    private void setUILogined(boolean isLogined) {
    	btLogin.setEnabled(!isLogined);
    	btLogout.setEnabled(isLogined);
    	btPostWall.setEnabled(isLogined);
    	btPhotoUpload.setEnabled(isLogined);
    	
    	btLogin.invalidate();
    	btLogout.invalidate();
    	btPostWall.invalidate();
    	btPhotoUpload.invalidate();
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btLogin:
		{
			login();
		}
			break;
		case R.id.btPostWall:
		{
			postWall();
		}
			break;
		case R.id.btPhotoUpload:
		{
			postPhoto();
		}
			break;
		case R.id.btLogout:
		{
			logout();
		}
			break;
		default:
			break;
		}
	}
	
	private void login() {
		facebook.authorize(this, new String[] { "publish_stream", "read_stream"  }, facebookDialogListener);
	}
	
	private void logout() {
		try {
			String success = facebook.logout(this);
			if(success.equals("true")){
				SessionStore.clear(this);
				setUILogined(false);
			}
		} catch (MalformedURLException e) {
			Log.e("FBWorkshop", "ERROR", e);
		} catch (IOException e) {
			Log.e("FBWorkshop", "ERROR", e);
		}
	}
	
	private void postWall() {
		Bundle params = new Bundle();
		params.putString("message", "Test MT2");
		params.putString("name", "โครงการ MT2");
		params.putString("caption", "Android Workshop");
		params.putString("description", "Android Facebook Integration Workshop at Software Park");
		params.putString("link", "http://www.swpark.or.th/mt2/");
		params.putString("picture", "http://www.swpark.or.th/mt2/images/stories/mt2.png");
		try {
			String  result = facebook.request("/me/feed", params, "post");
			Log.e("Test", result);
		} catch (FileNotFoundException e) {
			Log.e("FBWorkshop", "ERROR", e);
		} catch (MalformedURLException e) {
			Log.e("FBWorkshop", "ERROR", e);
		} catch (IOException e) {
			Log.e("FBWorkshop", "ERROR", e);
		}
	}
	
	private void postPhoto() {
		Bundle params = new Bundle();
		params.putString("message", "my picture description");
		params.putByteArray("source", getImageByteArray());
		try {
			String imageId = facebook.request("/me/photos", params, "post");
		} catch (FileNotFoundException e) {
			Log.e("FBWorkshop", "ERROR", e);
		} catch (MalformedURLException e) {
			Log.e("FBWorkshop", "ERROR", e);
		} catch (IOException e) {
			Log.e("FBWorkshop", "ERROR", e);
		}
	}
	
	private byte[] getImageByteArray() {
		try {
			InputStream is = getResources().getAssets().open("steak.jpg");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int len = 0;
			while((len=is.read(buff)) != -1) {
				baos.write(buff, 0, len);
			}
			byte[] imageBytes = baos.toByteArray();
			return imageBytes;
		} catch (IOException e) {
			Log.e("FBWorkshop", "ERROR", e);
			return null;
		}
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
    
	
	Facebook.DialogListener facebookDialogListener = new Facebook.DialogListener() {
		
		public void onFacebookError(FacebookError e) {
			Log.e("FBWorkshop", "ERROR", e);
		}
		
		public void onError(DialogError e) {
			Log.e("FBWorkshop", "ERROR", e);
		}
		
		public void onComplete(Bundle values) {
			SessionStore.save(facebook, SWPFacebookWorkshopActivity.this);
			if(facebook.isSessionValid()) {
				setUILogined(true);
			}
		}
		
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}
	};
}
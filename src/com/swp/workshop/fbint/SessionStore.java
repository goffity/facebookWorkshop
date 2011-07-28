/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swp.workshop.fbint;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.facebook.android.Facebook;

public class SessionStore {
    
    public static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    public static final String KEY = "facebook-session";
    public static final String USER_ID = "user_id";
    
    public static boolean save(Facebook session, Context context) {
        Editor editor =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(TOKEN, session.getAccessToken());
        editor.putLong(EXPIRES, session.getAccessExpires());
        String jsonStr;
		try {
			jsonStr = session.request("/me");
			JSONObject json = new JSONObject(jsonStr);
	        String userID = json.getString("id");
	        editor.putString(USER_ID, userID);
		} catch (Exception e) {
			Log.e("FacebookWorkshop", "ERROR", e);
		}
        
        return editor.commit();
    }

    public static boolean restore(Facebook session, Context context) {
        SharedPreferences savedSession =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        session.setAccessToken(savedSession.getString(TOKEN, null));
        session.setAccessExpires(savedSession.getLong(EXPIRES, 0));
        return session.isSessionValid();
    }

    public static void clear(Context context) {
        Editor editor = 
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
    
}

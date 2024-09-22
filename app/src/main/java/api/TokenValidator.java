package api;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.jwt.JWT;
import com.auth0.android.jwt.DecodeException;
import java.util.Date;

public class TokenValidator {




    public static String getTokenFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    public static String decodeTokenAndGetUserDisplayname(String token) {
        JWT jwt = new JWT(token);
        return jwt.getClaim("displayname").asString();
    }



    public static boolean isTokenValid(String token) {
        try {
            JWT jwt = new JWT(token);
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.after(new Date())) {
                // The token is not expired
                return true;
            }
        } catch (DecodeException e) {
            // The token is invalid
            e.printStackTrace();
        }
        return false;
    }

    // Method to decode the token and get the user ID
    public static String decodeTokenAndGetUserId(String token) {
        JWT jwt = new JWT(token);
        return jwt.getClaim("id").asString();
    }
}

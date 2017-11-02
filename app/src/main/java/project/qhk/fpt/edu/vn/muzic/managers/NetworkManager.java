package project.qhk.fpt.edu.vn.muzic.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by WindzLord on 10/22/2016.
 */

public class NetworkManager {

    private static NetworkManager instance;
    private ConnectivityManager connectivityManager;

    private NetworkManager(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkManager getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new NetworkManager(context);
    }

    public boolean isConnectedToInternet() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}

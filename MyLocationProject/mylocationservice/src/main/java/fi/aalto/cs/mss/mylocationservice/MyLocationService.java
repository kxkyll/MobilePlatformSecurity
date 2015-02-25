/*
 * Copyright (C) 2013 University of Helsinki
 * Copyright (C) 2015 Aalto University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.aalto.cs.mss.mylocationservice;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.LocationServices;

import fi.aalto.cs.mss.mylocationcommon.MyLocationCommon;
import fi.aalto.cs.mss.mylocationservice.MyAbstractLocationService;

public class MyLocationService extends MyAbstractLocationService {

    // Tag used for log message
    private static final String TAG = "MyLocationService";

    /** List used to keep track of all current registered clients. */
    protected final ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    /** Holds the address indicated by the last location change */
    protected Address mAddress;

    /** Geocoder instance used to perform reverse geocoding */
    private Geocoder geocoder;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyLocationCommon.MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    Log.d(TAG, "Location client registered");
                    break;
                case MyLocationCommon.MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    Log.d(TAG, "Location client unregistered");
                    break;
                case MyLocationCommon.MSG_UPDATE_LOCATION:
                    Log.d(TAG, "Received location update request");

                    if (mGoogleApiClient.isConnected()) {
                        Location location = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        updateCurrentLocation(location);
                    }
                    sendLocationUpdate();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate() {
        Log.d(TAG, "Service starting");
        super.onCreate();

        // Initialize Geocoder instance used retrieve the the current address
        geocoder = new Geocoder(this, Locale.getDefault());

        // Tell the user we started.
        Toast.makeText(this, getString(R.string.notify_started),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            // After disconnect() is called, the client is considered "dead"
            mGoogleApiClient.disconnect();
        }

        // Tell the user we stopped
        Toast.makeText(this, getString(R.string.notify_stopped),
                Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Service stopping");
        super.onDestroy();
    }

    /**
     * When binding to the service, we return an interface to our messenger for
     * sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Location client bound to service");
        return mMessenger.getBinder();
    }

    /*
     * Called by Location Services when an location update is available
     */
    @Override
    public void onLocationChanged(Location location) {
        updateCurrentLocation(location);
        sendLocationUpdate();
    }

    /**
     * Update cached location to specified location.
     *
     * @param location
     *            New location
     */
    private void updateCurrentLocation(Location location) {
        if (location == null) {
            return;
        }
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                mAddress = addresses.get(0);
            }
        } catch (IOException e) {
            Log.e(TAG, "Reverse geocoding failed");
            e.printStackTrace();
        }
    }

    /**
     * Send location update to clients.
     */
    private void sendLocationUpdate() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MyLocationCommon.KEY_LOCATION_UPDATE_PARCELABLE,
                mAddress);
        Message msg = Message
                .obtain(null, MyLocationCommon.MSG_UPDATE_LOCATION);
        msg.setData(bundle);

        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                mClients.get(i).send(msg);
            } catch (RemoteException e) {
                // The client is dead. Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }

        Log.d(TAG, "Sent location update: '" + mAddress + "'");
    }
}
package cat.xojan.nearbyconnections20

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class MainActivity : AppCompatActivity(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mGoogleApiClient: GoogleApiClient
    /** Callbacks for connections to other devices.  */
    private val mConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.d(TAG, "onConnectionInitiated")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.d(TAG, "onConnectionResult")
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "onDisconnected")
        }
    }
    private val mEndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(
                endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
            // An endpoint was found!
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
        }
    }
    private val TAG = "GoogleApiClient"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build()
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect();
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    override fun onConnected(p0: Bundle?) {
        Log.d(TAG, "connected")
        startAdvertising()
        //startDiscovery()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "suspended")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "failed: " + connectionResult.errorMessage)
    }

    private fun startAdvertising() {
        Nearby.Connections.startAdvertising(
                mGoogleApiClient,
                "userNickName",
                "serviceID",
                mConnectionLifecycleCallback,
                AdvertisingOptions(Strategy.P2P_STAR))
                .setResultCallback { result ->
                    if (result.status.isSuccess) {
                        Log.d(TAG, "Advertising!")
                    } else {
                        Log.d(TAG, "Not Advertising!")
                    }
                }
    }

    private fun startDiscovery() {
        Nearby.Connections.startDiscovery(
                mGoogleApiClient,
                "serviceID",
                mEndpointDiscoveryCallback,
                DiscoveryOptions(Strategy.P2P_STAR))
                .setResultCallback(
                        object : ResultCallback<Status> {
                            override fun onResult(status: Status) {
                                if (status.isSuccess()) {
                                    Log.d(TAG, "Discovering!")
                                } else {
                                    Log.d(TAG, "Unable to start Discovering")
                                }
                            }
                        })
    }
}

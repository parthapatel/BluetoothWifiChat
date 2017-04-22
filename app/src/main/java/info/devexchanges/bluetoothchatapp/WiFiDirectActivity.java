package info.devexchanges.bluetoothchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import info.devexchanges.bluetoothchatapp.adapter.WifiListArrayAdapter;
import info.devexchanges.bluetoothchatapp.fragment.ChatFragment;
import info.devexchanges.bluetoothchatapp.fragment.WifiListFragment;
import info.devexchanges.bluetoothchatapp.services.ConnectionManager;
import info.devexchanges.bluetoothchatapp.services.WifiDirectBroadcastReceiver;

public class WiFiDirectActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, WifiListFragment.WifiListListener, ChatFragment.OnFragmentInteractionListener {
    private boolean isWifiP2pEnabled;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private List<WifiP2pDevice> peerList = new ArrayList<WifiP2pDevice>();
    private WifiListArrayAdapter wifiListArrayAdapter;
    private ConnectionManager mConnectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_direct);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);


        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        peerList = new ArrayList<>();
        mConnectionManager = new ConnectionManager(8888);

        wifiListArrayAdapter = new WifiListArrayAdapter(this, peerList);
    }

    private void showDeviceListFragment() {
        WifiListFragment wifiListFragment = new WifiListFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame, wifiListFragment).addToBackStack(null).commit();
    }

    @Override
    public WifiListArrayAdapter getListAdapter() {
        return wifiListArrayAdapter;
    }

    public void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(WiFiDirectActivity.this, "Discovering Peers...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wifi_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discover_peers:
                showDeviceListFragment();
                return true;
        }
        return false;
    }

    public boolean isWifiP2pEnabled() {
        return isWifiP2pEnabled;
    }

    public void setWifiP2pEnabled(boolean wifiP2pEnabled) {
        isWifiP2pEnabled = wifiP2pEnabled;
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        List<WifiP2pDevice> refreshedPeers = new ArrayList<>(peers.getDeviceList());
        if (!refreshedPeers.equals(peers)) {
            peerList.clear();
            peerList.addAll(refreshedPeers);
            wifiListArrayAdapter.notifyDataSetChanged();
        }

        if (peerList.size() == 0) {
            Log.d("csulb", "No devices found");
            return;
        }
    }

    private void connect(WifiP2pConfig config) {
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress; //.getHostAddress();

        if (info.groupFormed && info.isGroupOwner) {
            mConnectionManager.startServer(this);
            ChatFragment wifiListFragment = ChatFragment.newInstance(groupOwnerAddress, "");
            getFragmentManager().beginTransaction().replace(R.id.frame, wifiListFragment).commit();
        } else if (info.groupFormed) {
            ChatFragment wifiListFragment = ChatFragment.newInstance(groupOwnerAddress, "");
            getFragmentManager().beginTransaction().add(R.id.frame, wifiListFragment).addToBackStack(null).commit();
        }
    }

    public void onItemClick(int position) {
        Toast.makeText(this, "Connecting to " + peerList.get(position).deviceName, Toast.LENGTH_SHORT).show();

        WifiP2pDevice device = peerList.get(position);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        connect(config);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

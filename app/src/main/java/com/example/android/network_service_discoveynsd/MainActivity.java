package com.example.android.network_service_discoveynsd;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;

import static android.net.nsd.NsdManager.PROTOCOL_DNS_SD;

public class MainActivity extends AppCompatActivity implements NsdManager.RegistrationListener,NsdManager.ResolveListener, NsdManager.DiscoveryListener {
    NsdServiceInfo mService;

    NsdManager mNsdManager;
    String  mServiceName;
    ServerSocket mServerSocket;
    int mLocalPort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void register(View view)
    {
      //  tearDown();

        initializeServerSocket();

        registerService( 82349);




    }

    public void discover(View v)
    {
        discoverServices();
    }
    public void registerService(int port) {
        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("NsdChat");
        serviceInfo.setServiceType("nsdchat._tcp");
        serviceInfo.setPort(port);

     mNsdManager =(NsdManager)  getSystemService(Context.NSD_SERVICE);
        NsdManager.ResolveListener  mResolveListener=this;
        NsdManager.RegistrationListener mRegistrationListener = this;

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD,(NsdManager.RegistrationListener) this);

        mNsdManager.resolveService(serviceInfo,(NsdManager.ResolveListener) this);

    }
    @Override
    public void onDiscoveryStarted(String regType) {
        Toast.makeText(this,"onDiscoveryStarted",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceFound(NsdServiceInfo service) {
        // A service was found!  Do something wit h it.
        Log.d("sda", "Service discovery success" + service);
        Toast.makeText(this,"Service discovery success"+service,Toast.LENGTH_LONG).show();
        if (!service.getServiceType().equals("nsdchat._tcp")) {
            // Service type is the string containing the protocol and
            // transport layer for this service.
            Toast.makeText(this,"Unknown Service Type:"+ service.getServiceType(),Toast.LENGTH_LONG).show();

        } else if (service.getServiceName().equals(mServiceName)) {
            // The name of the service tells the user what they'd be
            // connecting to. It could be "Bob's Chat App".
            Log.d("sd", "Same machine: " + mServiceName);
            Toast.makeText(this,"Same machine: "+ mServiceName,Toast.LENGTH_LONG).show();
        } else if (service.getServiceName().contains("NsdChat")){
            mNsdManager.resolveService(service, this);
        }
    }

    @Override
    public void onServiceLost(NsdServiceInfo service) {
        // When the network service is no longer available.
        // Internal bookkeeping code goes here.

        Toast.makeText(this,"service lost"+ service.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDiscoveryStopped(String serviceType) {


        Toast.makeText(this,"Discovery stopped: "+  serviceType,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.e("sda", "Discovery failed: Error code:" + errorCode);

        Toast.makeText(this,"Discovery stopped: "+  serviceType,Toast.LENGTH_LONG).show();
        mNsdManager.stopServiceDiscovery(this);
    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.e("sd", "Discovery failed: Error code:" + errorCode);
        Toast.makeText(this,"Discovery failed: Error code: "+  serviceType,Toast.LENGTH_LONG).show();
        mNsdManager.stopServiceDiscovery(this);
    }
    @Override
    public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
        // Save the service name.  Android may have changed it in order to
        // resolve a conflict, so update the name you initially requested
        // with the name Android actually used.
        Toast.makeText(this,"onServiceRegistered "+ NsdServiceInfo.getServiceName(),Toast.LENGTH_LONG).show();
        mServiceName = NsdServiceInfo.getServiceName();
    }

    @Override
    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Registration failed!  Put debugging code here to determine why.
        Toast.makeText(this,"onRegistrationFailed"+ serviceInfo.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo arg0) {
        // Service has been unregistered.  This only happens when you call
        // NsdManager.unregisterService() and pass in this listener.

        Toast.makeText(this,"onServiceUnregistered" ,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Unregistration failed.  Put debugging code here to determine why.
    }
    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e("asd", "Resolve failed" + errorCode);
        Toast.makeText(this,"Resolve failed"+serviceInfo.toString(),Toast.LENGTH_LONG).show();
    }
    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {
        Log.e("asd", "Resolve Succeeded. " + serviceInfo);
        if (serviceInfo.getServiceName().equals(mServiceName)) {
            Log.d("asd", "Same IP.");
            return;
        }
        mService = serviceInfo;
    }

    public void initializeServerSocket() {
        // Initialize a server socket on the next available port.
        try {


            mServerSocket = new ServerSocket(0);

            // Store the chosen port.
            mLocalPort = mServerSocket.getLocalPort();
        }
        catch (IOException s)
        {
Toast.makeText(this,"sad",Toast.LENGTH_LONG);
        }
    }
    public void discoverServices() {
    // Cancel any existing discovery request
       // initializeDiscoveryListener();
        mNsdManager.discoverServices(
                "nsdchat._tcp", PROTOCOL_DNS_SD, this);
    }
    public void stopDiscovery() {

                mNsdManager.stopServiceDiscovery(this);

         //   mDiscoveryListener = null;

    }
    @Override
    protected void onPause() {

            tearDown();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
    }

    // NsdHelper's tearDown method
    public void tearDown() {
        mNsdManager.unregisterService(this);
        mNsdManager.stopServiceDiscovery(this);
    }
}

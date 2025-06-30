package net.imknown.testandroid

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.ext.zLog

class T07NetworkCallbackActivity : AppCompatActivity() {
    private val connectivityManager by lazy {
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            zLog("onAvailable(): The default network is now: $network")
        }

        override fun onCapabilitiesChanged(
            network: Network, networkCapabilities: NetworkCapabilities,
        ) {
            zLog("onCapabilitiesChanged(): The default network($network) changed capabilities: $networkCapabilities")
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            zLog("onLinkPropertiesChanged(): The default network($network) changed link properties: $linkProperties")
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            zLog("onLosing(): $network, $maxMsToLive")
        }

        override fun onLost(network: Network) {
            zLog("onLost(): The application no longer has a default network. The last default network was $network")
        }

        override fun onUnavailable() {
            zLog("onUnavailable()")
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            zLog("onBlockedStatusChanged(): $network, $blocked")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callBlock()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            zLog("isNetworkCapabilityValidated: " + isNetworkCapabilityValidated())
        }
        zLog("isConnected: " + isConnected())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkCapabilityValidated(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
        val currentNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(currentNetwork)
        // val linkProperties = connectivityManager?.getLinkProperties(currentNetwork)
        return (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true)
    }

    private fun isConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
        @Suppress("DEPRECATION")
        return (connectivityManager?.activeNetworkInfo?.isConnected == true)
    }

    private fun callBlock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    } else {
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    }
                )
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
//                .addTransportType(NetworkCapabilities.TRANSPORT_LOWPAN)
                .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
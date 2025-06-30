package net.imknown.testandroid

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Proxy
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import net.imknown.testandroid.ext.zLog

class T11NetworkProxyActivity : AppCompatActivity() {
    private val connectivityManager by lazy { getSystemService<ConnectivityManager>() }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            zLog("networkCallback onAvailable: $network")
        }
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            zLog("networkCallback onCapabilitiesChanged: $network, $networkCapabilities")
        }
        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            zLog("networkCallback onCapabilitiesChanged: $network, $linkProperties")
        }
        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            zLog("networkCallback onBlockedStatusChanged: $network, $blocked")
        }
        override fun onLosing(network: Network, maxMsToLive: Int) {
            zLog("networkCallback onLosing: $network, $maxMsToLive")
        }
        override fun onLost(network: Network) {
            zLog("networkCallback onLosing: $network")
        }
        override fun onUnavailable() {
            zLog("networkCallback onUnavailable")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        printProxies()
    }

    @Suppress("DEPRECATION")
    private fun printLegacyProxies() {
        zLog("defaultHost: ${Proxy.getDefaultHost()}")
        zLog("defaultPort: ${Proxy.getDefaultPort()}")
        zLog("host: ${Proxy.getHost(this)}")
        zLog("port: ${Proxy.getPort(this)}")
    }

    private fun printProxies() {
        printLegacyProxies()

        @Suppress("DEPRECATION")
        connectivityManager?.allNetworks?.forEachIndexed { index, network ->
            val linkProperties = connectivityManager?.getLinkProperties(network)
            val httpProxy = linkProperties?.httpProxy
            zLog("network $index httpProxy host: ${httpProxy?.host}")
            zLog("network $index httpProxy port: ${httpProxy?.port?.toString()}")
            zLog("network $index httpProxy exclusion: ${httpProxy?.exclusionList?.toString()}")
        }

        val networkRequest = NetworkRequest.Builder().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setIncludeOtherUidNetworks(true)
            }
        }.build()
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager?.activeNetwork
            val linkProperties = connectivityManager?.getLinkProperties(activeNetwork)
            val httpProxy = linkProperties?.httpProxy
            zLog("activeNetwork httpProxy host: ${httpProxy?.host}")
            zLog("activeNetwork httpProxy port: ${httpProxy?.port?.toString()}")
            zLog("activeNetwork httpProxy exclusion: ${httpProxy?.exclusionList?.toString()}")

            val defaultProxy = connectivityManager?.defaultProxy
            zLog("defaultProxy host: ${defaultProxy?.host}")
            zLog("defaultProxy port: ${defaultProxy?.port?.toString()}")
            zLog("defaultProxy exclusion: ${defaultProxy?.exclusionList?.toString()}")
        }

        // https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/doc-files/net-properties.html
        // https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html
        // https://docs.oracle.com/javase/8/docs/api/java/net/doc-files/net-properties.html
        with(System.getProperties()) {
            zLog("http.proxyHost: ${this["http.proxyHost"]}")
            zLog("http.proxyPort: ${this["http.proxyPort"]}")
            zLog("http.nonProxyHosts: ${this["http.nonProxyHosts"]}")

            zLog("https.proxyHost: ${this["https.proxyHost"]}")
            zLog("https.proxyPort: ${this["https.proxyPort"]}")
            zLog("https.nonProxyHosts: ${this["https.nonProxyHosts"]}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }
}
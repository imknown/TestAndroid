package net.imknown.testandroid

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.core.net.toUri
import net.imknown.testandroid.databinding.T24ActivityCustomTabsBinding
import net.imknown.testandroid.ext.zLog

class T24CustomTabsActivity : AppCompatActivity() {

    private var launcher: ActivityResultLauncher<Intent>? = null

    private val url = "https://WebAuthn.io"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = T24ActivityCustomTabsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        printStatue()

        // Check if the default browser support Custom Tabs
        val packageNameCt = CustomTabsClient.getPackageName(this, emptyList())
        if (packageNameCt == null) {
            zLog("Default browser does not support CT.")
            return
        }

        // Although Chrome 132 Beta already has Auth Tab,
        // versions 132 ~ 136 may not have this feature enabled and will return false.
        if(CustomTabsClient.isAuthTabSupported(this, packageNameCt)) {
            zLog("AT supported: $packageNameCt")
        } else {
            zLog("AT not supported: $packageNameCt")
        }

        launcher = T24AuthTabServiceConnection.initAuthTabLauncher(this) {
        }

        binding.btnAt.setOnClickListener {
            bindAuthTabService(packageNameCt)
        }
        binding.btnCt.setOnClickListener {
            bindCustomTabsService(packageNameCt)
        }
    }

    private fun printStatue() {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        val flags = PackageManager.MATCH_ALL // MATCH_DEFAULT_ONLY
        val packages = packageManager.queryIntentActivities(intent, flags).mapNotNull {
            it?.activityInfo
        }
        val packagesLog = packages.joinToString {
            val packageName = it.packageName
            val versionName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }.versionName
            "$packageName($versionName)"
        }
        zLog("All browsers: $packagesLog")

        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            packageManager.resolveActivity(intent, flags)
        }
        val packageNameDefault = resolveInfo?.activityInfo?.packageName
        zLog("Default browser: $packageNameDefault")

        val ctSupported = packages.mapNotNull {
            CustomTabsClient.getPackageName(this, listOf(it.packageName), true)
        }
        zLog("CT supported browser: $ctSupported")

        val atSupported = packages.filter {
            CustomTabsClient.isAuthTabSupported(this, it.packageName)
        }
        zLog("AT supported browser: ${atSupported.joinToString { it.packageName }}")
    }

    private var t24AuthTabServiceConnection: T24AuthTabServiceConnection? = null
    private var t24CustomTabsServiceConnection: T24CustomTabsServiceConnection? = null

    private fun bindAuthTabService(packageNameCt: String) {
        val connection = T24AuthTabServiceConnection(this, url, packageNameCt, launcher)
        this.t24AuthTabServiceConnection = connection
        CustomTabsClient.bindCustomTabsService(this, packageNameCt, connection)
    }

    private fun bindCustomTabsService(packageNameCt: String) {
        val connection = T24CustomTabsServiceConnection(this, url, packageNameCt)
        this.t24CustomTabsServiceConnection = connection
        CustomTabsClient.bindCustomTabsService(this, packageNameCt, connection)
    }

    override fun onDestroy() {
        super.onDestroy()

        t24AuthTabServiceConnection?.let {
            unbindService(it)
            it.destroy()
        }
        t24CustomTabsServiceConnection?.let {
            unbindService(it)
            it.destroy()
        }
    }
}
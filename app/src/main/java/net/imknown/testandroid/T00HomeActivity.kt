package net.imknown.testandroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import net.imknown.testandroid.databinding.T00ActivityHomeBinding
import kotlin.reflect.KClass

@Suppress("UNUSED_PARAMETER")
@SuppressLint("SetTextI18n")
class T00HomeActivity : T01LifecycleActivity() {
    private val binding by lazy { T00ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        requestNotificationPermission()

        gsm()

        val callback = onBackPressedDispatcher.addCallback(this) {
            finish()
        }
        callback.isEnabled = false
    }

    private fun gsm() {
        val gapi = GoogleApiAvailability.getInstance()
        val code = gapi.isGooglePlayServicesAvailable(this)
        val message = gapi.getErrorString(code)
        binding.btnFirebase.text = message

        if (code != ConnectionResult.SUCCESS) {
            gapi.makeGooglePlayServicesAvailable(this).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fcm()
                } else {
                    binding.btnFirebase.text = "makeGooglePlayServicesAvailable failed: ${task.exception}"
                }
            }
        } else {
            fcm()
        }
    }

    private fun fcm() {
        binding.btnFirebase.text = "Fetching FCM registration token..."

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                binding.btnFirebase.text = "Fetching FCM registration token failed: ${task.exception}"
                return@OnCompleteListener
            }

            // Get new FCM registration token
            binding.btnFirebase.text = "Firebase token: ${task.result}"
        })
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // FCM SDK (and your app) can post notifications.
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        ) {
            //
        } else {
            // Directly ask for the permission
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { }
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun <A> gotoActivity(kClass: KClass<A>) where A : Activity {
        startActivity(Intent(this, kClass.java))
    }

    fun goto01Lifecycle(view: View) {
        gotoActivity(T01LifecycleActivity::class)
    }

    fun goto02Emoji(view: View) {
        gotoActivity(T02EmojiActivity::class)
    }

    fun goto03BlockApi(view: View) {
        gotoActivity(T03CallBlockApiActivity::class)
    }

    fun goto04PrecomputedText(view: View) {
        gotoActivity(T04PrecomputedTextActivity::class)
    }

    fun goto05WindowInsets(view: View) {
        gotoActivity(T05WindowInsetsActivity::class)
    }

    fun goto06TextViewLineHeight(view: View) {
        gotoActivity(T06TextViewLineHeightActivity::class)
    }

    fun goto07NetworkCallback(view: View) {
        gotoActivity(T07NetworkCallbackActivity::class)
    }

    fun goto08PackageBroadcast(view: View) {
        gotoActivity(T08PackageBroadcastActivity::class)
    }

    fun goto09SamsungLeadingMarginSpanBug(view: View) {
        gotoActivity(T09SamsungLeadingMarginSpanBugActivity::class)
    }

    fun goto10Biometrics(view: View) {
        gotoActivity(T10BiometricsActivity::class)
    }

    fun goto11NetworkProxy(view: View) {
        gotoActivity(T11NetworkProxyActivity::class)
    }

    fun goto12DropDown(view: View) {
        gotoActivity(T12DropDownActivity::class)
    }

    fun goto13FunctionalProgramming(view: View) {
        gotoActivity(T13FunctionalProgrammingActivity::class)
    }

    fun gotoT14WebView(view: View) {
        gotoActivity(T14WebViewActivity::class)
    }

    fun gotoT15File(view: View) {
        gotoActivity(T15FileActivity::class)
    }

    fun gotoT16KeywordContext(view: View) {
        gotoActivity(T16KotlinKeywordContext::class)
    }

    fun gotoT17Font(view: View) {
        gotoActivity(T17FontActivity::class)
    }

    fun gotoT18Focus(view: View) {
        gotoActivity(T18FocusActivity::class)
    }

    fun gotoT19Coordinator(view: View) {
        gotoActivity(T19CoordinatorActivity::class)
    }

    fun gotoT20ComposeEffectSide(view: View) {
        gotoActivity(T20ComposeEffectSideActivity::class)
    }

    fun gotoT21Dpi(view: View) {
        gotoActivity(T21DpiActivity::class)
    }

    fun gotoT22Crypto(view: View) {
        gotoActivity(T22CryptoActivity::class)
    }
}
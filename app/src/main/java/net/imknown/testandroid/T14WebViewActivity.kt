package net.imknown.testandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.databinding.T14ActivityWebviewBinding

class T14WebViewActivity : AppCompatActivity() {

    private val binding by lazy { T14ActivityWebviewBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.webview.apply {
            WebView.setWebContentsDebuggingEnabled(true)

            with(settings) {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                allowFileAccess = true
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                    contract.fileChooserParams = fileChooserParams
                    requestPermissionLauncher.launch(Unit)

                    return true
                }
            }

            loadUrl("file:///android_asset/Test.html")
        }
    }

    private val contract = object : ActivityResultContract<Unit, Boolean>() {
        var fileChooserParams: FileChooserParams? = null

        override fun createIntent(context: Context, input: Unit): Intent {
            // val getContentIntent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            val chooserIntent = Intent.createChooser(
                fileChooserParams?.createIntent(), fileChooserParams?.title
            ).apply {
                putExtra(
                    Intent.EXTRA_INITIAL_INTENTS, arrayOf(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                )
            }
            return chooserIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            return resultCode == RESULT_OK
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(contract) { }
}
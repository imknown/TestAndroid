package net.imknown.testandroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.io.File

class T15FileActivity : AppCompatActivity(0) {

    companion object {
        const val SUB_DIR = "子目录"
        const val FILENAME = "文件.txt"
        const val CONTENT = "内容"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dealWithFilePermission()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private val manageAllFilesAccessLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        dealWithFilePermissionR()
    }

    private val applicationDetailsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        dealWithFilePermissionQ()
    }

    private val requestFilePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (shouldShowRationale) {
            // The user did not agree, so here you should tell the user why the permission is needed.
            dealWithFilePermissionQ(isGranted)
        } else {
            if (isGranted) {
                dealWithFilePermissionQ(isGranted)
            } else {
                // The user chooses not to ask again, so the only option is to enable it manually.
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:$packageName".toUri()
                }
                applicationDetailsLauncher.launch(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun dealWithFilePermissionR() {
        if (Environment.isExternalStorageManager()) {
            createFile()
        } else {
            manageAllFilesAccessLauncher.launch(
                Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            )
        }
    }

    private fun dealWithFilePermissionQ() {
        val permissionResult =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        dealWithFilePermissionQ(permissionResult == PackageManager.PERMISSION_GRANTED)
    }

    private fun dealWithFilePermissionQ(isGranted: Boolean) {
        if (isGranted) {
            createFile()
        } else {
            requestFilePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun dealWithFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dealWithFilePermissionR()
        } else {
            dealWithFilePermissionQ()
        }
    }

    @Suppress("Deprecation")
    private fun createFile() {
//        val rootDirPath = getExternalFilesDirs(Environment.MEDIA_MOUNTED)[0]
        val rootDirPath = Environment.getExternalStorageDirectory()
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(this, "$rootDirPath\ndoes not exist and cannot be created.", Toast.LENGTH_LONG).show()
            return
        }

        val parentDirPath = "$rootDirPath${File.separator}$SUB_DIR"
        val patentDir = File(parentDirPath)
        if (!patentDir.exists()) {
            if (!patentDir.mkdirs()) {
                Toast.makeText(this, "$patentDir\nCreate failed.", Toast.LENGTH_LONG).show()
                return
            }
        }

        val file = File(parentDirPath, FILENAME).apply {
            writeText(CONTENT)
        }
        Toast.makeText(this, "$file\nCreated successfully, the content is: ${file.readText()}", Toast.LENGTH_LONG).show()
    }
}
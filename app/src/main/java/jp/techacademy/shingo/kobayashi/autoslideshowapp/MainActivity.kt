package jp.techacademy.shingo.kobayashi.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import java.util.*

class MainActivity : AppCompatActivity() {


    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null

    //タイマー用の時間のための変数

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                } else {
                    Log.d("ANDROID", "許可されなかった")
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }
        }
        //

        play_and_pause_button.setOnClickListener {

            if (mTimer == null) {
                play_and_pause_button.text = "停止"
                back_button.isEnabled = false
                next_button.isEnabled = false

                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            if (cursor != null) {
                                if (!cursor.moveToNext()) {
                                    cursor.moveToFirst()
                                }
                            }
                            // indexからIDを取得し、そのIDから画像のURIを取得する
                            val fieldIndex = cursor?.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor?.getLong(fieldIndex!!)
                            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id!!)

                            imageView.setImageURI(imageUri)

                        }
                    }
                }, 100, 2000)
            } else {
                play_and_pause_button.text = "再生"
                back_button.isEnabled = true
                next_button.isEnabled = true
                if (mTimer != null) {
                    mTimer!!.cancel()
                    mTimer = null
                }
            }
        }


        back_button.setOnClickListener {
            if (cursor != null) {
                if (!cursor.moveToPrevious()) {
                    cursor.moveToLast()
                }
            }
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor?.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor?.getLong(fieldIndex!!)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id!!)

            imageView.setImageURI(imageUri)
        }


        next_button.setOnClickListener {
            if (cursor != null) {
                if (!cursor.moveToNext()) {
                    cursor.moveToFirst()
                }
            }
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor?.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor?.getLong(fieldIndex!!)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id!!)

            imageView.setImageURI(imageUri)
        }
    }
}

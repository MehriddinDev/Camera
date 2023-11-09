package uz.gita.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import com.google.common.util.concurrent.ListenableFuture
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var processFuture: ListenableFuture<ProcessCameraProvider>
    lateinit var imageCapture: ImageCapture
    lateinit var mediaStoreOutPut: MediaStoreOutputOptions

  /*  val selectorVideo = QualitySelector.from(
        Quality.HD, FallbackStrategy.higherQualityOrLowerThan(
            Quality.SD
        )
    )

    val recorder = Recorder.Builder()
        .setQualitySelector(selectorVideo)
        .build()

    val videoCapture by lazy { VideoCapture.withOutput(recorder) }
*/


    lateinit var savedImage: ImageView
    lateinit var savedImageContainer: CardView
    private lateinit var bitmap: Bitmap

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cameraView: PreviewView = findViewById(R.id.cameraPreview)
        val takePhoto = findViewById<Button>(R.id.btnTake)
        val takeVieo = findViewById<Button>(R.id.btnTakeVideo)

        savedImage = findViewById(R.id.savedImage)
        savedImageContainer = findViewById(R.id.savedImageContainer)
        findViewById<ImageButton>(R.id.capture).setOnClickListener {
            val i = Intent(this,VideoActivity::class.java)
            startActivity(i)
        }

        savedImageContainer.setOnClickListener {

            val i = Intent(this, ShowImagScreen::class.java)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)

            val byteArray: ByteArray = stream.toByteArray()
            i.putExtra("bitmap", byteArray)
            startActivity(i)
        }

        takePhoto.setOnClickListener {
            takePhoto()
        }


        processFuture = ProcessCameraProvider.getInstance(this)
        processFuture.addListener({
            val provider = processFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraView.surfaceProvider)
                }

            val selector = CameraSelector.DEFAULT_BACK_CAMERA


            val name = "${System.currentTimeMillis()}.mp4"
            val contentValue = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, name)
            }
            mediaStoreOutPut = MediaStoreOutputOptions.Builder(
                this.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            )
                .setContentValues(contentValue)
                .build()




            imageCapture = ImageCapture.Builder()
                .build()

            try {
                provider.unbindAll()
                provider.bindToLifecycle(this, selector, preview,imageCapture)
            } catch (e: Exception) {
                e.stackTrace
            }

        }, ContextCompat.getMainExecutor(this))

        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.RECORD_AUDIO)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

            }).check()
    }

    val recorderListener = Consumer<VideoRecordEvent>{event->
       when(event){
           is VideoRecordEvent.Start->{

           }
           is VideoRecordEvent.Finalize->{}
       }
    }
/*
    private fun takeVideos() {
        val recording = if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            videoCapture.output
                .prepareRecording(this,mediaStoreOutPut)
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(this),recorderListener)
            return
        } else {

        }

    }*/

    private fun takePhoto() {
        val imageOutFile = ImageCapture.OutputFileOptions.Builder(
            File(
                filesDir,
                "${System.currentTimeMillis()}.jpg"
            )
        ).build()
        imageCapture.takePicture(imageOutFile, ContextCompat.getMainExecutor(this),
            object : OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri
                    Log.d("TTT", "$savedUri")
                    val savedFile = File(savedUri?.path)

                    bitmap = BitmapFactory.decodeFile(savedFile.absolutePath)
                    savedImageContainer.visibility = View.VISIBLE
                    savedImage.setImageBitmap(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d("TTT", "onError: xato")
                }
            })
    }


}
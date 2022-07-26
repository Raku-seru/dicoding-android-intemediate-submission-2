package com.rakuseru.storyapp1.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.rakuseru.storyapp1.R
import com.rakuseru.storyapp1.databinding.ActivityAddBinding
import com.rakuseru.storyapp1.helper.Utils.createTempFile
import com.rakuseru.storyapp1.helper.Utils.reduceFileImage
import com.rakuseru.storyapp1.helper.Utils.rotateBitmap
import com.rakuseru.storyapp1.helper.Utils.uriToFile
import com.rakuseru.storyapp1.ui.viewmodel.AddViewModel
import com.rakuseru.storyapp1.ui.viewmodel.ViewModelFactory
import com.rakuseru.storyapp1.utils.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding

    private val addViewModel: AddViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var token: String
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var getFile: File? = null
    private var anyPhoto = false
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Init functions
        setObservers()
        setListeners()
    }

    // Check All needed permission
    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Observer and Listeners
    private fun setObservers() {
        addViewModel.fetchUser().observe(this) { userToken ->
            if (userToken != "") {
                token = userToken
            }
        }
        addViewModel.uploadResponse.observe(this) { response ->
            when(response) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> response.data.let {
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                    finish() // Login activity ditutup disini
                }
                is Result.Error -> response.data.let {
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setListeners() {
        // Click imageView to add file
        binding.ivAddFile.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
            findFile()
        }

        // Finish add file
        binding.btnUpload.setOnClickListener {
            uploadImage()
        }

        // Locations
        binding.swLocationShare.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                getCurrentLocation()
            } else {
                this.location = null
            }
        }
    }

    // get Location
    private fun getCurrentLocation(){
        if (checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ){
            fusedLocationClient.lastLocation.addOnSuccessListener { currentLocation ->
                if (currentLocation != null){
                    this.location = currentLocation
                } else {
                    Toast.makeText(this, getString(R.string.is_location_enable), Toast.LENGTH_SHORT).show()
                    binding.swLocationShare.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getCurrentLocation()
            }
            else -> {
                Snackbar.make(binding.root, getString(R.string.location_denied), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.location_denied_action)) {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    .show()
                binding.swLocationShare.isChecked = false
            }
        }
    }

    // Chooser to File
    private fun findFile() {
        // Chooser items
        val items = arrayOf<CharSequence>(
            getString(R.string.from_gallery),
            getString(R.string.take_picture),
            getString(R.string.cancel)
        )

        // Chooser Title
        val title = TextView(this)
        title.text = getString(R.string.select_photo)
        title.gravity = Gravity.CENTER
        title.setPadding(10, 15, 15, 10)
        title.setTextColor(resources.getColor(R.color.outline_primary, theme))
        title.textSize = 24f

        // Build Picker
        val builder = AlertDialog.Builder(this)
        builder.setCustomTitle(title)
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == getString(R.string.from_gallery) -> {
                    startGallery()
                    dialog.dismiss()
                }
                items[item] == getString(R.string.take_picture) -> {
                    startTakePhoto()
                    dialog.dismiss()
                }
                items[item] == getString(R.string.cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    // Gallery
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_pic))
        launcherIntentGallery.launch(chooser)
    }

    // Camera
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddActivity,
                getString(R.string.package_name),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    // Intent Camera
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                true
            )
            anyPhoto = true
            binding.ivAddFile.setImageBitmap(result)
            binding.etAddDesc.requestFocus()
        }
    }

    // Intent Gallery
    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage, this@AddActivity)
            getFile = myFile

            binding.ivAddFile.setImageURI(selectedImage)
        }
    }

    // Upload file
    private fun uploadImage() {
        val description = binding.etAddDesc.text.toString()

        when {
            // File null validation
            getFile == null -> {
                Toast.makeText(this@AddActivity, getString(R.string.validation_add_file), Toast.LENGTH_SHORT).show()
            }

            // Description null validation
            description.trim().isEmpty() -> {
                Toast.makeText(this@AddActivity, getString(R.string.validation_add_desc), Toast.LENGTH_SHORT).show()
            }

            // If form complete
            else -> {
                val file = reduceFileImage(getFile as File)
                val desc = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                var lat: RequestBody? = null
                var lon: RequestBody? = null

                if (location != null) {
                    lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                    lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                }
                addViewModel.upload(token, imageMultipart, desc, lat, lon)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Tidak mendapatkan permission.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val FILENAME_FORMAT = "yyyyMMdd"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
package com.app.eserkom.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.eserkom.R
import com.app.eserkom.databinding.FragmentFormEntryBinding
import com.app.eserkom.datastore.WargaHelper
import com.app.eserkom.model.Warga
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Suppress("DEPRECATION")
class FormEntryFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var dbHelper: WargaHelper
    private lateinit var binding: FragmentFormEntryBinding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val REQUEST_CODE_PERMISSIONS = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFormEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        dbHelper = WargaHelper(requireContext())
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnCekAlamat.setOnClickListener {
            getLocation()
        }

        binding.editTextTanggal.setOnClickListener {
            dateDialog()
        }

        binding.btnPilihFoto.setOnClickListener{
            showImageSourceDialog()
        }

        binding.btnSubmit.setOnClickListener {
            val nik = binding.editTextNIK.text.toString()
            val nama = binding.editTextNama.text.toString()
            val noHp = binding.editTextTelp.text.toString()
            val jenisKelamin = when (binding.radioGroupKelamin.checkedRadioButtonId) {
                R.id.radioButtonLakiLaki -> "Laki-Laki"
                R.id.radioButtonPerempuan -> "Perempuan"
                else -> "Kelamin Tidak Diketahui"
            }
            val tanggalPendataan = binding.editTextTanggal.text.toString()
            val alamat = binding.editTextAlamat.text.toString()

            if (nik.isNotEmpty() && nama.isNotEmpty() && noHp.isNotEmpty() && jenisKelamin.isNotEmpty() &&
                tanggalPendataan.isNotEmpty() && alamat.isNotEmpty() && getFile != null) {

                val file = reduceFileImage(getFile as File)
                val gambar = file.absolutePath  // Ambil path gambar

                val warga = Warga(nik, nama, noHp, jenisKelamin, tanggalPendataan, alamat, gambar)

                if (dbHelper.checkDataOntable(nik)) {
                    val bundle = Bundle()
                    bundle.putString("nik", nik)
                    Toast.makeText(requireContext(), "Data sudah ada", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_formEntryFragment_to_detailFragment,bundle)
                } else {
                    dbHelper.insertData(warga)
                    Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    binding.apply {
                        editTextNIK.setText("")
                        editTextNama.setText("")
                        editTextTelp.setText("")
                        editTextTanggal.setText("")
                        editTextAlamat.setText("")
                        imageUpload.setImageResource(R.drawable.upload)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Harap Isi Semua Data dan Pilih Gambar!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationTask = fusedLocation.lastLocation

        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addressList: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (addressList!!.isNotEmpty()) {
                    val streetAddress = addressList[0].getAddressLine(0)
                    binding.editTextAlamat.setText(streetAddress)
                } else {
                    Toast.makeText(requireContext(), "Lokasi Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Lokasi Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
            }
        }

        locationTask.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Gagal mengambil lokasi. Pastikan layanan lokasi diaktifkan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dateDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        binding.editTextTanggal.setText(formattedDate)
    }

    private fun showImageSourceDialog() {
        val pilih = arrayOf("Ambil Foto", "Pilih dari Galeri")
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(pilih) { _, menu ->
            when (menu) {
                0 -> {
                    if (allPermissionsGranted()) {
                        startTakePhoto()
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            REQUIRED_PERMISSIONS,
                            REQUEST_CODE_PERMISSIONS
                        )
                    }
                }
                1 -> {
                    startGallery()
                }
            }
        }
        builder.show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar!")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        createCustomTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.app.eserkom",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.imageUpload.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireContext())
                getFile = myFile
                binding.imageUpload.setImageURI(uri)
            }
        }
    }
}

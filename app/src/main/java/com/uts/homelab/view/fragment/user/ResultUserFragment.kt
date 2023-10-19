package com.uts.homelab.view.fragment.user

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentResultUserBinding
import com.uts.homelab.network.dataclass.ResultAppointment
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.adapter.AdapterUserResult
import com.uts.homelab.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

@AndroidEntryPoint
class ResultUserFragment : Fragment() {

    private lateinit var binding: FragmentResultUserBinding
    private val viewModel: UserViewModel by activityViewModels()

    private var information: InformationFragment? = null
    private val informationFragment by lazy { information!! }

    private val progressDialog = ProgressFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val onBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clear()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBack)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.blue_hospital)

        viewModel.getAllAppointmentFinish()

        setObserver()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {
        viewModel.listAppointmentModel.observe(viewLifecycleOwner) {
            if(it==null)return@observe
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAppointment.adapter = AdapterUserResult(it,Rol.USER){
                viewModel.getResultAppointment(it.dc)
            }
        }
        viewModel.progressDialog.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            if (it) {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager, javaClass.simpleName)
            } else {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
            }
        }

        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            information = InformationFragment()

            if (it == Cons.DOWNLOAD_RESULT) {
                informationFragment.getInstance(
                    getString(R.string.correct),
                    it
                )
            } else {
                informationFragment.getInstance(getString(R.string.attention), it)
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment.isVisible) {
                        informationFragment.dismiss()
                    }
                }
            }, 3500)

            if (informationFragment.isVisible) {
                informationFragment.dismiss()
            }

            informationFragment.show(
                requireActivity().supportFragmentManager,
                "InformationFragment"
            )
        }

        viewModel.resultAppointment.observe(viewLifecycleOwner){
            if(it == null )return@observe
            checkWriteExternalStoragePermission()
        }

    }

    private fun saveQRToPDF(resultAppointment: ResultAppointment?): Any {

        // Create the PDF document
        val document = Document()

        val pdfFileName = "${resultAppointment!!.appointmentUserModel.typeOfExam}${resultAppointment.tsResult}.pdf"

        val pdfDir =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            } else {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            }


        if (pdfDir != null && !pdfDir.exists()) {
            pdfDir.mkdirs()
        }

        val pdfFile = File(pdfDir, pdfFileName)

        if(pdfFile.exists()){
            pdfFile.delete()
        }

        val outputStream = FileOutputStream(pdfFile)

        try {
            PdfWriter.getInstance(document, outputStream)
            document.open()



            // Agregar la imagen del código QR al
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_app)

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = Image.getInstance(stream.toByteArray())
            image.alignment = Element.ALIGN_CENTER
            document.add(image)
            // Agregar el título al PDF
            val titleFont = Font(Font.FontFamily.TIMES_ROMAN, 27f, Font.BOLD)
            val titleParagraph = Paragraph("Resultado de la muestra", titleFont)
            titleParagraph.alignment = Element.ALIGN_CENTER
            titleParagraph.spacingBefore = 10f // Espacio después del título
            document.add(titleParagraph)

            val titleDescription = Font(Font.FontFamily.TIMES_ROMAN, 20f, Font.NORMAL)
            val titleParagraphDescription = Paragraph(resultAppointment.description, titleDescription)
            titleParagraphDescription.alignment = Element.ALIGN_LEFT
            titleParagraphDescription.spacingBefore = 10f // Espacio después del título
            document.add(titleParagraphDescription)

            val titleResult = Font(Font.FontFamily.TIMES_ROMAN, 20f, Font.NORMAL)
            val titleParagraphResult = Paragraph(resultAppointment.result, titleResult)
            titleParagraphResult.alignment = Element.ALIGN_LEFT
            titleParagraphResult.spacingBefore = 10f // Espacio después del título
            document.add(titleParagraphResult)

            document.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream.close()
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("Se han creado los resultados en PDF, en la opcion de descargas")
                .setCancelable(true)
            val alert = dialogBuilder.create()
            alert.show()
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri.fromFile(pdfFile)
        } else {
            ""
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with the task
                // Generate and save the PDF (already handled in onCreate)
                val uri = saveQRToPDF(viewModel.resultAppointment.value)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    savePDFToDownloads(uri as Uri)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val uri = saveQRToPDF(viewModel.resultAppointment.value)
                    savePDFToDownloads(uri as Uri)
                } else {
                    showPermissionExplanationDialog()
                }
            }
        }
    private fun showPermissionExplanationDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("The app needs access to storage to save the PDF.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun savePDFToDownloads(pdfUri: Uri) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${viewModel.resultAppointment.value!!.appointmentUserModel.typeOfExam}${viewModel.resultAppointment.value!!.tsResult}.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = requireActivity().contentResolver
        val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI


        val existingUri = resolver.query(contentUri, null, null, null, null)?.use { cursor ->
            val displayNameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val displayName = cursor.getString(displayNameIndex)
                if (displayName == "${viewModel.resultAppointment.value!!.appointmentUserModel.typeOfExam}${viewModel.resultAppointment.value!!.tsResult}.pdf") {
                    val fileId = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID) as Int)
                    return@use Uri.withAppendedPath(contentUri, fileId.toString())
                }
            }
            null
        }

        if (existingUri != null) {
            resolver.delete(existingUri, null, null)
        }

        val pdfFileUri = resolver.insert(contentUri, contentValues)

        if (pdfFileUri != null) {
            val outputStream = resolver.openOutputStream(pdfFileUri)
            val inputStream = requireActivity().contentResolver.openInputStream(pdfUri)
            if (outputStream != null && inputStream != null) {
                inputStream.copyTo(outputStream)
                outputStream.close()
                inputStream.close()
            }
        }
    }

    private fun checkWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request the permission
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }

        val uri = saveQRToPDF(viewModel.resultAppointment.value)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePDFToDownloads(uri as Uri)
        }
    }

    fun clear() {
        viewModel.listAppointmentModel.value = null
        viewModel.progressDialog.value = null
        viewModel.informationFragment.value  = null
        viewModel.resultAppointment.value = null
    }
    @Override
    override fun onDestroy() {
        super.onDestroy()
        clear()
    }

}
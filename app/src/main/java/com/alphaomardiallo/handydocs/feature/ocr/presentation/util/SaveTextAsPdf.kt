package com.alphaomardiallo.handydocs.feature.ocr.presentation.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import java.io.File
import java.io.FileOutputStream

fun saveTextAsPdf(
    context: Context,
    activity: Activity?,
    text: String,
    saveImage: (ImageDoc) -> Unit = { },
    savePageImages: (List<Uri>) -> Unit = { }
) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points
    val imageUris = mutableListOf<Uri>()

    // Create paint objects for different text styles
    val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
    }

    // Page margins and dimensions
    val margins = RectF(40f, 40f, pageInfo.pageWidth - 40f, pageInfo.pageHeight - 40f)
    val lineSpacing = textPaint.fontSpacing * 1.5f

    var pageCount = 0
    var currentPage = pdfDocument.startPage(pageInfo)
    var canvas = currentPage.canvas
    var y = margins.top

    // Split text into paragraphs
    val paragraphs = text.split("\n")

    for (paragraph in paragraphs) {
        // Check if we need a new page
        if (y + lineSpacing > margins.bottom) {
            pdfDocument.finishPage(currentPage)
            currentPage = pdfDocument.startPage(pageInfo)
            canvas = currentPage.canvas
            y = margins.top
            pageCount++
        }

        val words = paragraph.split(" ")
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val measureWidth = textPaint.measureText(testLine)

            if (measureWidth <= margins.width()) {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            } else {
                // Draw current line and start new line with current word
                canvas.drawText(currentLine.toString(), margins.left, y, textPaint)
                y += lineSpacing

                // Check if we need a new page
                if (y + lineSpacing > margins.bottom) {
                    pdfDocument.finishPage(currentPage)
                    currentPage = pdfDocument.startPage(pageInfo)
                    canvas = currentPage.canvas
                    y = margins.top
                    pageCount++
                }

                currentLine = StringBuilder(word)
            }
        }

        // Draw remaining text in the current line
        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine.toString(), margins.left, y, textPaint)
            y += lineSpacing * 1.5f // Add extra space after paragraph
        }
    }

    pdfDocument.finishPage(currentPage)

    try {
        activity?.let {
            val fileName = "HandyDocs${System.currentTimeMillis()}.pdf"
            val pdfFile = File(it.filesDir, fileName)

            // Write PDF to file
            pdfFile.outputStream().use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            // Create PDF renderer for the saved file
            val parcelFileDescriptor =
                ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(parcelFileDescriptor)

            // Convert each page to image
            for (pageIndex in 0 until renderer.pageCount) {
                val page = renderer.openPage(pageIndex)

                // Create bitmap with the same dimensions as the PDF page
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.ARGB_8888
                )

                // Initialize bitmap with white background
                val canvasElement = Canvas(bitmap)
                canvasElement.drawColor(Color.WHITE)

                // Render the page onto the bitmap
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                // Save bitmap as JPEG
                val imageFileName = "HandyDocs_page_${pageIndex}_${System.currentTimeMillis()}.jpg"
                val imageFile = File(it.filesDir, imageFileName)

                FileOutputStream(imageFile).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                }

                // Add the image URI to the list
                imageUris.add(Uri.fromFile(imageFile))

                // Clean up
                bitmap.recycle()
                page.close()
            }

            // Clean up renderer
            renderer.close()
            parcelFileDescriptor.close()

            // Save the PDF document info
            saveImage.invoke(
                ImageDoc(
                    name = fileName,
                    uriPdf = Uri.fromFile(pdfFile),
                    time = System.currentTimeMillis()
                )
            )

            // Save the list of image URIs
            savePageImages(imageUris)

            Toast.makeText(context, getString(context, R.string.util_saved_pdf), Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, getString(context, R.string.util_error_saving_pdf), Toast.LENGTH_SHORT).show()
    }

    pdfDocument.close()
}
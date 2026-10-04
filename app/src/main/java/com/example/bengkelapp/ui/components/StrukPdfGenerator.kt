package com.example.bengkelapp.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.bengkelapp.model.Invoice
import com.example.bengkelapp.model.WorkshopSettings
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

object StrukPdfGenerator {

    fun generateAndSaveReceiptPdf(
        context: Context,
        invoice: Invoice,
        settings: WorkshopSettings
    ): File? {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val pdfDocument = PdfDocument()

        // Page info: Width 300, Height 600 (Thermal receipt style)
        val pageInfo = PdfDocument.PageInfo.Builder(300, 650, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()

        // Background
        canvas.drawColor(Color.WHITE)

        var y = 30f

        // Title Header
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 14f
        titlePaint.color = Color.BLACK
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText(settings.name, 150f, y, titlePaint)

        y += 18f
        paint.textSize = 9f
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.DKGRAY
        canvas.drawText(settings.address, 150f, y, paint)

        y += 14f
        canvas.drawText("Telp: ${settings.phone}", 150f, y, paint)

        y += 20f
        paint.color = Color.LTGRAY
        canvas.drawLine(10f, y, 290f, y, paint)

        // Receipt Details
        y += 20f
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.BLACK
        paint.textSize = 9f
        canvas.drawText("No. Struk : ${invoice.invoiceNumber}", 15f, y, paint)

        y += 14f
        canvas.drawText("Tanggal   : ${invoice.createdAt}", 15f, y, paint)

        y += 14f
        canvas.drawText("Pelanggan : ${invoice.customerName}", 15f, y, paint)

        y += 14f
        canvas.drawText("No. Polisi: ${invoice.vehiclePlate} (${invoice.vehicleModel})", 15f, y, paint)

        y += 14f
        canvas.drawText("Kasir     : ${invoice.cashierName}", 15f, y, paint)

        y += 20f
        paint.color = Color.LTGRAY
        canvas.drawLine(10f, y, 290f, y, paint)

        // Item Headers
        y += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.BLACK
        canvas.drawText("ITEM / DESKRIPSI", 15f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("TOTAL", 285f, y, paint)

        y += 15f
        paint.typeface = Typeface.DEFAULT
        invoice.items.forEach { item ->
            paint.textAlign = Paint.Align.LEFT
            val nameText = if (item.name.length > 22) item.name.take(22) + ".." else item.name
            canvas.drawText("${item.qty}x $nameText", 15f, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(currencyFormat.format(item.total), 285f, y, paint)

            y += 16f
        }

        y += 10f
        paint.color = Color.LTGRAY
        canvas.drawLine(10f, y, 290f, y, paint)

        // Totals
        y += 20f
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Jasa Servis:", 15f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(currencyFormat.format(invoice.servicesCost), 285f, y, paint)

        y += 16f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Suku Cadang:", 15f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(currencyFormat.format(invoice.partsCost), 285f, y, paint)

        y += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("TOTAL BIAYA:", 15f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(currencyFormat.format(invoice.totalAmount), 285f, y, paint)

        y += 16f
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Metode Bayar: ${invoice.paymentMethod.displayName}", 15f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("STATUS: LUNAS", 285f, y, paint)

        // Footer
        y += 30f
        paint.color = Color.LTGRAY
        canvas.drawLine(10f, y, 290f, y, paint)

        y += 20f
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.DKGRAY
        canvas.drawText("Terima kasih atas kunjungan Anda!", 150f, y, paint)

        y += 14f
        canvas.drawText("Garansi servis 7 hari (S&K berlaku)", 150f, y, paint)

        pdfDocument.finishPage(page)

        // Save PDF to downloads/documents
        return try {
            val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            val file = File(fileDir, "Struk_${invoice.invoiceNumber}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            Toast.makeText(context, "Struk disimpan ke: ${file.name}", Toast.LENGTH_LONG).show()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            Toast.makeText(context, "Gagal membuat PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }
}

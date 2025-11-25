package com.example.blockfile.core.data.repository

import android.content.Context
import android.os.Environment
import com.example.blockfile.core.data.network.BlockFileApi
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

interface ProductFileRepository {
    /**
     * Descarga el archivo del producto y lo guarda en la carpeta
     * de descargas de la app. Devuelve el File resultante.
     */
    suspend fun downloadProduct(productId: Long): File
}

class ProductFileRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
    @ApplicationContext private val context: Context,
) : ProductFileRepository {

    override suspend fun downloadProduct(productId: Long): File {
        try {
            val response = api.downloadProduct(productId)
            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            val body = response.body() ?: throw Exception("Respuesta vacía del servidor.")
            val disposition = response.headers()["Content-Disposition"]
            val fileName = extractFileName(disposition, fallback = "producto_$productId.bin")

            // Carpeta de descargas de la APP (no la pública global)
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir

            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val outFile = File(downloadsDir, fileName)

            saveResponseBodyToFile(body, outFile)

            return outFile
        } catch (e: HttpException) {
            throw Exception("Error del servidor (${e.code()}).")
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    private fun extractFileName(disposition: String?, fallback: String): String {
        if (disposition == null) return fallback
        // Ejemplo: Content-Disposition: attachment; filename="nombre.ext"
        val filenameKey = "filename="
        val idx = disposition.indexOf(filenameKey)
        if (idx == -1) return fallback
        var name = disposition.substring(idx + filenameKey.length).trim()
        if (name.startsWith("\"") && name.endsWith("\"") && name.length >= 2) {
            name = name.substring(1, name.length - 1)
        }
        if (name.isBlank()) return fallback
        return name
    }

    private fun saveResponseBodyToFile(body: ResponseBody, file: File) {
        body.byteStream().use { input ->
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
    }
}

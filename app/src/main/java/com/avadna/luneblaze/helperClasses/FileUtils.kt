package com.avadna.luneblaze.helperClasses

import android.app.Activity
import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import java.io.*
import java.util.*
import kotlin.math.roundToInt

object FileUtils {
    private const val MINIMUM_FREE_SPACE = 250 * 1024 * 1024    // 250 MB

    const val MIME_TYPE_ALL = "*/*"
    const val MIME_TYPE_IMAGE = "image/*"
    const val MIME_TYPE_JPEG = "image/jpeg"
    const val MIME_TYPE_JPG = "image/jpg"
    const val MIME_TYPE_PNG = "image/png"
    const val MIME_TYPE_VIDEO = "video/*"
    const val MIME_TYPE_PDF = "application/pdf"
    const val MIME_TYPE_DOC = "application/msword"
    const val MIME_TYPE_TEXT = "text/plain"

    const val DIR_DOCUMENTS = "documents"

    /**
     * https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
     * */
    @JvmStatic
    fun getPath(context: Context, uri: Uri): String? {
        when {
            // DocumentProvider
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    // ExternalStorageProvider
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":")
                        if (split.size < 2) return null

                        val type = split[0]
                        if ("primary".equals(type, true)) {
                            return "${Environment.getExternalStorageDirectory()}/${split[1]}"
                        }
                    }

                    // DownloadsProvider
                    isDownloadsDocument(uri) -> {
                        val id = DocumentsContract.getDocumentId(uri)

                        if (id != null && id.startsWith("raw:")) {
                            return id.substring(4)
                        }

                        val contentUriPrefixesToTry = arrayOf(
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        )

                        contentUriPrefixesToTry.forEach { contentUriPrefix ->
                            val contentUri =
                                    ContentUris.withAppendedId(Uri.parse(contentUriPrefix), id.toLong())
                            try {
                                val path = getDataColumn(context, contentUri, null, null)
                                if (path != null) {
                                    return path
                                }
                            } catch (exception: Exception) {
                               // Timber.w(exception)
                            }
                        }

                        // Path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                        val fileName = getFileName(context, uri)
                        val cacheDir = getDocumentCacheDirectory(context)
                        val file = generateFileName(fileName, cacheDir)
                        var destinationPath: String? = null
                        if (file != null) {
                            destinationPath = file.absolutePath
                            saveFileFromUri(context, uri, destinationPath)
                        }

                        return destinationPath
                    }

                    // MediaProvider
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":")
                        if (split.size < 2) return null

                        val contentUri = when (split.firstOrNull()) {
                            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                            else -> return null
                        }

                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        return getDataColumn(context, contentUri, selection, selectionArgs)
                    }

                    else -> return null
                }
            }

            // MediaStore and general
            uri.scheme == "content" -> {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) {
                    uri.lastPathSegment
                } else {
                    getDataColumn(context, uri, null, null)
                }
            }

            uri.scheme == "file" -> return uri.path

            else -> return null
        }

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(
            context: Context,
            uri: Uri,
            selection: String?,
            selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } catch (exception: Exception) {
           // Timber.w(exception)
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    @JvmStatic
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun getMimeType(file: File): String? {
        val extension = getExtension(file.name) ?: return null

        return if (extension.isNotEmpty())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1))
        else
            "application/octet-stream"
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    private fun getExtension(uri: String?): String? {
        if (uri == null) {
            return null
        }

        val dot = uri.lastIndexOf(".")
        return if (dot >= 0) {
            uri.substring(dot)
        } else {
            // No extension.
            ""
        }
    }

    fun openPdfFile(context: Context, file: File) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), MIME_TYPE_PDF)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            context.startActivity(intent)
        } catch (exception: Exception) {
           // Timber.w(exception)
        }
    }

    fun openPdfUrl(context: Context, pdfUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            context.startActivity(intent)
        } catch (exception: Exception) {
           // Timber.w(exception)
        }
    }

    fun downloadFile(context: Context, fileUrl: String, fileName: String) {
        val downloadManager =
                context.applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(fileUrl))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
    }

    fun showFilePicker(
            fragment: Fragment,
            requestCode: Int,
            mimeTypes: Array<String> = arrayOf(MIME_TYPE_ALL)
    ) {
        try {
            fragment.startActivityForResult(getDocumentPickerIntent(mimeTypes), requestCode)
        } catch (exception: Exception) {
          //  Timber.d(exception)
        }
    }

    fun showFilePicker(
            activity: Activity,
            requestCode: Int,
            mimeTypes: Array<String> = arrayOf(MIME_TYPE_ALL)
    ) {
        try {
            activity.startActivityForResult(getDocumentPickerIntent(mimeTypes), requestCode)
        } catch (exception: Exception) {
           // Timber.d(exception)
        }
    }

    private fun getDocumentPickerIntent(mimeTypes: Array<String>): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = MIME_TYPE_ALL
        //intent.type = "application/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        return intent
    }

    @JvmStatic
    fun getFileSizeKiloBytes(file: File): Int {
        return file.length().toDouble().div(1024).roundToInt()
    }

    @JvmStatic
    fun getSelectedFileFromResult(context: Context, data: Intent?): File? {
        return if (data != null) {
            val fileUri = data.data ?: return null
            val filePath = getPath(context, fileUri) ?: return null
            return File(filePath)
        } else {
            //Timber.d("Result data is null")
            null
        }
    }

    @JvmStatic
    fun getAppCacheDirectory(context: Context): File {
        return context.externalCacheDir ?: context.cacheDir
    }

    @JvmStatic
    fun getAppCacheDirectoryPath(context: Context): String {
        return getAppCacheDirectory(context).absolutePath
    }

    @JvmStatic
    fun isLowStorage(context: Context): Boolean {
        // todo
        return getAppCacheDirectory(context).usableSpace < MINIMUM_FREE_SPACE
    }

    fun getFilePath(context: Context, uri: Uri?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(
                MediaStore.MediaColumns.DISPLAY_NAME
        )
        try {
            cursor = context.contentResolver.query(
                    uri!!, projection, null, null,
                    null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null) {
            val path = getAbsolutePathOrUriPath(context, uri)
            filename = if (path == null) {
                getName(uri.toString())
            } else {
                val file = File(path)
                file.name
            }
        } else {
            val returnCursor = context.contentResolver.query(
                    uri, null,
                    null, null, null
            )
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    fun getAbsolutePathOrUriPath(context: Context, uri: Uri): String? {
        val absolutePath = getPath(context, uri)
        return absolutePath ?: uri.toString()
    }

    fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    fun getDocumentCacheDirectory(context: Context): File {
        val directory = File(getAppCacheDirectory(context), DIR_DOCUMENTS)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    fun generateFileName(inputName: String?, directory: File): File? {
        var name = inputName ?: return null
        var file = File(directory, name)
        if (file.exists()) {
            var fileName = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                name = "$fileName($index)$extension"
                file = File(directory, name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
         //   Timber.w(e)
            return null
        }
        return file
    }

    fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String) {
        var inputStream: InputStream? = null
        var bufferedOutputStream: BufferedOutputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri)
            bufferedOutputStream = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val byteArray = ByteArray(1024)
            inputStream?.read(byteArray)
            do {
                bufferedOutputStream.write(byteArray)
            } while (inputStream?.read(byteArray) != -1)
        } catch (exception: IOException) {
          //  Timber.w(exception)
        } finally {
            try {
                inputStream?.close()
                bufferedOutputStream?.close()
            } catch (exception: IOException) {
             //   Timber.w(exception)
            }
        }
    }

    fun saveImageToExternalStoragePic(finalBitmap: Bitmap?): File? {
        val file: File
        val root =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString()
        val myDir = File("$root/Gigipo")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            return file
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file
    }

}
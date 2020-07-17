package com.af.canty;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class Common {
    public static final Object IMAGE_DOWNLOADABLE_URL ="DOWNLOADABLE_URL" ;
    public static final String SERVICES_ADDED ="SERVICE" ;


    public static String getFileName(ContentResolver contentResolver, Uri fileUri) {
        String result = null;
        if (fileUri.getScheme().equals("content"))
        {
            Cursor cursor = contentResolver.query(fileUri,null,null,null,null);
            try{
                if (cursor !=null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }finally {
                cursor.close();
            }
            if (result == null)
            {
                result = fileUri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1)
                    result = result.substring(cut+1);

            }
            return result;

        }
        return null;
    }
}

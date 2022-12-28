package com.mordechay.yemotapp.data;

import androidx.annotation.NonNull;

import com.mordechay.yemotapp.R;

public class filter {
    public static int getImageResources(@NonNull String ext){
        switch (ext.toLowerCase()) {
            case "mp3": //or
            case "wav": //or
            case "aiff": //or
            case "ogg": //or
            case "flac": //or
            case "m4a": //or
            case "wma": //or
            case "amr": //or
                return R.drawable.ic_baseline_audio_file_24;


            case "txt": //or
            case "rtf": //or
            return R.drawable.ic_baseline_text_snippet_24;

            default:
                // Perform action for unrecognized file format
        }
        return R.drawable.ic_baseline_question_mark_24;
    }

    public static String getTypes(String ext){
        switch (ext.toLowerCase()) {
            case "mp3": //or
            case "wav": //or
            case "aiff": //or
            case "ogg": //or
            case "flac": //or
            case "m4a": //or
            case "wma": //or
            case "amr": //or
                return "Audio";

            case "txt": //or
            case "rtf": //or
            return "Text";

            default:
                // Perform action for unrecognized file format
        }
        return "*/*";
    }
}

package com.example.myapp.base.widget.means

import androidx.annotation.IntDef

@IntDef(
    FileType.TYPE_PDF,
    FileType.TYPE_IMAGE,
    FileType.TYPE_WORD,
    FileType.TYPE_EXCEL,
    FileType.TYPE_PPT,
    FileType.TYPE_TXT,
    FileType.TYPE_ZIP,
    FileType.TYPE_VIDEO,
    FileType.TYPE_OTHER,
)
@Retention(AnnotationRetention.SOURCE)
annotation class FileType {
    companion object {
        /**
         * 视频打开的格式
         * mp4_mov_m4v
         * 音频打开的格式
         * mp3_wav_aac_m4a_
         * 图片打开的格式
         * jpg_png_jpeg
         * wps打开的格式
         * xltx_xlt_pptm_docm_dotx_dot_docx_pdf_doc_rar_zip_txt_pptx_xls_pps_ppt_wps_xlsx_xlsm_ppsx_wpt_pot_potx_csv_rtf_et_ett
         */
        // pdf
        const val TYPE_PDF = 1

        // jpg_png_jpeg
        const val TYPE_IMAGE = 2

        // docx_doc_wps_docm
        const val TYPE_WORD = 3

        // xls_xlsx_xlsm
        const val TYPE_EXCEL = 4

        // pptm_pptx_ppt
        const val TYPE_PPT = 5

        // txt
        const val TYPE_TXT = 6

        // rar_zip
        const val TYPE_ZIP = 7

        // mp4_mov_m4v
        const val TYPE_VIDEO = 8

        // 其他
        const val TYPE_OTHER = 0
    }
}
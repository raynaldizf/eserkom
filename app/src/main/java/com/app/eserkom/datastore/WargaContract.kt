package com.app.eserkom.datastore

import android.provider.BaseColumns

object WargaContract {
    class WargaEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "warga"
            const val COLUMN_NIK = "nik"
            const val COLUMN_NAMA = "nama"
            const val COLUMN_NO_HP = "nomor_handphone"
            const val COLUMN_JENIS_KELAMIN = "jenis_kelamin"
            const val COLUMN_TANGGAL = "tanggal_pendataan"
            const val COLUMN_ALAMAT = "alamat"
            const val COLUMN_GAMBAR = "gambar"
        }
    }
}
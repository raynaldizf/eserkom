package com.app.eserkom.datastore

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.app.eserkom.model.Warga

class WargaHelper (context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "pemilu.db"
        private const val DATABASE_VERSION = 1

        private val SQL_CREATE_ENTRIES =
            " CREATE TABLE ${WargaContract.WargaEntry.TABLE_NAME} (" +
                    "${WargaContract.WargaEntry.COLUMN_NIK} VARCHAR PRIMARY KEY," +
                    "${WargaContract.WargaEntry.COLUMN_NAMA} VARCHAR(255)," +
                    "${WargaContract.WargaEntry.COLUMN_NO_HP} VARCHAR(255)," +
                    "${WargaContract.WargaEntry.COLUMN_JENIS_KELAMIN} VARCHAR(255)," +
                    "${WargaContract.WargaEntry.COLUMN_TANGGAL} VARCHAR(255)," +
                    "${WargaContract.WargaEntry.COLUMN_ALAMAT} VARCHAR(255)," +
                    "${WargaContract.WargaEntry.COLUMN_GAMBAR} BLOB )"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${WargaContract.WargaEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
    }

    fun insertData(warga : Warga){
        val db = writableDatabase
        val sql = " INSERT INTO ${WargaContract.WargaEntry.TABLE_NAME} " +
                " (${WargaContract.WargaEntry.COLUMN_NIK}, " +
                " ${WargaContract.WargaEntry.COLUMN_NAMA}, " +
                " ${WargaContract.WargaEntry.COLUMN_NO_HP}, " +
                " ${WargaContract.WargaEntry.COLUMN_JENIS_KELAMIN}, " +
                " ${WargaContract.WargaEntry.COLUMN_TANGGAL}, " +
                " ${WargaContract.WargaEntry.COLUMN_ALAMAT}, " +
                " ${WargaContract.WargaEntry.COLUMN_GAMBAR}) " +
                " VALUES ('${warga.nik}', " +
                " '${warga.nama}', " +
                " '${warga.nomorHandphone}', " +
                " '${warga.jenisKelamin}', " +
                " '${warga.tanggalPendataan}', " +
                " '${warga.alamat}', " +
                " '${warga.gambar}')"
        db.execSQL(sql)
        db.close()
    }

    fun getWarga() : ArrayList<Warga>{
        val listWarga = ArrayList<Warga>()
        val db = readableDatabase
        val sql = " SELECT * FROM ${WargaContract.WargaEntry.TABLE_NAME}"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToFirst()){
            do {
                val nik = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_NIK))
                val nama = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_NAMA))
                val nomorHandphone = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_NO_HP))
                val jenisKelamin = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_JENIS_KELAMIN))
                val tanggalPendataan = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_TANGGAL))
                val alamat = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_ALAMAT))
                val gambar = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_GAMBAR))
                listWarga.add(Warga(nik, nama, nomorHandphone, jenisKelamin, tanggalPendataan, alamat, gambar))
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listWarga
    }

    fun getWargaByNik(nik: String): Warga? {
        val db = readableDatabase
        val sql = "SELECT * FROM ${WargaContract.WargaEntry.TABLE_NAME} WHERE ${WargaContract.WargaEntry.COLUMN_NIK} = '$nik'"
        val cursor = db.rawQuery(sql, null)
        var warga: Warga? = null

        if (cursor.moveToFirst()) {
            val nama = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_NAMA))
            val nomorHandphone = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_NO_HP))
            val jenisKelamin = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_JENIS_KELAMIN))
            val tanggalPendataan = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_TANGGAL))
            val alamat = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_ALAMAT))
            val gambar = cursor.getString(cursor.getColumnIndexOrThrow(WargaContract.WargaEntry.COLUMN_GAMBAR))
            warga = Warga(nik, nama, nomorHandphone, jenisKelamin, tanggalPendataan, alamat, gambar)
        }

        cursor.close()
        db.close()
        return warga
    }


    fun checkDataOntable(nik : String) : Boolean{
        val db = readableDatabase
        val sql = "SELECT * FROM ${WargaContract.WargaEntry.TABLE_NAME} WHERE ${WargaContract.WargaEntry.COLUMN_NIK} = '$nik'"
        val cursor = db.rawQuery(sql, null)
        var check = false
        if (cursor.moveToFirst()){
            check = true
        }
        cursor.close()
        db.close()
        return check
    }
}
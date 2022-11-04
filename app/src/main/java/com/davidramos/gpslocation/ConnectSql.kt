package com.davidramos.gpslocation

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnectSql {
    private val ip = "187.132.76.89:1434"
    private val db = "DFS"
    private val username = "mssql_user_querys"
    private val password = "9DMexUNhj9xD9ERsJ42Q449UXRBqZ3"

    fun dbConn(): Connection? {
    val policy =StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn : Connection? = null
        val connString : String
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
            connString = "jdbc:jtds:sqlserver://$ip;databaseName=$db;user=$username;password=$password"
            conn = DriverManager.getConnection(connString)
        }catch (ex: SQLException){
            Log.e("Error: ",ex.message!!)
        }catch (ex1: ClassNotFoundException){
            Log.e("Error: ",ex1.message!!)
        }catch (ex2: ClassNotFoundException){
            Log.e("Error: ",ex2.message!!)
        }
        return conn

    }


}
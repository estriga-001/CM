package org.example.app.app
import org.example.annotations.Extract

abstract class DataProcessor(val input: String) {
    @Extract(regex = "Name: (\\w+)")
    abstract fun getName(): String ?

    @Extract(regex = "Address: (.+)")
    abstract fun getAddress(): String ?
}
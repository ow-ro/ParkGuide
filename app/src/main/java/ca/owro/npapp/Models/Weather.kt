package ca.owro.npapp.Models

data class Weather (
        val id: Int,
        val main: String,
        val description: String,
        val icon: String,
        var unixDate: Long = 0,
        var farenheit: String = ""
)
package ca.owro.npapp.Models

data class Trip (
        val documentId: String,
        val name: String,
        val parks: String,
        val description: String,
        val driveTime: String,
        val length: String,
        val months: String,
        val start: String,
        val gradient: Int,
        val parkCount: Int,
        val bannerImage: String = "",
        val legs: ArrayList<Leg> = arrayListOf()
)
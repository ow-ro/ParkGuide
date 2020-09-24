package ca.owro.npapp.Models

data class Park
(val documentId: String,
 val name: String,
 val description: String,
 val listImage: String,
 val bannerImage: String = "",
 val directions: ArrayList<String> = arrayListOf(),
 val location: String = "",
 val address: String = "",
 val activities: ArrayList<String> = arrayListOf(),
 val terrain: ArrayList<String> = arrayListOf(),
 val bikeFriendly: Int = 0, // 1 to 3
 val petFriendly: Int = 0, // 1 to 3
 val cost: Int = 0, // 1 to 3
 val tripLength: String = "",
 val whenToVisit: String = "",
 val popularity: Int = 0, // 1 to 5
 val funFact: String = "",
 val highlights: ArrayList<Highlight> = arrayListOf(),
 val hiddenGems: ArrayList<HiddenGem> = arrayListOf(),
 val latitude: Double = 0.0,
 val longitude: Double = 0.0,
 val links: HashMap<String,String> = hashMapOf(),
 val car: Boolean = true,
 val preserve: Boolean = false,
 val code: String = "")
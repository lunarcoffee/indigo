package dev.lunarcoffee.indigo.bot.commands.service.xkcd

import com.google.gson.annotations.SerializedName

class XkcdComic(
    @SerializedName("num") val number: Int,
    @SerializedName("safe_title") val title: String,
    @SerializedName("img") val imageUrl: String,
    val day: String,
    val month: String,
    val year: String,
    val alt: String
) {
    companion object {
        val COMIC_404 = XkcdComic(
            404,
            "Not Found",
            "https://www.explainxkcd.com/wiki/images/9/92/not_found.png",
            "1",
            "4",
            "2008",
            "(none)"
        )
    }
}

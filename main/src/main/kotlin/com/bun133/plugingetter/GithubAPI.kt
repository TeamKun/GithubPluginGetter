package com.bun133.plugingetter

import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class GithubAPI {
    var gson: Gson = Gson()
    var client: OkHttpClient = OkHttpClient()

    fun getReleases(githubLink: String): GithubReleasesResponse {
        val req: Request = Request.Builder().url(getReleasesLink(githubLink)).build()
        val res: Response = client.newCall(req).execute()
        return gson.fromJson(res.body().string(), GithubReleasesResponse::class.java)
    }

    /**
     * @param githubLink like "https://github.com/TeamKun/Artillery"
     */
    fun getReleasesLink(githubLink: String): String {
        var link: String = ""
        if (githubLink.indexOf("github.com") == -1) {
            throw IllegalArgumentException("GithubLink is not github repository link!")
        } else {
            link += githubLink.substring(githubLink.indexOf("github.com/") + 11)
        }
        return "https://api.github.com/repos/$link/releases"
    }

    /**
     * Maybe work
     */
    fun downloadRelease(asset: GithubAsset, path: File) {
        val req: Request = Request.Builder()
                .url(asset.browser_download_url)
                .header("Accept", "application/octet-stream").build()

        val res: Response = client.newCall(req).execute()
        val input: InputStream = res.body().byteStream()

        val bufferedIn: BufferedInputStream = BufferedInputStream(input)
        val out: FileOutputStream = FileOutputStream(path)

        val bytes: ByteArray = ByteArray(1024)
        var count: Int = 0
        var total: Long = 0
        while (true) {
            count = bufferedIn.read(bytes)
            if (count == -1) break
            total += count
            out.write(bytes, 0, count)
        }

        out.flush()
        out.close()
        bufferedIn.close()
        input.close()
    }
}

data class GithubReleasesResponse(
        var releases: Array<GithubRelease>
)

data class GithubRelease(
        var url: String,
        var assets_url: String,
        var id: Long,
        var tag_name: String,
        var name: String,
        var draft: Boolean,
        var author: GithubAuthor,
        var assets: Array<GithubAsset>
)

data class GithubAsset(
        var url: String,
        var name: String,
        var browser_download_url: String
)


data class GithubAuthor(
        var login: String,
        var id: Long,
        var url: String,
        var html_url: String
)
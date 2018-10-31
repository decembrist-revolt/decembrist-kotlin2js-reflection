package org.decembrist.browser

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage

class Browser(private val port: Int) {

    /**
     * get http://0.0.0.0:port/ page body text
     */
    fun browse(): String {
        val webClient = WebClient()
        val page: HtmlPage = webClient.getPage("http://0.0.0.0:$port/")
        return page.body.asText()
    }

}
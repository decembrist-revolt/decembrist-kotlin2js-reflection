package org.decembrist.browser

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage

class Browser {

    /**
     * get http://localhost:8080/ page body text
     */
    fun browse(): String {
        val webClient = WebClient()
        val page: HtmlPage = webClient.getPage("http://0.0.0.0:8080/")
        return page.body.asText()
    }

}
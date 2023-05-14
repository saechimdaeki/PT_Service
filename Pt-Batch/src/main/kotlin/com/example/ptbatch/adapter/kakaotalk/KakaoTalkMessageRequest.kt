package com.example.ptbatch.adapter.kakaotalk

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*


class KakaoTalkMessageRequest(uuid: String, text: String) {
    @JsonProperty("template_object")
    private val templateObject: TemplateObject

    @JsonProperty("receiver_uuids")
    private val receiverUuids: List<String>


    class TemplateObject {
        @JsonProperty("object_type")
        var objectType: String? = null
        var text: String? = null
        var link: Link? = null

        class Link {
            @JsonProperty("web_url")
            private val webUrl: String? = null
        }
    }

    init {
        val receiverUuids: List<String> = listOf(uuid)
        val link = TemplateObject.Link()
        val templateObject = TemplateObject()
        templateObject.objectType = "text"
        templateObject.text = text
        templateObject.link = link
        this.receiverUuids = receiverUuids
        this.templateObject = templateObject
    }
}
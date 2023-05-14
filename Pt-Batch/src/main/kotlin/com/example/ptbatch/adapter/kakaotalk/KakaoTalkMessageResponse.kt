package com.example.ptbatch.adapter.kakaotalk

import com.fasterxml.jackson.annotation.JsonProperty


class KakaoTalkMessageResponse(
        @JsonProperty("successful_receiver_uuids")
        val successfulReceiverUuids: List<String>? = null) {
}
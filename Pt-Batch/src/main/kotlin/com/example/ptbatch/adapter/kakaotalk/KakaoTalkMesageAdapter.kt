package com.example.ptbatch.adapter.kakaotalk

import com.example.ptbatch.config.KakaoTalkMessageConfig
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient


@Service
class KakaoTalkMessageAdapter(config: KakaoTalkMessageConfig) {
    private val webClient: WebClient

    init {
        webClient = WebClient.builder()
                .baseUrl(config.host)
                .defaultHeaders { h ->
                    h.setBearerAuth(config.token)
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
                }.build()
    }

    fun sendKakaoTalkMessage(uuid: String?, text: String?): Boolean {
        val response: KakaoTalkMessageResponse? = webClient.post().uri("/v1/api/talk/friends/message/default/send")
                .body(BodyInserters.fromValue(KakaoTalkMessageRequest(uuid!!, text!!)))
                .retrieve()
                .bodyToMono(KakaoTalkMessageResponse::class.java)
                .block()
        return if (response?.successfulReceiverUuids == null) {
            false
        } else response.successfulReceiverUuids.isNotEmpty()
    }
}
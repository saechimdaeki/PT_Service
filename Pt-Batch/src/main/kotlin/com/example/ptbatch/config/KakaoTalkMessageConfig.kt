package com.example.ptbatch.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "kakaotalk")
class KakaoTalkMessageConfig(
        var host: String = "",
        var token: String = ""
) {

}
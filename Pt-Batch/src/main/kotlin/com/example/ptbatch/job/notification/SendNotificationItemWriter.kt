package com.example.ptbatch.job.notification

import com.example.ptbatch.adapter.kakaotalk.KakaoTalkMessageAdapter
import com.example.ptbatch.domain.notification.NotificationEntity
import com.example.ptbatch.domain.notification.NotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class SendNotificationItemWriter(
        val notificationRepository: NotificationRepository,
        val kakaoTalkMessageAdapter: KakaoTalkMessageAdapter,
) : ItemWriter<NotificationEntity> {

    val log = LoggerFactory.getLogger(this::class.java)
    override fun write(notificationEntities: Chunk<out NotificationEntity>) {
        var cnt = 0

        for (notificationEntity in notificationEntities) {
            val successful = kakaoTalkMessageAdapter.sendKakaoTalkMessage(notificationEntity.uuid, notificationEntity.text)

            if (successful) {
                notificationEntity.sent = true
                notificationRepository.save(notificationEntity)
                cnt++
            }
        }
        log.info("SendNotificationItemWriter - write : 수업전 알람 {} / {} 건 전송 완료", cnt, notificationEntities.size())

    }
}
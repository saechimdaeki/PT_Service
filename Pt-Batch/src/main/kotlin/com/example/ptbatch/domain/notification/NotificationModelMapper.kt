package com.example.ptbatch.domain.notification

import com.example.ptbatch.domain.booking.BookingEntity
import com.example.ptbatch.util.LocalDateTimeUtils
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import java.lang.String.format
import java.time.LocalDateTime


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NotificationModelMapper {
    @Mapping(target = "uuid", source = "bookingEntity.userEntity.uuid")
    @Mapping(target = "text", source = "bookingEntity.startedAt", qualifiedByName = ["text"])
    fun toNotificationEntity(bookingEntity: BookingEntity?, event: NotificationEvent?): NotificationEntity?

    // 알람 보낼 메시지 생성
    @Named("text")
    fun text(startedAt: LocalDateTime): String? {
        return format("안녕하세요. %s 수업 시작합니다. 수업 전 출석 체크 부탁드립니다. \uD83D\uDE0A", LocalDateTimeUtils.format(startedAt))
    }

    companion object {
        val INSTANCE = Mappers.getMapper(NotificationModelMapper::class.java)
    }
}
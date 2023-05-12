package com.example.ptbatch.domain.packaze.repository

import com.example.ptbatch.domain.packaze.PackageEntity
import com.example.ptbatch.domain.packaze.PackageRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class PackageRepositoryTest @Autowired constructor(
        val packageRepository: PackageRepository
){

    @Test
    fun test_Save() {
        // given
        val packageEntity = PackageEntity(packageName = "바디 타바타 챌린지 PT 12주", period = 84)

        //when
        packageRepository.save(packageEntity)

        //then

        Assertions.assertNotNull(packageEntity.packageSeq)

    }

    @Test
    fun testFindByCreatedAfter() {
        // given
        val dateTime = LocalDateTime.now().minusMinutes(1)

        val highSchoolPackage = PackageEntity(packageName = "고등학생 전용 3개월 PT", period = 90)

        packageRepository.save(highSchoolPackage)

        val universityPackage = PackageEntity(packageName = "대학생 전용 6개월 PT", period = 180)

        packageRepository.save(universityPackage)

        // when
        val packageEntities = packageRepository.findByCreatedAtAfter(dateTime, PageRequest.of(0, 1, Sort.by("packageSeq").descending()))

        // then

        Assertions.assertEquals(1, packageEntities.size)
        Assertions.assertEquals(packageEntities[0].packageSeq, universityPackage.packageSeq)
    }

    @Test
    fun test_updateCountAndPeriod() {
        // given
        val marioPackage = PackageEntity(packageName = "마리오 바디프로필 이벤트 3개월", period = 90)
        packageRepository.save(marioPackage)

        requireNotNull(marioPackage.packageSeq)

        //when
        val updatedCount = packageRepository.updateCountAndPeriod(marioPackage.packageSeq!!, 30, 120)
        val updatedPackageEntity = packageRepository.findById(marioPackage.packageSeq!!).orElseThrow()
        //then
        Assertions.assertEquals(1, updatedCount)
        Assertions.assertEquals(30, updatedPackageEntity.count)
        Assertions.assertEquals(120, updatedPackageEntity.period)

    }

    @Test
    fun test_delete() {

        // given
        val peachPackage = PackageEntity(packageName = "피치가 모든일의 원흉", period = 90)
        packageRepository.save(peachPackage)


        //when
        packageRepository.deleteById(peachPackage.packageSeq!!)
        //then

        Assertions.assertTrue(packageRepository.findById(peachPackage.packageSeq!!).isEmpty)

    }

}
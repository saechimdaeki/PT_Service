package com.example.ptbatch.domain.booking

import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<BookingEntity , Long> {
}
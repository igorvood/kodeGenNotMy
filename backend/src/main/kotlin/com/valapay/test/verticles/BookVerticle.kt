package com.valapay.test.verticles

import com.valapay.test.entity.Book
import com.valapay.test.repo.BookRepo
import org.springframework.stereotype.Service

@Service
class BookVerticle(bookRepo: BookRepo):AbstractBookVerticle(bookRepo) {
    override fun beforeNewEntityHook(entity: Book): Book {
        println("Before new book")
        return super.beforeNewEntityHook(entity)
    }
}
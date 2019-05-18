package com.valapay.test.entity

import com.valapay.test.annotations.*
import com.valapay.test.annotations.AbstractImplementation
import com.valapay.test.annotations.GenerateGetById
import com.valapay.test.annotations.GenerateList
import com.valapay.test.annotations.GenerateUpdate
import com.valapay.test.annotations.frontend.IsBoolean
import com.valapay.test.annotations.frontend.IsString
import com.valapay.test.annotations.frontend.MaxLength
import javax.persistence.*

@GenerateList
@GenerateGetById
@GenerateUpdate
@Entity
@AbstractImplementation
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @field:IsBoolean
    @Column(name = "is_deleted")
    var hardcover: Boolean,

    @field:IsString
    @field:MaxLength(128)
    @Column(nullable = false, length = 128)
    val title: String,

    @field:IsString
    @field:MaxLength(128)
    @Column(nullable = false, length = 255)
    val author: String
)

@GenerateList
@GenerateGetById
@GenerateUpdate
@GenerateDelete
@GenerateCreate
@Entity
@ChildOf(Book::class)
data class Chapter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    @Column(nullable = false, name = "book_id")
    var bookId: Long?,

    @field:IsString
    @field:MaxLength(128)
    @Column(nullable = false, length = 128)
    @field:FindBy
    val name: String,

    @Column(nullable = false)
    val page:Int
)
package com.valapay.test.verticles

import com.valapay.test.VerticleHandlerResponse
import com.valapay.test.entity.Chapter
import com.valapay.test.messages.api.SearchRequest
import com.valapay.test.repo.ChapterRepo
import com.valapay.test.utils.VertxUtils
import io.netty.handler.codec.http.HttpResponseStatus
import org.springframework.stereotype.Component

@Component
class SearchVerticle(
    private val chapterRepo: ChapterRepo,
    vertxUtils: VertxUtils
) : AbstractSearchRouterVerticle(vertxUtils) {
    override fun getSearch(message: String): VerticleHandlerResponse<Any> {
        val book = chapterRepo.findByName(message)
        return if (book.isEmpty())
            VerticleHandlerResponse.error(HttpResponseStatus.NOT_FOUND)
        else {
            val ret = ArrayList<Chapter>()
            ret.addAll(book)
            VerticleHandlerResponse.ok(ret)
        }
    }

    override suspend fun postSearch(message: SearchRequest): VerticleHandlerResponse<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
package com.example.urlshortener

import com.google.common.hash.Hashing
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
class UrlShortenerApplication

fun main(args: Array<String>) {
	runApplication<UrlShortenerApplication>(*args)
}

@Controller
class Controller(private val redis: StringRedisTemplate) {

	@GetMapping(value = ["/{id}"])
	fun resolveUrl(@PathVariable id: String, response: HttpServletResponse) {
		val url = redis.opsForValue().get(id)
		if (url != null) {
			response.sendRedirect(url)
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND)
		}
	}

	@PostMapping
	fun shortenUrl(req: HttpServletRequest): ResponseEntity<String> {
		val queryParams = if (req.queryString != null) "?" + req.queryString else ""

		val url = (req.requestURI + queryParams).substring(1)

		return if (UrlValidator(arrayOf("http", "https")).isValid(url)) {
			val id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()
			redis.opsForValue().set(id, url)
			ResponseEntity("http://myurlshortener/$id", HttpStatus.OK)
		} else {
			ResponseEntity(HttpStatus.BAD_REQUEST)
		}
	}
}
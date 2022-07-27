package com.example.urlshortener

import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.junit4.SpringRunner
import javax.annotation.Resource

@RunWith(SpringRunner::class)
@SpringBootTest
class ApplicationTests {
	val log = LogFactory.getLog(ApplicationTests::class.java)!!
	@Resource
	lateinit var stringRedisTemplate: StringRedisTemplate
	@Test
	fun `redis string test"`() {
		//  Save string
		stringRedisTemplate.opsForValue().set("url", "http://google.com")
		log.info(" test url : ${stringRedisTemplate.opsForValue().get("url")}")
	}

}

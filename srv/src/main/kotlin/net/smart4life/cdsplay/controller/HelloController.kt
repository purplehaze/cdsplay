package net.smart4life.cdsplay.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/hello/say/{name}")
    fun sayHello(@PathVariable name: String): String {
        return "Hello ${name}"
    }
}
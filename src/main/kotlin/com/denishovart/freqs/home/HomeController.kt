package com.denishovart.freqs.home

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HomeController {
    @RequestMapping("/")
    @ResponseBody
    fun home(@AuthenticationPrincipal authenticatedUser: AuthenticatedUser?): String {
        println("auth $authenticatedUser")
        return "foo"
    }
}
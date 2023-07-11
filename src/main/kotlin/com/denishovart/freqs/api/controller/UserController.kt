package com.denishovart.freqs.api.controller

import com.denishovart.freqs.api.entity.User
import com.denishovart.freqs.auth.entity.AuthenticatedUser
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller


@Controller
class UserController {

    @QueryMapping
    fun me(@AuthenticationPrincipal user: AuthenticatedUser): User {
        return user.toUserEntity()!!
    }

}
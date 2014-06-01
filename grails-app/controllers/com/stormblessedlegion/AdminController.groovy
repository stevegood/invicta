package com.stormblessedlegion

import com.stormblessedlegion.content.ForumGroup
import com.stormblessedlegion.sec.User
import grails.converters.JSON

class AdminController {

    def ts3Service
    def userService

    def index() {}

    def dashboard() {}

    def users() {}

    def forums() {}

    def teamSpeak() {}

    def ajaxLoadUsers() {
        def userList = []
        User.list(sort: 'username', order: 'ascending').each { User user ->
            def userMap = [:]
            user.properties.each {k, v ->
                if (!(k in ['class', 'password', 'springSecurityService'])) {
                    userMap."$k" = v
                }
            }
            userMap.id = user.id
            userList << userMap
        }
        render userList as JSON
    }

    def ajaxUpdateUser() {
        def user = userService.updateUser(
            params.id as long,
            params.username as String,
            params.email as String,
            params.boolean('enabled'),
            params.boolean('accountExpired'),
            params.boolean('passwordExpired'),
            params.boolean('accountLocked')
        )
        def result = [success: (user != null)]
        render result as JSON
    }

    def ajaxStartTS3ChatBot() {
        ts3Service.connect()
        ts3Service.initChatBot()
        ts3Service.initJoinHandler()
        def result = [success: true]
        render result as JSON
    }

    def ajaxStopTS3ChatBot() {
        ts3Service.disconnect()
        def result = [success: true]
        render result as JSON
    }

    def ajaxRestartTS3ChatBot() {
        ts3Service.disconnect()
        ts3Service.connect()
        ts3Service.initChatBot()
        ts3Service.initJoinHandler()
        def result = [success: true]
        render result as JSON
    }

    def ajaxSaveUser() {
        def user = userService.createUser(params.username, params.password, params.email)
        render user as JSON
    }

    def ajaxListForums() {
        def forumGroups = ForumGroup.list().sort { it.sortPosition }*.toMap()
        render forumGroups as JSON
    }

    def ajaxDeleteForumGroup() {
        def forumGroup = ForumGroup.get(params.id)
        forumGroup.delete(flush: true)
        def result = [success: (ForumGroup.countById(params.id) == 0)]
        render result as JSON
    }

}

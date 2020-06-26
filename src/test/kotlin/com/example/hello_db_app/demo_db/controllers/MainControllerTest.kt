package com.example.hello_db_app.demo_db.controllers

import com.example.hello_db_app.demo_db.MainController
import com.example.hello_db_app.demo_db.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
class MainControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var target: MainController

    /**
     * 全ユーザ検索_正常系
     */
    @Test
    fun getAllUsersTest() {
        mockMvc = MockMvcBuilders.standaloneSetup(target).build()
        mockMvc.perform(get("/all"))
            .andExpect(status().isOk)
            .andExpect(
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    /**
     * ユーザ新規作成_正常系
     */
    @Test
    fun addNewUserTest() {
        mockMvc = MockMvcBuilders.standaloneSetup(target).build()
        mockMvc.perform(post("/add").param("name", "unit_test"))
            .andExpect(status().isOk)
            .andExpect(content().string("Saved"))
    }

    /**
     * ユーザ更新_正常系
     */
    @Test
    @Sql(statements = ["INSERT INTO user (name) VALUES ('update_data');"])
    fun updateUserTest() {
        val lastUser: User = target.userRepository.findAll().last()

        mockMvc = MockMvcBuilders.standaloneSetup(target).build()
        mockMvc.perform(
                post("/update")
                        .param("id", lastUser.id.toString())
                        .param("name", "updated!!!")
        )
                .andExpect(status().isOk)
                .andExpect(content().string("Updated"))
    }

    /**
     * ユーザ削除_正常系_対象ユーザが存在しない
     */
    @Test
    fun deleteNoUserTest() {
        mockMvc = MockMvcBuilders.standaloneSetup(target).build()
        mockMvc.perform(
                post("/delete")
                        .param("id", "-1")
        )
                .andExpect(status().isOk)
                .andExpect(content().string("Non User"))
    }

    /**
     * ユーザ削除_正常系_対象ユーザが存在
     */
    @Test
    @Sql(statements = ["INSERT INTO user (name) VALUES ('delete_data');"])
    fun deleteUserTest() {
        val lastUser: User = target.userRepository.findAll().last()

        mockMvc = MockMvcBuilders.standaloneSetup(target).build()
        mockMvc.perform(
                post("/delete")
                        .param("id", lastUser.id.toString())
        )
                .andExpect(status().isOk)
                .andExpect(content().string("Deleted"))
    }
}
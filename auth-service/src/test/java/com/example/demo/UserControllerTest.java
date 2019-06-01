package com.example.demo;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.web.UserController;
import com.example.demo.web.UserForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, secure = false)
@RunWith(SpringRunner.class)
public class UserControllerTest {

    @MockBean
    UserRepository users;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setUp() {
        given(this.users.findById(1L))
            .willReturn(Optional.of(User.builder().username("test").build()));

        given(this.users.findById(2L))
            .willReturn(Optional.empty());

        given(this.users.save(any(User.class)))
            .willReturn(User.builder().username("test").build());

        doNothing().when(this.users).delete(any(User.class));
    }

    @Test
    public void testGetById() throws Exception {

        this.mockMvc
            .perform(
                get("/users/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("test"));

        verify(this.users, times(1)).findById(any(Long.class));
        verifyNoMoreInteractions(this.users);
    }

    @Test
    public void testGetByIdNotFound() throws Exception {

        this.mockMvc
            .perform(
                get("/users/{id}", 2L)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());

        verify(this.users, times(1)).findById(any(Long.class));
        verifyNoMoreInteractions(this.users);
    }

    @Test
    public void testSave() throws Exception {

        this.mockMvc
            .perform(
                post("/users")
                    .content(this.objectMapper.writeValueAsBytes(UserForm.builder().username("test").build()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated());

        verify(this.users, times(1)).save(any(User.class));
        verify(this.users, times(1)).findByUsername("test");
        verifyNoMoreInteractions(this.users);
    }

    @Test
    public void testUpdate() throws Exception {

        this.mockMvc
            .perform(
                put("/users/1")
                    .content(this.objectMapper.writeValueAsBytes(UserForm.builder().username("test").build()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        verify(this.users, times(1)).findById(any(Long.class));
        verify(this.users, times(1)).save(any(User.class));
        verifyNoMoreInteractions(this.users);
    }

    @Test
    public void testDelete() throws Exception {

        this.mockMvc
            .perform(
                delete("/users/1")
            )
            .andExpect(status().isNoContent());

        verify(this.users, times(1)).findById(any(Long.class));
        verify(this.users, times(1)).delete(any(User.class));
        verifyNoMoreInteractions(this.users);
    }

}

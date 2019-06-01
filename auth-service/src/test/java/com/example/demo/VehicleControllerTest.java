package com.example.demo;

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
    UserRepository Users;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setUp() {
        given(this.Users.findById(1L))
            .willReturn(Optional.of(User.builder().name("test").build()));

        given(this.Users.findById(2L))
            .willReturn(Optional.empty());

        given(this.Users.save(any(User.class)))
            .willReturn(User.builder().name("test").build());

        doNothing().when(this.Users).delete(any(User.class));
    }

    @Test
    public void testGetById() throws Exception {

        this.mockMvc
            .perform(
                get("/v1/Users/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("test"));

        verify(this.Users, times(1)).findById(any(Long.class));
        verifyNoMoreInteractions(this.Users);
    }

    @Test
    public void testGetByIdNotFound() throws Exception {

        this.mockMvc
            .perform(
                get("/v1/Users/{id}", 2L)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());

        verify(this.Users, times(1)).findById(any(Long.class));
        verifyNoMoreInteractions(this.Users);
    }

    @Test
    public void testSave() throws Exception {

        this.mockMvc
            .perform(
                post("/v1/Users")
                    .content(this.objectMapper.writeValueAsBytes(UserForm.builder().name("test").build()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated());

        verify(this.Users, times(1)).save(any(User.class));
        verifyNoMoreInteractions(this.Users);
    }

    @Test
    public void testUpdate() throws Exception {

        this.mockMvc
            .perform(
                put("/v1/Users/1")
                    .content(this.objectMapper.writeValueAsBytes(UserForm.builder().name("test").build()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        verify(this.Users, times(1)).findById(any(Long.class));
        verify(this.Users, times(1)).save(any(User.class));
        verifyNoMoreInteractions(this.Users);
    }

    @Test
    public void testDelete() throws Exception {

        this.mockMvc
            .perform(
                delete("/v1/Users/1")
            )
            .andExpect(status().isNoContent());

        verify(this.Users, times(1)).findById(any(Long.class));
        verify(this.Users, times(1)).delete(any(User.class));
        verifyNoMoreInteractions(this.Users);
    }

}

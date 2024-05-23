package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.repo.OrderRepository;
import com.example.demo.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DirtiesContext
    public void shouldReturnUserSummaryView() throws Exception {
        // Создание тестового пользователя без заказов
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user = userRepository.save(user);

        String expectedJson = objectMapper.writerWithView(User.Views.UserSummary.class).writeValueAsString(Collections.singletonList(user));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DirtiesContext
    public void shouldReturnUserDetailsView() throws Exception {
        // Создание тестового пользователя с заказом
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");

        Order order = new Order();
        order.setProduct("Test Product");
        order.setAmount(100.0);
        order.setStatus("Pending");
        order.setUser(user);

        user.setOrders(Collections.singletonList(order));

        user = userRepository.save(user);
        orderRepository.save(order);

        String expectedJson = objectMapper.writerWithView(User.Views.UserDetails.class).writeValueAsString(user);

        mockMvc.perform(get("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}

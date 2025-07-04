package com.example.servlet;

import com.example.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet("/orders/*")
public class OrderServlet extends HttpServlet {
    private final Map<Integer, Order> orders = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Order order = objectMapper.readValue(req.getInputStream(), Order.class);
        int id = idCounter.getAndIncrement();
        order.setId(id);
        orders.put(id, order);

        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), order);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        if (pathInfo == null || pathInfo.equals("/")) {
            objectMapper.writeValue(resp.getOutputStream(), orders.values());
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            Order order = orders.get(id);
            if (order == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            objectMapper.writeValue(resp.getOutputStream(), order);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int id = Integer.parseInt(pathInfo.substring(1));
        Order updatedOrder = objectMapper.readValue(req.getInputStream(), Order.class);
        updatedOrder.setId(id);
        orders.put(id, updatedOrder);

        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), updatedOrder);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int id = Integer.parseInt(pathInfo.substring(1));
        orders.remove(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    public void createDummyOrder() {
        Order order = new Order();
        order.setId(1);
        order.setDate(java.time.LocalDateTime.now());
        order.setCost(100);
        orders.put(1, order);
    }
}

package com.example.servlet;

import com.example.model.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import javax.servlet.http.*;
import java.io.*;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServletTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testCreateOrder() throws Exception {
        OrderServlet servlet = new OrderServlet();
        String jsonInput = "{\"date\":\"2025-07-01T00:00:00\",\"cost\":100,\"products\":[]}";
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(new ByteArrayInputStream(jsonInput.getBytes())));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream(baos));
        servlet.doPost(req, resp);
        Order order = mapper.readValue(baos.toString(), Order.class);
        assertEquals(1, order.getId());
        assertEquals(100, order.getCost());
    }

    @Test
    void testGetOrder() throws Exception {
        OrderServlet servlet = new OrderServlet();
        servlet.createDummyOrder();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getPathInfo()).thenReturn("/");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream(baos));
        servlet.doGet(req, resp);
        List<Order> orders = mapper.readValue(baos.toString(), new TypeReference<List<Order>>() {});
        assertEquals(1, orders.get(0).getId());
    }

    @Test
    void testUpdateOrder() throws Exception {
        OrderServlet servlet = new OrderServlet();
        servlet.createDummyOrder();
        String jsonInput = "{\"date\":\"2025-07-02T00:00:00\",\"cost\":200,\"products\":[]}";
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getPathInfo()).thenReturn("/1");
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(new ByteArrayInputStream(jsonInput.getBytes())));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream(baos));
        servlet.doPut(req, resp);
        Order order = mapper.readValue(baos.toString(), Order.class);
        assertEquals(200, order.getCost());
    }

    @Test
    void testDeleteOrder() throws Exception {
        OrderServlet servlet = new OrderServlet();
        servlet.createDummyOrder();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getPathInfo()).thenReturn("/1");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream(baos));
        servlet.doDelete(req, resp);
        assertTrue(baos.size() == 0);
    }
}

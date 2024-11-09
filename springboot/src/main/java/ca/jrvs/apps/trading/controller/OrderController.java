package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dto.MarketOrderDTO;
import ca.jrvs.apps.trading.model.SecurityOrder;
import ca.jrvs.apps.trading.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/marketOrder")
    public SecurityOrder postMarketOrder(@Valid @RequestBody MarketOrderDTO orderDto) {
        return orderService.executeMarketOrder(orderDto);
    }
}

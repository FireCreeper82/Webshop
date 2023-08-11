package de.firecreeper82.shop.controller

import de.firecreeper82.shop.model.*
import de.firecreeper82.shop.service.OrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(val orderService: OrderService) {

    @PostMapping("/orders")
    fun createOrder(@RequestBody request: OrderCreateRequest): OrderResponse {
        return orderService.createOrder(request)
    }

    @GetMapping("/orders")
    fun getAllOrders(): List<OrderResponse> {
        return orderService.orderRepository.orders
    }
    @PostMapping("/orders/{id}/positions")
    fun createOrderPositions(@PathVariable(name = "id") orderId: String, @RequestBody request: OrderPositionCreateRequest) {
        orderService.createNewPositionForOrder(orderId, request)
    }

    @PutMapping("orders/{id}")
    fun updateOrder(@PathVariable id: String, @RequestBody request: OrderUpdateRequest): OrderResponse {
        return orderService.updateOrder(id, request)
    }

    @GetMapping("/orders/positions")
    fun getAllOrderPositions(): List<OrderPositionResponse> {
        return orderService.orderPositionRepository.orderPositions
    }
}
package de.firecreeper82.shop.service

import de.firecreeper82.shop.entity.OrderEntity
import de.firecreeper82.shop.exceptions.IdNotFoundException
import de.firecreeper82.shop.model.OrderPositionResponse
import de.firecreeper82.shop.model.ShoppingCartResponse
import de.firecreeper82.shop.repository.*
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class ShoppingCartService(val orderRepository: OrderRepository,
                          val productRepository: ProductRepository,
                          val customerRepository: CustomerRepository
) {
    fun getShoppingCartForCustomer(customerId: String): ShoppingCartResponse {
        customerRepository.findById(customerId);

        val orders: List<OrderEntity> = orderRepository.findAllByCustomerIdWhereOrderStatusIsNew(customerId)

        val orderPositions = orders
            .flatMap { it.orderPositions }
            .map { OrderService.mapToResponse(it) }

        val deliveryCost = 800L
        val totalAmount = calculateSumForCart(orderPositions, deliveryCost)


        return ShoppingCartResponse(
            customerId = customerId,
            orderPositions = orderPositions,
            totalAmountInCent = totalAmount,
            deliveryCostInCent = deliveryCost,
            deliveryOption = "STANDARD"
        )
    }

    fun calculateSumForCart(orderPositions: List<OrderPositionResponse>, deliveryCost: Long): Long {
        val positionAmounts: List<Long> = orderPositions.map {
            val product: ProductEntity = productRepository
                .findById(it.productId)
                .orElseThrow { IdNotFoundException("Product with id ${it.productId} not found") }

            if(it.quantity <= 0)
                throw IllegalArgumentException("OrderPosition with quantity of ${it.quantity} is not allowed")

            it.quantity * product.priceInCent
        }
        val positionSum = positionAmounts.sum()

        return positionSum + deliveryCost
    }
}

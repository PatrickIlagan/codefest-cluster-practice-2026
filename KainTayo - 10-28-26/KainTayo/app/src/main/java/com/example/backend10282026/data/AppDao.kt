package com.example.backend10282026.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    fun insertUser(user: User)
    @Insert
    fun insertFood(food: Food)
    @Insert
    fun insertOrder(order: Order): Long
    @Insert
    fun insertOrderItem(orderItem: OrderItem)
    @Insert
    fun insertCartItem(cartItem: CartItem)
    @Update
    fun updateCartItem(cartItem: CartItem)
    @Update
    fun updateFood(food: Food)

    @Query("SELECT * FROM users_table WHERE email = :email AND password = :password")
    fun loginUser(email: String, password: String): User?

    @Query("SELECT * FROM foods_table")
    fun getAllFoodList(): List<Food>

    @Query("SELECT * FROM foods_table")
    fun getAllFood(): Flow<List<Food>>

    @Query("SELECT * FROM cartItems_table WHERE foodId = :foodId")
    fun getCartItemByFoodId(foodId: Long): CartItem?

    @Query("SELECT * FROM orders_table WHERE userId = :userId")
    fun getAllOrderByUserId(userId: Long): Flow<List<Order>>

    @Query("SELECT * FROM cartItems_table WHERE userId = :userId")
    fun getAllCardItemByUserId(userId: Long): Flow<List<CartItem>>

    @Query("SELECT * FROM cartItems_table WHERE userId = :userId")
    fun getAllCardItemListByUserId(userId: Long): List<CartItem>

    @Query("SELECT * FROM orderItems_table WHERE orderId = :orderId")
    fun getAllOrderItemListByOrderId(orderId: Long): List<OrderItem>

    @Query("SELECT * FROM orderItems_table WHERE orderId = :orderId")
    fun getAllOrderItemByOrderId(orderId: Long): Flow<List<OrderItem>>

    @Query("SELECT * FROM foods_table WHERE id = :id")
    fun getFoodById(id: Long): Food?

    @Query("DELETE FROM cartItems_table WHERE id = :id")
    fun deleteCartItem(id: Long)

    @Delete
    fun deleteFood(food: Food)

    @Query("DELETE FROM cartItems_table WHERE userId = :userId")
    fun clearCart(userId: Long)

    @Query("SELECT * FROM users_table WHERE id = :id")
    fun getUserById(id: Long): User?

    @Query("SELECT * FROM users_table")
    fun getAllUserList(): List<User>
}
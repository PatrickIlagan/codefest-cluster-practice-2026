package com.example.practice20261026.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    fun insertProduct(product: Product): Long

    @Insert
    fun insertTransaction(transaction: Transaction): Long

    @Insert
    fun insertUser(user: User)


    @Update
    fun updateProduct(product: Product)

    @Update
    fun updateTransaction(transaction: Transaction)

    @Delete
    fun deleteProduct(product: Product)

    @Delete
    fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM products_table WHERE userId = :id")
    fun getAllProductsByUserId(id: Long): Flow<List<Product>>
    @Query("SELECT * FROM transactions_table")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT name FROM products_table WHERE userId = :id")
    fun getAllProductNameListByUserId(id: Long): List<String>

    @Query("SELECT * FROM products_table WHERE userId = :id AND name = :name")
    fun getProductByNameAndUserId(id: Long, name: String): Product?

    @Query("SELECT * FROM users_table WHERE email = :email AND password = :password")
    fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM products_table WHERE id = :id")
    fun getProductById(id: Long): Product?

    @Query("SELECT * FROM transactions_table WHERE id = :id")
    fun getTransactionById(id: Long): Transaction?

    @Query("SELECT * FROM users_table WHERE id = :id")
    fun getUserById(id: Long): User?


}
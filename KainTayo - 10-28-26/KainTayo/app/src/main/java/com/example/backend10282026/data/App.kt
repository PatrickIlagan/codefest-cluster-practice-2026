package com.example.backend10282026.data

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.example.backend10282026.R

class App : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val dao by lazy { database.appDao()}

    override fun onCreate() {
        super.onCreate()

        if (dao.getAllFoodList().isEmpty()){
            val food1 = Food(
                name = "Chicken Adobo Rice",
                category = "Rice Meals",
                price = 95.00,
                image = R.drawable.food_chicken_adobo,
                description = "Tender chicken simmered in soy sauce, vinegar, garlic, and bay leaves, served with steamed rice."
            )
            val food2 = Food(
                name = "Pork Sisig Rice",
                category = "Rice Meals",
                price = 110.00,
                image = R.drawable.food_pork_sisig,
                description = "Sizzling chopped pork with onions, chili, and calamansi, topped with a fresh egg and served with rice."
            )
            val food3 = Food(
                name = "Beef Tapsilog",
                category = "Rice Meals",
                price = 120.00,
                image = R.drawable.food_beef_tapsilog,
                description = "Sweet-savory beef tapa with garlic fried rice and a sunny-side-up egg. The classic all-day breakfast."
            )

            val food4 = Food(
                name = "Pancit Canton",
                category = "Noodles & Pasta",
                price = 85.00,
                image = R.drawable.food_pancit_canton,
                description = "Stir-fried egg noodles with pork, shrimp, cabbage, carrots, and a squeeze of calamansi."
            )

            val food5 = Food(
                name = "Filipino Spaghetti",
                category = "Noodles & Pasta",
                price = 75.00,
                image = R.drawable.food_filipino_spaghetti,
                description = "Sweet-style spaghetti with banana ketchup sauce, sliced hotdogs, ground pork, and grated cheese."
            )

            val food6 = Food(
                name = "Beef Mami",
                category = "Noodles & Pasta",
                price = 90.00,
                image = R.drawable.food_beef_mami,
                description = "Hot noodle soup in rich beef broth with tender beef slices, boiled egg, and spring onions."
            )

            val food7 = Food(
                name = "Lumpiang Shanghai (6 pcs)",
                category = "Snacks",
                price = 65.00,
                image = R.drawable.food_lumpiang_shanghai,
                description = "Crispy fried spring rolls filled with seasoned ground pork and vegetables, with sweet chili dip."
            )

            val food8 = Food(
                name = "Cheeseburger",
                category = "Snacks",
                price = 79.00,
                image = R.drawable.food_cheeseburger,
                description = "Juicy beef patty with melted cheese, lettuce, tomato, and special sauce in a toasted bun."
            )
            val food9 = Food(
                name = "French Fries",
                category = "Snacks",
                price = 55.00,
                image = R.drawable.food_french_fries,
                description = "Golden crispy potato fries, lightly salted. Perfect for sharing or merienda."
            )

            val food10 = Food(
                name = "Halo-Halo",
                category = "Drinks & Desserts",
                price = 89.00,
                image = R.drawable.food_halo_halo,
                description = "Shaved ice with sweet beans, nata de coco, leche flan, ube halaya, and evaporated milk."
            )

            val food11 = Food(
                name = "Iced Milk Tea",
                category = "Drinks & Desserts",
                price = 69.00,
                image = R.drawable.food_iced_milk_tea,
                description = "Creamy black milk tea over ice with chewy tapioca pearls."
            )
            val food12 = Food(
                name = "Leche Flan",
                category = "Drinks & Desserts",
                price = 45.00,
                image = R.drawable.food_leche_flan,
                description = "Silky-smooth steamed custard with golden caramel syrup."
            )
            dao.insertFood(food1)
            dao.insertFood(food2)
            dao.insertFood(food3)
            dao.insertFood(food4)
            dao.insertFood(food5)
            dao.insertFood(food6)
            dao.insertFood(food7)
            dao.insertFood(food8)
            dao.insertFood(food9)
            dao.insertFood(food10)
            dao.insertFood(food11)
            dao.insertFood(food12)
        }

        if (dao.getAllUserList().isEmpty()){
            val user = User(
                name = "Aling Rosa",
                email = "admin@gmail.com",
                password = "123456",
                role = "ADMIN"
            )
            dao.insertUser(user)
        }
    }
}
val Activity.dao get() = (application as App).dao
val Fragment.dao get() = (requireActivity().application as App).dao
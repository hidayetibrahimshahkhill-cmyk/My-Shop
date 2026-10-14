package com.myshop.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

data class Product(
    val id: Long,
    val name: String,
    val category: String,
    val buy: Double,
    val sell: Double,
    val qty: Int
)

private val languages = listOf("Roman Urdu", "اردو", "English", "Português", "پښتو")
private val translations = mapOf(
    "English" to listOf("Add product", "Product name", "Category", "Buy price", "Sell price", "Quantity", "Save product", "Inventory", "Potential profit", "WhatsApp order", "Shop WhatsApp number (country code, digits only)", "Delete", "No products yet"),
    "Roman Urdu" to listOf("Samaan shamil karein", "Samaan ka naam", "Category", "Khareed qeemat", "Farokht qeemat", "Miqqaat", "Samaan save karein", "Stock", "Mumkin munafa", "WhatsApp order", "Dukaan ka WhatsApp number (country code samait, sirf digits)", "Delete", "Abhi samaan nahi"),
    "اردو" to listOf("سامان شامل کریں", "سامان کا نام", "کیٹیگری", "خرید قیمت", "فروخت قیمت", "مقدار", "سامان محفوظ کریں", "اسٹاک", "ممکنہ منافع", "WhatsApp آرڈر", "دکان کا WhatsApp نمبر (ملکی کوڈ سمیت، صرف ہندسے)", "حذف کریں", "ابھی سامان موجود نہیں"),
    "Português" to listOf("Adicionar produto", "Nome do produto", "Categoria", "Preço de compra", "Preço de venda", "Quantidade", "Guardar produto", "Stock", "Lucro potencial", "Pedido WhatsApp", "Número WhatsApp da loja (com código do país, só dígitos)", "Eliminar", "Ainda não há produtos"),
    "پښتو" to listOf("توکي زیات کړئ", "د توکي نوم", "کټګوري", "د پېرلو بیه", "د پلور بیه", "اندازه", "توکی خوندي کړئ", "سټاک", "اټکلي ګټه", "WhatsApp فرمایش", "د دوکان WhatsApp شمېره (د هېواد کوډ سره، یوازې شمېرې)", "حذف", "تر اوسه توکي نشته")
)

private fun tr(lang: String, index: Int): String =
    translations[lang]?.getOrNull(index) ?: translations["English"]!![index]

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyShopApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyShopApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("myshop", Context.MODE_PRIVATE) }
    var language by remember { mutableStateOf(prefs.getString("language", "Roman Urdu") ?: "Roman Urdu") }
    var whatsapp by remember { mutableStateOf(prefs.getString("whatsapp", "") ?: "") }
    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var buy by remember { mutableStateOf("") }
    var sell by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }
    var products by remember { mutableStateOf(loadProducts(prefs)) }
    var languageExpanded by remember { mutableStateOf(false) }

    val totalUnits = products.sumOf { it.qty }
    val potentialProfit = products.sumOf { (it.sell - it.buy) * it.qty }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Shop") },
                    actions = {
                        Box {
                            TextButton(onClick = { languageExpanded = true }) { Text(language) }
                            DropdownMenu(expanded = languageExpanded, onDismissRequest = { languageExpanded = false }) {
                                languages.forEach { lang ->
                                    DropdownMenuItem(text = { Text(lang) }, onClick = {
                                        language = lang
                                        prefs.edit().putString("language", lang).apply()
                                        languageExpanded = false
                                    })
                                }
                            }
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("My Shop", style = MaterialTheme.typography.headlineMedium)
                    Text("${tr(language, 7)}: $totalUnits     |     ${tr(language, 8)}: Rs ${"%.2f".format(Locale.US, potentialProfit)}")
                }
                item { Divider() }
                item { Text(tr(language, 0), style = MaterialTheme.typography.titleLarge) }
                item { OutlinedTextField(productName, { productName = it }, label = { Text(tr(language, 1)) }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
                item { OutlinedTextField(category, { category = it }, label = { Text(tr(language, 2)) }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(buy, { buy = it }, label = { Text(tr(language, 3)) }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                        OutlinedTextField(sell, { sell = it }, label = { Text(tr(language, 4)) }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                    }
                }
                item { OutlinedTextField(qty, { qty = it }, label = { Text(tr(language, 5)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true) }
                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val b = buy.toDoubleOrNull()
                            val s = sell.toDoubleOrNull()
                            val q = qty.toIntOrNull()
                            if (productName.isNotBlank() && category.isNotBlank() && b != null && s != null && q != null && b >= 0 && s >= 0 && q >= 0) {
                                products = products + Product(System.currentTimeMillis(), productName.trim(), category.trim(), b, s, q)
                                saveProducts(prefs, products)
                                productName = ""; category = ""; buy = ""; sell = ""; qty = "1"
                            }
                        }
                    ) { Text(tr(language, 6)) }
                }
                item { OutlinedTextField(whatsapp, { whatsapp = it.filter(Char::isDigit); prefs.edit().putString("whatsapp", whatsapp).apply() }, label = { Text(tr(language, 10)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true) }
                item { Text(tr(language, 7), style = MaterialTheme.typography.titleLarge) }
                if (products.isEmpty()) {
                    item { Text(tr(language, 12)) }
                } else {
                    items(products, key = { it.id }) { product ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(product.name, style = MaterialTheme.typography.titleMedium)
                                Text("${product.category} • ${tr(language, 5)}: ${product.qty}")
                                Text("Buy: Rs ${product.buy}  |  Sell: Rs ${product.sell}  |  Profit/unit: Rs ${product.sell - product.buy}")
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        if (whatsapp.isNotBlank()) {
                                            val message = "Order enquiry: ${product.name}%0ACategory: ${product.category}%0APrice: Rs ${product.sell}"
                                            val uri = Uri.parse("https://wa.me/$whatsapp?text=$message")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                        }
                                    }, modifier = Modifier.weight(1f)) { Text(tr(language, 9)) }
                                    OutlinedButton(onClick = {
                                        products = products.filterNot { it.id == product.id }
                                        saveProducts(prefs, products)
                                    }) { Text(tr(language, 11)) }
                                }
                            }
                        }
                    }
                }
                item {
                    Text(
                        "Starter version: products are saved on this phone only. For customers to see a shared online catalogue, a secure online database/hosting service must be connected.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun loadProducts(prefs: android.content.SharedPreferences): List<Product> {
    return try {
        val array = JSONArray(prefs.getString("products", "[]"))
        (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            Product(o.getLong("id"), o.getString("name"), o.getString("category"), o.getDouble("buy"), o.getDouble("sell"), o.getInt("qty"))
        }
    } catch (_: Exception) { emptyList() }
}

private fun saveProducts(prefs: android.content.SharedPreferences, products: List<Product>) {
    val array = JSONArray()
    products.forEach { p ->
        array.put(JSONObject().put("id", p.id).put("name", p.name).put("category", p.category).put("buy", p.buy).put("sell", p.sell).put("qty", p.qty))
    }
    prefs.edit().putString("products", array.toString()).apply()
}

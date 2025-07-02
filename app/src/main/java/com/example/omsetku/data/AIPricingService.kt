package com.example.omsetku.data

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.example.omsetku.BuildConfig
import java.text.NumberFormat
import java.util.Locale
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

private fun JsonReader.nextStringOrEmpty(): String =
    if (peek() == com.google.gson.stream.JsonToken.NULL) { nextNull(); "" } else { nextString() }

private fun JsonReader.nextStringOrArrayAsString(): String =
    when (peek()) {
        com.google.gson.stream.JsonToken.STRING -> nextString()
        com.google.gson.stream.JsonToken.BEGIN_ARRAY -> {
            val list = mutableListOf<String>()
            beginArray()
            while (hasNext()) {
                list.add(nextStringOrEmpty())
            }
            endArray()
            list.joinToString("\n")
        }
        com.google.gson.stream.JsonToken.NULL -> { nextNull(); "" }
        else -> { skipValue(); "" }
    }

fun Int.toRupiah(): String = NumberFormat.getNumberInstance(Locale("id", "ID")).format(this)
fun Double.toRupiah(): String = NumberFormat.getNumberInstance(Locale("id", "ID")).format(this.toInt())

@Singleton
class AIPricingService @Inject constructor() {
    
    companion object {
        private const val TAG = "AIPricingService"
    }
    
    // Use BuildConfig for API key (you'll need to add this to your local.properties)
    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    init {
        // Log untuk debugging API key
        Log.d(TAG, "🔍 Debug API Key - Length: ${apiKey.length}, Value: ${apiKey.take(20)}...")
        if (apiKey == "YOUR_GEMINI_API_KEY" || apiKey.isBlank()) {
            Log.w(TAG, "⚠️ API Key belum diset! Gunakan fallback mode.")
        } else {
            Log.d(TAG, "✅ API Key berhasil dimuat: ${apiKey.take(10)}...")
        }
    }
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )
    
    data class PricingRecommendation(
        val recommendedPrice: String,
        val confidence: Double,
        val reasoning: String = "",
        val marketAnalysis: String = "",
        val competitiveAdvantage: String = "",
        val riskFactors: List<String> = emptyList(),
        val suggestions: List<String> = emptyList(),
        val marketSegments: List<String>? = emptyList(),
        val pricingStrategy: String? = "",
        val growthPotential: String? = "",
        val hppOptimization: HPPOptimization? = null
    )
    
    data class HPPOptimization(
        val optimizations: List<SimpleOptimization> = emptyList()
    )
    
    data class SimpleOptimization(
        val nama: String,
        val saran: String
    )
    
    data class HPPData(
        val productName: String,
        val hppPerPorsi: Double,
        val marginProfit: Double,
        val bahanBaku: List<BahanBakuData>,
        val biayaOperasional: List<BiayaOperasionalData>,
        val targetPorsi: Double,
        val currentPrice: Double? = null
    )
    
    data class BahanBakuData(
        val nama: String,
        val hargaBeli: Double,
        val jumlahBeli: Double,
        val satuan: String,
        val terpakai: Double
    )
    
    data class BiayaOperasionalData(
        val nama: String,
        val hargaBeli: Double,
        val jumlahBeli: Double,
        val satuan: String,
        val terpakai: Double
    )
    
    data class AIInsightResult(
        val highlight: String = "",
        val saran: String = "",
        val narasi: String = "",
        val anomali: String = "" // kosong jika tidak ada anomali
    )
    
    class AIInsightResultAdapter : TypeAdapter<AIInsightResult>() {
        override fun write(out: JsonWriter, value: AIInsightResult) {
            out.beginObject()
            out.name("highlight").value(value.highlight)
            out.name("saran").value(value.saran)
            out.name("narasi").value(value.narasi)
            out.name("anomali").value(value.anomali)
            out.endObject()
        }
        override fun read(reader: JsonReader): AIInsightResult {
            var highlight = ""
            var saran = ""
            var narasi = ""
            var anomali = ""
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "highlight" -> highlight = reader.nextStringOrEmpty()
                    "saran" -> saran = reader.nextStringOrArrayAsString()
                    "narasi" -> narasi = reader.nextStringOrEmpty()
                    "anomali" -> anomali = reader.nextStringOrEmpty()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            return AIInsightResult(highlight, saran, narasi, anomali)
        }
    }
    
    suspend fun getPricingRecommendation(hppData: HPPData): PricingRecommendation {
        return withContext(Dispatchers.IO) {
            try {
                // Check if API key is valid
                Log.d(TAG, "🔍 Validating API Key - Length: ${apiKey.length}, Starts with: ${apiKey.take(10)}")
                if (apiKey == "YOUR_GEMINI_API_KEY" || apiKey.isBlank()) {
                    Log.w(TAG, "Menggunakan fallback recommendation karena API key tidak tersedia")
                    return@withContext createFallbackRecommendation(hppData)
                }
                
                Log.d(TAG, "Memulai analisis AI untuk produk: ${hppData.productName}")
                val prompt = buildPricingAnalysisPrompt(hppData)
                val response = generativeModel.generateContent(prompt)
                
                val responseText = response.text ?: throw Exception("No response from AI")
                Log.d(TAG, "AI Response: $responseText")
                return@withContext parseAIResponse(responseText)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error dalam AI analysis: ${e.message}", e)
                // Fallback to basic calculation if AI fails
                return@withContext createFallbackRecommendation(hppData)
            }
        }
    }
    
    private fun createFallbackRecommendation(hppData: HPPData): PricingRecommendation {
        val fallbackPrice = hppData.hppPerPorsi + (hppData.hppPerPorsi * hppData.marginProfit / 100.0)
        return PricingRecommendation(
            recommendedPrice = fallbackPrice.toRupiah(),
            confidence = 0.5,
            reasoning = "AI analysis tidak tersedia, menggunakan perhitungan dasar HPP + margin",
            marketAnalysis = "Tidak dapat menganalisis pasar karena layanan AI tidak tersedia",
            competitiveAdvantage = "Harga standar berdasarkan HPP + margin profit",
            riskFactors = listOf("Layanan AI tidak tersedia", "Perlu analisis pasar manual"),
            suggestions = listOf(
                "Lakukan riset pasar untuk harga yang lebih akurat",
                "Monitor harga kompetitor",
                "Pertimbangkan nilai tambah produk"
            )
        )
    }
    
    private fun buildPricingAnalysisPrompt(hppData: HPPData): String {
        val bahanBakuList = hppData.bahanBaku
        val biayaOperasionalList = hppData.biayaOperasional
        val bahanBakuText = if (bahanBakuList.isNotEmpty()) bahanBakuList.joinToString { it.nama } else "Tidak ada"
        val biayaOperasionalText = if (biayaOperasionalList.isNotEmpty()) biayaOperasionalList.joinToString { it.nama } else "Tidak ada"
        return """
Optimasi HPP untuk produk: ${hppData.productName}

Berdasarkan data yang diinput user berikut:
- Bahan Baku: $bahanBakuText
- Biaya Operasional: $biayaOperasionalText

Berikan saran optimasi HPP yang singkat, padat, actionable, dan detail, hanya untuk bahan baku dan biaya operasional yang diisi user saja. Jika tidak ada input, jangan tampilkan section optimasi HPP.

**PENTING:** Untuk setiap saran optimasi, tambahkan estimasi potensi penghematan HPP yang spesifik dan fixed, misal: 'Dapat mengurangi HPP sebesar Rp 2.000 per porsi.' Tampilkan angka penghematan dalam format rupiah dan jangan kosong. Penghematan ini harus menjadi bagian dari string saran, bukan field terpisah.

Gabungkan semua saran optimasi (baik bahan baku maupun biaya operasional) dalam satu list saja, tanpa label terpisah.

**PENTING:** Pastikan semua field (harga rekomendasi, alasan, analisis pasar, keunggulan kompetitif, dll) diisi dengan jawaban singkat, jangan kosong, walaupun hanya satu kalimat.

Format respons JSON:
{
  "recommendedPrice": "hasil perhitunganmu dalam format rupiah, misal: '12.500' atau '8.400'",
  "reasoning": "...",
  "marketAnalysis": "...",
  "competitiveAdvantage": "...",
  "hppOptimization": {
    "optimizations": [
      { "nama": "Apel", "saran": "Gunakan varietas apel lokal yang lebih murah tanpa mengurangi rasa. Dapat mengurangi HPP sebesar Rp 1.000 per porsi." },
      { "nama": "Listrik", "saran": "Gunakan alat hemat energi dan atur jadwal produksi di luar jam sibuk. Dapat mengurangi HPP sebesar Rp 500 per porsi." }
    ]
  }
}

**JANGAN** meniru angka pada contoh. Hitung dan tentukan sendiri recommendedPrice secara objektif berdasarkan data HPP, margin, dan analisis pasar. Jika data HPP per porsi adalah Rp 6.000 dan margin 40%, maka recommendedPrice harus sekitar Rp 8.400, bukan angka tetap.

Jangan tambahkan bahan baku atau biaya operasional yang tidak diinput user. Jangan tampilkan section optimasi HPP jika tidak ada input user.
Gunakan kemampuan super maksimalmu sebagai AI yang aku subscribe dengan mahal, kalau tidak, akan berhenti subscribe jika hasilmu tidak maksimal.

HPP per porsi: Rp ${hppData.hppPerPorsi.toInt().toRupiah()}
"""
    }
    
    private fun parseAIResponse(responseText: String): PricingRecommendation {
        return try {
            // Extract JSON from response (AI might add extra text)
            val jsonStart = responseText.indexOf('{')
            val jsonEnd = responseText.lastIndexOf('}') + 1
            val jsonString = if (jsonStart >= 0 && jsonEnd > jsonStart) {
                responseText.substring(jsonStart, jsonEnd)
            } else {
                responseText
            }
            
            Log.d(TAG, "Parsing JSON: $jsonString")
            val gson = Gson()
            gson.fromJson(jsonString, PricingRecommendation::class.java)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing AI response: ${e.message}", e)
            // Fallback parsing
            PricingRecommendation(
                recommendedPrice = "0",
                confidence = 0.0,
                reasoning = "Failed to parse AI response: ${e.message}",
                marketAnalysis = "Unable to analyze",
                competitiveAdvantage = "Unable to determine",
                riskFactors = listOf("AI response parsing error"),
                suggestions = listOf("Use manual pricing calculation")
            )
        }
    }
    
    suspend fun getMarketInsights(productName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Check if API key is valid
                if (apiKey == "YOUR_GEMINI_API_KEY" || apiKey.isBlank()) {
                    return@withContext "Layanan AI tidak tersedia. Silakan lakukan riset pasar manual."
                }
                
                val prompt = """
                Berikan insight pasar untuk produk "$productName" di Indonesia:
                - Kisaran harga umum di pasar
                - Tren harga terkini
                - Faktor yang mempengaruhi harga
                - Tips pricing untuk UMKM
                
                Berikan respons dalam bahasa Indonesia yang mudah dipahami.
                
                Gunakan kemampuan super maksimalmu sebagai AI yang aku subscribe dengan mahal, kalau tidak, akan berhenti subscribe jika hasilmu tidak maksimal.
                """
                
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Tidak dapat menganalisis pasar saat ini"
                
            } catch (e: Exception) {
                "Tidak dapat mengakses data pasar: ${e.message}"
            }
        }
    }
} 
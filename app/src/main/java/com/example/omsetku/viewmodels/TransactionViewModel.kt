package com.example.omsetku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.models.Transaction
import com.example.omsetku.firebase.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.omsetku.data.AIPricingService.AIInsightResult
import com.example.omsetku.data.AIPricingService
import com.google.ai.client.generativeai.GenerativeModel
import com.example.omsetku.BuildConfig
import com.google.gson.GsonBuilder

class TransactionViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _incomeAmount = MutableStateFlow(0L)
    val incomeAmount: StateFlow<Long> = _incomeAmount.asStateFlow()

    private val _expenseAmount = MutableStateFlow(0L)
    val expenseAmount: StateFlow<Long> = _expenseAmount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _insightAI = MutableStateFlow<AIInsightResult?>(null)
    val insightAI: StateFlow<AIInsightResult?> = _insightAI.asStateFlow()

    private val _isLoadingInsightAI = MutableStateFlow(false)
    val isLoadingInsightAI: StateFlow<Boolean> = _isLoadingInsightAI.asStateFlow()

    private val _errorInsightAI = MutableStateFlow<String?>(null)
    val errorInsightAI: StateFlow<String?> = _errorInsightAI.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    /**
     * Fungsi lama (default): load transaksi 1 bulan terakhir
     */
    fun loadTransactions() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.timeInMillis
        val endDate = System.currentTimeMillis()
        loadTransactions(startDate, endDate)
    }

    /**
     * Fungsi baru: load transaksi berdasarkan rentang tanggal
     */
    fun loadTransactions(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val transactionList = repository.getUserTransactions(startDate, endDate)

                val transactionItems = transactionList.mapNotNull { transactionMap ->
                    try {
                        val date = transactionMap["date"] as? Long ?: 0L
                        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                        val type = transactionMap["type"] as? String ?: ""
                        val description = transactionMap["description"] as? String ?: ""
                        val category = transactionMap["category"] as? String ?: ""

                        val amount = when (val amountValue = transactionMap["amount"]) {
                            is Number -> amountValue.toLong()
                            is String -> amountValue.toLongOrNull() ?: 0L
                            else -> 0L
                        }

                        Transaction(
                            id = transactionMap["id"] as? String ?: "",
                            userId = transactionMap["userId"] as? String ?: "",
                            type = type,
                            amount = amount,
                            date = date,
                            category = category,
                            description = description,
                            createdAt = transactionMap["createdAt"] as? Long ?: 0L
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                val sortedTransactions = transactionItems.sortedByDescending { it.date }

                _transactions.value = sortedTransactions
                calculateAmounts(sortedTransactions)

            } catch (e: Exception) {
                _transactions.value = emptyList()
                _incomeAmount.value = 0L
                _expenseAmount.value = 0L
                _error.value = "Gagal memuat transaksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTransaction(
        type: String,
        amount: Long,
        date: Long,
        description: String,
        category: String
    ) {
        if (amount <= 0L) {
            _error.value = "Nominal harus lebih dari 0"
            return
        }

        if (date <= 0L) {
            _error.value = "Tanggal tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.saveTransaction(
                    type = type,
                    amount = amount,
                    date = date,
                    category = category,
                    description = description
                )

                loadTransactions() // panggil default load

                if (_error.value == null) {
                    _error.value = null
                }

            } catch (e: Exception) {
                _error.value = "Gagal menyimpan transaksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateAmounts(transactions: List<Transaction>) {
        var income = 0L
        var expense = 0L

        transactions.forEach { transaction ->
            val isIncome = transaction.type.equals("INCOME", ignoreCase = true) ||
                    transaction.type.equals("Pemasukan", ignoreCase = true)

            if (isIncome) {
                income += transaction.amount
            } else {
                expense += transaction.amount
            }
        }

        _incomeAmount.value = income
        _expenseAmount.value = expense
    }

    fun getSummary(): Triple<List<Transaction>, Long, Long> {
        return Triple(_transactions.value, _incomeAmount.value.toLong(), _expenseAmount.value.toLong())
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * Mengelompokkan dan menjumlahkan pengeluaran berdasarkan description
     * Hanya untuk transaksi dengan type EXPENSE
     * @return List<Pair<description, totalAmount>>
     */
    fun getGroupedExpenses(): List<Pair<String, Long>> {
        return _transactions.value
            .filter { it.type.equals("EXPENSE", ignoreCase = true) }
            .groupBy { it.description }
            .map { (desc, list) ->
                desc to list.sumOf { it.amount }
            }
            .sortedByDescending { it.second }
    }

    suspend fun getAIInsightReport(
        totalPendapatan: Int,
        totalPengeluaran: Int,
        labaKotor: Int,
        pajakUmkm: Int,
        labaBersih: Int,
        rincianPendapatan: List<Pair<String, Int>>,
        rincianPengeluaran: List<Pair<String, Int>>
    ) {
        _isLoadingInsightAI.value = true
        _errorInsightAI.value = null
        try {
            val pendapatanList = rincianPendapatan.joinToString("\n") { "- ${it.first}: Rp ${String.format("%,d", it.second).replace(',', '.')}" }
            val pengeluaranList = rincianPengeluaran.joinToString("\n") { "- ${it.first}: Rp ${String.format("%,d", it.second).replace(',', '.')}" }
            val prompt = """
Berikut adalah ringkasan laporan keuangan UMKM:

- Total Pendapatan: Rp ${String.format("%,d", totalPendapatan).replace(',', '.')}
- Total Pengeluaran: Rp ${String.format("%,d", totalPengeluaran).replace(',', '.')}
- Laba Kotor: Rp ${String.format("%,d", labaKotor).replace(',', '.')}
- Pajak UMKM: Rp ${String.format("%,d", pajakUmkm).replace(',', '.')}
- Laba Bersih: Rp ${String.format("%,d", labaBersih).replace(',', '.')}

Rincian Pendapatan:
$pendapatanList

Rincian Pengeluaran:
$pengeluaranList

Analisis dan insight yang dibutuhkan:
1. Highlight utama (misal: tren naik/turun, perbandingan, dsb)
2. Saran perbaikan atau peluang
3. Narasi ringkas laporan keuangan
4. Deteksi anomali: Jika ada pola tidak wajar (misal: pengeluaran tiba-tiba melonjak, pendapatan turun drastis, dsb), jelaskan di field 'anomali'. Jika tidak ada anomali, kosongkan field 'anomali'.

Jawab dalam format JSON:
{
  \"highlight\": \"...\",
  \"saran\": \"...\",
  \"narasi\": \"...\",
  \"anomali\": \"...\" // Kosongkan jika tidak ada anomali
}

Catatan penting untuk bagian 'saran':
- Tuliskan dalam bentuk daftar poin, namun setiap poin langsung berupa kalimat saran tanpa membuat judul atau kata kunci tebal di awal.
- Hindari format seperti '**Judul:** penjelasan', cukup tulis saran langsung.
- Contoh yang benar: '1. Gunakan sistem pencatatan keuangan yang lebih detail dan terstruktur.'
- Contoh yang salah: '**Perbaiki sistem pencatatan keuangan:** Gunakan sistem pencatatan ...'

PENTING: Semua field pada JSON, termasuk 'saran', WAJIB selalu berupa string (bukan array/list). Jika ada beberapa saran, gabungkan menjadi satu string dengan pemisah baris baru (\n). Jangan pernah mengembalikan array/list di field manapun.
"""
            val response = generativeModel.generateContent(prompt)
            val responseText = response.text ?: throw Exception("No response from AI")
            val jsonStart = responseText.indexOf('{')
            val jsonEnd = responseText.lastIndexOf('}') + 1
            val jsonString = if (jsonStart >= 0 && jsonEnd > jsonStart) {
                responseText.substring(jsonStart, jsonEnd)
            } else {
                responseText
            }
            val gson = GsonBuilder().registerTypeAdapter(AIInsightResult::class.java, com.example.omsetku.data.AIPricingService.AIInsightResultAdapter()).create()
            val insight = gson.fromJson(jsonString, AIInsightResult::class.java)
            _insightAI.value = insight
        } catch (e: Exception) {
            _errorInsightAI.value = "Gagal mendapatkan insight AI: ${e.message}"
        } finally {
            _isLoadingInsightAI.value = false
        }
    }
}

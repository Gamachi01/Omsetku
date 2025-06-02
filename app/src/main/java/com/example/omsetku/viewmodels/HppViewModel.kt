package com.example.omsetku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.firebase.FirestoreRepository
import com.example.omsetku.ui.data.ProductItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class HppViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _products = MutableStateFlow<List<ProductItem>>(emptyList())
    val products: StateFlow<List<ProductItem>> = _products.asStateFlow()

    private val _selectedProduct = MutableStateFlow<ProductItem?>(null)
    val selectedProduct: StateFlow<ProductItem?> = _selectedProduct.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _totalBiaya = MutableStateFlow(0.0)
    val totalBiaya: StateFlow<Double> = _totalBiaya.asStateFlow()

    private val _estimasiTerjual = MutableStateFlow(0)
    val estimasiTerjual: StateFlow<Int> = _estimasiTerjual.asStateFlow()

    private val _persediaanAwal = MutableStateFlow("")
    val persediaanAwal: StateFlow<String> = _persediaanAwal.asStateFlow()

    private val _persediaanAkhir = MutableStateFlow("")
    val persediaanAkhir: StateFlow<String> = _persediaanAkhir.asStateFlow()

    private val _pembelianBersih = MutableStateFlow("")
    val pembelianBersih: StateFlow<String> = _pembelianBersih.asStateFlow()

    // List untuk menyimpan biaya operasional (nama dan jumlah)
    data class BiayaOperasional(val nama: String = "", val jumlah: String = "")
    private val _biayaOperasionalList = MutableStateFlow(listOf(BiayaOperasional()))
    val biayaOperasionalList: StateFlow<List<BiayaOperasional>> = _biayaOperasionalList.asStateFlow()

    // Data untuk perhitungan HPP berdasarkan bahan baku
    data class BahanBaku(
        val nama: String = "",
        val hargaPerUnit: String = "",
        val jumlahDigunakan: String = "",
        val satuan: String = "",
        val totalHarga: String = ""
    )
    private val _bahanBakuList = MutableStateFlow(listOf(BahanBaku()))
    val bahanBakuList: StateFlow<List<BahanBaku>> = _bahanBakuList.asStateFlow()

    // State untuk status penyimpanan
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // Tambahan state baru
    private val _targetPorsi = MutableStateFlow("")
    val targetPorsi: StateFlow<String> = _targetPorsi.asStateFlow()

    private val _marginProfit = MutableStateFlow("")
    val marginProfit: StateFlow<String> = _marginProfit.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * Memuat daftar produk dari Firestore
     */
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val productsList = repository.getUserProducts()

                // Convert dari Map ke ProductItem
                val productItems = productsList.map { productMap ->
                    val firestoreId = productMap["id"] as? String ?: ""
                    val id = firestoreId.hashCode()

                    ProductItem(
                        id = id,
                        name = productMap["name"] as? String ?: "",
                        price = (productMap["price"] as? Number)?.toInt() ?: 0,
                        imageRes = com.example.omsetku.R.drawable.logo,  // Default image
                        quantity = 0
                    )
                }

                _products.value = productItems
            } catch (e: Exception) {
                _error.value = "Gagal memuat produk: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectProduct(product: ProductItem) {
        _selectedProduct.value = product
    }

    fun updatePersediaanAwal(value: String) {
        _persediaanAwal.value = value
    }

    fun updatePersediaanAkhir(value: String) {
        _persediaanAkhir.value = value
    }

    fun updatePembelianBersih(value: String) {
        _pembelianBersih.value = value
    }

    fun updateEstimasiTerjual(value: String) {
        _estimasiTerjual.value = value.toIntOrNull() ?: 0
    }

    fun addBiayaOperasional() {
        _biayaOperasionalList.value = _biayaOperasionalList.value + BiayaOperasional()
    }

    fun removeBiayaOperasional(index: Int) {
        if (index >= 0 && index < _biayaOperasionalList.value.size) {
            _biayaOperasionalList.value = _biayaOperasionalList.value.toMutableList().apply {
                removeAt(index)
            }
        }
    }

    fun updateBiayaOperasionalNama(index: Int, nama: String) {
        if (index >= 0 && index < _biayaOperasionalList.value.size) {
            val biayaList = _biayaOperasionalList.value.toMutableList()
            biayaList[index] = biayaList[index].copy(nama = nama)
            _biayaOperasionalList.value = biayaList
        }
    }

    fun updateBiayaOperasionalJumlah(index: Int, jumlah: String) {
        if (index >= 0 && index < _biayaOperasionalList.value.size) {
            val biayaList = _biayaOperasionalList.value.toMutableList()
            biayaList[index] = biayaList[index].copy(jumlah = jumlah)
            _biayaOperasionalList.value = biayaList
        }
    }

    // Fungsi untuk bahan baku
    fun addBahanBaku() {
        _bahanBakuList.value = _bahanBakuList.value + BahanBaku()
    }

    fun removeBahanBaku(index: Int) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            _bahanBakuList.value = _bahanBakuList.value.toMutableList().apply {
                removeAt(index)
            }
        }
    }

    fun updateBahanBakuNama(index: Int, nama: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(nama = nama)
            _bahanBakuList.value = bahanList
        }
    }

    fun updateBahanBakuHarga(index: Int, harga: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(hargaPerUnit = harga)
            _bahanBakuList.value = bahanList
        }
    }

    fun updateBahanBakuJumlah(index: Int, jumlah: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(jumlahDigunakan = jumlah)
            _bahanBakuList.value = bahanList
        }
    }

    fun updateBahanBakuTotalHarga(index: Int, total: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(totalHarga = total)
            _bahanBakuList.value = bahanList
        }
    }

    fun updateBahanBakuSatuan(index: Int, satuan: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(satuan = satuan)
            _bahanBakuList.value = bahanList
        }
    }

    // Fungsi baru untuk biaya operasional (harga)
    fun updateBiayaOperasionalHarga(index: Int, harga: String) {
        if (index >= 0 && index < _biayaOperasionalList.value.size) {
            val biayaList = _biayaOperasionalList.value.toMutableList()
            biayaList[index] = biayaList[index].copy(jumlah = harga)
            _biayaOperasionalList.value = biayaList
        }
    }

    fun hitungHpp(): Double {
        // Hitung total biaya bahan baku
        val totalBiayaBahanBaku = _bahanBakuList.value.sumOf { bahan ->
            val hargaPerUnit = bahan.hargaPerUnit.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
            val jumlahDigunakan = bahan.jumlahDigunakan.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
            hargaPerUnit * jumlahDigunakan
        }
        // Hitung total biaya operasional
        val totalBiayaOperasional = _biayaOperasionalList.value.sumOf {
            it.jumlah.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
        }
        // Hitung target porsi
        val targetPorsiValue = _targetPorsi.value.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
        // Hitung margin profit
        val marginProfitValue = _marginProfit.value.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0

        // Jika target porsi tidak valid, return 0
        if (targetPorsiValue <= 0) return 0.0

        // Hitung HPP dasar per porsi
        val hppDasarPerPorsi = (totalBiayaBahanBaku + totalBiayaOperasional) / targetPorsiValue
        // Hitung HPP final dengan margin profit
        val hppFinal = hppDasarPerPorsi * (1 + (marginProfitValue / 100.0))

        // Simpan total biaya (bahan baku + operasional) jika diperlukan di tempat lain
        _totalBiaya.value = totalBiayaBahanBaku + totalBiayaOperasional

        return hppFinal
    }

    /**
     * Menyimpan hasil perhitungan HPP ke Firestore
     */
    fun simpanHpp() {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            try {
                val selectedProduct = _selectedProduct.value ?: throw Exception("Produk belum dipilih")
                val hpp = hitungHpp()

                // Data yang akan disimpan
                val hppData = mapOf(
                    "productId" to selectedProduct.id.toString(),
                    "productName" to selectedProduct.name,
                    "hppValue" to hpp,
                    "totalBiaya" to _totalBiaya.value,
                    "estimasiTerjual" to _estimasiTerjual.value,
                    "metode" to "Bahan Baku",
                    "tanggalHitung" to Date(),
                    "persediaanAwal" to _persediaanAwal.value,
                    "persediaanAkhir" to _persediaanAkhir.value,
                    "pembelianBersih" to _pembelianBersih.value,
                    "biayaOperasional" to _biayaOperasionalList.value.map {
                        mapOf(
                            "nama" to it.nama,
                            "jumlah" to it.jumlah
                        )
                    },
                    "bahanBaku" to _bahanBakuList.value.map {
                        mapOf(
                            "nama" to it.nama,
                            "hargaPerUnit" to it.hargaPerUnit,
                            "jumlahDigunakan" to it.jumlahDigunakan,
                            "satuan" to it.satuan,
                            "totalHarga" to it.totalHarga
                        )
                    }
                )

                // Simpan ke Firestore
                repository.saveHpp(hppData)
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan HPP: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Update fungsi untuk state baru
    fun updateTargetPorsi(value: String) {
        _targetPorsi.value = value
    }

    fun updateMarginProfit(value: String) {
        _marginProfit.value = value
    }
}
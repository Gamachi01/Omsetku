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

    // Tambahkan mapping ID
    private val productIdMap = mutableMapOf<Int, String>()

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
    data class BiayaOperasional(
        val nama: String = "",
        val hargaBeli: String = "",
        val jumlahBeli: String = "",
        val satuan: String = "",
        val terpakai: String = ""
    )
    private val _biayaOperasionalList = MutableStateFlow(listOf(BiayaOperasional()))
    val biayaOperasionalList: StateFlow<List<BiayaOperasional>> = _biayaOperasionalList.asStateFlow()

    // Data untuk perhitungan HPP berdasarkan bahan baku
    data class BahanBaku(
        val nama: String = "",
        val hargaBeli: String = "",
        val jumlahBeli: String = "",
        val satuan: String = "",
        val terpakai: String = ""
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

                    // Simpan mapping ID
                    productIdMap[id] = firestoreId

                    ProductItem(
                        id = id,
                        firestoreId = firestoreId,
                        name = productMap["name"] as? String ?: "",
                        price = (productMap["price"] as? Number)?.toInt() ?: 0,
                        imageRes = com.example.omsetku.R.drawable.logo,  // Default image
                        imageUrl = productMap["imageUrl"] as? String ?: "",  // Tambah imageUrl
                        quantity = 0,
                        hpp = (productMap["hpp"] as? Number)?.toDouble() ?: 0.0  // Tambah hpp
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

    fun updateBiayaOperasionalHargaBeli(index: Int, hargaBeli: String) {
        if (index >= 0 && index < _biayaOperasionalList.value.size) {
            val biayaList = _biayaOperasionalList.value.toMutableList()
            biayaList[index] = biayaList[index].copy(hargaBeli = hargaBeli)
            _biayaOperasionalList.value = biayaList
        }
    }

    fun updateBiayaOperasionalJumlahBeli(index: Int, jumlahBeli: String) {
        if (index >= 0 && index < _biayaOperasionalList.value.size) {
            val biayaList = _biayaOperasionalList.value.toMutableList()
            biayaList[index] = biayaList[index].copy(jumlahBeli = jumlahBeli)
            _biayaOperasionalList.value = biayaList
        }
    }

    fun updateBiayaOperasionalSatuan(index: Int, satuan: String) {
        if (index >= 0 && index < _biayaOperasionalList.value.size) {
            val biayaList = _biayaOperasionalList.value.toMutableList()
            biayaList[index] = biayaList[index].copy(satuan = satuan)
            _biayaOperasionalList.value = biayaList
        }
    }

    fun updateBiayaOperasionalTerpakai(index: Int, terpakai: String) {
        if (index >= 0 && index < _biayaOperasionalList.value.size) {
            val biayaList = _biayaOperasionalList.value.toMutableList()
            biayaList[index] = biayaList[index].copy(terpakai = terpakai)
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

    fun updateBahanBakuHargaBeli(index: Int, hargaBeli: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(hargaBeli = hargaBeli)
            _bahanBakuList.value = bahanList
        }
    }

    fun updateBahanBakuJumlahBeli(index: Int, jumlahBeli: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(jumlahBeli = jumlahBeli)
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

    fun updateBahanBakuTerpakai(index: Int, terpakai: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(terpakai = terpakai)
            _bahanBakuList.value = bahanList
        }
    }

    fun hitungHpp(): Double {
        // Hitung total biaya bahan baku
        val totalBiayaBahanBaku = _bahanBakuList.value.sumOf { bahan ->
            val hargaPerUnit = bahan.hargaBeli.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
            val jumlahDigunakan = bahan.jumlahBeli.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
            hargaPerUnit * jumlahDigunakan
        }
        // Hitung total biaya operasional
        val totalBiayaOperasional = _biayaOperasionalList.value.sumOf {
            it.jumlahBeli.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
        }
        // Hitung target porsi
        val targetPorsiValue = _targetPorsi.value.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0

        // Jika target porsi tidak valid, return 0
        if (targetPorsiValue <= 0) return 0.0

        // Hitung HPP per porsi (biaya pokok produksi)
        val hppPerPorsi = (totalBiayaBahanBaku + totalBiayaOperasional) / targetPorsiValue

        // Simpan total biaya (bahan baku + operasional) jika diperlukan di tempat lain
        _totalBiaya.value = totalBiayaBahanBaku + totalBiayaOperasional

        return hppPerPorsi
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
                            "hargaBeli" to it.hargaBeli,
                            "jumlahBeli" to it.jumlahBeli,
                            "satuan" to it.satuan,
                            "terpakai" to it.terpakai
                        )
                    },
                    "bahanBaku" to _bahanBakuList.value.map {
                        mapOf(
                            "nama" to it.nama,
                            "hargaBeli" to it.hargaBeli,
                            "jumlahBeli" to it.jumlahBeli,
                            "satuan" to it.satuan,
                            "terpakai" to it.terpakai
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

    /**
     * Menyimpan HPP ke database
     */
    fun saveHpp(productId: Int, hppValue: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Dapatkan Firestore ID dari mapping
                val firestoreId = productIdMap[productId] ?: run {
                    _error.value = "ID produk tidak valid"
                    return@launch
                }

                // Update di Firestore
                val success = repository.updateProduct(
                    productId = firestoreId,
                    hppValue = hppValue
                )

                if (success) {
                    // Update di state lokal
                    val updatedProducts = _products.value.map {
                        if (it.id == productId) {
                            it.copy(hpp = hppValue)
                        } else {
                            it
                        }
                    }

                    _products.value = updatedProducts
                } else {
                    _error.value = "Gagal menyimpan HPP di database"
                }
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan HPP: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
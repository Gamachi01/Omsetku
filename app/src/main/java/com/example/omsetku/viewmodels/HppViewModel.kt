package com.example.omsetku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.firebase.FirestoreRepository
import com.example.omsetku.ui.data.ProductItem
import com.example.omsetku.ui.screen.HppTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        val totalHarga: String = ""
    )
    private val _bahanBakuList = MutableStateFlow(listOf(BahanBaku()))
    val bahanBakuList: StateFlow<List<BahanBaku>> = _bahanBakuList.asStateFlow()
    
    // State untuk tab yang aktif
    private val _activeTab = MutableStateFlow(HppTab.STOK)
    val activeTab: StateFlow<HppTab> = _activeTab.asStateFlow()
    
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
    
    fun updateBahanBakuHargaPerUnit(index: Int, harga: String) {
        if (index >= 0 && index < _bahanBakuList.value.size) {
            val bahanList = _bahanBakuList.value.toMutableList()
            bahanList[index] = bahanList[index].copy(hargaPerUnit = harga)
            _bahanBakuList.value = bahanList
        }
    }
    
    fun updateBahanBakuJumlahDigunakan(index: Int, jumlah: String) {
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
    
    fun setActiveTab(tab: HppTab) {
        _activeTab.value = tab
    }
    
    fun hitungHpp(): Double {
        // Konversi input ke angka
        val persediaanAwalValue = _persediaanAwal.value.replace(".", "").toDoubleOrNull() ?: 0.0
        val persediaanAkhirValue = _persediaanAkhir.value.replace(".", "").toDoubleOrNull() ?: 0.0
        val pembelianBersihValue = _pembelianBersih.value.replace(".", "").toDoubleOrNull() ?: 0.0
        
        // Hitung total biaya operasional
        val totalBiayaOperasional = _biayaOperasionalList.value.sumOf {
            it.jumlah.replace(".", "").toDoubleOrNull() ?: 0.0
        }
        
        // Hitung total biaya berdasarkan tab yang aktif
        val totalCost = when (_activeTab.value) {
            HppTab.STOK -> {
                // Rumus: Persediaan Awal + Pembelian Bersih - Persediaan Akhir
                persediaanAwalValue + pembelianBersihValue - persediaanAkhirValue
            }
            HppTab.BAHAN_BAKU -> {
                // Rumus: (Bahan Baku Terpakai × Harga per Unit Bahan Baku) / Jumlah Terjual
                val totalBahanBaku = _bahanBakuList.value.sumOf { bahan ->
                    val hargaPerUnit = bahan.hargaPerUnit.replace(".", "").toDoubleOrNull() ?: 0.0
                    val jumlahDigunakan = bahan.jumlahDigunakan.replace(".", "").toDoubleOrNull() ?: 0.0
                    hargaPerUnit * jumlahDigunakan
                }
                totalBahanBaku
            }
        }
        
        // Rumus HPP: (Total Cost + Biaya Operasional) / Estimasi Terjual Bulanan
        val estimasiTerjualValue = _estimasiTerjual.value.toDouble()
        val hpp = if (estimasiTerjualValue > 0) {
            (totalCost + totalBiayaOperasional) / estimasiTerjualValue
        } else {
            0.0
        }
        
        _totalBiaya.value = totalCost + totalBiayaOperasional
        return hpp
    }
    
    fun clearError() {
        _error.value = null
    }
} 
# AI Implementation Guide - Omsetku App

## Overview
Implementasi AI Product Pricing menggunakan Google Gemini 2.5 untuk memberikan rekomendasi harga yang cerdas berdasarkan analisis HPP, pasar, dan tren kompetitor.

## Fitur AI yang Diimplementasikan

### 1. AI Product Pricing Analysis
- **Lokasi**: Dialog hasil perhitungan HPP
- **Fungsi**: Menganalisis data HPP dan memberikan rekomendasi harga optimal
- **Input**: Data HPP, bahan baku, biaya operasional, margin profit
- **Output**: Rekomendasi harga dengan confidence level, analisis pasar, dan saran

## Setup dan Konfigurasi

### 1. Dependencies yang Ditambahkan
```kotlin
// Google AI SDK for Gemini
implementation("com.google.ai.client.generativeai:generativeai:0.1.2")

// Retrofit for API calls (if needed for external market data)
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

### 2. Konfigurasi API Key
1. Dapatkan API key dari [Google AI Studio](https://aistudio.google.com/)
2. Tambahkan ke file `local.properties`:
```properties
GEMINI_API_KEY=your_actual_api_key_here
```

### 3. Build Configuration
```kotlin
buildConfigField("String", "GEMINI_API_KEY", "\"${properties["GEMINI_API_KEY"] ?: "YOUR_GEMINI_API_KEY"}\"")
```

## Struktur Implementasi

### 1. AIPricingService (`app/src/main/java/com/example/omsetku/data/AIPricingService.kt`)
- **Fungsi**: Service utama untuk interaksi dengan Gemini AI
- **Fitur**:
  - Analisis pricing berdasarkan data HPP
  - Fallback mechanism jika AI tidak tersedia
  - Market insights untuk produk tertentu

### 2. HppViewModel Enhancement
- **State baru**:
  - `aiPricingRecommendation`: Rekomendasi dari AI
  - `isAnalyzingAI`: Status loading AI
  - `aiError`: Error handling untuk AI
- **Method baru**:
  - `getAIPricingRecommendation()`: Memanggil AI service
  - `clearAIRecommendation()`: Clear AI state

### 3. AIPricingSection Component (`app/src/main/java/com/example/omsetku/ui/components/AIPricingSection.kt`)
- **UI Component** untuk menampilkan hasil analisis AI
- **Fitur**:
  - Confidence indicator dengan progress bar
  - Expandable detail sections
  - Color-coded information cards
  - Loading state

## Cara Penggunaan

### 1. Di Screen HPP
1. Isi data bahan baku dan biaya operasional
2. Set target porsi dan margin profit
3. Klik "Hitung HPP"
4. Di dialog hasil, klik "Dapatkan Rekomendasi AI"
5. AI akan menganalisis dan memberikan rekomendasi

### 2. Output AI
- **Harga Rekomendasi**: Harga optimal berdasarkan analisis
- **Tingkat Kepercayaan**: Persentase keakuratan rekomendasi
- **Analisis Pasar**: Insight tentang kondisi pasar
- **Keunggulan Kompetitif**: Analisis daya saing
- **Faktor Risiko**: Potensi risiko yang perlu diperhatikan
- **Saran**: Rekomendasi tindakan selanjutnya

## Prompt Engineering

### 1. Pricing Analysis Prompt
```kotlin
private fun buildPricingAnalysisPrompt(hppData: HPPData): String {
    return """
    Analisis Harga Produk UMKM: ${hppData.productName}
    
    Data HPP:
    - HPP per porsi: Rp ${hppData.hppPerPorsi.toInt()}
    - Margin profit yang diinginkan: ${hppData.marginProfit}%
    - Target porsi: ${hppData.targetPorsi}
    
    Berdasarkan data di atas, berikan analisis pricing yang komprehensif untuk produk UMKM ini.
    
    Berikan respons dalam format JSON berikut:
    {
        "recommendedPrice": 25000,
        "confidence": 0.85,
        "reasoning": "Analisis berdasarkan HPP, margin, dan tren pasar",
        "marketAnalysis": "Produk ini berada di kisaran harga menengah",
        "competitiveAdvantage": "Harga kompetitif dengan kualitas premium",
        "riskFactors": ["Fluktuasi harga bahan baku"],
        "suggestions": ["Monitor harga kompetitor"]
    }
    """
}
```

### 2. Response Parsing
- Menggunakan Gson untuk parse JSON response
- Fallback mechanism jika parsing gagal
- Error handling yang robust

## Error Handling

### 1. API Key Issues
- Jika API key tidak valid, akan menggunakan fallback calculation
- Pesan error yang informatif untuk user

### 2. Network Issues
- Timeout handling
- Retry mechanism (bisa ditambahkan)
- Offline fallback

### 3. AI Service Issues
- Graceful degradation ke perhitungan manual
- User-friendly error messages

## Keunggulan Implementasi

### 1. User Experience
- **Seamless Integration**: AI terintegrasi dengan flow HPP yang sudah ada
- **Progressive Disclosure**: Detail AI bisa di-expand/collapse
- **Visual Feedback**: Loading state dan confidence indicator
- **Fallback Mechanism**: Tetap berfungsi meski AI tidak tersedia

### 2. Technical Excellence
- **Clean Architecture**: Separation of concerns yang baik
- **Dependency Injection**: Menggunakan Hilt
- **State Management**: Reactive UI dengan StateFlow
- **Error Handling**: Comprehensive error handling

### 3. Business Value
- **Data-Driven Pricing**: Rekomendasi berdasarkan analisis mendalam
- **Market Intelligence**: Insight pasar yang relevan
- **Risk Assessment**: Identifikasi risiko pricing
- **Competitive Analysis**: Analisis daya saing

## Future Enhancements

### 1. Advanced Features
- **Historical Data Analysis**: Analisis tren harga historis
- **Competitor Price Monitoring**: Integrasi dengan data kompetitor
- **Seasonal Pricing**: Rekomendasi harga musiman
- **Demand Forecasting**: Prediksi permintaan

### 2. Performance Optimizations
- **Caching**: Cache hasil analisis AI
- **Batch Processing**: Analisis multiple produk sekaligus
- **Offline AI**: Model AI lokal untuk analisis offline

### 3. Additional AI Services
- **Inventory Optimization**: Rekomendasi stok optimal
- **Customer Segmentation**: Analisis segmentasi pelanggan
- **Marketing Recommendations**: Saran strategi marketing

## Troubleshooting

### 1. API Key Issues
```bash
# Pastikan API key sudah ditambahkan di local.properties
GEMINI_API_KEY=your_actual_api_key_here
```

### 2. Build Issues
```bash
# Clean dan rebuild project
./gradlew clean
./gradlew build
```

### 3. Runtime Issues
- Check logcat untuk error details
- Verify internet connection
- Ensure API key is valid

## Monitoring dan Analytics

### 1. Usage Tracking
- Track berapa kali AI digunakan
- Monitor accuracy of recommendations
- Analyze user feedback

### 2. Performance Monitoring
- Response time dari AI service
- Error rates
- User satisfaction metrics

## Security Considerations

### 1. API Key Security
- API key disimpan di local.properties (tidak di version control)
- BuildConfig untuk runtime access
- Consider API key rotation

### 2. Data Privacy
- Data HPP tidak dikirim ke AI service secara permanen
- Temporary data processing only
- No sensitive business data stored externally

## Conclusion

Implementasi AI Product Pricing ini memberikan nilai tambah signifikan untuk aplikasi Omsetku dengan:

1. **Smart Pricing**: Rekomendasi harga yang lebih akurat dan data-driven
2. **Market Intelligence**: Insight pasar yang berharga untuk UMKM
3. **User Experience**: Interface yang intuitif dan informatif
4. **Scalability**: Arsitektur yang siap untuk enhancement masa depan

AI integration ini menjadikan Omsetku sebagai aplikasi POS yang lebih cerdas dan kompetitif di pasar. 
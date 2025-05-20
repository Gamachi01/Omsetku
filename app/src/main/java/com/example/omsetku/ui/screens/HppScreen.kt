@Composable
fun HppScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(PrimaryVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Hitung HPP",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        Text(
            text = "HPP (Harga Pokok Penjualan) adalah total biaya yang dikeluarkan untuk memproduksi atau memperoleh produk yang dijual. HPP mencakup biaya bahan baku, tenaga kerja langsung, dan overhead produksi.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Form Input
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
// ... existing code ... 
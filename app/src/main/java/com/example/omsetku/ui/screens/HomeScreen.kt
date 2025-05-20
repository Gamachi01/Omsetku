@Composable
fun HomeScreen(
    onNavigateToCashier: () -> Unit,
    onNavigateToTransaction: () -> Unit,
    onNavigateToHpp: () -> Unit,
    onNavigateToReport: () -> Unit
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
                text = "Omsetku",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
} 
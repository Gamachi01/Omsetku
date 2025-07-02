package com.example.omsetku.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.data.AIPricingService
import com.example.omsetku.ui.theme.*

@Composable
fun AIPricingSection(
    aiRecommendation: AIPricingService.PricingRecommendation?,
    isAnalyzing: Boolean,
    onGetAIRecommendation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryLight
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header dengan icon AI
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI Analysis",
                    tint = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analisis Harga AI",
                    fontFamily = Poppins,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isAnalyzing) {
                // Loading state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Menganalisis dengan AI...",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = PrimaryColor
                    )
                }
            } else if (aiRecommendation != null) {
                // AI Recommendation content
                AIPricingContent(aiRecommendation = aiRecommendation)
            } else {
                // Get AI recommendation button
                Button(
                    onClick = onGetAIRecommendation,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dapatkan Rekomendasi AI",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AIPricingContent(
    aiRecommendation: AIPricingService.PricingRecommendation,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Confidence indicator REMOVED
        Spacer(modifier = Modifier.height(12.dp))

        // Recommended Price
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = IncomeColor
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "Harga Rekomendasi AI",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "Rp ${aiRecommendation.recommendedPrice}",
                    fontFamily = Poppins,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Expandable sections
        var showDetails by remember { mutableStateOf(false) }

        Button(
            onClick = { showDetails = !showDetails },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryVariant
            ),
            border = BorderStroke(1.dp, PrimaryColor)
        ) {
            Text(
                text = if (showDetails) "Sembunyikan Detail" else "Lihat Detail Analisis",
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = PrimaryColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(16.dp)
            )
        }

        AnimatedVisibility(
            visible = showDetails,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                // Reasoning
                if (!aiRecommendation.reasoning.isNullOrBlank()) {
                    DetailSection(
                        title = "Alasan Rekomendasi",
                        content = aiRecommendation.reasoning ?: "",
                        icon = Icons.Default.Lightbulb,
                        color = PrimaryLight
                    )
                }

                // Market Analysis
                if (!aiRecommendation.marketAnalysis.isNullOrBlank()) {
                    DetailSection(
                        title = "Analisis Pasar",
                        content = aiRecommendation.marketAnalysis ?: "",
                        icon = Icons.Default.TrendingUp,
                        color = PrimaryVariant
                    )
                }

                // Competitive Advantage
                if (!aiRecommendation.competitiveAdvantage.isNullOrBlank()) {
                    DetailSection(
                        title = "Keunggulan Kompetitif",
                        content = aiRecommendation.competitiveAdvantage ?: "",
                        icon = Icons.Default.Star,
                        color = ExpenseLightColor
                    )
                }

                // Risk Factors
                if ((aiRecommendation.riskFactors ?: emptyList()).isNotEmpty()) {
                    DetailSection(
                        title = "Faktor Risiko",
                        content = (aiRecommendation.riskFactors ?: emptyList()).joinToString("\n• ", "• "),
                        icon = Icons.Default.Warning,
                        color = ExpenseColor.copy(alpha = 0.08f)
                    )
                }

                // Suggestions
                if ((aiRecommendation.suggestions ?: emptyList()).isNotEmpty()) {
                    DetailSection(
                        title = "Saran",
                        content = (aiRecommendation.suggestions ?: emptyList()).joinToString("\n• ", "• "),
                        icon = Icons.Default.TipsAndUpdates,
                        color = PrimaryLight
                    )
                }
                
                // HPP Optimization
                aiRecommendation.hppOptimization?.let { hppOpt ->
                    HPPOptimizationSection(hppOptimization = hppOpt)
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = Color.DarkGray,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun HPPOptimizationSection(
    hppOptimization: AIPricingService.HPPOptimization,
    modifier: Modifier = Modifier
) {
    val optimizations = hppOptimization.optimizations ?: emptyList()
    if (optimizations.isEmpty()) return
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryLight
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Optimasi HPP",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            optimizations.forEach { optimization ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceColor
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${optimization.nama}: ${optimization.saran}",
                            fontFamily = Poppins,
                            fontSize = 10.sp,
                            color = DarkText
                        )
                    }
                }
            }
        }
    }
} 
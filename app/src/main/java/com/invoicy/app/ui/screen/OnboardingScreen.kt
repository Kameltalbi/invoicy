package com.invoicy.app.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.invoicy.app.R
import kotlinx.coroutines.launch

/**
 * Écran d'onboarding avec 3 slides
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(
                page = page,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Text("Précédent")
                }
            } else {
                TextButton(onClick = onComplete) {
                    Text(stringResource(R.string.onboarding_skip))
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = MaterialTheme.shapes.small,
                        color = if (index == pagerState.currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        }
                    ) {}
                }
            }
            
            if (pagerState.currentPage < 2) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Text(stringResource(R.string.onboarding_next))
                }
            } else {
                Button(onClick = onComplete) {
                    Text(stringResource(R.string.onboarding_start))
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(
    page: Int,
    modifier: Modifier = Modifier
) {
    val (icon, title, description) = when (page) {
        0 -> Triple(
            Icons.Default.Receipt,
            stringResource(R.string.onboarding_title_1),
            stringResource(R.string.onboarding_desc_1)
        )
        1 -> Triple(
            Icons.Default.People,
            stringResource(R.string.onboarding_title_2),
            stringResource(R.string.onboarding_desc_2)
        )
        else -> Triple(
            Icons.Default.TrendingUp,
            stringResource(R.string.onboarding_title_3),
            stringResource(R.string.onboarding_desc_3)
        )
    }
    
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

package com.example.fiend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(viewModel: PlayerViewModel = viewModel()) {
    val recommendations by viewModel.recommendations.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()

    val ytBlack = Color(0xFF030303)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFAAAAAA)
    
    Scaffold(
        containerColor = ytBlack,
        bottomBar = {
            Column {
                // Persistent Mini Player above Bottom Nav
                currentSong?.let { song ->
                    PlayerBottomBar(song)
                }
                // Mock Bottom Navigation
                BottomNavigationBar()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Music",
                    color = textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("🔍", color = textPrimary, fontSize = 20.sp)
                    Text("👤", color = textPrimary, fontSize = 20.sp)
                }
            }
            
            // Category Pills
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                val categories = listOf("Energise", "Workout", "Relax", "Commute", "Focus")
                items(categories) { category ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF212121))
                            .clickable { }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(category, color = textPrimary, fontSize = 14.sp)
                    }
                }
            }

            if (recommendations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Split recommendations into mock sections
                    item {
                        CarouselSection(
                            title = "Mixed for you", 
                            items = recommendations.take(5), 
                            onItemClick = { viewModel.playSong(it) }
                        )
                    }
                    item {
                        CarouselSection(
                            title = "Listen again", 
                            items = recommendations.drop(5).take(5), 
                            onItemClick = { viewModel.playSong(it) }
                        )
                    }
                    item {
                        CarouselSection(
                            title = "New releases", 
                            items = recommendations.drop(10), 
                            onItemClick = { viewModel.playSong(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CarouselSection(title: String, items: List<MusicItem>, onItemClick: (MusicItem) -> Unit) {
    if (items.isEmpty()) return
    
    Column {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, bottom = 12.dp)
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                MusicCarouselItem(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
fun MusicCarouselItem(item: MusicItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
    ) {
        // Thumbnail (Square for YT Music style)
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF212121))
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )
        Text(
            text = item.artist,
            color = Color(0xFFAAAAAA),
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun PlayerBottomBar(song: MusicItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF212121))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini thumbnail
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = song.artist,
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            
            // Mock Controls
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("▶", color = Color.White, fontSize = 20.sp)
                Text("⏭", color = Color.White, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF030303))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem("🏠", "Home", true)
        BottomNavItem("🎵", "Samples", false)
        BottomNavItem("🧭", "Explore", false)
        BottomNavItem("📚", "Library", false)
    }
}

@Composable
fun BottomNavItem(icon: String, label: String, isSelected: Boolean) {
    val color = if (isSelected) Color.White else Color(0xFFAAAAAA)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { }
    ) {
        Text(icon, fontSize = 20.sp, color = color)
        Text(label, fontSize = 10.sp, color = color, modifier = Modifier.padding(top = 2.dp))
    }
}

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

    val appleRed = Color(0xFFFA243C)
    
    Scaffold(
        bottomBar = {
            currentSong?.let { song ->
                PlayerBottomBar(song)
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Listen Now",
                color = Color.Black,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 20.dp, top = 40.dp, bottom = 24.dp)
            )
            
            Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))
            
            Spacer(modifier = Modifier.height(16.dp))

            if (recommendations.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp),
                    color = appleRed
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(recommendations) { item ->
                        MusicItemRow(item = item, onClick = { viewModel.playSong(item) })
                    }
                }
            }
        }
    }
}

@Composable
fun MusicItemRow(item: MusicItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.artist,
                color = Color.Gray,
                fontSize = 15.sp
            )
        }
        
        // Ellipsis Icon Mock
        Text("...", color = Color.Gray, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
    }
}

@Composable
fun PlayerBottomBar(song: MusicItem) {
    val appleRed = Color(0xFFFA243C)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF2F2F7)) // Soft light grey like iOS frosted glass
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini thumbnail
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.LightGray)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Text(
                    text = song.artist,
                    color = appleRed,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
            
            // Mock Play button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text("▶", color = Color.Black, fontSize = 20.sp)
            }
        }
    }
}

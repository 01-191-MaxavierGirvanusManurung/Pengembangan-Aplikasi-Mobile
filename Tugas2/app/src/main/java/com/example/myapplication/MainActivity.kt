package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Data class to represent a news article
data class News(val id: Int, val title: String, val category: String)

// ViewModel to manage the news feed
class NewsViewModel : ViewModel() {

    private val _newsFlow = flow {
        var count = 0
        while (true) {
            val category = when (count % 3) {
                0 -> "Sports"
                1 -> "Technology"
                else -> "Politics"
            }
            emit(News(count, "News Title #$count", category))
            count++
            delay(2000)
        }
    }

    private val _readNewsCount = MutableStateFlow(0)
    val readNewsCount: StateFlow<Int> = _readNewsCount.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val newsFeed: Flow<List<String>> = combine(
        _newsFlow,
        selectedCategory
    ) { news, category ->
        if (category == "All" || news.category == category) {
            news
        } else {
            null
        }
    }.filterNotNull()
        .map { news -> "Transform: ${news.title} [${news.category}]" }
        .buffer(10)
        .scan(emptyList<String>()) { acc, value -> (acc + value).takeLast(10) }


    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun fetchNewsDetail(news: String) {
        viewModelScope.launch {
            // Simulate network delay
            delay(1000)
            println("Details for $news fetched")
            _readNewsCount.value++
        }
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                NewsFeedScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(viewModel: NewsViewModel) {
    val newsItems by viewModel.newsFeed.collectAsState(initial = emptyList())
    val readCount by viewModel.readNewsCount.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Feed Simulator") },
                actions = {
                    Text(text = "Read: $readCount", modifier = Modifier.padding(end = 16.dp))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoryFilter(selectedCategory) { category ->
                viewModel.setCategory(category)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(newsItems.size) { index ->
                    val newsItem = newsItems[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.fetchNewsDetail(newsItem) }
                    ) {
                        Text(
                            text = newsItem,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilter(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("All", "Sports", "Technology", "Politics")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Button(
                onClick = { onCategorySelected(category) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) Color.White else Color.Black
                )
            ) {
                Text(category)
            }
        }
    }
}

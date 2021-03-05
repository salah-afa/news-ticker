package com.salahafaghani.newsticker

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class MainViewModel: ViewModel() {

    private val maxDisplayedNewsLength = 200

    private val space = "                    "
    private val news = mutableListOf<Char>()
    private val suspendNews = mutableListOf<Char>()
    private var index = 0
    private val allNews = mutableListOf<String>()
    private var displayedNews = ""

    private val newsEventChannel = Channel<NewsEvent>()
    val newsEvent = newsEventChannel.receiveAsFlow()

    private val semaphore = Semaphore(1)

    sealed class NewsEvent {
        data class UpdateNewsTicker(val text: String) : NewsEvent()
    }

    init {
        viewModelScope.launch {
            while (viewModelScope.isActive) {
                delay(200)
                semaphore.withPermit {
                    newsEventChannel.send(NewsEvent.UpdateNewsTicker(moveNext()))
                }
            }
        }

    }

    private fun moveNext(): String {
        if(index >= news.size) {
            index = 0
        }
        displayedNews = if (suspendNews.isNotEmpty() &&
            (news.isEmpty() || (news.isNotEmpty() && index == 0))) {
            news.clear()
            news.addAll(suspendNews)
            suspendNews.clear()
            displayedNews + news[index++]
        } else {
            if (news.isNotEmpty()) {
                displayedNews + news[index++]
            } else {
                "$displayedNews "
            }
        }
        displayedNews = displayedNews.takeLast(maxDisplayedNewsLength)
        return displayedNews
    }

    fun setNews(news: List<String>) {
        viewModelScope.launch {
            semaphore.withPermit {
                allNews.clear()
                allNews.addAll(news)

                suspendNews.clear()
                suspendNews.addAll(allNews.joinToString(separator = space, postfix = space).toList())
            }
        }
    }

    fun addNews(news: String) {
        viewModelScope.launch {
            semaphore.withPermit {
                allNews.add(news)
                suspendNews.clear()
                suspendNews.addAll(
                    allNews.joinToString(separator = space, postfix = space).toList()
                )
            }
        }
    }

    fun deleteFirstNews() {
        viewModelScope.launch {
            semaphore.withPermit {
                if (allNews.isNotEmpty()) {
                    allNews.removeAt(0)
                    suspendNews.clear()
                    suspendNews.addAll(allNews.joinToString(separator = space, postfix = space).toList())
                }
            }
        }
    }

    fun getNumberOfNews() = allNews.size
}
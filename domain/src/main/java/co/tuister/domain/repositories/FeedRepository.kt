package co.tuister.domain.repositories

import co.tuister.domain.entities.FeedCard

interface FeedRepository {
    suspend fun getFeed(): List<FeedCard>
}

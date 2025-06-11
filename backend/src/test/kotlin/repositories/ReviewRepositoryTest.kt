package com.bibliophile.repositories

import kotlin.test.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

import com.bibliophile.models.ReviewRequest
import com.bibliophile.utils.TestDatabaseFactory

class ReviewRepositoryTest {

    @BeforeTest
    fun setup() {
        TestDatabaseFactory.init()
    }

    @AfterTest
    fun teardown() {
        TestDatabaseFactory.reset()
    }

    @Test
    fun `test create review`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val reviewRequest = ReviewRequest(
            bookId = "book123",
            content = "Great book!",
            rate = 5,
            favorite = true,
            reviewedAt = LocalDate.parse("2000-01-01")
        )

        val review = ReviewRepository.addReview(userId = user.id, review = reviewRequest)

        assertNotNull(review)
        assertEquals("book123", review.bookId)
        assertEquals("Great book!", review.content)
        assertEquals(5, review.rate)
        assertTrue(review.favorite)
    }

    @Test
    fun `test get review`() = runBlocking {
        val user1 = UserRepository.create("a@a.com", "user1", "pw1")
        val user2 = UserRepository.create("b@b.com", "user2", "pw2")
        val reviewRequest1 = ReviewRequest("book123", "Amazing!", 5, true, LocalDate.parse("2000-01-01"))
        val reviewRequest2 = ReviewRequest("book456", "Not bad", 3, false, LocalDate.parse("2000-01-01"))

        ReviewRepository.addReview(userId = user1.id, review = reviewRequest1)
        ReviewRepository.addReview(userId = user2.id, review = reviewRequest2)

        val reviews = ReviewRepository.allReviews()
        assertEquals(2, reviews.size)
        assertNotNull(reviews[0])
        assertEquals("book123", reviews[0].bookId)
        assertEquals("Amazing!", reviews[0].content)
        assertNotNull(reviews[1])
        assertEquals("book456", reviews[1].bookId)
        assertEquals("Not bad", reviews[1].content)
    }

    @Test
    fun `test get review by ID`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val reviewRequest = ReviewRequest(
            bookId = "book123",
            content = "Great book!",
            rate = 5,
            favorite = true,
            reviewedAt = LocalDate.parse("2000-01-01")
        )

        val createdReview = ReviewRepository.addReview(userId = user.id, review = reviewRequest)
        val review = ReviewRepository.review(createdReview.id)

        assertNotNull(review)
        assertEquals("book123", review.bookId)
        assertEquals("Great book!", review.content)
    }

    @Test
    fun `test get reviews by user ID`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val reviewRequest1 = ReviewRequest("book123", "Amazing!", 5, true, LocalDate.parse("2000-01-01"))
        val reviewRequest2 = ReviewRequest("book456", "Not bad", 3, false, LocalDate.parse("2000-01-01"))

        ReviewRepository.addReview(userId = user.id, review = reviewRequest1)
        ReviewRepository.addReview(userId = user.id, review = reviewRequest2)

        val reviews = ReviewRepository.getReviewsByUserId(user.id)
        assertEquals(2, reviews.size)
    }

    @Test
    fun `test get reviews by book ID`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val reviewRequest = ReviewRequest("book123", "Amazing!", 5, true, LocalDate.parse("2000-01-01"))

        ReviewRepository.addReview(userId = user.id, review = reviewRequest)

        val reviews = ReviewRepository.getReviewsById("book123")
        assertEquals(1, reviews.size)
        assertEquals("Amazing!", reviews[0].content)
    }

    @Test
    fun `test update review`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val reviewRequest = ReviewRequest("book123", "Good book", 4, false, LocalDate.parse("2000-01-01"))

        val createdReview = ReviewRepository.addReview(userId = user.id, review = reviewRequest)

        val updatedRequest = ReviewRequest("book123", "Excellent book", 5, true, LocalDate.parse("2000-01-02"))
        val updateResult = ReviewRepository.updateReview(createdReview.id, user.id, updatedRequest)

        assertTrue(updateResult)

        val updatedReview = ReviewRepository.review(createdReview.id)
        assertNotNull(updatedReview)
        assertEquals("Excellent book", updatedReview.content)
        assertEquals(5, updatedReview.rate)
        assertTrue(updatedReview.favorite)
    }

    @Test
    fun `test update review with invalid ID returns false`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val reviewRequest = ReviewRequest("book123", "Good book", 4, false, LocalDate.parse("2000-01-01"))

        val updateResult = ReviewRepository.updateReview(-1, user.id, reviewRequest)
        assertFalse(updateResult)
    }

    @Test
    fun `test update review with wrong user ID returns false`() = runBlocking {
        val user1 = UserRepository.create("a@a.com", "user1", "pw1")
        val user2 = UserRepository.create("b@b.com", "user2", "pw2")
        val reviewRequest = ReviewRequest("book123", "Good book", 4, false, LocalDate.parse("2000-01-01"))

        val createdReview = ReviewRepository.addReview(user1.id, reviewRequest)

        val updateResult = ReviewRepository.updateReview(createdReview.id, user2.id, reviewRequest)
        assertFalse(updateResult)
    }

    @Test
    fun `test delete review`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val reviewRequest = ReviewRequest("book123", "Good book", 4, false, LocalDate.parse("2000-01-01"))

        val createdReview = ReviewRepository.addReview(userId = user.id, review = reviewRequest)

        val deleteResult = ReviewRepository.deleteReview(createdReview.id, user.id)
        assertTrue(deleteResult)

        val deletedReview = ReviewRepository.review(createdReview.id)
        assertNull(deletedReview)
    }

    @Test
    fun `test delete review with invalid ID returns false`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")

        val deleteResult = ReviewRepository.deleteReview(-1, user.id)
        assertFalse(deleteResult)
    }

    @Test
    fun `test delete review with wrong user ID returns false`() = runBlocking {
        val user1 = UserRepository.create("a@a.com", "user1", "pw1")
        val user2 = UserRepository.create("b@b.com", "user2", "pw2")
        val reviewRequest = ReviewRequest("book123", "Good book", 4, false, LocalDate.parse("2000-01-01"))

        val createdReview = ReviewRepository.addReview(user1.id, reviewRequest)

        val deleteResult = ReviewRepository.deleteReview(createdReview.id, user2.id)
        assertFalse(deleteResult)
    }
}
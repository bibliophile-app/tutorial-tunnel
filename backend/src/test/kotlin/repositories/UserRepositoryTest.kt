package com.bibliophile.repositories

import kotlin.test.*
import kotlinx.coroutines.runBlocking

import com.bibliophile.models.User
import com.bibliophile.utils.TestDatabaseFactory

class UserRepositoryTest {

    @BeforeTest
    fun setup() {
        TestDatabaseFactory.init()
    }

    @AfterTest
    fun teardown() {
        TestDatabaseFactory.reset()
    }

    @Test
    fun `test create user`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")

        assertNotNull(user)
        assertEquals("test@example.com", user.email)
        assertEquals("testuser", user.username)
    }

    @Test
    fun `test get all users`() = runBlocking {
        UserRepository.create("email1", "user1", "password1")
        UserRepository.create("email2", "user2", "password2")
        val users = UserRepository.getAllUsers()
        assertEquals(2, users.size)
    }

    @Test
    fun `test find user by ID`() = runBlocking {
        val createdUser = UserRepository.create(
            email = "test@example.com",
            username = "testuser",
            passwordHash = "hashedpassword"
        )

        val foundUser = UserRepository.findById(createdUser.id!!)
        assertNotNull(foundUser)
        assertEquals(createdUser.id, foundUser.id)
        assertEquals(createdUser.username, foundUser.username)
    }

    @Test
    fun `test find user by username`() = runBlocking {
        val createdUser = UserRepository.create(
            email = "test@example.com",
            username = "testuser",
            passwordHash = "hashedpassword"
        )

        val foundUser = UserRepository.findByUsername("testuser")
        assertNotNull(foundUser)
        assertEquals(createdUser.id, foundUser.id)
        assertEquals(createdUser.email, foundUser.email)
    }

    @Test
    fun `test find user by invalid ID`() = runBlocking {
        val user = UserRepository.findById(-1)
        assertNull(user)
    }

    @Test
    fun `test update user`() = runBlocking {
        val createdUser = UserRepository.create(
            email = "test@example.com",
            username = "testuser",
            passwordHash = "hashedpassword"
        )

        val updatedUser = createdUser.copy(username = "updateduser", passwordHash = "newhashedpassword")
        val updateResult = UserRepository.update(updatedUser)

        assertTrue(updateResult)

        val foundUser = UserRepository.findById(createdUser.id!!)
        assertNotNull(foundUser)
        assertEquals("updateduser", foundUser.username)
        assertEquals("newhashedpassword", foundUser.passwordHash)
    }

    @Test
    fun `test update non-existent user returns false`() = runBlocking {
        val nonExistentUser = User(id = 9999, email = "nope", username = "ghost", passwordHash = "none")
        val result = UserRepository.update(nonExistentUser)
        assertFalse(result)
    }

    @Test
    fun `test delete user by ID`() = runBlocking {
        val createdUser = UserRepository.create(
            email = "test@example.com",
            username = "testuser",
            passwordHash = "hashedpassword"
        )

        val deleteResult = UserRepository.delete(createdUser.id!!)
        assertTrue(deleteResult)

        val foundUser = UserRepository.findById(createdUser.id!!)
        assertNull(foundUser)
    }

    @Test
    fun `test delete user by username`() = runBlocking {
        val createdUser = UserRepository.create(
            email = "test@example.com",
            username = "testuser",
            passwordHash = "hashedpassword"
        )

        val deleteResult = UserRepository.delete("testuser")
        assertTrue(deleteResult)

        val foundUser = UserRepository.findByUsername("testuser")
        assertNull(foundUser)
    }

    @Test
    fun `test delete user by invalid ID returns false`() = runBlocking {
        val result = UserRepository.delete(-1)
        assertFalse(result)
    }

    @Test
    fun `test delete user by invalid username returns false`() = runBlocking {
        val result = UserRepository.delete("ghostuser")
        assertFalse(result)
    }

    @Test
    fun `test authenticate user with valid credentials`() = runBlocking {
        val createdUser = UserRepository.create(
            email = "test@example.com",
            username = "testuser",
            passwordHash = "hashedpassword"
        )

        val authenticatedUser = UserRepository.authenticate("testuser", "hashedpassword")
        assertNotNull(authenticatedUser)
        assertEquals(createdUser.id, authenticatedUser.id)
    }

    @Test
    fun `test authenticate user with invalid credentials`() = runBlocking {
        UserRepository.create(
            email = "test@example.com",
            username = "testuser",
            passwordHash = "hashedpassword"
        )

        val authenticatedUser = UserRepository.authenticate("testuser", "wrongpassword")
        assertNull(authenticatedUser)
    }

    @Test
    fun `test authenticate non-existent user returns null`() = runBlocking {
        val authenticatedUser = UserRepository.authenticate("testuser", "wrongpassword")
        assertNull(authenticatedUser)
    }

    @Test
    fun `test get user profile`() = runBlocking {
        val user = UserRepository.create("testemail", "testuser", "hashedpassword")
        val profile = UserRepository.getUserProfile(user.id!!)
        assertNotNull(profile)
        assertEquals("testuser", profile.username)
        assertTrue(profile.booklists.isEmpty())
        assertTrue(profile.quotes.isEmpty())
        assertTrue(profile.reviews.isEmpty())
    }

    @Test
    fun `test get user profile with invalid ID returns null`() = runBlocking {
        val profile = UserRepository.getUserProfile(-1)
        assertNull(profile)
    }
}
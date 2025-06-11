package com.bibliophile.repositories

import kotlin.test.*
import kotlinx.coroutines.runBlocking

import com.bibliophile.models.BooklistRequest
import com.bibliophile.utils.TestDatabaseFactory

class BooklistRepositoryTest {

    @BeforeTest
    fun setup() {
        TestDatabaseFactory.init()
    }

    @AfterTest
    fun teardown() {
        TestDatabaseFactory.reset()
    }

    @Test
    fun `test create booklist`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val booklistRequest = BooklistRequest(
            listName = "My Booklist",
            listDescription = "A list of my favorite books"
        )

        BooklistRepository.addBooklist(userId = user.id, booklist = booklistRequest)

        val booklists = BooklistRepository.allBooklists()
        assertEquals(1, booklists.size)
        assertEquals("My Booklist", booklists[0].listName)
        assertEquals("A list of my favorite books", booklists[0].listDescription)
    }

    @Test
    fun `test get booklist by ID`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val booklistRequest = BooklistRequest(
            listName = "My Booklist",
            listDescription = "A list of my favorite books"
        )

        BooklistRepository.addBooklist(userId = user.id, booklist = booklistRequest)
        val booklists = BooklistRepository.allBooklists()
        val booklistId = booklists[0].id

        val booklist = BooklistRepository.booklist(booklistId)
        assertNotNull(booklist)
        assertEquals("My Booklist", booklist.listName)
        assertEquals("A list of my favorite books", booklist.listDescription)
    }

    @Test
    fun `test get booklist with invalid ID returns null`() = runBlocking {
        val result = BooklistRepository.booklist(-1)
        assertNull(result)
    }

    @Test
    fun `test update booklist`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val booklistRequest = BooklistRequest(
            listName = "My Booklist",
            listDescription = "A list of my favorite books"
        )

        BooklistRepository.addBooklist(userId = user.id, booklist = booklistRequest)
        val booklists = BooklistRepository.allBooklists()
        val booklistId = booklists[0].id

        val updatedRequest = BooklistRequest(
            listName = "Updated Booklist",
            listDescription = "An updated description"
        )

        val updateResult = BooklistRepository.updateBooklist(booklistId, userId = user.id, updatedBooklist = updatedRequest)
        assertTrue(updateResult)

        val updatedBooklist = BooklistRepository.booklist(booklistId)
        assertNotNull(updatedBooklist)
        assertEquals("Updated Booklist", updatedBooklist.listName)
        assertEquals("An updated description", updatedBooklist.listDescription)
    }

    @Test
    fun `test update booklist with invalid ID returns false`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")

        val updateResult = BooklistRepository.updateBooklist(
            booklistId = -1,
            userId = user.id,
            updatedBooklist = BooklistRequest("New name", "New desc")
        )
        assertFalse(updateResult)
    }

    @Test
    fun `test update booklist with wrong user ID returns false`() = runBlocking {
        val user1 = UserRepository.create("a@a.com", "user1", "pw1")
        val user2 = UserRepository.create("b@b.com", "user2", "pw2")
        val booklist = BooklistRepository.addBooklist(user1.id, BooklistRequest("Name", "Desc"))

        val result = BooklistRepository.updateBooklist(booklist.id, user2.id, BooklistRequest("X", "Y"))
        assertFalse(result)
    }

    @Test
    fun `test delete booklist`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val booklistRequest = BooklistRequest(
            listName = "My Booklist",
            listDescription = "A list of my favorite books"
        )

        BooklistRepository.addBooklist(userId = user.id, booklist = booklistRequest)
        val booklists = BooklistRepository.allBooklists()
        val booklistId = booklists[0].id

        val deleteResult = BooklistRepository.removeBooklist(booklistId, userId = user.id)
        assertTrue(deleteResult)

        val deletedBooklist = BooklistRepository.booklist(booklistId)
        assertNull(deletedBooklist)
    }

    @Test
    fun `test remove booklist with invalid ID returns false`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")

        val result = BooklistRepository.removeBooklist(-1, user.id)
        assertFalse(result)
    }

    @Test
    fun `test remove booklist with wrong user ID returns false`() = runBlocking {
        val user1 = UserRepository.create("a@a.com", "user1", "pw1")
        val user2 = UserRepository.create("b@b.com", "user2", "pw2")
        val booklist = BooklistRepository.addBooklist(user1.id, BooklistRequest("Name", "Desc"))

        val result = BooklistRepository.removeBooklist(booklist.id, user2.id)
        assertFalse(result)
    }

    @Test
    fun `test add book to booklist`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val booklistRequest = BooklistRequest(
            listName = "My Booklist",
            listDescription = "A list of my favorite books"
        )

        BooklistRepository.addBooklist(userId = 1, booklist = booklistRequest)
        val booklists = BooklistRepository.allBooklists()
        val booklistId = booklists[0].id

        val addBookResult = BooklistRepository.addBookToBooklist(booklistId, userId = user.id, bookId = "book123")
        assertTrue(addBookResult)

        val booklistWithBooks = BooklistRepository.booklistWithBooks(booklistId)
        assertNotNull(booklistWithBooks)
        assertEquals(1, booklistWithBooks.books.size)
        assertEquals("book123", booklistWithBooks.books[0])
    }

    @Test
    fun `test add book to nonexistent booklist returns false`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val result = BooklistRepository.addBookToBooklist(-1, user.id, "book123")
        assertFalse(result)
    }

    @Test
    fun `test add book to booklist with wrong user ID returns false`() = runBlocking {
        val user1 = UserRepository.create("a@a.com", "user1", "pw1")
        val user2 = UserRepository.create("b@b.com", "user2", "pw2")
        val booklist = BooklistRepository.addBooklist(user1.id, BooklistRequest("Name", "Desc"))

        val result = BooklistRepository.addBookToBooklist(booklist.id, user2.id, "book123")
        assertFalse(result)
    }

    @Test
    fun `test remove book from booklist`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val booklistRequest = BooklistRequest(
            listName = "My Booklist",
            listDescription = "A list of my favorite books"
        )

        BooklistRepository.addBooklist(userId = 1, booklist = booklistRequest)
        val booklists = BooklistRepository.allBooklists()
        val booklistId = booklists[0].id

        BooklistRepository.addBookToBooklist(booklistId, userId = user.id, bookId = "book123")
        val removeBookResult = BooklistRepository.removeBookFromBooklist(booklistId, userId = user.id, bookId = "book123")
        assertTrue(removeBookResult)

        val booklistWithBooks = BooklistRepository.booklistWithBooks(booklistId)
        assertNotNull(booklistWithBooks)
        assertTrue(booklistWithBooks.books.isEmpty())
    }

    @Test
    fun `test remove book from nonexistent booklist returns false`() = runBlocking {
        val user = UserRepository.create("test@example.com", "testuser", "hashedpassword")
        val result = BooklistRepository.removeBookFromBooklist(-1, user.id, "book123")
        assertFalse(result)
    }


    @Test
    fun `test remove book to booklist with wrong user ID returns false`() = runBlocking {
        val user1 = UserRepository.create("a@a.com", "user1", "pw1")
        val user2 = UserRepository.create("b@b.com", "user2", "pw2")
        val booklist = BooklistRepository.addBooklist(user1.id, BooklistRequest("Name", "Desc"))
        val booklistBook = BooklistRepository.addBookToBooklist(booklist.id, user1.id, "book123")

        val result = BooklistRepository.removeBookFromBooklist(booklist.id, user2.id, "book123")
        assertFalse(result)
    }

    @Test
    fun `test get booklist with books for invalid ID returns null`() = runBlocking {
        val result = BooklistRepository.booklistWithBooks(-1)
        assertNull(result)
    }

    @Test
    fun `test booklistWithBooks returns empty description when null`() = runBlocking {
        val user = UserRepository.create("null@desc.com", "user", "pw")

        val booklist = BooklistRepository.addBooklist(
            userId = user.id,
            booklist = BooklistRequest(
                listName = "No description",
                listDescription = null
            )
        )

        val result = BooklistRepository.booklistWithBooks(booklist.id)
        assertNotNull(result)
        assertEquals("", result.listDescription)
    }
}
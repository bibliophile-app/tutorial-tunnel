import { useState, useEffect } from "react";

const API_URL = "/booklists";

const BooklistManager = () => {
    const [booklists, setBooklists] = useState([]);
    const [currentBooklist, setCurrentBooklist] = useState(null);
    const [newBooklist, setNewBooklist] = useState({ listName: "", listDescription: "" });
    const [bookId, setBookId] = useState("");
    const [isEditing, setIsEditing] = useState(false);
    const [editBooklist, setEditBooklist] = useState({ listName: "", listDescription: "" });

    useEffect(() => {
        displayAllBooklists();
    }, []);

    const sendRequest = async (url, method = "GET", data = null) => {
        const options = {
            method,
            headers: { "Content-Type": "application/json" },
            credentials: 'include'
        };

        if (data) options.body = JSON.stringify(data);

        const response = await fetch(url, options);
        return response.ok ? response.json() : [];
    };

    const displayAllBooklists = async () => {
        const booklists = await sendRequest(API_URL);
        setBooklists(booklists);
    };

    const displayBooklist = async (id) => {
        const booklist = await sendRequest(`${API_URL}/${id}/books`);
        setCurrentBooklist(booklist);
        setEditBooklist({ listName: booklist.listName, listDescription: booklist.listDescription });
        setIsEditing(false);
    };

    const deleteBooklist = async (id) => {
        await sendRequest(`${API_URL}/${id}`, "DELETE");
        setCurrentBooklist(null);
        displayAllBooklists();
    };

    const addNewBooklist = async (event) => {
        event.preventDefault();
        await sendRequest(API_URL, "POST", newBooklist);
        setNewBooklist({ listName: "", listDescription: "" });
        displayAllBooklists();
    };

    const updateBooklist = async () => {
        if (currentBooklist) {
            await sendRequest(`${API_URL}/${currentBooklist.id}`, "PUT", {...editBooklist});
            setIsEditing(false);
            displayBooklist(currentBooklist.id);
        }
        displayAllBooklists();
    };

    const addBookToList = async () => {
        if (currentBooklist && bookId) {
            await sendRequest(`${API_URL}/${currentBooklist.id}/books`, "POST", { bookId });
            displayBooklist(currentBooklist.id);
            setBookId("");
        }
    };

    const removeBookFromList = async (bookId) => {
        if (currentBooklist) {
            await sendRequest(`${API_URL}/${currentBooklist.id}/books/${bookId}`, "DELETE");
            displayBooklist(currentBooklist.id);
        }
    };

    return (
        <div>
            <h1>Booklist Manager</h1>

            <form onSubmit={addNewBooklist}>
                <h3>Create New Booklist</h3>
                <input type="text" placeholder="List Name" value={newBooklist.listName} onChange={(e) => setNewBooklist({ ...newBooklist, listName: e.target.value })} required />
                <input type="text" placeholder="Description" value={newBooklist.listDescription} onChange={(e) => setNewBooklist({ ...newBooklist, listDescription: e.target.value })} />
                <button type="submit">Add Booklist</button>
            </form>

            <h3>All Booklists</h3>
            <ul>
                {booklists.map((booklist) => (
                    <li key={booklist.id}>
                        {booklist.listName} - {booklist.listDescription}{" "}
                        <button onClick={() => displayBooklist(booklist.id)}>View</button>
                        <button onClick={() => deleteBooklist(booklist.id)}>Delete</button>
                    </li>
                ))}
            </ul>

            {currentBooklist && (
                <div>
                    <h3>Current Booklist</h3>
                    {isEditing ? (
                        <div>
                            <input type="text" value={editBooklist.listName} onChange={(e) => setEditBooklist({ ...editBooklist, listName: e.target.value })} />
                            <input type="text" value={editBooklist.listDescription} onChange={(e) => setEditBooklist({ ...editBooklist, listDescription: e.target.value })} />
                            <button onClick={updateBooklist}>Save</button>
                            <button onClick={() => setIsEditing(false)}>Cancel</button>
                        </div>
                    ) : (
                        <div>
                            <p><b>Name:</b> {currentBooklist.listName}</p>
                            <p><b>Description:</b> {currentBooklist.listDescription}</p>
                            <button onClick={() => setIsEditing(true)}>Edit</button>
                        </div>
                    )}

                    <h4>Books:</h4>
                    <ul>
                        {currentBooklist.books?.map((book) => (
                            <li key={book}>
                                {book} <button onClick={() => removeBookFromList(book)}>Remove</button>
                            </li>
                        ))}
                    </ul>

                    <h4>Add Book to List</h4>
                    <input type="number" placeholder="Book ID" value={bookId} onChange={(e) => setBookId(e.target.value)} />
                    <button onClick={addBookToList}>Add Book</button>
                </div>
            )}
        </div>
    );
};

export default BooklistManager;
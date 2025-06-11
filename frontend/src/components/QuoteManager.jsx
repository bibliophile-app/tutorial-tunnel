import { useEffect, useState } from "react";

const API_URL = "/quotes";

const QuoteManager = () => {
    const [quotes, setQuotes] = useState([]);
    const [newQuote, setNewQuote] = useState({ content: "" });
    const [searchId, setSearchId] = useState("");
    const [foundQuote, setFoundQuote] = useState(null);
    const [editQuote, setEditQuote] = useState(null);
    const [editData, setEditData] = useState({ content: "" });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        fetchQuotes();
    }, []);

    const sendRequest = async (url, method = "GET", data = null) => {
        try {
            setError("");
            const options = {
                method,
                headers: { "Content-Type": "application/json" },
                credentials: 'include',
                body: data ? JSON.stringify(data) : null
            };

            const response = await fetch(url, options);
            
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error("Request failed:", error);
            setError(error.message);
            return null;
        }
    };

    const fetchQuotes = async () => {
        setLoading(true);
        try {
            const data = await sendRequest(API_URL);
            setQuotes(Array.isArray(data) ? data : []);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async () => {
        if (!searchId) return;
        const id = parseInt(searchId);
        
        if (isNaN(id)) {
            setError("Please enter a valid numeric ID");
            return;
        }

        const data = await sendRequest(`${API_URL}/${id}`);
        setFoundQuote(data);
    };

    const addQuote = async (e) => {
        e.preventDefault();
        

        if (await sendRequest(API_URL, "POST", {
            content: newQuote.content
        })) {
            setNewQuote({ content: "" });
            await fetchQuotes();
        }
    };

    const deleteQuote = async (id) => {
        if (window.confirm("Are you sure you want to delete this quote?")) {
            if (await sendRequest(`${API_URL}/${id}`, "DELETE")) {
                await fetchQuotes();
            }
        }
    };

    const startEdit = (quote) => {
        setEditQuote(quote);
        setEditData({
            content: quote.content
        });
    };

    const updateQuote = async () => {
        if (!editQuote) return;

        if (await sendRequest(`${API_URL}/${editQuote.id}`, "PUT", {
            content: editData.content
        })) {
            setEditQuote(null);
            setEditData({ content: "" });
            await fetchQuotes();
        }
    };

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Quote Manager</h1>
            
            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    {error}
                </div>
            )}

            {/* Add Quote Form */}
            <form onSubmit={addQuote} className="mb-6 p-4 border rounded">
                <h3 className="text-lg font-semibold mb-2">Add New Quote</h3>
                <div className="flex gap-2 mb-2">
                    <input
                        type="text"
                        placeholder="Quote Content"
                        value={newQuote.content}
                        onChange={(e) => setNewQuote({...newQuote, content: e.target.value})}
                        className="border p-2 flex-1"
                        required
                    />
                    <button type="submit" className="bg-blue-500 text-white p-2 px-4 hover:bg-blue-600">
                        Add
                    </button>
                </div>
            </form>

            {/* Search Section */}
            <div className="mb-6 p-4 border rounded">
                <h3 className="text-lg font-semibold mb-2">Search Quote</h3>
                <div className="flex gap-2">
                    <input
                        type="number"
                        placeholder="Quote ID"
                        value={searchId}
                        onChange={(e) => setSearchId(e.target.value)}
                        className="border p-2"
                        min="1"
                    />
                    <button 
                        onClick={handleSearch} 
                        className="bg-green-500 text-white p-2 px-4 hover:bg-green-600"
                    >
                        Search
                    </button>
                </div>
                {foundQuote ? (
                    <div className="mt-4 p-3 border rounded bg-gray-50">
                        <p><b>ID:</b> {foundQuote.id}</p>
                        <p><b>User ID:</b> {foundQuote.userId}</p>
                        <p><b>Content:</b> {foundQuote.content}</p>
                    </div>
                ) : searchId && (
                    <p className="mt-2 text-gray-500">No quote found with ID {searchId}</p>
                )}
            </div>

            {/* Quotes List */}
            <div className="mb-6">
                <h3 className="text-lg font-semibold mb-2">All Quotes</h3>
                {loading ? (
                    <div className="text-center p-4 text-gray-500">
                        Loading quotes...
                    </div>
                ) : (
                    <ul className="space-y-2">
                        {quotes.length === 0 ? (
                            <p className="text-gray-500">No quotes available</p>
                        ) : (
                            quotes.map((quote) => (
                                <li 
                                    key={quote.id || `quote-${Math.random()}`}
                                    className="flex justify-between items-center p-3 border rounded hover:bg-gray-50"
                                >
                                    <div className="flex-1">
                                        <p className="font-medium">User {quote.userId}</p>
                                        <p className="text-gray-600">{quote.content}</p>
                                    </div>
                                    <div className="flex gap-2">
                                        <button 
                                            onClick={() => startEdit(quote)}
                                            className="bg-yellow-500 text-white p-1 px-3 rounded hover:bg-yellow-600"
                                        >
                                            Edit
                                        </button>
                                        <button 
                                            onClick={() => deleteQuote(quote.id)}
                                            className="bg-red-500 text-white p-1 px-3 rounded hover:bg-red-600"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </li>
                            ))
                        )}
                    </ul>
                )}
            </div>

            {/* Edit Modal */}
            {editQuote && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                    <div className="bg-white p-6 rounded-lg w-full max-w-2xl">
                        <h3 className="text-lg font-semibold mb-4">Edit Quote</h3>
                        <div className="space-y-3">
                            <textarea
                                placeholder="Quote Content"
                                value={editData.content}
                                onChange={(e) => setEditData({...editData, content: e.target.value})}
                                className="border p-2 w-full h-32"
                                required
                            />
                            <div className="flex justify-end gap-2">
                                <button 
                                    onClick={() => {
                                        setEditQuote(null);
                                        setEditData({ content: "" });
                                    }}
                                    className="bg-gray-500 text-white p-2 px-4 rounded hover:bg-gray-600"
                                >
                                    Cancel
                                </button>
                                <button 
                                    onClick={updateQuote}
                                    className="bg-blue-500 text-white p-2 px-4 rounded hover:bg-blue-600"
                                >
                                    Save Changes
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default QuoteManager;
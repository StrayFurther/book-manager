import { useEffect, useState } from "react";
import { Book } from "../../../models/book";
import { bookService } from "../../../services/book-service";
import AddBookModal from "./AddBookModal";
import { useNavigate } from "react-router-dom";

const Books = () => {
    const [books, setBooks] = useState<Book[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [showAdd, setShowAdd] = useState(false)
    const navigate = useNavigate();

    async function load() {
        setLoading(true);
        setError(null);
        try {
            const list = await bookService.list();
            console.log("[Books] loaded", list);
            setBooks(list);
        } catch (err: any) {
            console.error("[Books] load failed", err);
            setError(err?.message ?? "Failed to load books");
            setBooks([]);
        } finally {
            setLoading(false);
        }
    }
    
    useEffect(() => { load(); }, []);

    return (
        <div className="container">
            <div className="header">
                <h2 style={{ margin: 0 }}>Books</h2>
                <div className="controls">
                    <button className="btn" onClick={() => setShowAdd(true)}>Add book</button>
                    <button className="btn secondary" onClick={() => load()} disabled={loading}>Refresh</button>
                    {loading && <span className="muted">Loading...</span>}
                </div>
            </div>

            <ul className="list">
                {books.map(b => (
                    <li key={b.id} className="list-item">
                        <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
                            <div style={{ fontWeight: 600 }}>{b.title}</div>
                            <div className="muted">— {b.author}</div>
                        </div>
                        <div style={{ display: "flex", gap: 8 }}>
                            <button className="btn secondary" onClick={() => navigate(`/books/${b.id}`)}>Details</button>
                        </div>
                    </li>
                ))}
            </ul>

            {error && <div style={{ color: 'red', marginTop: 12 }}>Error: {error}</div>}

            <AddBookModal
                isOpen={showAdd}
                onClose={() => setShowAdd(false)}
                onCreated={(created) => {
                    setBooks(prev => [created, ...prev]);
                    setShowAdd(false);
                    load();
                }}
                onError={(msg) => setError(msg)}
            />
        </div>
    );
}
export default Books;
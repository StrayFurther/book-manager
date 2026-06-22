import React, { useState } from "react";
import { Book } from "../../../models/book";
import { bookService } from "../../../services/book-service";

type Props = {
  isOpen: boolean;
  onClose: () => void;
  onCreated?: (book: Book) => void;
  onError?: (msg: string | null) => void;
};

export default function AddBookModal({ isOpen, onClose, onCreated, onError }: Props) {
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [isbn, setIsbn] = useState("");
  const [publicationYear, setPublicationYear] = useState<number | "">("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    onError?.(null);
    if (!title || !author || !isbn || publicationYear === "") {
      const msg = "Please fill title, author, isbn and publication year.";
      setError(msg);
      onError?.(msg);
      return;
    }
    setLoading(true);
    try {
      const created = await bookService.createBook({
        title,
        author,
        isbn,
        publicationYear: Number(publicationYear),
        description: description || null,
      });
      onCreated?.(created);
      // reset
      setTitle("");
      setAuthor("");
      setIsbn("");
      setPublicationYear("");
      setDescription("");
      onClose();
    } catch (err: any) {
      const msg = err?.message ?? "Failed to create book";
      setError(msg);
      onError?.(msg);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{
      position: "fixed", inset: 0, display: "flex", alignItems: "center", justifyContent: "center",
      background: "rgba(0,0,0,0.4)", zIndex: 9999
    }}>
      <form onSubmit={submit} style={{ background: "white", padding: 20, borderRadius: 8, width: 420 }}>
        <h3>Add Book</h3>
        {error && <div style={{ color: "red", marginBottom: 8 }}>{error}</div>}
        <div style={{ marginBottom: 8 }}>
          <label>Title</label><br />
          <input value={title} onChange={e => setTitle(e.target.value)} style={{ width: "100%" }} />
        </div>
        <div style={{ marginBottom: 8 }}>
          <label>Author</label><br />
          <input value={author} onChange={e => setAuthor(e.target.value)} style={{ width: "100%" }} />
        </div>
        <div style={{ marginBottom: 8 }}>
          <label>ISBN</label><br />
          <input value={isbn} onChange={e => setIsbn(e.target.value)} style={{ width: "100%" }} />
        </div>
        <div style={{ marginBottom: 8 }}>
          <label>Publication Year</label><br />
          <input
            value={publicationYear}
            onChange={e => setPublicationYear(e.target.value === "" ? "" : Number(e.target.value))}
            style={{ width: "100%" }}
            type="number"
          />
        </div>
        <div style={{ marginBottom: 8 }}>
          <label>Description</label><br />
          <textarea value={description} onChange={e => setDescription(e.target.value)} style={{ width: "100%" }} />
        </div>
        <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
          <button type="button" onClick={() => { setError(null); onError?.(null); onClose(); }} disabled={loading}>Cancel</button>
          <button type="submit" disabled={loading}>{loading ? "Saving..." : "Create"}</button>
        </div>
      </form>
    </div>
  );
}
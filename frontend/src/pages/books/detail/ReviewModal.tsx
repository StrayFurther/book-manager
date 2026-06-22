import React, { useState } from "react";

export default function ReviewModal({ isOpen, onClose, onSubmit, initialRating = 5 }) {
  const [content, setContent] = useState("");
  const [rating, setRating] = useState(initialRating);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen) return null;

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    if (!content || rating === "" || rating == null) {
      setError("Please provide review content and rating.");
      return;
    }
    setLoading(true);
    try {
      await onSubmit({ content, rating: Number(rating) });
      setContent("");
      setRating(initialRating);
      onClose();
    } catch (err) {
      setError(err?.message ?? "Failed to submit review");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{
      position: "fixed", inset: 0, display: "flex", alignItems: "center", justifyContent: "center",
      background: "rgba(0,0,0,0.4)", zIndex: 9999
    }}>
      <form onSubmit={handleSubmit} style={{ background: "white", padding: 20, borderRadius: 8, width: 560 }}>
        <h3>Add Review</h3>
        {error && <div style={{ color: "red", marginBottom: 8 }}>{error}</div>}
        <div style={{ marginBottom: 8 }}>
          <label>Content</label><br />
          <textarea value={content} onChange={e => setContent(e.target.value)} style={{ width: "100%" }} />
        </div>
        <div style={{ marginBottom: 8 }}>
          <label>Rating</label><br />
          <input type="number" min={1} max={5} value={rating} onChange={e => setRating(e.target.value === "" ? "" : Number(e.target.value))} style={{ width: 100 }} />
        </div>
        <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
          <button type="button" onClick={() => { setError(null); onClose(); }} disabled={loading}>Cancel</button>
          <button type="submit" disabled={loading}>{loading ? "Adding..." : "Add Review"}</button>
        </div>
      </form>
    </div>
  );
}
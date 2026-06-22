import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { bookService } from "../../../services/book-service";
import ReviewModal from "./ReviewModal";
import { safeFormatDate } from "../../../utils/safe-date";
import { renderStars } from "../../../utils/render-stars";

export default function BookDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [addingError, setAddingError] = useState(null);

  useEffect(() => {
    if (!id) return;
    let alive = true;
    (async () => {
      setLoading(true);
      setError(null);
      try {
        const b = await bookService.getById(id);
        if (!alive) return;
        // ensure reviews is always an array to avoid render crashes
        setBook({ ...(b || {}), reviews: (b && b.reviews) ? b.reviews : [] });
      } catch (e) {
        setError(e?.message ?? "Failed to load book");
      } finally {
        setLoading(false);
      }
    })();
    return () => { alive = false; };
  }, [id]);

  async function handleModalSubmit({ content, rating }) {
    setAddingError(null);
    try {
      await bookService.addReview(Number(id), content, Number(rating));
      const b = await bookService.getById(id);
      setBook(b);
    } catch (err) {
      setAddingError(err?.message ?? "Failed to add review");
      throw err;
    }
  }

  return (
        <div className="container">
          <div className="detail">
            <div className="detail-header">
              <button className="btn secondary back-btn" onClick={() => navigate(-1)}>Back</button>
              <div style={{ flex: 1 }}>
                {loading ? <div className="skeleton title-skeleton" /> : <h2 style={{ margin: 0 }}>{book?.title}</h2>}
                {!loading && book && (
                  <div className="meta">
                    <span>Author: <strong>{book.author}</strong></span>
                    <span>Year: <strong>{book.publicationYear}</strong></span>
                    <span>ISBN: <strong>{book.isbn}</strong></span>
                  </div>
                )}
              </div>
              <div className="actions">
                <button className="btn" onClick={() => setShowReviewModal(true)}>Add Review</button>
              </div>
            </div>
            {loading && <div className="skeleton paragraph-skeleton" />}
            {error && <div style={{ color: "red" }}>Error: {error}</div>}
    {!loading && book && (
                <>
                  <div style={{ marginBottom: 12 }}>{book.description}</div>
      
                  <section style={{ marginTop: 16 }}>
                    <h3>Reviews</h3>
                    {(book.reviews && book.reviews.length > 0) ? (
                      <ul style={{ padding: 0, listStyle: "none", margin: 0 }}>
                        {(book.reviews || []).map((r: any) => {
                          // defensive: skip invalid entries and log them
                          if (!r) {
                            console.warn("BookDetail: encountered falsy review entry", r);
                            return null;
                          }
                          const dateStr = safeFormatDate(r.date);
                          return (
                            <li key={r.id ?? JSON.stringify(r)} className="review-card">
                              <div style={{ marginBottom: 8 }}>{r.content}</div>
                              <div className="muted">
                                <span className="stars">{renderStars(Number(r.rating || 0))}</span>
                                <span style={{ marginLeft: 8 }}>{dateStr ? `— ${dateStr}` : ""}</span>
                              </div>
                            </li>
                          );
                        })}
                      </ul>
                    ) : (
                      <div className="empty">No reviews yet. Be the first to add one.</div>
                    )}
                  </section>
      
                  {addingError && <div style={{ color: "red", marginTop: 12 }}>{addingError}</div>}
                </>
              )}
        <ReviewModal
          isOpen={showReviewModal}
          onClose={() => setShowReviewModal(false)}
          onSubmit={handleModalSubmit}
          />
        </div>
      </div>
    );
}
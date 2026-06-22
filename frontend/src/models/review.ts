export interface Review {
    id: number;
    content: string;
    date: string;        // backend uses Instant -> ISO string
    rating?: number | null;
    bookId?: number;     // matches GetReviewDTO.bookId (Long)
  }
  
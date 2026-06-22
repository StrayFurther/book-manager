import { Review } from "./review";

export interface Book {
    id: number;
    title: string;
    author: string;
    publicationYear?: number | null; // matches GetBookDTO.publicationYear
    isbn?: string | null;
    description?: string | null;
    reviews?: Review[];  // matches GetBookDTO.reviews
  }
  
  export interface BookPageDTO {
    content: Book[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
  }
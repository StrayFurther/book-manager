import { Book } from "../models/book";
import { Review } from "../models/review";
import { graphql } from "../utils/graphql-helper";

export class BookService {
    async list(): Promise<Book[]> {
      const q = `
        query {
            allBooks {
            content {
                id
                title
                author
                publicationYear
                isbn
                description
            }
            }
        }
      `;
      const data = await graphql<{ allBooks: { content: Book[] } }>(q);
      // debugging: log raw response so we can see why it's empty
      console.log("[bookService.list] graphql response:", data);
      const list = data?.allBooks?.content ?? [];
      console.log("[bookService.list] resolved list length:", list.length, list);
      return list;
    }
  
    async getById(id: number): Promise<Book | null> {
        const q = `
            query ($id: ID!) {
                bookById(id: $id) {
                id
                title
                author
                publicationYear
                isbn
                description
                    reviews {
                        id
                        content
                        date
                        bookId
                        rating
                    }
                }
            }
        `;
        const data = await graphql<{ bookById: Book | null }>(q, { id });
        return data?.bookById ?? null;
    }
  
    async addReview(bookId: number, content: string, rating?: number): Promise<Review> {
        const q = `
                mutation ($input: AddReviewInput!) {
                  addReview(input: $input) {
                    id
                    content
                    date
                    rating
                    bookId
                  }
                }
              `;
        const variables = { input: { bookId, content, rating } };
        const data = await graphql<{ addReview: Review }>(q, variables);
        return data.addReview;
    }
    async createBook(input: {
        title: string;
        author: string;
        isbn: string;
        publicationYear: number;
        description?: string | null;
      }): Promise<Book> {
        const q = `
          mutation ($input: CreateBookInput!) {
            createBook(input: $input) {
              id
              title
              author
              publicationYear
              isbn
              description
            }
          }
        `;
        const data = await graphql<{ createBook: Book }>(q, { input });
        return data.createBook;
    }
}

export const bookService = new BookService();
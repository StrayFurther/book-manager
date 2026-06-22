import React, { useState } from 'react'
import { Link, Route, Routes } from 'react-router-dom'
import Books from './pages/books/overview/Books'
import BookDetail from './pages/books/detail/BookDetail'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <main style={{ padding: 20 }}>
        <Routes>
          <Route path="/" element={<Books />} />
          <Route path="/books" element={<Books />} />
          <Route path="/books/:id" element={<BookDetail />} />
        </Routes>
      </main>
    </>
  )
}

export default App;

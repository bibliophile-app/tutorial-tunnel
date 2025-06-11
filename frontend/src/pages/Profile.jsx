import React from "react"
import { useAuth } from "../utils/AuthContext"

function Profile() {
  const { user } = useAuth()

  if (!user) return <p>Carregando...</p>

  return (
    <div>
      <h1>{user.username}</h1>

      <h2>{user.username}'s booklists</h2>
      <ul>
        {user.booklists.map(list => (
          <li key={list.id}>{list.listName}</li>
        ))}
      </ul>

      <h2>{user.username}'s favorites quotes!</h2>
      <ul>
        {user.quotes.map(q => (
          <li key={q.id}>“{q.content}” — {q.book_title}</li>
        ))}
      </ul>

      <h2>{user.username}'s reviews</h2>
      <ul>
        {user.reviews.map(r => (
          <li key={r.id}>
            <strong>{r.isbn}</strong> — {r.rating / 2}/5
            <p>{r.content}</p>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default Profile;

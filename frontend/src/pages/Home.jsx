import React from 'react'
import { useAuth } from '../utils/AuthContext'

function Home() {
  const {user} = useAuth();

  if (!user) {
    return (<div style={{ minHeight: '100%' }}>
              <h1></h1>
            </div>
    )
  }

  return (
    <div style={{ minHeight: '100%' }}>
      <h1>Bem-vindo, {user.username}</h1>
    </div>
  )
}

export default Home;
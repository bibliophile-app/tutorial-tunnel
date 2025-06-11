const API_URL = "/reviews";

async function fetchReviews() {
  try {
    const response = await fetch(API_URL);
    if (!response.ok) {
      throw new Error(`Erro ao buscar reviews: HTTP ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    throw new Error(`Erro ao buscar reviews: ${error.message}`);
  }
}

async function searchById(reviewId) {
  if (!reviewId) throw new Error("Parâmetro 'reviewId' é obrigatório");
  try {
    const response = await fetch(`${API_URL}/${reviewId}`);
    if (!response.ok) {
      throw new Error(`Erro ao buscar review por ID: HTTP ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    throw new Error(`Erro ao buscar review por ID: ${error.message}`);
  }
}

async function searchByBook(bookId) {
  if (!bookId) throw new Error("Parâmetro 'bookId' é obrigatório");
  try {
    const response = await fetch(`${API_URL}/book/${bookId}`);
    if (!response.ok) {
      throw new Error(`Erro ao buscar reviews do livro: HTTP ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    throw new Error(`Erro ao buscar reviews do livro: ${error.message}`);
  }
}

async function searchByUser(userId) {
  if (!userId) throw new Error("Parâmetro 'userId' é obrigatório");
  try {
    const response = await fetch(`${API_URL}/user/${userId}`);
    if (!response.ok) {
      throw new Error(`Erro ao buscar reviews do usuário: HTTP ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    throw new Error(`Erro ao buscar reviews do usuário: ${error.message}`);
  }
}

async function addReview({ bookId, content, rate, favorite, reviewedAt }) {
  if (!bookId || !rate) throw new Error("Parâmetros 'bookId' e 'rate' são obrigatórios");
  try {
    const response = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: 'include',
      body: JSON.stringify({
        bookId,
        content,
        rate: Number(rate),
        favorite,
        reviewedAt
      })
    });
    if (!response.ok) {
      throw new Error(`Erro ao adicionar review: HTTP ${response.status}`);
    }
    return true;
  } catch (error) {
    throw new Error(`Erro ao adicionar review: ${error.message}`);
  }
}

async function deleteReview(id) {
  if (!id) throw new Error("Parâmetro 'id' é obrigatório");
  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: "DELETE",
      credentials: 'include'
    });
    if (!response.ok) {
      throw new Error(`Erro ao deletar review: HTTP ${response.status}`);
    }
    return true;
  } catch (error) {
    throw new Error(`Erro ao deletar review: ${error.message}`);
  }
}

async function updateReview(id, { bookId, content, rate, favorite, reviewedAt }) {
  if (!id || !bookId || !rate) {
    throw new Error("Parâmetros 'id', 'bookId' e 'rate' são obrigatórios");
  }
  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      credentials: 'include',
      body: JSON.stringify({
        bookId,
        content,
        favorite,
        reviewedAt,
        rate: Number(rate)
      })
    });
    if (!response.ok) {
      throw new Error(`Erro ao atualizar review: HTTP ${response.status}`);
    }
    return true;
  } catch (error) {
    throw new Error(`Erro ao atualizar review: ${error.message}`);
  }
}

export {
  fetchReviews,
  searchById,
  searchByBook,
  searchByUser,
  addReview,
  deleteReview,
  updateReview
};
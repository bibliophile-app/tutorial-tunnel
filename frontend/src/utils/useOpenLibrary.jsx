import { useState } from 'react';

const BASE_REQUEST_URL = 'https://openlibrary.org';
const BASE_COVER_URL = 'https://covers.openlibrary.org'

// Hook para gerenciar requisições à OpenLibrary
const useOpenLibrary = ({ language = 'pt-br', onResults, onError }) => {
  const [loading, setLoading] = useState(false);

  // Função para buscar detalhes de um único livro por OLID
  async function fetchBookByOLID(olid) {
    setLoading(true);
    try {
      // 1. Buscar edição
      const bookRes = await fetch(`${BASE_REQUEST_URL}/books/${olid}.json`);
      const bookData = await bookRes.json();

      // 2. Pegar work ID da edição
      const workKey = bookData.works?.[0]?.key; // ex: "/works/OL27448W"
      const workId = workKey?.split('/').pop();

      // 3. Buscar dados da obra (opcionalmente mais ricos)
      let workData = {};
      if (workId) {
        const workRes = await fetch(`${BASE_REQUEST_URL}${workKey}.json`);
        workData = await workRes.json();
      }

      // 4. Buscar autor(es)
      let authorNames = [];
      if (bookData.authors?.length) {
        const authorFetches = bookData.authors.map(async (author) => {
          const authorKey = author.key; // ex: "/authors/OL26320A"
          const authorRes = await fetch(`${BASE_REQUEST_URL}${authorKey}.json`);
          const authorData = await authorRes.json();
          return authorData.name;
        });
        authorNames = await Promise.all(authorFetches);
      }

      // 5. Gerar URL da capa
      const coverUrl = `${BASE_COVER_URL}/b/olid/${olid}-M.jpg`;

      // 6. Montar objeto final
      const book = {
        olid,
        title: bookData.title || workData.title || 'Título não disponível',
        description:
          bookData.description ||
          workData.description ||
          'No description available.',
        coverUrl,
        first_publish_year:
          bookData.publish_date || workData.first_publish_date || null,
        author_name: authorNames,
      };

      onResults(book);
    } catch (error) {
      onError(error);
    } finally {
      setLoading(false);
    }
  }

  // Função para buscar livros com base em uma query
  async function fetchBooks(query) {
    setLoading(true);
    try {
      const url = `${BASE_REQUEST_URL}/search.json?q=${query}&fields=key,title,author_name,cover_edition_key&lang=${language}`;
      const response = await fetch(url);
      const data = await response.json();

      const books = data.docs
        .filter((book) =>
          Array.isArray(book.author_name) &&
          book.author_name.length > 0 &&
          book.cover_edition_key
        )
        .map((book) => ({
          title: book.title,
          author: book.author_name.join(', '),
          olid: book.cover_edition_key,
          coverUrl: `${BASE_COVER_URL}/b/olid/${book.cover_edition_key}-M.jpg`,
      }));

      onResults(books);
    } catch (error) {
      onError(error);
    } finally {
      setLoading(false);
    }
  }

  // Função principal que decide qual requisição fazer
  async function fetchResults(query, olid = null) {
    if (olid) {
      await fetchBookByOLID(olid);
    } else {
      await fetchBooks(query);
    }
  }

  return {
    fetchResults,
    loading,
  };
};

export default useOpenLibrary;
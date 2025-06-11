import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { Box, Stack, Typography, useMediaQuery } from '@mui/material';
import { useTheme } from '@mui/material/styles';

import { useAuth } from '../utils/AuthContext';
import useOpenLibrary from '../utils/useOpenLibrary';
import { searchByBook } from '../components/reviews/utils';

import Divider from '../atoms/Divider';
import BookImage from '../atoms/BookImage';
import LoadingBox from '../atoms/LoadingBox';
import ActionsMenu from '../components/ActionsMenu';
import DescriptionText from '../components/DescriptionText';
import ReviewForm from '../components/reviews/ReviewForm';
import ReviewSection from '../components/reviews/ReviewSection';
import ReviewHistogram from '../components/reviews/ReviewHistogram';

function BookPage() {
  const { user } = useAuth();
  const { olid } = useParams();
  
  const [book, setBook] = useState(null);
  const [error, setError] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [reviewFormOpen, setReviewFormOpen] = useState(false);

  const theme = useTheme();
  const isMdUp = useMediaQuery(theme.breakpoints.up('md'));

  const { fetchResults, loading } = useOpenLibrary({
    onResults: setBook,
    onError: setError,
  });

  const fetchReviews = async () => {
    if (!olid) return;
    const results = await searchByBook(olid);
    setReviews(results || []);
  };

  useEffect(() => {
    if (!olid) return;
    fetchResults(null, olid);
    fetchReviews();
  }, [olid]);

  const handleReviewSubmit = () => {
    fetchReviews();
    setReviewFormOpen(false);
  };

  if (loading) return <LoadingBox />;

  if (error || !book) {
    return (
      <Typography mt={4}>
        Livro não encontrado!
      </Typography>
    );
  }

  const filteredReviews = {
    user: reviews.filter(r => r.username === user?.username && r.content),
    others: reviews.filter(r => r.username !== user?.username && r.content),
  };

  const BookCover = ({ width = 180, height = '100%' }) => (
    <BookImage
      src={book.coverUrl}
      alt={`Capa de ${book.title}`}
      sx={{ width, height }}
    />
  );

  return (
    <>
      <Box sx={{ minHeight: '100vh', justifyContent: 'center', pb: 5, px: { xs: 3, lg: 0 } }}>
        <Stack spacing={4} direction="row">
          
          {isMdUp && <BookCover />}

          <Stack spacing={2} sx={{ flex: 1 }}>
            <Stack spacing={1}>
              <Box sx={{ display: 'flex', alignItems: 'baseline', flexWrap: 'wrap', gap: 1 }}>
                <Typography variant="h4" fontWeight="bold">
                  {book.title}
                </Typography>
                {book.first_publish_year && (
                  <Typography
                    variant="h5"
                    component="span"
                    sx={{ fontWeight: 400, fontSize: '1.2rem', opacity: 0.8 }}
                  >
                    ({book.first_publish_year})
                  </Typography>
                )}
              </Box>
              {book.author_name?.length > 0 && (
                <Typography sx={{ fontSize: '0.8rem', opacity: 0.8 }}>
                  Escrito por {book.author_name.join(', ')}
                </Typography>
              )}
            </Stack>

            <Divider />
            <DescriptionText description={book.description} />

            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              {user && (
                <ReviewSection
                  title="Suas avaliações"
                  reviews={filteredReviews.user}
                />
              )}
              <ReviewSection
                title="Avaliações recentes"
                reviews={filteredReviews.others}
              />
            </Box>
          </Stack>

          <Box
            sx={{
              gap: 2,
              display: 'flex',
              flexDirection: 'column',
              width: { xs: 'auto', sm: 180, md: 240 },
            }}
          >
            {!isMdUp && <BookCover width={180} height={270} />}

            <ActionsMenu handleReview={() => setReviewFormOpen(true)} />

            <Box>
              <Typography variant="h5" gutterBottom>
                Avaliações
              </Typography>
              <Divider sx={{ width: '100%', mb: 2 }} />
              <ReviewHistogram reviews={reviews} />
            </Box>
          </Box>
        </Stack>
      </Box>

      <ReviewForm
        book={book}
        open={reviewFormOpen}
        onClose={() => setReviewFormOpen(false)}
        onSubmit={handleReviewSubmit}
      />
    </>
  );
}

export default BookPage;
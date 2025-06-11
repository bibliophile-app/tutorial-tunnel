import { useState, useEffect } from 'react';
import { Box, Typography, Stack } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import FormatAlignJustifyIcon from '@mui/icons-material/FormatAlignJustify';

import { useAuth } from '../../utils/AuthContext';
import { searchById } from './utils';
import useOpenLibrary from '../../utils/useOpenLibrary';

import Rating from '../../atoms/Rating';
import Favorite from '../../atoms/Favorite';
import ReviewForm from './ReviewForm';
import UserAvatar from '../../atoms/UserAvatar';
import BookImage from '../../atoms/BookImage';

function ReviewCard ({
  review,
  displayDate = false,
  displayOwner = true,
  displayBookDetails = false,
  displayContent = true
}) {
  const { user } = useAuth();
  const [book, setBook] = useState(null);
  const [currentReview, setCurrentReview] = useState(review);
  const [reviewFormOpen, setReviewFormOpen] = useState(false);

  const owner = user?.username === currentReview.username;

  const { fetchResults } = useOpenLibrary({
    onResults: setBook,
    onError: null,
  });

  useEffect(() => {
    if (currentReview?.bookId) {
      fetchResults(null, currentReview.bookId);
    }
  }, [currentReview]);

  const handleOpen = () => {
    if (owner) setReviewFormOpen(true);
  };

  const handleClose = () => {
    setReviewFormOpen(false);
  };

  const handleReviewUpdated = async () => {
    const updated = await searchById(currentReview.id);
    setCurrentReview(updated);
  };

  if (!currentReview) return null;

  const { username, content, rate, favorite, reviewedAt } = currentReview;

  return (
    <>
      <Box sx={{ display: 'flex', cursor: owner ? 'pointer' : 'default', gap: 1.5, p: 1 }} onClick={handleOpen}>
        {displayBookDetails && book ? (
          <BookImage
            src={book.coverUrl}
            alt={`Capa de ${book.title}`}
            sx={{ width: 60, height: 90 }}
          />
        ) : (
          <UserAvatar username={username} />
        )}

        <Box sx={{ flex: 1 }}>
          <Stack spacing={0.5}>
            {displayBookDetails && book && (
              <Typography
                variant="h6"
                fontSize=".9rem"
                fontWeight="bold"
                component={RouterLink}
                to={`/book/${currentReview.bookId}`}
                onClick={(e) => e.stopPropagation()}
              >
                {book.title}
                {book.first_publish_year && ` (${book.first_publish_year})`}
              </Typography>
            )}

            <Stack direction="row" alignItems="center" spacing={.5}>
              {displayOwner && (
                <>
                  <Typography variant="body2" sx={{ color: '#9da5b4', fontSize: '0.8rem' }}>
                    Avaliado por
                  </Typography>
                  <Typography
                    variant="body2"
                    fontWeight="bold"
                    sx={{ cursor: 'pointer' }}
                    component={RouterLink}
                    to={`/profile/${username}`}
                    onClick={(e) => e.stopPropagation()}
                  >
                    {username}
                  </Typography>
                </>
              )}
              <Rating value={rate / 2} readOnly size="small" precision={0.5} />
              {favorite && (
                <Favorite selected={true} sx={{ cursor: owner ? 'pointer' : 'default', fontSize: '1rem', color: 'background.muted' }} />
              )}
            </Stack>

            {displayContent ? (
              <Typography
                variant="body2"
                sx={{ color: 'neutral.main', wordBreak: 'break-word', overflowWrap: 'break-word' }}
              >
                {content}
              </Typography>
            ) : (
              content && (
                <FormatAlignJustifyIcon
                  sx={{ pt: 0.5, fontSize: '1rem', color: 'background.muted' }}
                />
              )
            )}

            {displayDate && (
              <Typography
                variant="caption"
                sx={{ mt: 1, display: 'block', color: '#9da5b4' }}
              >
                {new Date(reviewedAt + 'T00:00:00').toLocaleDateString('pt-BR', {
                  day: 'numeric',
                  month: 'long',
                  year: 'numeric'
                })}
              </Typography>
            )}
          </Stack>
        </Box>
      </Box>

      <ReviewForm
        book={book}
        open={reviewFormOpen}
        onClose={handleClose}
        reviewId={currentReview.id}
        onSubmit={handleReviewUpdated}
      />
    </>
  );
};

export default ReviewCard;
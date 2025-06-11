import React, { useState, useEffect } from 'react';
import { styled } from '@mui/material/styles';
import {
  Box,
  Button,
  TextField,
  Typography,
  IconButton,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

import Modal from '../../atoms/Modal';
import Divider from '../../atoms/Divider';
import Dialog from '../../atoms/Dialog';
import BookImage from '../../atoms/BookImage';
import LoadingBox from '../../atoms/LoadingBox';
import Favorite from '../../atoms/Favorite';
import StyledRating from '../../atoms/Rating';
import { addReview, updateReview, deleteReview, searchById } from './utils';

const StyledDate = styled('input')(({ theme }) => ({
  backgroundColor: `${theme.palette.background.default}66`,
  border: 'none',
  borderRadius: 4,
  color: theme.palette.white.main,
  outline: 'none',
  fontSize: '0.8em',
  fontFamily: '"Inter", sans-serif',
  padding: '3px 5px',
  transition: 'color 0.2s ease',

  '&:hover': {
    color: theme.palette.info.main
  },
}));

function ReviewForm({ 
  book,
  open,
  onClose,
  onSubmit,
  reviewId = null 
}) {
  const today = new Date().toISOString().slice(0, 10);
  const maxLength = 255;

  const [readDate, setReadDate] = useState(today);
  const [content, setContent] = useState('');
  const [rating, setRating] = useState(0);
  const [favorite, setIsFavorite] = useState(false);

  const [mode, setMode] = useState('create'); // 'create' ou 'edit'
  const [loading, setIsLoading] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [actionToConfirm, setActionToConfirm] = useState(null); // 'delete' ou 'close'

  useEffect(() => {
    if (!reviewId || !open) return;

    setMode('edit');
    setIsLoading(true);

    const fetchReview = async () => {
      try {
        const review = await searchById(reviewId);
        setReadDate(review.reviewedAt || today);
        setContent(review.content || '');
        setRating(review.rate / 2 || 0);
        setIsFavorite(review.favorite || false);
      } catch (error) {
        // feedback visual de erro (pode adicionar um Snackbar ou Toast)
      } finally {
        setIsLoading(false);
      }
    };

    fetchReview();
  }, [reviewId, open]);

  const handleExit = () => {
    setIsFavorite(false);
    setContent('');
    setRating(0);
    onClose();
  };

  const handleSave = async () => {
    const reviewData = {
      bookId: book.olid,
      reviewedAt: readDate,
      content: content,
      rate: rating * 2,
      favorite,
    };

    if (mode === 'create') await addReview(reviewData);
    else await updateReview(reviewId, reviewData);
    if (onSubmit) onSubmit();
    handleExit();
  };

  const handleAttemptClose = () => {
    setActionToConfirm('close');
    setConfirmOpen(true);
  };

  const handleAttemptDelete = () => {
    setActionToConfirm('delete');
    setConfirmOpen(true);
  };

  const handleConfirm = () => {
    setConfirmOpen(false);

    if (actionToConfirm === 'close') {
      handleExit();
    } else if (actionToConfirm === 'delete') {
      deleteReview(reviewId);
      handleExit();
    }

    setActionToConfirm(null);
  };

  const handleCancel = () => {
    setConfirmOpen(false);
    setActionToConfirm(null);
  };

  const dialogProps = {
    close: {
      title: 'Descartar alterações?',
      description: 'As alterações serão perdidas. Deseja continuar?',
      confirmText: 'Descartar',
    },
    delete: {
      title: 'Remover avaliação?',
      description: 'Tem certeza que deseja remover esta avaliação? Esta ação não pode ser desfeita.',
      confirmText: 'Remover',
    },
  }[actionToConfirm || 'close'];

  if (!book) return;

  return (
    <React.Fragment>
      <Modal open={open} onClose={onClose} sx={{ width: { xs: '100%', md: '50%' } }}>
        {loading ? <LoadingBox /> : (
          <React.Fragment>
            <Box sx={{ px: 2, py: 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h4">
                Eu li...
              </Typography>
              <IconButton onClick={handleAttemptClose}>
                <CloseIcon sx={{ color: '#fefefe' }} />
              </IconButton>
            </Box>

            <Divider />

            <Box sx={{ p: 2, display: 'flex', flexDirection: 'row', alignItems: 'top', gap: 3 }}>
              <BookImage
                src={book.coverUrl}
                alt={book.title}
                sx={{ width: 180, height: 240, display: { xs: 'none', md: 'block' } }}
              />

              <Box sx={{ width: '100%' }}>
                <Typography variant="h5" gutterBottom>
                  {book.title}
                </Typography>

                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                  <Typography variant="body1">Lido em</Typography>
                  <StyledDate
                    type="date"
                    value={readDate}
                    onChange={(e) => setReadDate(e.target.value)}
                  />
                </Box>

                <Box>
                  <TextField
                    placeholder="Escreva sua avaliação..."
                    multiline
                    fullWidth
                    minRows={4}
                    maxRows={12}
                    value={content}
                    onChange={(e) => {
                      if (e.target.value.length > maxLength) return;
                      setContent(e.target.value);
                    }}
                    sx={{
                      backgroundColor: '#ecf0f1',
                      borderRadius: 1,
                      '& .MuiInputBase-root': {
                        fontSize: '0.875rem',
                        padding: 1,
                      },
                    }}
                  />

                  <Typography
                    variant="caption"
                    align="right"
                    sx={{ display: 'block', mt: 0.5, fontSize: '0.75rem' }}
                  >
                    {content.length} / {maxLength}
                  </Typography>
                </Box>

                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-start' }}>
                  <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                    <Box
                      sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', width: '100%' }}
                    >
                      <Typography variant="h5" fontSize="0.9em">Nota</Typography>
                      <Typography variant="body2">{rating} / 5</Typography>
                    </Box>
                    <StyledRating value={rating} precision={0.5} onChange={(e, newValue) => !!newValue && setRating(newValue)} />
                  </Box>

                  <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                    <Typography variant="h5" fontSize="0.9em">Favorito</Typography>
                    <Favorite onClick={() => setIsFavorite((prev) => !prev)} selected={favorite} />
                  </Box>
                </Box>
              </Box>
            </Box>

            <Divider />

            <Box sx={{ px: 2, py: 1, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
              {mode === 'edit' && (
                <Button color="error" variant="contained" sx={{ color: '#fefefe' }} onClick={handleAttemptDelete}>
                  REMOVER
                </Button>
              )}

              <Button variant="contained" color="success" sx={{ color: '#fefefe' }} onClick={handleSave}>
                {mode === 'edit' ? 'SALVAR ALTERAÇÕES' : 'SALVAR'}
              </Button>
            </Box>
          </React.Fragment>
        )}
      </Modal>

      <Dialog
        open={confirmOpen}
        title={dialogProps.title}
        description={dialogProps.description}
        confirmText={dialogProps.confirmText}
        cancelText="Cancelar"
        onConfirm={handleConfirm}
        onCancel={handleCancel}
      />
    </React.Fragment>
  );
}

export default ReviewForm;
import React, { useState } from 'react';
import { Box, Typography, Stack, Button, Dialog, DialogTitle, DialogContent, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

import ReviewCard from './ReviewCard';
import Divider from '../../atoms/Divider';

function ReviewSection({ 
  title, 
  reviews = [] 
}) {
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const visibleReviews = reviews.slice(0, 3);

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
        <Typography variant="h5" gutterBottom sx={{ color: '#dfe6ec' }}>
          {title}
        </Typography>
        {reviews && reviews.length > 3 && (
          <Button
            onClick={handleOpen}
            sx={{ fontSize: '0.8rem', textTransform: 'none' }}
          >
            Ver mais
          </Button>
        )}
      </Box>
      <Divider sx={{ mb: 2 }} />

      {reviews.length === 0 ? (
        <Typography variant="body2" sx={{ color: '#9da5b4' }}>
          Nenhuma avaliação!
        </Typography>
      ) : (
        <Stack spacing={2}>
          {visibleReviews && visibleReviews.map((review, index) => (
            <React.Fragment key={review.id}>
              {index !== 0 && <Divider sx={{ opacity: 0.5 }} />}
              <ReviewCard review={review} />
            </React.Fragment>
          ))}
        </Stack>
      )}

      {/* Diálogo em tela cheia */}
      <Dialog
        open={open}
        onClose={handleClose}
        fullScreen
        sx={{
          '& .MuiDialog-paper': {
            backgroundColor: 'background.surface',
            borderRadius: 0,
            margin: 0,
            maxWidth: '100%',
            width: '100%',
          },
        }}
      >
        <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between' }}>
          {title}
          <IconButton onClick={handleClose} sx={{ color: '#dfe6ec' }}>
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <Divider />

        <DialogContent>
          <Stack spacing={2}>
            {reviews.map((review, index) => (
              <React.Fragment key={review.id}>
                {index !== 0 && <Divider sx={{ opacity: 0.5 }} />}
                <ReviewCard key={review.id} review={review} displayDate={true} />
              </React.Fragment>
            ))}
          </Stack>
        </DialogContent>
      </Dialog>
    </Box>
  );
};

export default ReviewSection;
import { useState } from 'react';
import { alpha } from '@mui/material/styles';
import { Stack, Button, Snackbar, Alert } from '@mui/material';
import { PlaylistAdd } from '@mui/icons-material';

import Divider from '../atoms/Divider';
import { useAuth } from '../utils/AuthContext';

function ActionsMenu({ handleReview }) {
  const { user, handleSignin } = useAuth();
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  const handleShare = async () => {
    const currentUrl = window.location.href;
    if (navigator.share) {
      try {
        await navigator.share({
          title: 'Veja isso!',
          url: currentUrl,
        });
      } catch (err) {
        console.error('Falha ao compartilhar:', err);
      }
    } else {
      try {
        await navigator.clipboard.writeText(currentUrl);
        setSnackbarOpen(true);
      } catch (err) {
        console.error('Falha ao copiar:', err);
      }
    }
  };

  return (
    <>
      <Stack
        sx={{
          width: '100%',
          borderRadius: '5px',
          bgcolor: (theme) => alpha(theme.palette.background.contrast, 0.8),
          backdropFilter: 'blur(4px)',
          boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
        }}
      >
        {user ? (
          <>
            <Button startIcon={<PlaylistAdd />} fullWidth variant="text" color="inherit">
              Quero ler
            </Button>

            <Divider />

            <Button fullWidth variant="text" color="inherit" onClick={handleReview}>
              Avaliar ou registrar novamente...
            </Button>

            <Divider />

            <Button fullWidth variant="text" color="inherit">
              Adicionar às listas...
            </Button>
          </>
        ) : (
          <Button fullWidth variant="text" color="inherit" onClick={handleSignin}>
            Faça login para registrar, avaliar ou comentar
          </Button>
        )}

        <Divider />

        <Button fullWidth variant="text" color="inherit" onClick={handleShare}>
          Compartilhar
        </Button>
      </Stack>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={() => setSnackbarOpen(false)} severity="success" sx={{ width: '100%' }}>
          Link copiado para a área de transferência!
        </Alert>
      </Snackbar>
    </>
  );
}

export default ActionsMenu;
import PropTypes from 'prop-types';
import { Box, Modal, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 300,
  bgcolor: '#ffffff',
  borderRadius: 0.2, // bordas arredondadas
  boxShadow: 24,
  p: 4,
};

export default function PopUp({ open, onClose, children}) {

  return (
    <Modal
      open={open}
      onClose={onClose}
      aria-labelledby="login-modal-title"
      aria-describedby="login-modal-description"
      disablePortal
    >
      <Box sx={style}>
        <IconButton
          onClick={onClose}
          sx={{
            position: 'absolute',
            top: 8,
            right: 8,
          }}
          aria-label="fechar"
        >
          <CloseIcon />
        </IconButton>

        
        {children}
      </Box>
    </Modal>
  );
}

PopUp.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  children: PropTypes.node.isRequired,
};


import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
} from '@mui/material';

function ConfirmDialog({
  open,
  title,
  description,
  confirmText = 'Confirmar',
  cancelText = 'Cancelar',
  onConfirm,
  onCancel,
}) {
  return (
    <Dialog open={open} onClose={onCancel}>
        <DialogTitle color='black'>{title}</DialogTitle>
        <DialogContent>
            <DialogContentText>{description}</DialogContentText>
        </DialogContent>
        <DialogActions>
            <Button onClick={onCancel}>{cancelText}</Button>
            <Button onClick={onConfirm} color="error" variant="contained">
                {confirmText}
            </Button>
        </DialogActions>
    </Dialog>
  );
}

export default ConfirmDialog;
import { styled } from '@mui/material/styles';
import { Rating } from '@mui/material';

const StyledRating = styled(Rating)(({ theme }) => ({
  '& .MuiRating-iconFilled': {
    color: theme.palette.purple.secondary,
  },
  '& .MuiRating-iconHover': {
    color: theme.palette.purple.main,
  },
  '& .MuiRating-iconEmpty': {
    color: theme.palette.background.muted,
  }
}));

export default StyledRating;
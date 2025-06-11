import { styled } from '@mui/material/styles';
import { IconButton } from '@mui/material';

import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';

const StyledFavorite = styled(IconButton, {
  shouldForwardProp: (prop) => prop !== 'isFavorite',
})(({ isFavorite }) => ({
  padding: 0,
  color: isFavorite ? '#ff6d75' : 'rgba(255,255,255,0.3)',
  '&:hover': {
    color: '#ff3d47',
  },
  transition: 'color 0.2s',
}));

function Favorite({ selected, onClick, ...props }) {
    return (
        <StyledFavorite onClick={onClick} isFavorite={selected}>
            {selected ? <FavoriteIcon {...props}/> : <FavoriteBorderIcon {...props}/>}
        </StyledFavorite>
    )
}

export default Favorite;
import { Box, Typography, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { styled } from '@mui/material/styles';

import BookImage from '../../atoms/BookImage';

const StyledItem = styled(Paper)(({ theme }) => ({
    display: 'flex',
    marginBottom: "10px",
    alignItems: 'center',
    backgroundColor: 'transparent',
    transition: '0.2s',
    color: theme.palette.white,
    borderRadius: theme.shape.borderRadius,
  
    '&:hover': {
      backgroundColor: theme.palette.background.surface,
    },
}));
  
function ResultBooks({ books, paginatedBooks }) {
    const navigate = useNavigate();
    function onSelect(bookId) {
        navigate(`/book/${encodeURIComponent(bookId)}`);
    };

    return !books.length ? (
        <Typography variant="body" fontSize={"0.8rem"}>
          NO RESULTS FOUND...
        </Typography>           
    ) : ( 
        paginatedBooks.map((book, _) => (
            <StyledItem key={book.olid} elevation={1} onClick={() => onSelect(book.olid)}>
                <BookImage
                  src={book.coverUrl}
                  alt={book.title}
                  sx={{width: 64, height: 96}}
                />

                <Box sx={{ ml: 2 }}>
                    <Typography variant="h7" fontWeight="bold" color='white' gutterBottom>
                        {book.title}
                    </Typography>
                    <Typography variant="body2">
                        {book.author}
                    </Typography>
                </Box>
            </StyledItem>
      ))
    )
};

export default ResultBooks;
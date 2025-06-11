import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { Box, Divider, Pagination, Stack, Typography, useMediaQuery } from '@mui/material';
import { styled, useTheme } from '@mui/material/styles';

import LoadingBox from '../atoms/LoadingBox';
import useOpenLibrary from '../utils/useOpenLibrary';
import Categories from '../components/search/Categories';
import ResultBooks from '../components/search/ResultBooks';

const ITEMS_PER_PAGE = 10;

const StyledPagination = styled(Pagination)(({ theme }) => ({
  '& .MuiPaginationItem-root': {
    color: theme.palette.neutral.main,
  },
  '& .Mui-selected': {
    backgroundColor: theme.palette.secondary.main,
    color: theme.palette.common.white,
    '&:hover': {
      backgroundColor: '#0d47a1',
    },
  },
}));

function SearchPage() {
  const { query } = useParams();
  const [page, setPage] = useState(1);
  const [results, setResults] = useState([]);
  const [error, setError] = useState(null);
  const [category, setCategory] = useState('Livros');

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const { fetchResults, loading } = useOpenLibrary({
    onResults: setResults,
    onError: setError,
  });

  useEffect(() => {
    if (query) {
      setPage(1);
      fetchResults(query);
    }
  }, [query]);

  const paginatedResults = results.slice(
    (page - 1) * ITEMS_PER_PAGE,
    page * ITEMS_PER_PAGE
  );

  if (loading) {
    return <LoadingBox />
  }

  if (error) {
    return (
      <Typography color="error" mt={4}>
        Erro ao buscar livros: {error.message}
      </Typography>
    );
  }

  return (
    <Box sx={{ minHeight: '100vh', justifyContent: 'center', px: { xs: 3, lg: 0 } }}>
      <Stack spacing={4} direction="row">
        <Stack sx={{ width: { xs: "100%", md: "70%" } }}>
          <Typography variant="body" fontSize={"0.8rem"} gutterBottom>
            MOSTRANDO RESULTADOS PARA “{query.toUpperCase()}”
          </Typography>
          <Divider sx={{ my: 2, bgcolor: "background.muted" }} />
          
          <ResultBooks books={results} paginatedBooks={paginatedResults}/>

          {results.length > 0 &&
            <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}>
              <StyledPagination
                count={Math.ceil(results.length / ITEMS_PER_PAGE)}
                page={page}
                onChange={(_, val) => setPage(val)}
              />
            </Box>
          }
        </Stack>

        {!isMobile && (
          <Stack sx={{ width: "30%" }}>
            <Categories selected={category} onSelect={setCategory}/>
          </Stack>
        )}
      </Stack>
    </Box>
  );
}

export default SearchPage;
